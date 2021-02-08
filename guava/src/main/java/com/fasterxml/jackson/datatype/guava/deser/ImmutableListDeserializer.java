package com.fasterxml.jackson.datatype.guava.deser;

import com.google.common.collect.ImmutableList;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public class ImmutableListDeserializer extends
    GuavaImmutableCollectionDeserializer<ImmutableList<Object>>
{
    public ImmutableListDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    public ImmutableListDeserializer withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new ImmutableListDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    protected ImmutableList.Builder<Object> createBuilder() {
        return ImmutableList.builder();
    }

    @Override
    protected ImmutableList<Object> _createEmpty(DeserializationContext ctxt) {
        return ImmutableList.of();
    }

    @Override
    protected ImmutableList<Object> _createWithSingleElement(DeserializationContext ctxt, Object value) {
        return ImmutableList.of(value);
    }
}
