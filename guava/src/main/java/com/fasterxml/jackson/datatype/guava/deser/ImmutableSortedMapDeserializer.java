package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ValueDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;

import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSortedMap;

public class ImmutableSortedMapDeserializer
    extends GuavaImmutableMapDeserializer<ImmutableSortedMap<Object, Object>>
{
    public ImmutableSortedMapDeserializer(JavaType type, KeyDeserializer keyDeser,
            ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller) {
        super(type, keyDeser, valueDeser, typeDeser, nuller);
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) {
        return ImmutableSortedMap.of();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Builder<Object, Object> createBuilder() {
        // Not quite sure what to do with sorting/ordering; may require better
        // support either via annotations, or via custom serialization (bean
        // style that includes ordering aspects)
        @SuppressWarnings("rawtypes")
        ImmutableSortedMap.Builder<?, Object> naturalOrder = ImmutableSortedMap.<Comparable, Object>naturalOrder();
        ImmutableSortedMap.Builder<Object, Object> builder = (ImmutableSortedMap.Builder<Object, Object>) naturalOrder;
        return builder;
    }

    @Override
    public GuavaMapDeserializer<ImmutableSortedMap<Object, Object>> withResolved(KeyDeserializer keyDeser,
            ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller)
    {
        return new ImmutableSortedMapDeserializer(_containerType, keyDeser, valueDeser, typeDeser, nuller);
    }
}
