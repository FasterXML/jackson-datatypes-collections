package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableCollection.Builder;
import com.google.common.collect.ImmutableSet;

public class ImmutableSetDeserializer extends GuavaImmutableCollectionDeserializer<ImmutableSet<Object>>
{
    public ImmutableSetDeserializer(JavaType selfType,
            JsonDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    public ImmutableSetDeserializer withResolved(JsonDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new ImmutableSetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle);
    }
    
    @Override
    protected Builder<Object> createBuilder() {
        return ImmutableSet.builder();
    }

    @Override
    protected ImmutableSet<Object> _createEmpty(DeserializationContext ctxt) throws IOException {
        return ImmutableSet.of();
    }

    @Override
    protected ImmutableSet<Object> _createWithSingleElement(DeserializationContext ctxt, Object value) throws IOException {
        return ImmutableSet.of(value);
    }
}
