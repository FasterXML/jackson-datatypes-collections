package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.ImmutableCollection.Builder;
import com.google.common.collect.ImmutableSortedSet;

public class ImmutableSortedSetDeserializer extends GuavaImmutableCollectionDeserializer<ImmutableSortedSet<Object>>
{
    private static final long serialVersionUID = 1L;

    public ImmutableSortedSetDeserializer(CollectionType type,
            TypeDeserializer typeDeser, JsonDeserializer<?> deser)
    {
        super(type, typeDeser, deser);
    }

    @Override
    public ImmutableSortedSetDeserializer withResolved(TypeDeserializer typeDeser,
            JsonDeserializer<?> valueDeser) {
        return new ImmutableSortedSetDeserializer(_containerType,
                typeDeser, valueDeser);
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
}
