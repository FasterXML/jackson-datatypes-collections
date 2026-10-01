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
import tools.jackson.datatype.fastutil.ser.map.PrimitiveRefMapSerializers;
import tools.jackson.datatype.fastutil.ser.map.RefPrimitiveMapSerializers;
import tools.jackson.datatype.primitive_collections_base.ser.map.PrimitiveMapSerializer;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2CharMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;

/**
 * Serializers for fastutil collections and maps that contain primitive values.
 * Since all fastutil containers implement the matching {@code java.util} interfaces,
 * containers of references (like {@code ObjectList}) are left to the standard
 * Jackson serializers.
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
        final Class<?> raw = type.getRawClass();
        // Property-level `@JsonTypeInfo` for values is only attached to the content type
        if (elementTypeSerializer == null) {
            elementTypeSerializer = (TypeSerializer) type.getContentType().getTypeHandler();
        }

        // Primitive keys, reference values
        if (Byte2ObjectMap.class.isAssignableFrom(raw)) {
            return new PrimitiveRefMapSerializers.Byte<>(type, null, elementTypeSerializer, elementValueSerializer);
        }
        if (Short2ObjectMap.class.isAssignableFrom(raw)) {
            return new PrimitiveRefMapSerializers.Short<>(type, null, elementTypeSerializer, elementValueSerializer);
        }
        if (Char2ObjectMap.class.isAssignableFrom(raw)) {
            return new PrimitiveRefMapSerializers.Char<>(type, null, elementTypeSerializer, elementValueSerializer);
        }
        if (Int2ObjectMap.class.isAssignableFrom(raw)) {
            return new PrimitiveRefMapSerializers.Int<>(type, null, elementTypeSerializer, elementValueSerializer);
        }
        if (Long2ObjectMap.class.isAssignableFrom(raw)) {
            return new PrimitiveRefMapSerializers.Long<>(type, null, elementTypeSerializer, elementValueSerializer);
        }
        if (Float2ObjectMap.class.isAssignableFrom(raw)) {
            return new PrimitiveRefMapSerializers.Float<>(type, null, elementTypeSerializer, elementValueSerializer);
        }
        if (Double2ObjectMap.class.isAssignableFrom(raw)) {
            return new PrimitiveRefMapSerializers.Double<>(type, null, elementTypeSerializer, elementValueSerializer);
        }

        // Reference keys, primitive values
        if (Object2BooleanMap.class.isAssignableFrom(raw)) {
            return new RefPrimitiveMapSerializers.Boolean<>(type, null, keySerializer);
        }
        if (Object2ByteMap.class.isAssignableFrom(raw)) {
            return new RefPrimitiveMapSerializers.Byte<>(type, null, keySerializer);
        }
        if (Object2ShortMap.class.isAssignableFrom(raw)) {
            return new RefPrimitiveMapSerializers.Short<>(type, null, keySerializer);
        }
        if (Object2CharMap.class.isAssignableFrom(raw)) {
            return new RefPrimitiveMapSerializers.Char<>(type, null, keySerializer);
        }
        if (Object2IntMap.class.isAssignableFrom(raw)) {
            return new RefPrimitiveMapSerializers.Int<>(type, null, keySerializer);
        }
        if (Object2LongMap.class.isAssignableFrom(raw)) {
            return new RefPrimitiveMapSerializers.Long<>(type, null, keySerializer);
        }
        if (Object2FloatMap.class.isAssignableFrom(raw)) {
            return new RefPrimitiveMapSerializers.Float<>(type, null, keySerializer);
        }
        if (Object2DoubleMap.class.isAssignableFrom(raw)) {
            return new RefPrimitiveMapSerializers.Double<>(type, null, keySerializer);
        }

        // Primitive keys, primitive values
        for (Map.Entry<Class<?>, PrimitiveMapSerializer<?>> entry
                : PrimitivePrimitiveMapSerializers.getInstances().entrySet()) {
            if (entry.getKey().isAssignableFrom(raw)) {
                return entry.getValue();
            }
        }
        return null;
    }
}
