package com.fasterxml.jackson.datatype.pcollections.deser;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.pcollections.OrderedPSet;

public class OrderedPSetDeserializer extends
        PCollectionsCollectionDeserializer<OrderedPSet<Object>>
{
    private static final long serialVersionUID = 1L;

    public OrderedPSetDeserializer(CollectionType type,
            TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public OrderedPSetDeserializer withResolved(TypeDeserializer typeDeser,
            JsonDeserializer<?> valueDeser) {
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
