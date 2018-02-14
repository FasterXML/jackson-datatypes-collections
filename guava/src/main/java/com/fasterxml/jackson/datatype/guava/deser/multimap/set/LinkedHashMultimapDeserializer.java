package com.fasterxml.jackson.datatype.guava.deser.multimap.set;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.datatype.guava.deser.multimap.GuavaMultimapDeserializer;
import com.google.common.collect.LinkedHashMultimap;

import java.lang.reflect.Method;

/**
 * Provides deserialization for the Guava LinkedHashMultimap class.
 *
 * @author mvolkhart
 */
public class LinkedHashMultimapDeserializer
    extends GuavaMultimapDeserializer<LinkedHashMultimap<Object, Object>>
{
    private static final long serialVersionUID = 1L;

    public LinkedHashMultimapDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer);
    }

    public LinkedHashMultimapDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer,
            Method creatorMethod, NullValueProvider nvp) {
        super(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, creatorMethod, nvp);
    }

    @Override
    protected LinkedHashMultimap<Object, Object> createMultimap() {
        return LinkedHashMultimap.create();
    }

    @Override
    protected JsonDeserializer<?> _createContextual(MapLikeType type,
            KeyDeserializer keyDeserializer, TypeDeserializer typeDeserializer,
            JsonDeserializer<?> elementDeserializer, Method method, NullValueProvider nvp)
    {
        return new LinkedHashMultimapDeserializer(type, keyDeserializer, typeDeserializer,
                elementDeserializer, method, nvp);
    }
}
