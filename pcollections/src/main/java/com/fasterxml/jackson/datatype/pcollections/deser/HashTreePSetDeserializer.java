package com.fasterxml.jackson.datatype.pcollections.deser;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.pcollections.HashTreePSet;
import org.pcollections.MapPSet;

public class HashTreePSetDeserializer extends
        PCollectionsCollectionDeserializer<MapPSet<Object>>
{
    private static final long serialVersionUID = 1L;

    public HashTreePSetDeserializer(CollectionType type,
            TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public HashTreePSetDeserializer withResolved(TypeDeserializer typeDeser,
            JsonDeserializer<?> valueDeser) {
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
