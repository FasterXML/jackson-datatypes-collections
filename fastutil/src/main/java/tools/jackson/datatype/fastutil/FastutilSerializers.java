package tools.jackson.datatype.fastutil;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.Serializers;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;
import tools.jackson.datatype.fastutil.ser.PrimitiveCollectionSerializers;
import tools.jackson.datatype.fastutil.ser.map.PrimitivePrimitiveMapSerializers;
import tools.jackson.datatype.primitive_collections_base.ser.map.PrimitiveMapSerializer;

/**
 * Serializers for fastutil collections and maps that contain primitive values.
 * Since all fastutil containers implement the matching {@code java.util} interfaces,
 * containers of references (like {@code ObjectList}), as well as maps with
 * reference keys or values (like {@code Int2ObjectMap} or {@code Object2IntMap}),
 * are left to the standard Jackson serializers: this way all their features
 * (like content inclusion and custom key and content serializers) are supported.
 */
public class FastutilSerializers extends Serializers.Base
{
    @Override
    public ValueSerializer<?> findCollectionSerializer(SerializationConfig config,
            CollectionType type, BeanDescription.Supplier beanDescRef,
            JsonFormat.Value formatOverrides,
            TypeSerializer elementTypeSerializer, ValueSerializer<Object> elementValueSerializer)
    {
        return PrimitiveCollectionSerializers.findSerializer(type.getRawClass());
    }

    @Override
    public ValueSerializer<?> findMapSerializer(SerializationConfig config,
            MapType type, BeanDescription.Supplier beanDescRef,
            JsonFormat.Value formatOverrides,
            ValueSerializer<Object> keySerializer,
            TypeSerializer elementTypeSerializer, ValueSerializer<Object> elementValueSerializer)
    {
        // Primitive keys, primitive values
        final Class<?> raw = type.getRawClass();
        for (Map.Entry<Class<?>, PrimitiveMapSerializer<?>> entry
                : PrimitivePrimitiveMapSerializers.getInstances().entrySet()) {
            if (entry.getKey().isAssignableFrom(raw)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
