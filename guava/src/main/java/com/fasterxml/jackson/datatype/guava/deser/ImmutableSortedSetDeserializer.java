package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.ImmutableCollection.Builder;
import com.google.common.collect.ImmutableSortedSet;

public class ImmutableSortedSetDeserializer
    extends GuavaImmutableCollectionDeserializer<ImmutableSortedSet<Object>>
{
    private static final long serialVersionUID = 1L;

    public ImmutableSortedSetDeserializer(JavaType selfType,
            JsonDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    public ImmutableSortedSetDeserializer withResolved(JsonDeserializer<?> valueDeser, TypeDeserializer typeDeser,
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
    protected ImmutableSortedSet<Object> _createWithSingleElement(DeserializationContext ctxt, Object value) throws IOException {
        return (ImmutableSortedSet<Object>) createBuilder()
                .add(value)
                .build();
    }
}
