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
    GuavaMultisetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
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
            _add(ctxt, set, value);
        }
        return set;
    }

    @Override
    protected T _createEmpty(DeserializationContext ctxt) {
        return createMultiset();
    }

    @Override
    protected T _createWithSingleElement(DeserializationContext ctxt, Object value)
        throws JacksonException
    {
        final T result = createMultiset();
        _add(ctxt, result, value);
        return result;
    }

    private void _add(DeserializationContext ctxt, T set, Object value)
        throws JacksonException
    {
        try {
            set.add(value);
        } catch (ClassCastException e) {
            // Sorted multisets (`TreeMultiset`) fail this way if elements are not
            // mutually comparable (or, for the first element, not `Comparable` at all)
            _reportBuildFailure(ctxt, e.getMessage());
        }
    }
}
