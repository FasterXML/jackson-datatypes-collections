package com.fasterxml.jackson.datatype.pcollections.deser;

import com.fasterxml.jackson.databind.ValueDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.pcollections.ConsPStack;

public class ConsPStackDeserializer extends
        PCollectionsCollectionDeserializer<ConsPStack<Object>>
{
    public ConsPStackDeserializer(CollectionType type,
            TypeDeserializer typeDeser, ValueDeserializer<?> deser) {
        super(type, typeDeser, deser);
    }

    @Override
    public ConsPStackDeserializer withResolved(TypeDeserializer typeDeser,
            ValueDeserializer<?> valueDeser) {
        return new ConsPStackDeserializer(_containerType, typeDeser,
                valueDeser);
    }

    /*
    /**********************************************************
    /* Deserialization
    /**********************************************************
     */

    @Override
    protected ConsPStack<Object> createEmptyCollection() {
        return ConsPStack.empty();
    }
}
