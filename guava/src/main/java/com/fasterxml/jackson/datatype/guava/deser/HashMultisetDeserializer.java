package com.fasterxml.jackson.datatype.guava.deser;


import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import com.google.common.collect.HashMultiset;

public class HashMultisetDeserializer
    extends GuavaMultisetDeserializer<HashMultiset<Object>>
{
    private static final long serialVersionUID = 1L;

    public HashMultisetDeserializer(JavaType selfType,
            JsonDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    public HashMultisetDeserializer withResolved(JsonDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new HashMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle);
    }

    @Override
    protected HashMultiset<Object> createMultiset() {
        return HashMultiset.<Object> create();
    }
}
