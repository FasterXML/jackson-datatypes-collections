package com.fasterxml.jackson.datatype.pcollections.deser;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.pcollections.HashTreePBag;
import org.pcollections.MapPBag;

public class HashTreePBagDeserializer extends
        PCollectionsCollectionDeserializer<MapPBag<Object>>
{
    public HashTreePBagDeserializer(CollectionType type,
            TypeDeserializer typeDeser, JsonDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public HashTreePBagDeserializer withResolved(TypeDeserializer typeDeser,
            JsonDeserializer<?> valueDeser) {
        return new HashTreePBagDeserializer(_containerType, typeDeser,
                valueDeser);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    protected MapPBag<Object> createEmptyCollection() {
        return HashTreePBag.empty();
    }
}
