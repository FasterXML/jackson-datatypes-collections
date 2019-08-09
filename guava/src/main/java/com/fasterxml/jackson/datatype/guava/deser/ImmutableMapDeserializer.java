package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableMap;

public class ImmutableMapDeserializer
    extends GuavaImmutableMapDeserializer<ImmutableMap<Object, Object>>
{
    private static final long serialVersionUID = 2L;

    public ImmutableMapDeserializer(JavaType type, KeyDeserializer keyDeser,
            JsonDeserializer<?> valueDeser, TypeDeserializer valueTypeDeser,
            NullValueProvider nuller)
    {
        super(type, keyDeser, valueDeser, valueTypeDeser, nuller);
    }

    @Override
    public ImmutableMapDeserializer withResolved(KeyDeserializer keyDeser,
            JsonDeserializer<?> valueDeser, TypeDeserializer valueTypeDeser,
            NullValueProvider nuller) {
        return new ImmutableMapDeserializer(_containerType, keyDeser,
                valueDeser, valueTypeDeser, nuller);
    }

    @Override
    protected ImmutableMap.Builder<Object, Object> createBuilder() {
        return ImmutableMap.builder();
    }
}
