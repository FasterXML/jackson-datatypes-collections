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
import tools.jackson.databind.util.ClassUtil;

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

    GuavaMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        this(selfType, deser, typeDeser, nuller, unwrapSingle, true);
    }

    GuavaMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle, boolean asEntries) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
        _asEntries = asEntries;
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
            _deserializeMultisetEntries(p, ctxt, set::add);
            return set;
        }
        ValueDeserializer<?> valueDes = _valueDeserializer;
        JsonToken t;
        final TypeDeserializer typeDeser = _valueTypeDeserializer;
        T set = createMultiset();
    
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            Object value;

            if (t == JsonToken.VALUE_NULL) {
                if (_skipNullValues) {
                    continue;
                }
                value = _nullProvider.getNullValue(ctxt);
                if (value == null) {
                    _tryToAddNull(p, ctxt, set);
                    continue;
                }
            } else if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
            } else {
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
            _addToMultiset(ctxt, set, value);
        }
        return set;
    }

    @Override
    protected T _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (_asEntries) {
            T set = createMultiset();
            _deserializeMultisetEntry(p, ctxt, set::add);
            return set;
        }
        return super._deserializeFromSingleValue(p, ctxt);
    }

    @Override
    protected T _createEmpty(DeserializationContext ctxt) {
        return createMultiset();
    }

    @Override
    protected T _createWithSingleElement(DeserializationContext ctxt, Object value) {
        final T result = createMultiset();
        _addToMultiset(ctxt, result, value);
        return result;
    }

    private void _addToMultiset(DeserializationContext ctxt, T set, Object value)
        throws JacksonException
    {
        try {
            set.add(value);
        } catch (NullPointerException e) {
            if (value != null) {
                throw e;
            }
            ctxt.reportInputMismatch(this,
                    "Guava `Collection` of type %s does not accept `null` values",
                    ClassUtil.getTypeDescription(getValueType(ctxt)));
        } catch (ClassCastException e) {
            // elements of sorted Multiset not mutually comparable
            _reportMultisetFailure(ctxt, e);
        }
    }
}
