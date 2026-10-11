package tools.jackson.datatype.guava.deser;

import java.util.function.ObjIntConsumer;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.util.ClassUtil;

/**
 * Shared entry reader for mutable and immutable Guava Multisets.
 *
 * @since 3.3
 */
final class MultisetEntryReader implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    private final GuavaCollectionDeserializer<?> _owner;
    private final int _maxSize;

    MultisetEntryReader(GuavaCollectionDeserializer<?> owner, int maxSize) {
        _owner = owner;
        _maxSize = maxSize;
    }

    /**
     * Helper method for reading {@link com.google.common.collect.Multiset} entries,
     * serialized as {@code {"element":...,"count":...}}, until the end of
     * enclosing JSON Array.
     *
     * @since 3.3
     */
    void readEntries(JsonParser p, DeserializationContext ctxt,
            ObjIntConsumer<Object> adder)
        throws JacksonException
    {
        int size = 0;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            size += readEntry(p, ctxt, adder, size);
        }
    }

    /**
     * Helper method for reading a single {@link com.google.common.collect.Multiset}
     * entry: parser is expected to point to {@code START_OBJECT} of the entry.
     *
     * @param sizeSoFar Number of elements (sum of counts) already read
     *
     * @return Number of elements added (count of the entry, or 0 if skipped)
     *
     * @since 3.3
     */
    int readEntry(JsonParser p, DeserializationContext ctxt,
            ObjIntConsumer<Object> adder, int sizeSoFar)
        throws JacksonException
    {
        if (!p.hasToken(JsonToken.START_OBJECT)) {
            ctxt.reportInputMismatch(_owner,
"Unexpected token (%s) for `Multiset` entry: expected JSON Object with properties \"element\" and \"count\"",
                    p.currentToken());
        }
        Object element = null;
        JsonToken elementToken = null;
        boolean hasElement = false;
        boolean skipEntry = false;
        int count = 0;
        boolean hasCount = false;

        for (String name = p.nextName(); name != null; name = p.nextName()) {
            final JsonToken t = p.nextToken();
            if ("element".equals(name)) {
                hasElement = true;
                elementToken = t;
                element = _owner._deserializeElement(p, ctxt, t);
                skipEntry = (element == GuavaCollectionDeserializer.SKIPPED_ELEMENT);
            } else if ("count".equals(name)) {
                if (t != JsonToken.VALUE_NUMBER_INT) {
                    ctxt.reportInputMismatch(_owner,
                            "Invalid `Multiset` entry \"count\": expected positive integer, got %s", t);
                }
                count = p.getIntValue();
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
            return 0;
        }
        if ((long) sizeSoFar + count > _maxSize) {
            ctxt.reportInputMismatch(_owner,
                    "`Multiset` size (%d) exceeds the maximum allowed (%d, from `GuavaModule.configureMaxMultisetSize()`)",
                    (long) sizeSoFar + count, _maxSize);
        }
        try {
            if (element == null) {
                final int copies = count;
                Integer added = _owner._addElement(p, ctxt, elementToken, null, value -> {
                    adder.accept(value, copies);
                    return copies;
                });
                return (added == null) ? 0 : added;
            }
            adder.accept(element, count);
            return count;
        } catch (ClassCastException | IllegalArgumentException e) {
            // elements of sorted Multiset not mutually comparable; or too many occurrences
            reportFailure(_owner, ctxt, e);
        }
        return 0;
    }

    /**
     * @since 3.3
     */
    static <R> R reportFailure(ValueDeserializer<?> owner,
            DeserializationContext ctxt, RuntimeException e)
        throws JacksonException
    {
        String msg = e.getMessage();
        return ctxt.reportInputMismatch(owner,
                "Failed to build `%s` from deserialized elements: %s",
                owner.handledType().getSimpleName(),
                (msg == null) ? ClassUtil.nameOf(e.getClass()) : msg);
    }
}
