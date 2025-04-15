package tools.jackson.datatype.eclipsecollections;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.SerializationConfig;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.Serializers;
import tools.jackson.databind.ser.jdk.CollectionSerializer;
import tools.jackson.databind.type.CollectionLikeType;
import tools.jackson.datatype.eclipsecollections.ser.BooleanIterableSerializer;
import tools.jackson.datatype.eclipsecollections.ser.ByteIterableSerializer;
import tools.jackson.datatype.eclipsecollections.ser.CharIterableSerializer;
import tools.jackson.datatype.eclipsecollections.ser.DoubleIterableSerializer;
import tools.jackson.datatype.eclipsecollections.ser.FloatIterableSerializer;
import tools.jackson.datatype.eclipsecollections.ser.IntIterableSerializer;
import tools.jackson.datatype.eclipsecollections.ser.LongIterableSerializer;
import tools.jackson.datatype.eclipsecollections.ser.ShortIterableSerializer;
import tools.jackson.datatype.eclipsecollections.ser.map.PrimitivePrimitiveMapSerializers;
import tools.jackson.datatype.eclipsecollections.ser.map.PrimitiveRefMapSerializers;
import tools.jackson.datatype.eclipsecollections.ser.map.RefPrimitiveMapSerializers;
import tools.jackson.datatype.eclipsecollections.ser.map.RefRefMapIterableSerializer;
import tools.jackson.datatype.primitive_collections_base.ser.map.PrimitiveMapSerializer;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.primitive.ByteObjectMap;
import org.eclipse.collections.api.map.primitive.CharObjectMap;
import org.eclipse.collections.api.map.primitive.DoubleObjectMap;
import org.eclipse.collections.api.map.primitive.FloatObjectMap;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.LongObjectMap;
import org.eclipse.collections.api.map.primitive.ObjectBooleanMap;
import org.eclipse.collections.api.map.primitive.ObjectByteMap;
import org.eclipse.collections.api.map.primitive.ObjectCharMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectFloatMap;
import org.eclipse.collections.api.map.primitive.ObjectIntMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.map.primitive.ObjectShortMap;
import org.eclipse.collections.api.map.primitive.PrimitiveObjectMap;
import org.eclipse.collections.api.map.primitive.ShortObjectMap;

public final class EclipseCollectionsSerializers extends Serializers.Base {
    @Override
    public ValueSerializer<?> findCollectionLikeSerializer(
            SerializationConfig config,
            CollectionLikeType type,
            BeanDescription.Supplier beanDescRef,
            JsonFormat.Value formatOverrides,
            TypeSerializer elementTypeSerializer,
            ValueSerializer<Object> elementValueSerializer
    ) {
        if (ImmutableCollection.class.isAssignableFrom(type.getRawClass())) {
            return new CollectionSerializer(
                    type.getContentType(),
                    false,
                    elementTypeSerializer,
                    elementValueSerializer
            );
        }
        return null;
    }

    @Override
    public ValueSerializer<?> findSerializer(SerializationConfig config,
            JavaType type, BeanDescription.Supplier beanDescRef, JsonFormat.Value formatOverrides)
    {
        Class<?> rawClass = type.getRawClass();

        if (MapIterable.class.isAssignableFrom(rawClass)) {
            return new RefRefMapIterableSerializer(type, null, null, null, null);
        }

        if (PrimitiveObjectMap.class.isAssignableFrom(rawClass)) {
            if (ByteObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializers.Byte<>(type, null, null, null);
            } else if (ShortObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializers.Short<>(type, null, null, null);
            } else if (CharObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializers.Char<>(type, null, null, null);
            } else if (IntObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializers.Int<>(type, null, null, null);
            } else if (FloatObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializers.Float<>(type, null, null, null);
            } else if (LongObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializers.Long<>(type, null, null, null);
            } else if (DoubleObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializers.Double<>(type, null, null, null);
            }
        }

        if (PrimitiveIterable.class.isAssignableFrom(rawClass)) {
            if (ObjectBooleanMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializers.Boolean<>(type, null, null);
            } else if (ObjectByteMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializers.Byte<>(type, null, null);
            } else if (ObjectShortMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializers.Short<>(type, null, null);
            } else if (ObjectCharMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializers.Char<>(type, null, null);
            } else if (ObjectIntMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializers.Int<>(type, null, null);
            } else if (ObjectFloatMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializers.Float<>(type, null, null);
            } else if (ObjectLongMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializers.Long<>(type, null, null);
            } else if (ObjectDoubleMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializers.Double<>(type, null, null);
            }

            for (Map.Entry<Class<? extends PrimitiveIterable>, PrimitiveMapSerializer<?>> entry :
                    PrimitivePrimitiveMapSerializers.getInstances().entrySet()) {
                if (entry.getKey().isAssignableFrom(rawClass)) {
                    return entry.getValue();
                }
            }

            if (BooleanIterable.class.isAssignableFrom(rawClass)) {
                return new BooleanIterableSerializer(null, null);
            } else if (ByteIterable.class.isAssignableFrom(rawClass)) {
                return ByteIterableSerializer.INSTANCE;
            } else if (ShortIterable.class.isAssignableFrom(rawClass)) {
                return new ShortIterableSerializer(null, null);
            } else if (CharIterable.class.isAssignableFrom(rawClass)) {
                return CharIterableSerializer.INSTANCE;
            } else if (IntIterable.class.isAssignableFrom(rawClass)) {
                return new IntIterableSerializer(null, null);
            } else if (FloatIterable.class.isAssignableFrom(rawClass)) {
                return new FloatIterableSerializer(null, null);
            } else if (LongIterable.class.isAssignableFrom(rawClass)) {
                return new LongIterableSerializer(null, null);
            } else if (DoubleIterable.class.isAssignableFrom(rawClass)) {
                return new DoubleIterableSerializer(null, null);
            }
        }
        return super.findSerializer(config, type, beanDescRef, formatOverrides);
    }
}
