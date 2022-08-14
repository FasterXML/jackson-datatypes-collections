package com.fasterxml.jackson.datatype.guava.deser;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableCollection.Builder;
import com.google.common.collect.ImmutableSortedSet;

public class ImmutableSortedSetDeserializer
    extends GuavaImmutableCollectionDeserializer<ImmutableSortedSet<Object>>
{
    public ImmutableSortedSetDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    public ImmutableSortedSetDeserializer withResolved(ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new ImmutableSortedSetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Builder<Object> createBuilder() {
        /* Not quite sure what to do with sorting/ordering; may require better support either
         * via annotations, or via custom serialization (bean style that includes ordering
         * aspects)
         */
        @SuppressWarnings("rawtypes")
        ImmutableSortedSet.Builder<?> builderComp = ImmutableSortedSet.<Comparable> naturalOrder();
        ImmutableSortedSet.Builder<Object> builder = (ImmutableSortedSet.Builder<Object>) builderComp;
        return builder;
    }

    @Override
    protected ImmutableSortedSet<Object> _createEmpty(DeserializationContext ctxt) {
        return ImmutableSortedSet.of();
    }

    @Override
    protected ImmutableSortedSet<Object> _createWithSingleElement(DeserializationContext ctxt, Object value) {
        return (ImmutableSortedSet<Object>) createBuilder()
                .add(value)
                .build();
    }
}
