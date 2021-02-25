package com.fasterxml.jackson.datatype.guava.deser.multimap.set;

import java.lang.reflect.Method;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ValueDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.multimap.GuavaMultimapDeserializer;

import com.google.common.collect.HashMultimap;

/**
 * Provides deserialization for the Guava HashMultimap class.
 *
 * @author mvolkhart
 */
public class HashMultimapDeserializer
    extends GuavaMultimapDeserializer<HashMultimap<Object, Object>>
{
    public HashMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
    }

    public HashMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer,
            Method creatorMethod, NullValueProvider nvp) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, creatorMethod, nvp);
    }

    @Override
    protected HashMultimap<Object, Object> createMultimap() {
        return HashMultimap.create();
    }

    @Override
    protected ValueDeserializer<?> _createContextual(JavaType type,
            KeyDeserializer keyDeserializer, TypeDeserializer typeDeserializer,
            ValueDeserializer<?> elementDeserializer, Method method, NullValueProvider nvp) {
        return new HashMultimapDeserializer(type, keyDeserializer, typeDeserializer,
                elementDeserializer, method, nvp);
    }
}
