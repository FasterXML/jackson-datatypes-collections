package com.fasterxml.jackson.datatype.guava.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.guava.deser.BasePrimitiveCollectionDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.BooleansPrimitiveCollectionDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.BytesPrimitiveCollectionDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.CharsPrimitiveCollectionDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.DoublesPrimitiveCollectionDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.FloatsPrimitiveCollectionDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.IntsPrimitiveCollectionDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.LongsPrimitiveCollectionDeserializer;
import com.fasterxml.jackson.datatype.guava.deser.primitives.ShortsPrimitiveCollectionDeserializer;
import com.google.common.base.Optional;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Utility class to cover all primitive types
 *
 * @author robert@albertlr.ro
 */
public class PrimitiveTypes {

    /** An enum with all the primitives */
    public enum Primitives {
        BOOLEAN(BooleansType, boolean.class, Boolean.class, () -> new BooleansPrimitiveCollectionDeserializer()),
        BYTE(BytesType, byte.class, Byte.class, () -> new BytesPrimitiveCollectionDeserializer()),
        CHAR(CharsType, char.class, Character.class, () -> new CharsPrimitiveCollectionDeserializer()),
        DOUBLE(DoublesType, double.class, Double.class, () -> new DoublesPrimitiveCollectionDeserializer()),
        FLOAT(FloatsType, float.class, Float.class, () -> new FloatsPrimitiveCollectionDeserializer()),
        INT(IntsType, int.class, Integer.class, () -> new IntsPrimitiveCollectionDeserializer()),
        LONG(LongsType, long.class, Long.class, () -> new LongsPrimitiveCollectionDeserializer()),
        SHORT(ShortsType, short.class, Short.class, () -> new ShortsPrimitiveCollectionDeserializer());

        private final Class<? extends List<?>> type;
        private final Class<?> primitiveType;
        private final Class<?> objectType;
        private final Supplier<? extends BasePrimitiveCollectionDeserializer> deserializerFactory;

        private Primitives(Class<? extends List<?>> type, Class<?> primitiveType, Class<?> objectType, Supplier<? extends BasePrimitiveCollectionDeserializer> deserializerFactory) {
            this.type = type;
            this.primitiveType = primitiveType;
            this.objectType = objectType;
            this.deserializerFactory = deserializerFactory;
        }

        public <T> Class<? extends List<T>> type() {
            return (Class<? extends List<T>>) type;
        }

        public Class<?> primitiveType() {
            return primitiveType;
        }

        public <T> Class<T> objectType() {
            return (Class<T>) objectType;
        }

        public <T> BasePrimitiveCollectionDeserializer<T, List<T>, Collection<T>> newDeserializer() {
            return deserializerFactory.get();
        }
    }

    public static Optional<PrimitiveTypes.Primitives> isAssignableFromPrimitive(Class<?> valueType) {
        for (PrimitiveTypes.Primitives primitive : PrimitiveTypes.Primitives.values()) {
            if (primitive.type().isAssignableFrom(valueType)) {
                return Optional.of(primitive);
            }
        }
        return Optional.absent();
    }

    /** Type of list returned by {@link Booleans#asList(boolean...)} */
    public static final Class<? extends List<Boolean>> BooleansType;
    /** Type of list returned by {@link Bytes#asList(byte...)} */
    public static final Class<? extends List<Byte>> BytesType;
    /** Type of list returned by {@link Chars#asList(char...)} */
    public static final Class<? extends List<Character>> CharsType;
    /** Type of list returned by {@link Doubles#asList(double...)} */
    public static final Class<? extends List<Double>> DoublesType;
    /** Type of list returned by {@link Floats#asList(float...)} */
    public static final Class<? extends List<Float>> FloatsType;
    /** Type of list returned by {@link Ints#asList(int...)} */
    public static final Class<? extends List<Integer>> IntsType;
    /** Type of list returned by {@link Longs#asList(long...)} */
    public static final Class<? extends List<Long>> LongsType;
    /** Type of list returned by {@link Shorts#asList(short...)} */
    public static final Class<? extends List<Short>> ShortsType;

    static {
        /*
         * get the actual name of the underlying private class by creating a dummy list .. and than get its name
         */

        BooleansType = (Class<? extends List<Boolean>>) Booleans.asList(true, false).getClass();
        BytesType = (Class<? extends List<Byte>>) Bytes.asList((byte) 1, (byte) 2).getClass();
        CharsType = (Class<? extends List<Character>>) Chars.asList((char) 1, (char) 2).getClass();
        DoublesType = (Class<? extends List<Double>>) Doubles.asList(1d, 2d).getClass();
        FloatsType = (Class<? extends List<Float>>) Floats.asList((float) 1d, (float) 2d).getClass();
        IntsType = (Class<? extends List<Integer>>) Ints.asList(0, 1).getClass();
        LongsType = (Class<? extends List<Long>>) Longs.asList(0L, 1L).getClass();
        ShortsType = (Class<? extends List<Short>>) Shorts.asList((short) 0, (short) 1).getClass();
    }

    public static final TypeReference<List<Boolean>> BooleansTypeReference = typeRefOf(BooleansType);
    public static final TypeReference<List<Byte>> BytesTypeReference = typeRefOf(BytesType);
    public static final TypeReference<List<Character>> CharsTypeReference = typeRefOf(CharsType);
    public static final TypeReference<List<Double>> DoublesTypeReference = typeRefOf(DoublesType);
    public static final TypeReference<List<Float>> FloatsTypeReference = typeRefOf(FloatsType);
    public static final TypeReference<List<Integer>> IntsTypeReference = typeRefOf(IntsType);
    public static final TypeReference<List<Long>> LongsTypeReference = typeRefOf(LongsType);
    public static final TypeReference<List<Short>> ShortsTypeReference = typeRefOf(ShortsType);

    public static final String BooleansTypeName = BooleansType.getName();
    public static final String BytesTypeName = BytesType.getName();
    public static final String CharsTypeName = CharsType.getName();
    public static final String DoublesTypeName = DoublesType.getName();
    public static final String FloatsTypeName = FloatsType.getName();
    public static final String IntsTypeName = IntsType.getName();
    public static final String LongsTypeName = LongsType.getName();
    public static final String ShortsTypeName = ShortsType.getName();


    private static <T> TypeReference<T> typeRefOf(Type type) {
        return new PrimitiveTypeReference<>(type);
    }

    private static class PrimitiveTypeReference<T> extends TypeReference<T> {
        private final Type primitiveType;

        private PrimitiveTypeReference(Type primitiveType) {
            this.primitiveType = primitiveType;
        }

        @Override
        public Type getType() {
            return primitiveType;
        }
    }

}
