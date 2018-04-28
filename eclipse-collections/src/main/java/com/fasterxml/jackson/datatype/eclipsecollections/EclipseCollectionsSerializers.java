package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.BooleanIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.ByteIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.CharIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.DoubleIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.FloatIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.IntIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.LongIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.ShortIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.map.PrimitiveMapSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.map.PrimitivePrimitiveMapSerializers;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.map.PrimitiveRefMapSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.map.RefPrimitiveMapSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.map.RefRefMapSerializer;
import java.util.Map;
import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.api.ShortIterable;
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
    public JsonSerializer<?> findSerializer(
            SerializationConfig config, JavaType type, BeanDescription beanDesc
    ) {
        Class<?> rawClass = type.getRawClass();

        if (MapIterable.class.isAssignableFrom(rawClass)) {
            return new RefRefMapSerializer(type, null, null, null, null);
        }

        if (PrimitiveObjectMap.class.isAssignableFrom(rawClass)) {
            if (ByteObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializer.Byte<>(type, null, null, null);
            } else if (ShortObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializer.Short<>(type, null, null, null);
            } else if (CharObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializer.Char<>(type, null, null, null);
            } else if (IntObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializer.Int<>(type, null, null, null);
            } else if (FloatObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializer.Float<>(type, null, null, null);
            } else if (LongObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializer.Long<>(type, null, null, null);
            } else if (DoubleObjectMap.class.isAssignableFrom(rawClass)) {
                return new PrimitiveRefMapSerializer.Double<>(type, null, null, null);
            }
        }

        if (PrimitiveIterable.class.isAssignableFrom(rawClass)) {
            if (ObjectBooleanMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializer.Boolean<>(type, null, null);
            } else if (ObjectByteMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializer.Byte<>(type, null, null);
            } else if (ObjectShortMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializer.Short<>(type, null, null);
            } else if (ObjectCharMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializer.Char<>(type, null, null);
            } else if (ObjectIntMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializer.Int<>(type, null, null);
            } else if (ObjectFloatMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializer.Float<>(type, null, null);
            } else if (ObjectLongMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializer.Long<>(type, null, null);
            } else if (ObjectDoubleMap.class.isAssignableFrom(rawClass)) {
                return new RefPrimitiveMapSerializer.Double<>(type, null, null);
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
        return super.findSerializer(config, type, beanDesc);
    }
}
