package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSortedMap;

public class ImmutableSortedMapDeserializer extends GuavaImmutableMapDeserializer<ImmutableSortedMap<Object, Object>> {

    public ImmutableSortedMapDeserializer(MapType type, KeyDeserializer keyDeser, TypeDeserializer typeDeser,
            JsonDeserializer<?> deser) {
        super(type, keyDeser, typeDeser, deser);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Builder<Object, Object> createBuilder() {
        /*
         * Not quite sure what to do with sorting/ordering; may require better
         * support either via annotations, or via custom serialization (bean
         * style that includes ordering aspects)
         */
        @SuppressWarnings("rawtypes")
        ImmutableSortedMap.Builder<?, Object> naturalOrder = ImmutableSortedMap.<Comparable, Object>naturalOrder();
        ImmutableSortedMap.Builder<Object, Object> builder = (ImmutableSortedMap.Builder<Object, Object>) naturalOrder;
        return builder;
    }

    @Override
    public GuavaMapDeserializer<ImmutableSortedMap<Object, Object>> withResolved(KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser) {
        return new ImmutableSortedMapDeserializer(_mapType, keyDeser, typeDeser, valueDeser);
    }
}
