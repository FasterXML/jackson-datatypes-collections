package tools.jackson.datatype.guava.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.exc.InputCoercionException;
import tools.jackson.databind.DeserializationContext;

/**
 * Shared entry reader for mutable and immutable Guava Multisets: reads entries
 * serialized as {@code {"element":...,"count":...}} one at a time, leaving
 * adding of elements to the caller.
 *<p>
 * A new instance is to be created for each Multiset being read, as it keeps
 * track of number of elements (sum of counts) read so far.
 *
 * @since 3.3
 */
final class MultisetEntryReader
{
    private final GuavaCollectionDeserializer<?> _owner;
    private final int _maxSize;

    /**
     * Number of elements (sum of counts) read so far
     */
    private int _size;

    private Object _element;
    private int _count;

    MultisetEntryReader(GuavaCollectionDeserializer<?> owner, int maxSize) {
        _owner = owner;
        _maxSize = maxSize;
    }

    /**
     * Method for advancing to the next (non-skipped) entry within enclosing JSON Array.
     *
     * @return {@code true} if an entry was read (accessible with {@link #element()}
     *    and {@link #count()}); {@code false} if end of Array was reached
     */
    public boolean nextEntry(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        while (p.nextToken() != JsonToken.END_ARRAY) {
            if (readEntry(p, ctxt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method for reading a single entry: parser is expected to point to
     * {@code START_OBJECT} of the entry, and is left at its {@code END_OBJECT}.
     *
     * @return {@code true} if an entry was read (accessible with {@link #element()}
     *    and {@link #count()}); {@code false} if entry is to be skipped
     */
    public boolean readEntry(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (!p.hasToken(JsonToken.START_OBJECT)) {
            // JSON `null` entries are skipped if so configured (as with other collections)
            if (p.hasToken(JsonToken.VALUE_NULL) && _owner.skipsNullValues()) {
                return false;
            }
            // otherwise give problem handlers a chance to recover; if one does, entry is dropped
            ctxt.handleUnexpectedToken(_owner.containerType(), p.currentToken(), p,
"Unexpected token (%s) for `Multiset` entry: expected JSON Object with properties \"element\" and \"count\"",
                    p.currentToken());
            p.skipChildren();
            return false;
        }
        Object element = null;
        boolean hasElement = false;
        boolean skipEntry = false;
        int count = 0;
        boolean hasCount = false;

        for (String name = p.nextName(); name != null; name = p.nextName()) {
            final JsonToken t = p.nextToken();
            if ("element".equals(name)) {
                hasElement = true;
                element = _owner._deserializeElement(p, ctxt, t);
                skipEntry = (element == GuavaCollectionDeserializer.SKIPPED_ELEMENT);
            } else if ("count".equals(name)) {
                if (t != JsonToken.VALUE_NUMBER_INT) {
                    ctxt.reportInputMismatch(_owner,
                            "Invalid `Multiset` entry \"count\": expected positive integer, got %s", t);
                }
                try {
                    count = p.getIntValue();
                } catch (InputCoercionException e) {
                    // value does not fit in `int`
                    count = 0;
                    ctxt.reportInputMismatch(_owner,
                            "Invalid `Multiset` entry \"count\": value out of range for `int`: %s",
                            p.getString());
                }
                if (count < 1) {
                    ctxt.reportInputMismatch(_owner,
                            "Invalid `Multiset` entry \"count\": expected positive integer, got %d", count);
                }
                hasCount = true;
            } else {
                if (!ctxt.handleUnknownProperty(p, _owner, _owner.handledType(), name)) {
                    p.skipChildren();
                }
            }
        }
        if (!hasElement) {
            ctxt.reportInputMismatch(_owner, "Invalid `Multiset` entry: missing \"element\" property");
        }
        if (!hasCount) {
            ctxt.reportInputMismatch(_owner, "Invalid `Multiset` entry: missing \"count\" property");
        }
        if (skipEntry) {
            return false;
        }
        // NOTE: counted even if `null` element is later dropped by a problem handler
        final long newSize = (long) _size + count;
        if (newSize > _maxSize) {
            ctxt.reportInputMismatch(_owner,
                    "`Multiset` size (%d) exceeds the maximum allowed (%d, from `GuavaModule.configureMaxMultisetSize()`)",
                    newSize, _maxSize);
        }
        _size = (int) newSize;
        _element = element;
        _count = count;
        return true;
    }

    /**
     * @return Element of the entry last read (may be {@code null})
     */
    public Object element() {
        return _element;
    }

    /**
     * @return Count of the entry last read (always positive)
     */
    public int count() {
        return _count;
    }
}
