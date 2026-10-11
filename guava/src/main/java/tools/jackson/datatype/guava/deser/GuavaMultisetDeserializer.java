package tools.jackson.datatype.guava.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.datatype.guava.GuavaModule;
import tools.jackson.databind.util.AccessPattern;

import com.google.common.collect.Multiset;

abstract class GuavaMultisetDeserializer<T extends Multiset<Object>>
    extends GuavaCollectionDeserializer<T>
{
    /**
     * Whether entries are expected as {@code {"element":...,"count":...}}
     * (if {@code true}) or as repeated elements (if {@code false}).
     *
     * @since 3.3
     */
    protected final boolean _asEntries;

    /**
     * Maximum number of elements (sum of counts) allowed when reading entries.
     *
     * @since 3.3
     */
    protected final int _maxSize;

    private final MultisetEntryReader _entryReader;

    GuavaMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        this(selfType, deser, typeDeser, nuller, unwrapSingle, true, GuavaModule.DEFAULT_MAX_MULTISET_SIZE);
    }

    GuavaMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, boolean asEntries, int maxSize) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
        _asEntries = asEntries;
        _maxSize = maxSize;
        _entryReader = new MultisetEntryReader(this, maxSize);
    }

    protected abstract T createMultiset();

    @Override
    public AccessPattern getEmptyAccessPattern() {
        // mutable, hence must be:
        return AccessPattern.DYNAMIC;
    }

    @Override
    public T getEmptyValue(DeserializationContext ctxt) {
        return _createEmpty(ctxt);
    }

    @Override
    protected T _deserializeContents(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (_asEntries) {
            T set = createMultiset();
            _entryReader.readEntries(p, ctxt, set::add);
            return set;
        }
        T set = createMultiset();
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            Object value = _deserializeElement(p, ctxt, t);
            if (value == null) {
                _addElement(p, ctxt, t, null, v -> _add(ctxt, set, v));
            } else if (value != SKIPPED_ELEMENT) {
                // Avoid lambda allocation for the common non-null case
                _add(ctxt, set, value);
            }
        }
        return set;
    }

    @Override
    protected T _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (_asEntries) {
            T set = createMultiset();
            _entryReader.readEntry(p, ctxt, set::add, 0);
            return set;
        }
        return super._deserializeFromSingleValue(p, ctxt);
    }

    @Override
    protected T _createEmpty(DeserializationContext ctxt) {
        return createMultiset();
    }

    @Override
    protected T _createWithSingleElement(DeserializationContext ctxt, Object value)
        throws JacksonException
    {
        return _add(ctxt, createMultiset(), value);
    }

    private T _add(DeserializationContext ctxt, T set, Object value)
        throws JacksonException
    {
        try {
            set.add(value);
        } catch (ClassCastException e) {
            // Sorted multisets (`TreeMultiset`) fail this way if elements are not
            // mutually comparable (or, for the first element, not `Comparable` at all)
            _reportBuildFailure(ctxt, e.getMessage());
        }
        return set;
    }
}
