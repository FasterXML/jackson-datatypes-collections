package com.fasterxml.jackson.datatype.pcollections.deser;

import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionType;
import org.pcollections.OrderedPSet;

public class OrderedPSetDeserializer extends
        PCollectionsCollectionDeserializer<OrderedPSet<Object>>
{
    public OrderedPSetDeserializer(CollectionType type,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public OrderedPSetDeserializer withResolved(TypeDeserializer typeDeser,
            ValueDeserializer<?> valueDeser) {
        return new OrderedPSetDeserializer(_containerType, typeDeser,
                valueDeser);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    protected OrderedPSet<Object> createEmptyCollection() {
        return OrderedPSet.empty();
    }
}
