package tools.jackson.datatype.guava.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.util.AccessPattern;
import tools.jackson.databind.util.ClassUtil;

import com.google.common.collect.ImmutableCollection;

abstract class GuavaImmutableCollectionDeserializer<T extends ImmutableCollection<Object>>
        extends GuavaCollectionDeserializer<T>
{
    GuavaImmutableCollectionDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    protected abstract ImmutableCollection.Builder<Object> createBuilder();

    // Can not modify Immutable collections now can we
    @Override
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.FALSE;
    }

    @Override
    public AccessPattern getEmptyAccessPattern() {
        // But we should be able to just share immutable empty instance
        return AccessPattern.CONSTANT;
    }

    @Override
    public T getEmptyValue(DeserializationContext ctxt) {
        return _createEmpty(ctxt);
    }

    @Override
    protected T _deserializeContents(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        Object first = null;
        boolean hasFirst = false;

        while (p.nextToken() != JsonToken.END_ARRAY) {
            Object value = _deserializeSingleValue(p, ctxt);
            if (value == null) {
                // Null values need builder for proper error handling
                ImmutableCollection.Builder<Object> builder = createBuilder();
                if (hasFirst) {
                    builder.add(first);
                }
                if (!_skipNullValues) {
                    _tryToAddNull(p, ctxt, builder);
                }
                return _finishWithBuilder(p, ctxt, builder);
            }
            if (!hasFirst) {
                first = value;
                hasFirst = true;
            } else {
                ImmutableCollection.Builder<Object> builder = createBuilder();
                builder.add(first);
                builder.add(value);
                return _finishWithBuilder(p, ctxt, builder);
            }
        }

        if (!hasFirst) {
            return _createEmpty(ctxt);
        }
        return _createWithSingleElement(ctxt, first);
    }

    private Object _deserializeSingleValue(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (p.currentToken() == JsonToken.VALUE_NULL) {
            return _resolveNullToValue(ctxt);
        }
        return _valueTypeDeserializer == null
            ? _valueDeserializer.deserialize(p, ctxt)
            : _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
    }

    private T _finishWithBuilder(JsonParser p, DeserializationContext ctxt,
            ImmutableCollection.Builder<Object> builder) throws JacksonException
    {
        while (p.nextToken() != JsonToken.END_ARRAY) {
            Object value = _deserializeSingleValue(p, ctxt);
            if (value == null) {
                if (!_skipNullValues) {
                    _tryToAddNull(p, ctxt, builder);
                }
            } else {
                builder.add(value);
            }
        }
        // No class outside of the package will be able to subclass us,
        // and we provide the proper builder for the subclasses we implement.
        @SuppressWarnings("unchecked")
        T collection = (T) builder.build();
        return collection;
    }

    protected Object _resolveNullToValue(DeserializationContext ctxt)
        throws JacksonException
    {
        Object value = _nullProvider.getNullValue(ctxt);

        // Since Guava (immutable) collections are not very happy with {@code null} values,
        // we may eventually need to do additional mapping (see [databind#53]).
        // But for now just use null provider:

        /*
        if (value == null) {
            value = _valueDeserializer.getNullValue(ctxt);
         }
         */
        return value;
    }

    /**
     * Some/many Guava containers do not allow addition of {@code null} values,
     * so isolate handling here.
     */
    protected void _tryToAddNull(JsonParser p, DeserializationContext ctxt,
            ImmutableCollection.Builder<Object> builder)
    {
        // Ideally we'd have better idea of where nulls are accepted, but first
        // let's just produce something better than NPE:
        try {
            builder.add((Object) null);
        } catch (NullPointerException e) {
            ctxt.handleUnexpectedToken(_valueType, JsonToken.VALUE_NULL, p,
                    "Guava `Collection` of type %s does not accept `null` values",
                    ClassUtil.getTypeDescription(getValueType(ctxt)));
        }
    }
}
