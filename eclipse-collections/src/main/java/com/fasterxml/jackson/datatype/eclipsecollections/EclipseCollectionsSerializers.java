package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.BooleanIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.ByteIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.CharIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.DoubleIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.FloatIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.IntIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.LongIterableSerializer;
import com.fasterxml.jackson.datatype.eclipsecollections.ser.ShortIterableSerializer;
import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.api.ShortIterable;

public final class EclipseCollectionsSerializers extends Serializers.Base {

    @Override
    public JsonSerializer<?> findSerializer(
            SerializationConfig config, JavaType type, BeanDescription beanDesc
    ) {
        Class<?> rawClass = type.getRawClass();
        if (PrimitiveIterable.class.isAssignableFrom(rawClass)) {
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
