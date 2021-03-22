package com.fasterxml.jackson.datatype.guava.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.BaseImmutableArrayDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.ImmutableDoubleArrayDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.ImmutableIntArrayDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.ImmutableLongArrayDeserializer;
import com.fasterxml.jackson.datatype.guava.ser.primitives.BaseImmutableArraySerializer;
import com.fasterxml.jackson.datatype.guava.ser.primitives.ImmutableDoubleArraySerializer;
import com.fasterxml.jackson.datatype.guava.ser.primitives.ImmutableIntArraySerializer;
import com.fasterxml.jackson.datatype.guava.ser.primitives.ImmutableLongArraySerializer;
import com.google.common.base.Optional;
import com.google.common.primitives.ImmutableDoubleArray;
import com.google.common.primitives.ImmutableIntArray;
import com.google.common.primitives.ImmutableLongArray;

import java.io.Serializable;
import java.util.function.Supplier;

import static com.fasterxml.jackson.datatype.guava.util.PrimitiveTypes.typeRefOf;

/**
 * Utility class to cover all {@code Immutable[Primitive]Array} primitive types
 *
 * @author robert@albertlr.ro
 */
public class ImmutablePrimitiveTypes {
    /**
     * An enum with all the primitives
     */
    public enum ImmutablePrimitiveArrays {
        INT(ImmutableIntArrayType, int.class, Integer.class,
                ImmutableIntArraySerializer::new,
                ImmutableIntArrayDeserializer::new
        ),
        DOUBLE(ImmutableDoubleArrayType, double.class, Double.class,
                ImmutableDoubleArraySerializer::new,
                ImmutableDoubleArrayDeserializer::new
        ),
        LONG(ImmutableLongArrayType, long.class, Long.class,
                ImmutableLongArraySerializer::new,
                ImmutableLongArrayDeserializer::new
        );

        private final Class<?> type;
        private final Class<?> primitiveType;
        private final Class<?> objectType;
        private final Supplier<? extends BaseImmutableArraySerializer> serializerFactory;
        private final Supplier<? extends BaseImmutableArrayDeserializer> deserializerFactory;

        private ImmutablePrimitiveArrays(Class<?> type, Class<?> primitiveType, Class<?> objectType,
                                         Supplier<? extends BaseImmutableArraySerializer> serializerFactory,
                                         Supplier<? extends BaseImmutableArrayDeserializer> deserializerFactory) {
            this.type = type;
            this.primitiveType = primitiveType;
            this.objectType = objectType;
            this.deserializerFactory = deserializerFactory;
            this.serializerFactory = serializerFactory;
        }

        public Class<?> type() {
            return type;
        }

        public Class<?> primitiveType() {
            return primitiveType;
        }

        public <T> Class<T> objectType() {
            return (Class<T>) objectType;
        }

        public <T extends Serializable> BaseImmutableArraySerializer<T> newSerializer() {
            return serializerFactory.get();
        }

        public <T> StdDeserializer<T> newDeserializer() {
            return deserializerFactory.get();
        }

    }

    public static Optional<ImmutablePrimitiveTypes.ImmutablePrimitiveArrays> isAssignableFromImmutableArray(Class<?> immutableArrayType) {
        for (ImmutablePrimitiveArrays primitive : ImmutablePrimitiveArrays.values()) {
            if (primitive.type().isAssignableFrom(immutableArrayType)) {
                return Optional.of(primitive);
            }
        }
        return Optional.absent();
    }


    /**
     * Type of array returned by {@link ImmutableIntArray}
     */
    public static final Class<? extends ImmutableIntArray> ImmutableIntArrayType = ImmutableIntArray.class;
    /**
     * Type of array returned by {@link ImmutableLongArray}
     */
    public static final Class<? extends ImmutableLongArray> ImmutableLongArrayType = ImmutableLongArray.class;
    /**
     * Type of array returned by {@link ImmutableDoubleArray}
     */
    public static final Class<? extends ImmutableDoubleArray> ImmutableDoubleArrayType = ImmutableDoubleArray.class;

    public static final TypeReference<ImmutableIntArray> ImmutableIntArrayReference = typeRefOf(ImmutableIntArrayType);
    public static final TypeReference<ImmutableLongArray> ImmutableLongArrayReference = typeRefOf(ImmutableLongArrayType);
    public static final TypeReference<ImmutableDoubleArray> ImmutableDoubleArrayReference = typeRefOf(ImmutableDoubleArrayType);

    public static final String ImmutableIntArrayName = ImmutableIntArrayType.getName();
    public static final String ImmutableLongArrayName = ImmutableLongArrayType.getName();
    public static final String ImmutableDoubleArrayName = ImmutableDoubleArrayType.getName();

}
