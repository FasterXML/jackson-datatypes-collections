package com.fasterxml.jackson.datatype.guava.deser;

import com.google.common.collect.ImmutableList;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

public class ImmutableListDeserializer extends
        GuavaImmutableCollectionDeserializer<ImmutableList<Object>>
{
    private static final long serialVersionUID = 1L;

    public ImmutableListDeserializer(JavaType selfType,
            JsonDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    public ImmutableListDeserializer withResolved(JsonDeserializer<?> valueDeser, TypeDeserializer typeDeser,
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
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        return builder;
    }
}
