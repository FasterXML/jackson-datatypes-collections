package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.Multiset;

abstract class GuavaMultisetDeserializer<T extends Multiset<Object>>
    extends GuavaCollectionDeserializer<T>
{
    GuavaMultisetDeserializer(JavaType selfType,
            JsonDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    protected abstract T createMultiset();

    @Override
    protected T _deserializeContents(JsonParser p, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        JsonDeserializer<?> valueDes = _valueDeserializer;
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
            } else if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
            } else {
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
            set.add(value);
        }
        return set;
    }

    @Override
    protected T _createEmpty(DeserializationContext ctxt) throws IOException {
        return createMultiset();
    }

    @Override
    protected T _createWithSingleElement(DeserializationContext ctxt, Object value) throws IOException {
        final T result = createMultiset();
        result.add(value);
        return result;
    }
}