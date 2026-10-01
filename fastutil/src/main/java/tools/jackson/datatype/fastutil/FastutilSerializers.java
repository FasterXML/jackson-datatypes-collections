package tools.jackson.datatype.fastutil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
import tools.jackson.datatype.fastutil.util.OptionalTypes;
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
    // Maps with a reference key or value; types missing from `fastutil-core` (a subset
    // of `fastutil`, without maps that have `byte`, `short`, `char` or `float` keys or
    // values) are skipped
    private static final List<MapEntry> MAP_ENTRIES = new ArrayList<>();

    static {
        // Primitive keys, reference values
        _register(() -> new MapEntry(Byte2ObjectMap.class,
                (t, ks, vts, vs) -> new PrimitiveRefMapSerializers.Byte<>(t, null, vts, vs)));
        _register(() -> new MapEntry(Short2ObjectMap.class,
                (t, ks, vts, vs) -> new PrimitiveRefMapSerializers.Short<>(t, null, vts, vs)));
        _register(() -> new MapEntry(Char2ObjectMap.class,
                (t, ks, vts, vs) -> new PrimitiveRefMapSerializers.Char<>(t, null, vts, vs)));
        _register(() -> new MapEntry(Int2ObjectMap.class,
                (t, ks, vts, vs) -> new PrimitiveRefMapSerializers.Int<>(t, null, vts, vs)));
        _register(() -> new MapEntry(Long2ObjectMap.class,
                (t, ks, vts, vs) -> new PrimitiveRefMapSerializers.Long<>(t, null, vts, vs)));
        _register(() -> new MapEntry(Float2ObjectMap.class,
                (t, ks, vts, vs) -> new PrimitiveRefMapSerializers.Float<>(t, null, vts, vs)));
        _register(() -> new MapEntry(Double2ObjectMap.class,
                (t, ks, vts, vs) -> new PrimitiveRefMapSerializers.Double<>(t, null, vts, vs)));

        // Reference keys, primitive values
        _register(() -> new MapEntry(Object2BooleanMap.class,
                (t, ks, vts, vs) -> new RefPrimitiveMapSerializers.Boolean<>(t, null, ks)));
        _register(() -> new MapEntry(Object2ByteMap.class,
                (t, ks, vts, vs) -> new RefPrimitiveMapSerializers.Byte<>(t, null, ks)));
        _register(() -> new MapEntry(Object2ShortMap.class,
                (t, ks, vts, vs) -> new RefPrimitiveMapSerializers.Short<>(t, null, ks)));
        _register(() -> new MapEntry(Object2CharMap.class,
                (t, ks, vts, vs) -> new RefPrimitiveMapSerializers.Char<>(t, null, ks)));
        _register(() -> new MapEntry(Object2IntMap.class,
                (t, ks, vts, vs) -> new RefPrimitiveMapSerializers.Int<>(t, null, ks)));
        _register(() -> new MapEntry(Object2LongMap.class,
                (t, ks, vts, vs) -> new RefPrimitiveMapSerializers.Long<>(t, null, ks)));
        _register(() -> new MapEntry(Object2FloatMap.class,
                (t, ks, vts, vs) -> new RefPrimitiveMapSerializers.Float<>(t, null, ks)));
        _register(() -> new MapEntry(Object2DoubleMap.class,
                (t, ks, vts, vs) -> new RefPrimitiveMapSerializers.Double<>(t, null, ks)));
    }

    private static void _register(Supplier<MapEntry> entry) {
        OptionalTypes.registerIfPresent(() -> MAP_ENTRIES.add(entry.get()));
    }

    @FunctionalInterface
    private interface MapSerializerFactory {
        ValueSerializer<?> create(MapType type, ValueSerializer<Object> keySerializer,
                TypeSerializer valueTypeSerializer, ValueSerializer<Object> valueSerializer);
    }

    private record MapEntry(Class<?> mapType, MapSerializerFactory factory) { }

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

        for (MapEntry entry : MAP_ENTRIES) {
            if (entry.mapType().isAssignableFrom(raw)) {
                return entry.factory().create(type, keySerializer,
                        elementTypeSerializer, elementValueSerializer);
            }
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
