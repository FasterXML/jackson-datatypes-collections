package com.fasterxml.jackson.datatype.guava.deser.multimap.list;

import java.lang.reflect.Method;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.multimap.GuavaMultimapDeserializer;

import com.google.common.collect.LinkedListMultimap;

/**
 * Provides deserialization for the Guava LinkedListMultimap class.
 *
 * @author mvolkhart
 */
public class LinkedListMultimapDeserializer
    extends GuavaMultimapDeserializer<LinkedListMultimap<Object,Object>>
{
    public LinkedListMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
    }

    public LinkedListMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer,
            Method creatorMethod, NullValueProvider nvp) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, creatorMethod, nvp);
    }

    @Override
    protected LinkedListMultimap<Object, Object> createMultimap() {
        return LinkedListMultimap.create();
    }

    @Override
    protected JsonDeserializer<?> _createContextual(JavaType type,
            KeyDeserializer keyDeserializer, TypeDeserializer typeDeserializer,
            JsonDeserializer<?> elementDeserializer, Method method, NullValueProvider nvp) {
        return new LinkedListMultimapDeserializer(type, keyDeserializer, typeDeserializer,
                elementDeserializer, method, nvp);
    }
}
