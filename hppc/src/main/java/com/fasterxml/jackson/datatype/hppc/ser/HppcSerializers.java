package com.fasterxml.jackson.datatype.hppc.ser;

import com.carrotsearch.hppc.ObjectContainer;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.ObjectArraySerializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;

public class HppcSerializers extends Serializers.Base
{
    public HppcSerializers() { }
    
    @Override
    public JsonSerializer<?> findCollectionLikeSerializer(SerializationConfig config,
            CollectionLikeType containerType,
            BeanDescription beanDesc, TypeSerializer elementTypeSerializer,
            JsonSerializer<Object> elementValueSerializer)
    {
        if (ObjectContainer.class.isAssignableFrom(containerType.getRawClass())) {
            // hmmh. not sure if we can find 'forceStaticTyping' anywhere...
            boolean staticTyping = config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            ObjectArraySerializer ser = new ObjectArraySerializer(containerType.getContentType(),
                    staticTyping, elementTypeSerializer, elementValueSerializer);
            return new ObjectContainerSerializer(containerType, ser);
        }
        return null;
    }

    @Override
    public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config,
            MapLikeType arg1, BeanDescription arg2,
            JsonSerializer<Object> arg4, TypeSerializer arg5,
            JsonSerializer<Object> arg6) {
        // TODO: handle XxxMap with Object keys and/or values
        return null;
    }

    /**
     * Anything that we don't explicitly mark as Map- or Collection-like
     * will end up here...
     */
    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config,
            JavaType type, BeanDescription beanDesc)
    {
        return HppcContainerSerializers.getMatchingSerializer(config, type);
    }
    
}