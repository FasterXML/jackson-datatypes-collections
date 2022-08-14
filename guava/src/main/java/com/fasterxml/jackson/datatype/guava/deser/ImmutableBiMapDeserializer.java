package com.fasterxml.jackson.datatype.guava.deser;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ImmutableBiMapDeserializer
    extends GuavaImmutableMapDeserializer<ImmutableBiMap<Object, Object>>
{
    public ImmutableBiMapDeserializer(JavaType type, KeyDeserializer keyDeser,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller) {
        super(type, keyDeser, deser, typeDeser, nuller);
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) {
        return ImmutableBiMap.of();
    }

    @Override
    protected Builder<Object, Object> createBuilder() {
        return ImmutableBiMap.builder();
    }

    @Override
    public GuavaMapDeserializer<ImmutableBiMap<Object, Object>> withResolved(KeyDeserializer keyDeser,
            ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller) {
        return new ImmutableBiMapDeserializer(_containerType, keyDeser, valueDeser, typeDeser, nuller);
    }
}
