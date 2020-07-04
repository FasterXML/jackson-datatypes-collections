package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.google.common.collect.ImmutableCollection;

abstract class GuavaImmutableCollectionDeserializer<T extends ImmutableCollection<Object>>
        extends GuavaCollectionDeserializer<T>
{
    private static final long serialVersionUID = 1L;

    GuavaImmutableCollectionDeserializer(JavaType selfType,
            JsonDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    protected abstract ImmutableCollection.Builder<Object> createBuilder();

    // Can not modify Immutable collections now can we
    @Override // since 2.10
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.FALSE;
    }

    @Override // since 2.10
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
            throws IOException, JsonProcessingException
    {
        JsonDeserializer<?> valueDes = _valueDeserializer;
        JsonToken t;
        final TypeDeserializer typeDeser = _valueTypeDeserializer;
        // No way to pass actual type parameter; but does not matter, just
        // compiler-time fluff:
        ImmutableCollection.Builder<Object> builder = createBuilder();

        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            Object value;

            if (t == JsonToken.VALUE_NULL) {
                if (_skipNullValues) {
                    continue;
                }
                value = _resolveNullToValue(ctxt);
            } else if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
            } else {
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
            builder.add(value);
        }
        // No class outside of the package will be able to subclass us,
        // and we provide the proper builder for the subclasses we implement.
        @SuppressWarnings("unchecked")
        T collection = (T) builder.build();
        return collection;
    }

    /**
     * @since 2.10
     */
    protected Object _resolveNullToValue(DeserializationContext ctxt) throws IOException
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
}
