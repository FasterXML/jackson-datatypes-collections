package com.fasterxml.jackson.datatype.pcollections.deser;

import com.fasterxml.jackson.databind.ValueDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.pcollections.HashTreePSet;
import org.pcollections.MapPSet;

public class HashTreePSetDeserializer extends
        PCollectionsCollectionDeserializer<MapPSet<Object>>
{
    public HashTreePSetDeserializer(CollectionType type,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public HashTreePSetDeserializer withResolved(TypeDeserializer typeDeser,
            ValueDeserializer<?> valueDeser) {
        return new HashTreePSetDeserializer(_containerType, typeDeser,
                valueDeser);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    protected MapPSet<Object> createEmptyCollection() {
        return HashTreePSet.empty();
    }
}
