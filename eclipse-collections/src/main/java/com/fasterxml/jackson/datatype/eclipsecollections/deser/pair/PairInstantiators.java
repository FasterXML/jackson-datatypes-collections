package com.fasterxml.jackson.datatype.eclipsecollections.deser.pair;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.api.tuple.primitive.BooleanBooleanPair;
import org.eclipse.collections.api.tuple.primitive.BooleanBytePair;
import org.eclipse.collections.api.tuple.primitive.BooleanCharPair;
import org.eclipse.collections.api.tuple.primitive.BooleanDoublePair;
import org.eclipse.collections.api.tuple.primitive.BooleanFloatPair;
import org.eclipse.collections.api.tuple.primitive.BooleanIntPair;
import org.eclipse.collections.api.tuple.primitive.BooleanLongPair;
import org.eclipse.collections.api.tuple.primitive.BooleanObjectPair;
import org.eclipse.collections.api.tuple.primitive.BooleanShortPair;
import org.eclipse.collections.api.tuple.primitive.ByteBooleanPair;
import org.eclipse.collections.api.tuple.primitive.ByteBytePair;
import org.eclipse.collections.api.tuple.primitive.ByteCharPair;
import org.eclipse.collections.api.tuple.primitive.ByteDoublePair;
import org.eclipse.collections.api.tuple.primitive.ByteFloatPair;
import org.eclipse.collections.api.tuple.primitive.ByteIntPair;
import org.eclipse.collections.api.tuple.primitive.ByteLongPair;
import org.eclipse.collections.api.tuple.primitive.ByteObjectPair;
import org.eclipse.collections.api.tuple.primitive.ByteShortPair;
import org.eclipse.collections.api.tuple.primitive.CharBooleanPair;
import org.eclipse.collections.api.tuple.primitive.CharBytePair;
import org.eclipse.collections.api.tuple.primitive.CharCharPair;
import org.eclipse.collections.api.tuple.primitive.CharDoublePair;
import org.eclipse.collections.api.tuple.primitive.CharFloatPair;
import org.eclipse.collections.api.tuple.primitive.CharIntPair;
import org.eclipse.collections.api.tuple.primitive.CharLongPair;
import org.eclipse.collections.api.tuple.primitive.CharObjectPair;
import org.eclipse.collections.api.tuple.primitive.CharShortPair;
import org.eclipse.collections.api.tuple.primitive.DoubleBooleanPair;
import org.eclipse.collections.api.tuple.primitive.DoubleBytePair;
import org.eclipse.collections.api.tuple.primitive.DoubleCharPair;
import org.eclipse.collections.api.tuple.primitive.DoubleDoublePair;
import org.eclipse.collections.api.tuple.primitive.DoubleFloatPair;
import org.eclipse.collections.api.tuple.primitive.DoubleIntPair;
import org.eclipse.collections.api.tuple.primitive.DoubleLongPair;
import org.eclipse.collections.api.tuple.primitive.DoubleObjectPair;
import org.eclipse.collections.api.tuple.primitive.DoubleShortPair;
import org.eclipse.collections.api.tuple.primitive.FloatBooleanPair;
import org.eclipse.collections.api.tuple.primitive.FloatBytePair;
import org.eclipse.collections.api.tuple.primitive.FloatCharPair;
import org.eclipse.collections.api.tuple.primitive.FloatDoublePair;
import org.eclipse.collections.api.tuple.primitive.FloatFloatPair;
import org.eclipse.collections.api.tuple.primitive.FloatIntPair;
import org.eclipse.collections.api.tuple.primitive.FloatLongPair;
import org.eclipse.collections.api.tuple.primitive.FloatObjectPair;
import org.eclipse.collections.api.tuple.primitive.FloatShortPair;
import org.eclipse.collections.api.tuple.primitive.IntBooleanPair;
import org.eclipse.collections.api.tuple.primitive.IntBytePair;
import org.eclipse.collections.api.tuple.primitive.IntCharPair;
import org.eclipse.collections.api.tuple.primitive.IntDoublePair;
import org.eclipse.collections.api.tuple.primitive.IntFloatPair;
import org.eclipse.collections.api.tuple.primitive.IntIntPair;
import org.eclipse.collections.api.tuple.primitive.IntLongPair;
import org.eclipse.collections.api.tuple.primitive.IntObjectPair;
import org.eclipse.collections.api.tuple.primitive.IntShortPair;
import org.eclipse.collections.api.tuple.primitive.LongBooleanPair;
import org.eclipse.collections.api.tuple.primitive.LongBytePair;
import org.eclipse.collections.api.tuple.primitive.LongCharPair;
import org.eclipse.collections.api.tuple.primitive.LongDoublePair;
import org.eclipse.collections.api.tuple.primitive.LongFloatPair;
import org.eclipse.collections.api.tuple.primitive.LongIntPair;
import org.eclipse.collections.api.tuple.primitive.LongLongPair;
import org.eclipse.collections.api.tuple.primitive.LongObjectPair;
import org.eclipse.collections.api.tuple.primitive.LongShortPair;
import org.eclipse.collections.api.tuple.primitive.ObjectBooleanPair;
import org.eclipse.collections.api.tuple.primitive.ObjectBytePair;
import org.eclipse.collections.api.tuple.primitive.ObjectCharPair;
import org.eclipse.collections.api.tuple.primitive.ObjectDoublePair;
import org.eclipse.collections.api.tuple.primitive.ObjectFloatPair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.api.tuple.primitive.ObjectLongPair;
import org.eclipse.collections.api.tuple.primitive.ObjectShortPair;
import org.eclipse.collections.api.tuple.primitive.ShortBooleanPair;
import org.eclipse.collections.api.tuple.primitive.ShortBytePair;
import org.eclipse.collections.api.tuple.primitive.ShortCharPair;
import org.eclipse.collections.api.tuple.primitive.ShortDoublePair;
import org.eclipse.collections.api.tuple.primitive.ShortFloatPair;
import org.eclipse.collections.api.tuple.primitive.ShortIntPair;
import org.eclipse.collections.api.tuple.primitive.ShortLongPair;
import org.eclipse.collections.api.tuple.primitive.ShortObjectPair;
import org.eclipse.collections.api.tuple.primitive.ShortShortPair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

/**
 * @author yawkat
 */
public final class PairInstantiators extends ValueInstantiators.Base {
    private static final Map<Class<?>, ValueInstantiator> PURE_PRIMITIVE_INSTANTIATORS = new HashMap<>();

    @SuppressWarnings("serial")
    @Override
    public ValueInstantiator findValueInstantiator(
            DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator
    ) {
        Class<?> beanClass = beanDesc.getBeanClass();
        ValueInstantiator purePrimitive = PURE_PRIMITIVE_INSTANTIATORS.get(beanClass);
        if (purePrimitive != null) {
            return purePrimitive;
        }

        JavaType beanType = beanDesc.getType();

        if (beanClass == BooleanObjectPair.class) {
            return primitiveObjectInstantiator(beanType, boolean.class,
                                               (one, two) -> PrimitiveTuples.pair((boolean) one, two));
        } else if (beanClass == ByteObjectPair.class) {
            return primitiveObjectInstantiator(beanType, byte.class,
                                               (one, two) -> PrimitiveTuples.pair((byte) one, two));
        } else if (beanClass == ShortObjectPair.class) {
            return primitiveObjectInstantiator(beanType, short.class,
                                               (one, two) -> PrimitiveTuples.pair((short) one, two));
        } else if (beanClass == CharObjectPair.class) {
            return primitiveObjectInstantiator(beanType, char.class,
                                               (one, two) -> PrimitiveTuples.pair((char) one, two));
        } else if (beanClass == IntObjectPair.class) {
            return primitiveObjectInstantiator(beanType, int.class,
                                               (one, two) -> PrimitiveTuples.pair((int) one, two));
        } else if (beanClass == FloatObjectPair.class) {
            return primitiveObjectInstantiator(beanType, float.class,
                                               (one, two) -> PrimitiveTuples.pair((float) one, two));
        } else if (beanClass == LongObjectPair.class) {
            return primitiveObjectInstantiator(beanType, long.class,
                                               (one, two) -> PrimitiveTuples.pair((long) one, two));
        } else if (beanClass == DoubleObjectPair.class) {
            return primitiveObjectInstantiator(beanType, double.class,
                                               (one, two) -> PrimitiveTuples.pair((double) one, two));
        }

        if (beanClass == ObjectBooleanPair.class) {
            return objectPrimitiveInstantiator(beanType, boolean.class,
                                               (one, two) -> PrimitiveTuples.pair(one, (boolean) two));
        } else if (beanClass == ObjectBytePair.class) {
            return objectPrimitiveInstantiator(beanType, byte.class,
                                               (one, two) -> PrimitiveTuples.pair(one, (byte) two));
        } else if (beanClass == ObjectShortPair.class) {
            return objectPrimitiveInstantiator(beanType, short.class,
                                               (one, two) -> PrimitiveTuples.pair(one, (short) two));
        } else if (beanClass == ObjectCharPair.class) {
            return objectPrimitiveInstantiator(beanType, char.class,
                                               (one, two) -> PrimitiveTuples.pair(one, (char) two));
        } else if (beanClass == ObjectIntPair.class) {
            return objectPrimitiveInstantiator(beanType, int.class,
                                               (one, two) -> PrimitiveTuples.pair(one, (int) two));
        } else if (beanClass == ObjectFloatPair.class) {
            return objectPrimitiveInstantiator(beanType, float.class,
                                               (one, two) -> PrimitiveTuples.pair(one, (float) two));
        } else if (beanClass == ObjectLongPair.class) {
            return objectPrimitiveInstantiator(beanType, long.class,
                                               (one, two) -> PrimitiveTuples.pair(one, (long) two));
        } else if (beanClass == ObjectDoublePair.class) {
            return objectPrimitiveInstantiator(beanType, double.class,
                                               (one, two) -> PrimitiveTuples.pair(one, (double) two));
        }

        if (beanClass == Pair.class) {
            return new ValueInstantiator.Base(beanType) {
                @Override
                public boolean canCreateFromObjectWith() {
                    return true;
                }

                @Override
                public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
                    JavaType oneType = beanType.containedType(0);
                    JavaType twoType = beanType.containedType(1);
                    return makeProperties(config, oneType, twoType);
                }

                @Override
                public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
                    return Tuples.pair(args[0], args[1]);
                }
            };
        }

        if (beanClass == Twin.class) {
            return new ValueInstantiator.Base(beanType) {
                @Override
                public boolean canCreateFromObjectWith() {
                    return true;
                }

                @Override
                public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
                    JavaType memberType = beanType.containedType(0);
                    return makeProperties(config, memberType, memberType);
                }

                @Override
                public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
                    return Tuples.twin(args[0], args[1]);
                }
            };
        }

        return defaultInstantiator;
    }

    @SuppressWarnings("serial")
    private static <P> ValueInstantiator primitiveObjectInstantiator(
            JavaType inputType, Class<?> one,
            BiFunction<Object, Object, P> factory
    ) {
        return new ValueInstantiator.Base(inputType) {
            @Override
            public boolean canCreateFromObjectWith() {
                return true;
            }

            @Override
            public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
                JavaType oneType = config.constructType(one);
                JavaType twoType = inputType.containedType(0);
                return makeProperties(config, oneType, twoType);
            }

            @Override
            public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
                return factory.apply(args[0], args[1]);
            }
        };
    }

    private static <P> ValueInstantiator objectPrimitiveInstantiator(
            JavaType inputType, Class<?> two,
            BiFunction<Object, Object, P> factory
    ) {
        return new ValueInstantiator.Base(inputType) {
            @Override
            public boolean canCreateFromObjectWith() {
                return true;
            }

            @Override
            public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
                JavaType oneType = inputType.containedType(0);
                JavaType twoType = config.constructType(two);
                return makeProperties(config, oneType, twoType);
            }

            @Override
            public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
                return factory.apply(args[0], args[1]);
            }
        };
    }

    @SuppressWarnings("serial")
    private static <P> void purePrimitiveInstantiator(
            Class<P> pairClass, Class<?> one, Class<?> two,
            BiFunction<Object, Object, P> factory
    ) {
        PURE_PRIMITIVE_INSTANTIATORS.put(pairClass, new ValueInstantiator.Base(pairClass) {
            @Override
            public boolean canCreateFromObjectWith() {
                return true;
            }

            @Override
            public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
                JavaType oneType = config.constructType(one);
                JavaType twoType = config.constructType(two);
                return makeProperties(config, oneType, twoType);
            }

            @Override
            public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
                return factory.apply(args[0], args[1]);
            }
        });
    }

    static SettableBeanProperty[] makeProperties(
            DeserializationConfig config,
            JavaType oneType,
            JavaType twoType
    ) {
        try {
            return new SettableBeanProperty[]{
                    CreatorProperty.construct(
                            PropertyName.construct("one"),
                            oneType,
                            null,
                            config.findTypeDeserializer(oneType),
                            AnnotationCollector.emptyAnnotations(),
                            null, 0, null, PropertyMetadata.STD_REQUIRED
                    ),
                    CreatorProperty.construct(
                            PropertyName.construct("two"),
                            twoType,
                            null,
                            config.findTypeDeserializer(twoType),
                            AnnotationCollector.emptyAnnotations(),
                            null, 1, null, PropertyMetadata.STD_REQUIRED
                    )
            };
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        //region Boolean -> Primitive
        purePrimitiveInstantiator(BooleanBooleanPair.class, boolean.class, boolean.class,
                                  (one, two) -> PrimitiveTuples.pair((boolean) one, (boolean) two));
        purePrimitiveInstantiator(BooleanBytePair.class, boolean.class, byte.class,
                                  (one, two) -> PrimitiveTuples.pair((boolean) one, (byte) two));
        purePrimitiveInstantiator(BooleanShortPair.class, boolean.class, short.class,
                                  (one, two) -> PrimitiveTuples.pair((boolean) one, (short) two));
        purePrimitiveInstantiator(BooleanCharPair.class, boolean.class, char.class,
                                  (one, two) -> PrimitiveTuples.pair((boolean) one, (char) two));
        purePrimitiveInstantiator(BooleanIntPair.class, boolean.class, int.class,
                                  (one, two) -> PrimitiveTuples.pair((boolean) one, (int) two));
        purePrimitiveInstantiator(BooleanFloatPair.class, boolean.class, float.class,
                                  (one, two) -> PrimitiveTuples.pair((boolean) one, (float) two));
        purePrimitiveInstantiator(BooleanLongPair.class, boolean.class, long.class,
                                  (one, two) -> PrimitiveTuples.pair((boolean) one, (long) two));
        purePrimitiveInstantiator(BooleanDoublePair.class, boolean.class, double.class,
                                  (one, two) -> PrimitiveTuples.pair((boolean) one, (double) two));
        //endregion
        //region Byte -> Primitive
        purePrimitiveInstantiator(ByteBooleanPair.class, byte.class, boolean.class,
                                  (one, two) -> PrimitiveTuples.pair((byte) one, (boolean) two));
        purePrimitiveInstantiator(ByteBytePair.class, byte.class, byte.class,
                                  (one, two) -> PrimitiveTuples.pair((byte) one, (byte) two));
        purePrimitiveInstantiator(ByteShortPair.class, byte.class, short.class,
                                  (one, two) -> PrimitiveTuples.pair((byte) one, (short) two));
        purePrimitiveInstantiator(ByteCharPair.class, byte.class, char.class,
                                  (one, two) -> PrimitiveTuples.pair((byte) one, (char) two));
        purePrimitiveInstantiator(ByteIntPair.class, byte.class, int.class,
                                  (one, two) -> PrimitiveTuples.pair((byte) one, (int) two));
        purePrimitiveInstantiator(ByteFloatPair.class, byte.class, float.class,
                                  (one, two) -> PrimitiveTuples.pair((byte) one, (float) two));
        purePrimitiveInstantiator(ByteLongPair.class, byte.class, long.class,
                                  (one, two) -> PrimitiveTuples.pair((byte) one, (long) two));
        purePrimitiveInstantiator(ByteDoublePair.class, byte.class, double.class,
                                  (one, two) -> PrimitiveTuples.pair((byte) one, (double) two));
        //endregion
        //region Short -> Primitive
        purePrimitiveInstantiator(ShortBooleanPair.class, short.class, boolean.class,
                                  (one, two) -> PrimitiveTuples.pair((short) one, (boolean) two));
        purePrimitiveInstantiator(ShortBytePair.class, short.class, byte.class,
                                  (one, two) -> PrimitiveTuples.pair((short) one, (byte) two));
        purePrimitiveInstantiator(ShortShortPair.class, short.class, short.class,
                                  (one, two) -> PrimitiveTuples.pair((short) one, (short) two));
        purePrimitiveInstantiator(ShortCharPair.class, short.class, char.class,
                                  (one, two) -> PrimitiveTuples.pair((short) one, (char) two));
        purePrimitiveInstantiator(ShortIntPair.class, short.class, int.class,
                                  (one, two) -> PrimitiveTuples.pair((short) one, (int) two));
        purePrimitiveInstantiator(ShortFloatPair.class, short.class, float.class,
                                  (one, two) -> PrimitiveTuples.pair((short) one, (float) two));
        purePrimitiveInstantiator(ShortLongPair.class, short.class, long.class,
                                  (one, two) -> PrimitiveTuples.pair((short) one, (long) two));
        purePrimitiveInstantiator(ShortDoublePair.class, short.class, double.class,
                                  (one, two) -> PrimitiveTuples.pair((short) one, (double) two));
        //endregion
        //region Char -> Primitive
        purePrimitiveInstantiator(CharBooleanPair.class, char.class, boolean.class,
                                  (one, two) -> PrimitiveTuples.pair((char) one, (boolean) two));
        purePrimitiveInstantiator(CharBytePair.class, char.class, byte.class,
                                  (one, two) -> PrimitiveTuples.pair((char) one, (byte) two));
        purePrimitiveInstantiator(CharShortPair.class, char.class, short.class,
                                  (one, two) -> PrimitiveTuples.pair((char) one, (short) two));
        purePrimitiveInstantiator(CharCharPair.class, char.class, char.class,
                                  (one, two) -> PrimitiveTuples.pair((char) one, (char) two));
        purePrimitiveInstantiator(CharIntPair.class, char.class, int.class,
                                  (one, two) -> PrimitiveTuples.pair((char) one, (int) two));
        purePrimitiveInstantiator(CharFloatPair.class, char.class, float.class,
                                  (one, two) -> PrimitiveTuples.pair((char) one, (float) two));
        purePrimitiveInstantiator(CharLongPair.class, char.class, long.class,
                                  (one, two) -> PrimitiveTuples.pair((char) one, (long) two));
        purePrimitiveInstantiator(CharDoublePair.class, char.class, double.class,
                                  (one, two) -> PrimitiveTuples.pair((char) one, (double) two));
        //endregion
        //region Int -> Primitive
        purePrimitiveInstantiator(IntBooleanPair.class, int.class, boolean.class,
                                  (one, two) -> PrimitiveTuples.pair((int) one, (boolean) two));
        purePrimitiveInstantiator(IntBytePair.class, int.class, byte.class,
                                  (one, two) -> PrimitiveTuples.pair((int) one, (byte) two));
        purePrimitiveInstantiator(IntShortPair.class, int.class, short.class,
                                  (one, two) -> PrimitiveTuples.pair((int) one, (short) two));
        purePrimitiveInstantiator(IntCharPair.class, int.class, char.class,
                                  (one, two) -> PrimitiveTuples.pair((int) one, (char) two));
        purePrimitiveInstantiator(IntIntPair.class, int.class, int.class,
                                  (one, two) -> PrimitiveTuples.pair((int) one, (int) two));
        purePrimitiveInstantiator(IntFloatPair.class, int.class, float.class,
                                  (one, two) -> PrimitiveTuples.pair((int) one, (float) two));
        purePrimitiveInstantiator(IntLongPair.class, int.class, long.class,
                                  (one, two) -> PrimitiveTuples.pair((int) one, (long) two));
        purePrimitiveInstantiator(IntDoublePair.class, int.class, double.class,
                                  (one, two) -> PrimitiveTuples.pair((int) one, (double) two));
        //endregion
        //region Float -> Primitive
        purePrimitiveInstantiator(FloatBooleanPair.class, float.class, boolean.class,
                                  (one, two) -> PrimitiveTuples.pair((float) one, (boolean) two));
        purePrimitiveInstantiator(FloatBytePair.class, float.class, byte.class,
                                  (one, two) -> PrimitiveTuples.pair((float) one, (byte) two));
        purePrimitiveInstantiator(FloatShortPair.class, float.class, short.class,
                                  (one, two) -> PrimitiveTuples.pair((float) one, (short) two));
        purePrimitiveInstantiator(FloatCharPair.class, float.class, char.class,
                                  (one, two) -> PrimitiveTuples.pair((float) one, (char) two));
        purePrimitiveInstantiator(FloatIntPair.class, float.class, int.class,
                                  (one, two) -> PrimitiveTuples.pair((float) one, (int) two));
        purePrimitiveInstantiator(FloatFloatPair.class, float.class, float.class,
                                  (one, two) -> PrimitiveTuples.pair((float) one, (float) two));
        purePrimitiveInstantiator(FloatLongPair.class, float.class, long.class,
                                  (one, two) -> PrimitiveTuples.pair((float) one, (long) two));
        purePrimitiveInstantiator(FloatDoublePair.class, float.class, double.class,
                                  (one, two) -> PrimitiveTuples.pair((float) one, (double) two));
        //endregion
        //region Long -> Primitive
        purePrimitiveInstantiator(LongBooleanPair.class, long.class, boolean.class,
                                  (one, two) -> PrimitiveTuples.pair((long) one, (boolean) two));
        purePrimitiveInstantiator(LongBytePair.class, long.class, byte.class,
                                  (one, two) -> PrimitiveTuples.pair((long) one, (byte) two));
        purePrimitiveInstantiator(LongShortPair.class, long.class, short.class,
                                  (one, two) -> PrimitiveTuples.pair((long) one, (short) two));
        purePrimitiveInstantiator(LongCharPair.class, long.class, char.class,
                                  (one, two) -> PrimitiveTuples.pair((long) one, (char) two));
        purePrimitiveInstantiator(LongIntPair.class, long.class, int.class,
                                  (one, two) -> PrimitiveTuples.pair((long) one, (int) two));
        purePrimitiveInstantiator(LongFloatPair.class, long.class, float.class,
                                  (one, two) -> PrimitiveTuples.pair((long) one, (float) two));
        purePrimitiveInstantiator(LongLongPair.class, long.class, long.class,
                                  (one, two) -> PrimitiveTuples.pair((long) one, (long) two));
        purePrimitiveInstantiator(LongDoublePair.class, long.class, double.class,
                                  (one, two) -> PrimitiveTuples.pair((long) one, (double) two));
        //endregion
        //region Double -> Primitive
        purePrimitiveInstantiator(DoubleBooleanPair.class, double.class, boolean.class,
                                  (one, two) -> PrimitiveTuples.pair((double) one, (boolean) two));
        purePrimitiveInstantiator(DoubleBytePair.class, double.class, byte.class,
                                  (one, two) -> PrimitiveTuples.pair((double) one, (byte) two));
        purePrimitiveInstantiator(DoubleShortPair.class, double.class, short.class,
                                  (one, two) -> PrimitiveTuples.pair((double) one, (short) two));
        purePrimitiveInstantiator(DoubleCharPair.class, double.class, char.class,
                                  (one, two) -> PrimitiveTuples.pair((double) one, (char) two));
        purePrimitiveInstantiator(DoubleIntPair.class, double.class, int.class,
                                  (one, two) -> PrimitiveTuples.pair((double) one, (int) two));
        purePrimitiveInstantiator(DoubleFloatPair.class, double.class, float.class,
                                  (one, two) -> PrimitiveTuples.pair((double) one, (float) two));
        purePrimitiveInstantiator(DoubleLongPair.class, double.class, long.class,
                                  (one, two) -> PrimitiveTuples.pair((double) one, (long) two));
        purePrimitiveInstantiator(DoubleDoublePair.class, double.class, double.class,
                                  (one, two) -> PrimitiveTuples.pair((double) one, (double) two));
        //endregion
    }
}
