package tools.jackson.datatype.guava.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.util.AccessPattern;

import com.google.common.collect.Multiset;

abstract class GuavaMultisetDeserializer<T extends Multiset<Object>>
    extends GuavaCollectionDeserializer<T>
{
    /**
     * Multiset-specific settings.
     *
     * @since 3.3
     */
    protected final MultisetConfig _config;

    GuavaMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        this(selfType, deser, typeDeser, nuller, unwrapSingle, MultisetConfig.DEFAULT);
    }

    GuavaMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, MultisetConfig config) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
        _config = config;
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
        T set = createMultiset();
        if (_config.asEntries()) {
            MultisetEntryReader r = new MultisetEntryReader(this, _config.maxSize());
            while (r.nextEntry(p, ctxt)) {
                _addEntry(p, ctxt, set, r.element(), r.count());
            }
            return set;
        }
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            Object value = _deserializeElement(p, ctxt, t);
            if (value == null) {
                _addElement(p, ctxt, p.currentToken(), null, v -> _add(ctxt, set, v, 1));
            } else if (value != SKIPPED_ELEMENT) {
                // Avoid lambda allocation for the common non-null case
                _add(ctxt, set, value, 1);
            }
        }
        return set;
    }

    @Override
    protected T _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (_config.asEntries()) {
            T set = createMultiset();
            MultisetEntryReader r = new MultisetEntryReader(this, _config.maxSize());
            if (r.readEntry(p, ctxt)) {
                _addEntry(p, ctxt, set, r.element(), r.count());
            }
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
        return _add(ctxt, createMultiset(), value, 1);
    }

    private void _addEntry(JsonParser p, DeserializationContext ctxt, T set,
            Object value, int count)
        throws JacksonException
    {
        // Avoid lambda allocation for the common non-null case
        if (value == null) {
            // NOTE: parser is at END_OBJECT of the entry, as "count" may follow "element"
            _addElement(p, ctxt, p.currentToken(), null, v -> _add(ctxt, set, v, count));
        } else {
            _add(ctxt, set, value, count);
        }
    }

    private T _add(DeserializationContext ctxt, T set, Object value, int count)
        throws JacksonException
    {
        try {
            set.add(value, count);
        } catch (ClassCastException e) {
            // Sorted multisets (`TreeMultiset`) fail this way if elements are not
            // mutually comparable (or, for the first element, not `Comparable` at all)
            _reportBuildFailure(ctxt, e.getMessage());
        }
        return set;
    }
}
