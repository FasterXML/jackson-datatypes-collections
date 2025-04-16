package tools.jackson.datatype.hppc.deser;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.Deserializers;

public class HppcDeserializers extends Deserializers.Base
{
    @Override
    public ValueDeserializer<?> findBeanDeserializer(JavaType type,
            DeserializationConfig config, BeanDescription.Supplier beanDescRef)
    {
        return HppcContainerDeserializers.findDeserializer(config, type);
    }

    /*
    @Override
    public JsonDeserializer<?> findCollectionLikeDeserializer(
            CollectionLikeType type, DeserializationConfig config,
            BeanDescription.Supplier beanDescRef arg3,
            BeanProperty arg4, TypeDeserializer arg5,
            JsonDeserializer<?> arg6) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JsonDeserializer<?> findMapLikeDeserializer(MapLikeType type,
            DeserializationConfig config,
            BeanDescription.Supplier beanDescRef arg3, BeanProperty arg4, KeyDeserializer arg5,
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
