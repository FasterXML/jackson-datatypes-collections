package tools.jackson.datatype.fastutil;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.Deserializers;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;
import tools.jackson.datatype.fastutil.deser.PrimitiveCollectionDeserializers;
import tools.jackson.datatype.fastutil.deser.map.FastutilMapDeserializers;

/**
 * Deserializers for fastutil collections and maps that contain primitive values.
 * Abstract containers of references (like {@code ObjectList}) are instead mapped
 * to default implementations by {@link FastutilAbstractTypeResolver}.
 */
public class FastutilDeserializers extends Deserializers.Base
{
    @Override
    public ValueDeserializer<?> findCollectionDeserializer(CollectionType type,
            DeserializationConfig config, BeanDescription.Supplier beanDescRef,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer)
    {
        return PrimitiveCollectionDeserializers.findDeserializer(type);
    }

    @Override
    public ValueDeserializer<?> findMapDeserializer(MapType type,
            DeserializationConfig config, BeanDescription.Supplier beanDescRef,
            KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer)
    {
        return FastutilMapDeserializers.findDeserializer(type, keyDeserializer,
                elementTypeDeserializer, elementDeserializer);
    }

    @Override
    public boolean hasDeserializerFor(DeserializationConfig config, Class<?> valueType) {
        return PrimitiveCollectionDeserializers.handles(valueType)
                || FastutilMapDeserializers.handles(valueType);
    }
}
