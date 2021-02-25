package com.fasterxml.jackson.datatype.pcollections.deser;

import com.fasterxml.jackson.databind.ValueDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.pcollections.TreePVector;

public class TreePVectorDeserializer extends
        PCollectionsCollectionDeserializer<TreePVector<Object>>
{
    public TreePVectorDeserializer(CollectionType type,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public TreePVectorDeserializer withResolved(TypeDeserializer typeDeser,
            ValueDeserializer<?> valueDeser) {
        return new TreePVectorDeserializer(_containerType, typeDeser,
                valueDeser);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    protected TreePVector<Object> createEmptyCollection() {
        return TreePVector.empty();
    }
}
