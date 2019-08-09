package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSortedMap;

public class ImmutableSortedMapDeserializer
    extends GuavaImmutableMapDeserializer<ImmutableSortedMap<Object, Object>>
{
    private static final long serialVersionUID = 2L;

    public ImmutableSortedMapDeserializer(JavaType type, KeyDeserializer keyDeser,
            JsonDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller) {
        super(type, keyDeser, valueDeser, typeDeser, nuller);
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
            JsonDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller)
    {
        return new ImmutableSortedMapDeserializer(_containerType, keyDeser, valueDeser, typeDeser, nuller);
    }
}
