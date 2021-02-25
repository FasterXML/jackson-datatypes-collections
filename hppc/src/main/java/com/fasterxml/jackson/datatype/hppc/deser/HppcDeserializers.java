package com.fasterxml.jackson.datatype.hppc.deser;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ValueDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;

public class HppcDeserializers extends Deserializers.Base
{
    @Override
    public ValueDeserializer<?> findBeanDeserializer(JavaType type,
            DeserializationConfig config, BeanDescription beanDesc)
    {
        return HppcContainerDeserializers.findDeserializer(config, type);
    }

    /*
    @Override
    public JsonDeserializer<?> findCollectionLikeDeserializer(
            CollectionLikeType type, DeserializationConfig config,
             BeanDescription arg3,
            BeanProperty arg4, TypeDeserializer arg5,
            JsonDeserializer<?> arg6) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type,
            DeserializationConfig config,
            BeanDescription arg3, BeanProperty arg4, KeyDeserializer arg5,
            TypeDeserializer arg6, JsonDeserializer<?> arg7) {
        // TODO Auto-generated method stub
        return null;
    }
*/        

    @Override
    public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
        return HppcContainerDeserializers.hasDeserializerFor(config, valueType);
    }
}
