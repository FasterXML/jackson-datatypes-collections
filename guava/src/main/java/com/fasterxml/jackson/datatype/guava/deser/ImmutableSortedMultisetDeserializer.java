package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.common.collect.ImmutableCollection.Builder;
import com.google.common.collect.ImmutableSortedMultiset;

public class ImmutableSortedMultisetDeserializer extends GuavaImmutableCollectionDeserializer<ImmutableSortedMultiset<Object>>
{
    private static final long serialVersionUID = 1L;

    public ImmutableSortedMultisetDeserializer(CollectionType type, TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    protected Builder<Object> createBuilder() {
        /* This is suboptimal. See the considerations in ImmutableSortedSetDeserializer. */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        Builder<Object> builder =  (Builder) ImmutableSortedMultiset.naturalOrder();
        return builder;
    }

    @Override
    public GuavaCollectionDeserializer<ImmutableSortedMultiset<Object>> withResolved(TypeDeserializer typeDeser,
            JsonDeserializer<?> valueDeser) {
        return new ImmutableSortedMultisetDeserializer(_containerType, typeDeser, valueDeser);
    }
}
