package com.fasterxml.jackson.datatype.pcollections.deser;

import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionType;
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
