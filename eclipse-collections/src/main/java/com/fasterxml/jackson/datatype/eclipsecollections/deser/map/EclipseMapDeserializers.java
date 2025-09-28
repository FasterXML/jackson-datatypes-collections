package com.fasterxml.jackson.datatype.eclipsecollections.deser.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.util.ClassUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.ImmutableMapIterable;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.UnsortedMapIterable;
import org.eclipse.collections.api.map.sorted.ImmutableSortedMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.map.sorted.SortedMapIterable;
import org.eclipse.collections.api.map.primitive.ByteBooleanMap;
import org.eclipse.collections.api.map.primitive.ByteByteMap;
import org.eclipse.collections.api.map.primitive.ByteCharMap;
import org.eclipse.collections.api.map.primitive.ByteDoubleMap;
import org.eclipse.collections.api.map.primitive.ByteFloatMap;
import org.eclipse.collections.api.map.primitive.ByteIntMap;
import org.eclipse.collections.api.map.primitive.ByteLongMap;
import org.eclipse.collections.api.map.primitive.ByteObjectMap;
import org.eclipse.collections.api.map.primitive.ByteShortMap;
import org.eclipse.collections.api.map.primitive.CharBooleanMap;
import org.eclipse.collections.api.map.primitive.CharByteMap;
import org.eclipse.collections.api.map.primitive.CharCharMap;
import org.eclipse.collections.api.map.primitive.CharDoubleMap;
import org.eclipse.collections.api.map.primitive.CharFloatMap;
import org.eclipse.collections.api.map.primitive.CharIntMap;
import org.eclipse.collections.api.map.primitive.CharLongMap;
import org.eclipse.collections.api.map.primitive.CharObjectMap;
import org.eclipse.collections.api.map.primitive.CharShortMap;
import org.eclipse.collections.api.map.primitive.DoubleBooleanMap;
import org.eclipse.collections.api.map.primitive.DoubleByteMap;
import org.eclipse.collections.api.map.primitive.DoubleCharMap;
import org.eclipse.collections.api.map.primitive.DoubleDoubleMap;
import org.eclipse.collections.api.map.primitive.DoubleFloatMap;
import org.eclipse.collections.api.map.primitive.DoubleIntMap;
import org.eclipse.collections.api.map.primitive.DoubleLongMap;
import org.eclipse.collections.api.map.primitive.DoubleObjectMap;
import org.eclipse.collections.api.map.primitive.DoubleShortMap;
import org.eclipse.collections.api.map.primitive.FloatBooleanMap;
import org.eclipse.collections.api.map.primitive.FloatByteMap;
import org.eclipse.collections.api.map.primitive.FloatCharMap;
import org.eclipse.collections.api.map.primitive.FloatDoubleMap;
import org.eclipse.collections.api.map.primitive.FloatFloatMap;
import org.eclipse.collections.api.map.primitive.FloatIntMap;
import org.eclipse.collections.api.map.primitive.FloatLongMap;
import org.eclipse.collections.api.map.primitive.FloatObjectMap;
import org.eclipse.collections.api.map.primitive.FloatShortMap;
import org.eclipse.collections.api.map.primitive.ImmutableByteBooleanMap;
import org.eclipse.collections.api.map.primitive.ImmutableByteByteMap;
import org.eclipse.collections.api.map.primitive.ImmutableByteCharMap;
import org.eclipse.collections.api.map.primitive.ImmutableByteDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableByteFloatMap;
import org.eclipse.collections.api.map.primitive.ImmutableByteIntMap;
import org.eclipse.collections.api.map.primitive.ImmutableByteLongMap;
import org.eclipse.collections.api.map.primitive.ImmutableByteObjectMap;
import org.eclipse.collections.api.map.primitive.ImmutableByteShortMap;
import org.eclipse.collections.api.map.primitive.ImmutableCharBooleanMap;
import org.eclipse.collections.api.map.primitive.ImmutableCharByteMap;
import org.eclipse.collections.api.map.primitive.ImmutableCharCharMap;
import org.eclipse.collections.api.map.primitive.ImmutableCharDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableCharFloatMap;
import org.eclipse.collections.api.map.primitive.ImmutableCharIntMap;
import org.eclipse.collections.api.map.primitive.ImmutableCharLongMap;
import org.eclipse.collections.api.map.primitive.ImmutableCharObjectMap;
import org.eclipse.collections.api.map.primitive.ImmutableCharShortMap;
import org.eclipse.collections.api.map.primitive.ImmutableDoubleBooleanMap;
import org.eclipse.collections.api.map.primitive.ImmutableDoubleByteMap;
import org.eclipse.collections.api.map.primitive.ImmutableDoubleCharMap;
import org.eclipse.collections.api.map.primitive.ImmutableDoubleDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableDoubleFloatMap;
import org.eclipse.collections.api.map.primitive.ImmutableDoubleIntMap;
import org.eclipse.collections.api.map.primitive.ImmutableDoubleLongMap;
import org.eclipse.collections.api.map.primitive.ImmutableDoubleObjectMap;
import org.eclipse.collections.api.map.primitive.ImmutableDoubleShortMap;
import org.eclipse.collections.api.map.primitive.ImmutableFloatBooleanMap;
import org.eclipse.collections.api.map.primitive.ImmutableFloatByteMap;
import org.eclipse.collections.api.map.primitive.ImmutableFloatCharMap;
import org.eclipse.collections.api.map.primitive.ImmutableFloatDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableFloatFloatMap;
import org.eclipse.collections.api.map.primitive.ImmutableFloatIntMap;
import org.eclipse.collections.api.map.primitive.ImmutableFloatLongMap;
import org.eclipse.collections.api.map.primitive.ImmutableFloatObjectMap;
import org.eclipse.collections.api.map.primitive.ImmutableFloatShortMap;
import org.eclipse.collections.api.map.primitive.ImmutableIntBooleanMap;
import org.eclipse.collections.api.map.primitive.ImmutableIntByteMap;
import org.eclipse.collections.api.map.primitive.ImmutableIntCharMap;
import org.eclipse.collections.api.map.primitive.ImmutableIntDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableIntFloatMap;
import org.eclipse.collections.api.map.primitive.ImmutableIntIntMap;
import org.eclipse.collections.api.map.primitive.ImmutableIntLongMap;
import org.eclipse.collections.api.map.primitive.ImmutableIntObjectMap;
import org.eclipse.collections.api.map.primitive.ImmutableIntShortMap;
import org.eclipse.collections.api.map.primitive.ImmutableLongBooleanMap;
import org.eclipse.collections.api.map.primitive.ImmutableLongByteMap;
import org.eclipse.collections.api.map.primitive.ImmutableLongCharMap;
import org.eclipse.collections.api.map.primitive.ImmutableLongDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableLongFloatMap;
import org.eclipse.collections.api.map.primitive.ImmutableLongIntMap;
import org.eclipse.collections.api.map.primitive.ImmutableLongLongMap;
import org.eclipse.collections.api.map.primitive.ImmutableLongObjectMap;
import org.eclipse.collections.api.map.primitive.ImmutableLongShortMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectBooleanMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectByteMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectCharMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectFloatMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectIntMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.ImmutableObjectShortMap;
import org.eclipse.collections.api.map.primitive.ImmutableShortBooleanMap;
import org.eclipse.collections.api.map.primitive.ImmutableShortByteMap;
import org.eclipse.collections.api.map.primitive.ImmutableShortCharMap;
import org.eclipse.collections.api.map.primitive.ImmutableShortDoubleMap;
import org.eclipse.collections.api.map.primitive.ImmutableShortFloatMap;
import org.eclipse.collections.api.map.primitive.ImmutableShortIntMap;
import org.eclipse.collections.api.map.primitive.ImmutableShortLongMap;
import org.eclipse.collections.api.map.primitive.ImmutableShortObjectMap;
import org.eclipse.collections.api.map.primitive.ImmutableShortShortMap;
import org.eclipse.collections.api.map.primitive.IntBooleanMap;
import org.eclipse.collections.api.map.primitive.IntByteMap;
import org.eclipse.collections.api.map.primitive.IntCharMap;
import org.eclipse.collections.api.map.primitive.IntDoubleMap;
import org.eclipse.collections.api.map.primitive.IntFloatMap;
import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.api.map.primitive.IntLongMap;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.IntShortMap;
import org.eclipse.collections.api.map.primitive.LongBooleanMap;
import org.eclipse.collections.api.map.primitive.LongByteMap;
import org.eclipse.collections.api.map.primitive.LongCharMap;
import org.eclipse.collections.api.map.primitive.LongDoubleMap;
import org.eclipse.collections.api.map.primitive.LongFloatMap;
import org.eclipse.collections.api.map.primitive.LongIntMap;
import org.eclipse.collections.api.map.primitive.LongLongMap;
import org.eclipse.collections.api.map.primitive.LongObjectMap;
import org.eclipse.collections.api.map.primitive.LongShortMap;
import org.eclipse.collections.api.map.primitive.MutableByteBooleanMap;
import org.eclipse.collections.api.map.primitive.MutableByteByteMap;
import org.eclipse.collections.api.map.primitive.MutableByteCharMap;
import org.eclipse.collections.api.map.primitive.MutableByteDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableByteFloatMap;
import org.eclipse.collections.api.map.primitive.MutableByteIntMap;
import org.eclipse.collections.api.map.primitive.MutableByteLongMap;
import org.eclipse.collections.api.map.primitive.MutableByteObjectMap;
import org.eclipse.collections.api.map.primitive.MutableByteShortMap;
import org.eclipse.collections.api.map.primitive.MutableCharBooleanMap;
import org.eclipse.collections.api.map.primitive.MutableCharByteMap;
import org.eclipse.collections.api.map.primitive.MutableCharCharMap;
import org.eclipse.collections.api.map.primitive.MutableCharDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableCharFloatMap;
import org.eclipse.collections.api.map.primitive.MutableCharIntMap;
import org.eclipse.collections.api.map.primitive.MutableCharLongMap;
import org.eclipse.collections.api.map.primitive.MutableCharObjectMap;
import org.eclipse.collections.api.map.primitive.MutableCharShortMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleBooleanMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleByteMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleCharMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleFloatMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleIntMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleLongMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleObjectMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleShortMap;
import org.eclipse.collections.api.map.primitive.MutableFloatBooleanMap;
import org.eclipse.collections.api.map.primitive.MutableFloatByteMap;
import org.eclipse.collections.api.map.primitive.MutableFloatCharMap;
import org.eclipse.collections.api.map.primitive.MutableFloatDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableFloatFloatMap;
import org.eclipse.collections.api.map.primitive.MutableFloatIntMap;
import org.eclipse.collections.api.map.primitive.MutableFloatLongMap;
import org.eclipse.collections.api.map.primitive.MutableFloatObjectMap;
import org.eclipse.collections.api.map.primitive.MutableFloatShortMap;
import org.eclipse.collections.api.map.primitive.MutableIntBooleanMap;
import org.eclipse.collections.api.map.primitive.MutableIntByteMap;
import org.eclipse.collections.api.map.primitive.MutableIntCharMap;
import org.eclipse.collections.api.map.primitive.MutableIntDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableIntFloatMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntLongMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableIntShortMap;
import org.eclipse.collections.api.map.primitive.MutableLongBooleanMap;
import org.eclipse.collections.api.map.primitive.MutableLongByteMap;
import org.eclipse.collections.api.map.primitive.MutableLongCharMap;
import org.eclipse.collections.api.map.primitive.MutableLongDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableLongFloatMap;
import org.eclipse.collections.api.map.primitive.MutableLongIntMap;
import org.eclipse.collections.api.map.primitive.MutableLongLongMap;
import org.eclipse.collections.api.map.primitive.MutableLongObjectMap;
import org.eclipse.collections.api.map.primitive.MutableLongShortMap;
import org.eclipse.collections.api.map.primitive.MutableObjectBooleanMap;
import org.eclipse.collections.api.map.primitive.MutableObjectByteMap;
import org.eclipse.collections.api.map.primitive.MutableObjectCharMap;
import org.eclipse.collections.api.map.primitive.MutableObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableObjectFloatMap;
import org.eclipse.collections.api.map.primitive.MutableObjectIntMap;
import org.eclipse.collections.api.map.primitive.MutableObjectLongMap;
import org.eclipse.collections.api.map.primitive.MutableObjectShortMap;
import org.eclipse.collections.api.map.primitive.MutableShortBooleanMap;
import org.eclipse.collections.api.map.primitive.MutableShortByteMap;
import org.eclipse.collections.api.map.primitive.MutableShortCharMap;
import org.eclipse.collections.api.map.primitive.MutableShortDoubleMap;
import org.eclipse.collections.api.map.primitive.MutableShortFloatMap;
import org.eclipse.collections.api.map.primitive.MutableShortIntMap;
import org.eclipse.collections.api.map.primitive.MutableShortLongMap;
import org.eclipse.collections.api.map.primitive.MutableShortObjectMap;
import org.eclipse.collections.api.map.primitive.MutableShortShortMap;
import org.eclipse.collections.api.map.primitive.ObjectBooleanMap;
import org.eclipse.collections.api.map.primitive.ObjectByteMap;
import org.eclipse.collections.api.map.primitive.ObjectCharMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectFloatMap;
import org.eclipse.collections.api.map.primitive.ObjectIntMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.map.primitive.ObjectShortMap;
import org.eclipse.collections.api.map.primitive.PrimitiveObjectMap;
import org.eclipse.collections.api.map.primitive.ShortBooleanMap;
import org.eclipse.collections.api.map.primitive.ShortByteMap;
import org.eclipse.collections.api.map.primitive.ShortCharMap;
import org.eclipse.collections.api.map.primitive.ShortDoubleMap;
import org.eclipse.collections.api.map.primitive.ShortFloatMap;
import org.eclipse.collections.api.map.primitive.ShortIntMap;
import org.eclipse.collections.api.map.primitive.ShortLongMap;
import org.eclipse.collections.api.map.primitive.ShortObjectMap;
import org.eclipse.collections.api.map.primitive.ShortShortMap;

/**
 * @author yawkat
 */
public final class EclipseMapDeserializers {
    private static final Map<Class<?>, Entry<?, ?, ?, ?>> ENTRIES = new HashMap<>();

    private EclipseMapDeserializers() {
    }

    public static EclipseMapDeserializer<?, ?, ?, ?> createDeserializer(JavaType type) throws JsonMappingException {
        Class<?> rawClass = type.getRawClass();
        Entry<?, ?, ?, ?> entry = ENTRIES.get(rawClass);
        if (entry == null) { return null; }

        return entry.createDeserializer(type);
    }

    private static <T, K extends KeyHandler<K>, V extends ValueHandler<V>> void add(
            Class<T> type,
            TypeHandlerPair<? extends T, K, V> handlerPair
    ) {
        ENTRIES.put(type, new Entry<>(handlerPair, null));
    }

    private static <T, I> void add(
            Class<T> type,
            TypeHandlerPair<I, ?, ?> handlerPair,
            Function<I, T> finish
    ) {
        ENTRIES.put(type, new Entry<>(handlerPair, finish));
    }

    private static final class Entry<T, I, K extends KeyHandler<K>, V extends ValueHandler<V>> {
        final TypeHandlerPair<I, K, V> typeHandlerPair;
        // null if this is the identity function
        final Function<I, T> finish;

        Entry(TypeHandlerPair<I, K, V> typeHandlerPair, Function<I, T> finish) {
            this.typeHandlerPair = typeHandlerPair;
            this.finish = finish;
        }

        EclipseMapDeserializer<T, I, K, V> createDeserializer(JavaType type) throws JsonMappingException {
            Class<?> rawClass = type.getRawClass();
            List<JavaType> typeParameters = type.getBindings().getTypeParameters();
            boolean refValue = PrimitiveObjectMap.class.isAssignableFrom(rawClass) ||
                               MapIterable.class.isAssignableFrom(rawClass);
            boolean refKey = refValue ? (typeParameters.size() == 2) : (typeParameters.size() == 1);

            // Generic key type validation
            if (typeHandlerPair == TypeHandlerPair.COMPARABLE_OBJECT) {
                Class<?> expectedKeyClass = Comparable.class;
                Class<?> actualKeyClass = typeParameters.get(0).getRawClass();

                if (!expectedKeyClass.isAssignableFrom(actualKeyClass)) {
                    String message = String.format(
                        "Cannot deserialize %s: key type %s does not implement %s",
                        ClassUtil.getTypeDescription(type),
                        ClassUtil.nameOf(actualKeyClass),
                        ClassUtil.nameOf(expectedKeyClass)
                    );
                    throw JsonMappingException.from((JsonParser) null, message);
                }
            }
            K keyHandler = typeHandlerPair.keyHandler(refKey ? typeParameters.get(0) : null);
            V valueHandler = typeHandlerPair.valueHandler(refValue ? typeParameters.get(typeParameters.size() - 1) : null);

            return new DeserializerImpl(keyHandler, valueHandler);
        }

        class DeserializerImpl extends EclipseMapDeserializer<T, I, K, V> {
            DeserializerImpl(K keyHandler, V valueHandler) {
                super(keyHandler, valueHandler);
            }

            @Override
            protected EclipseMapDeserializer<T, ?, ?, ?> withResolved(K keyHandler, V valueHandler) {
                return new DeserializerImpl(keyHandler, valueHandler);
            }

            @Override
            protected I createIntermediate() {
                return typeHandlerPair.createEmpty();
            }

            @Override
            protected void deserializeEntry(
                    I target,
                    K keyHandler,
                    V valueHandler,
                    DeserializationContext ctx,
                    String key,
                    JsonParser valueParser
            ) throws IOException {
                typeHandlerPair.add(target, keyHandler, valueHandler, ctx, key, valueParser);
            }

            @SuppressWarnings("unchecked")
            @Override
            protected T finish(I intermediate) {
                //noinspection unchecked
                return finish == null ? (T) intermediate : finish.apply(intermediate);
            }
        }
    }

    static {
        add(MutableMap.class, TypeHandlerPair.OBJECT_OBJECT);
        add(MutableMapIterable.class, TypeHandlerPair.OBJECT_OBJECT);
        add(MapIterable.class, TypeHandlerPair.OBJECT_OBJECT);
        add(UnsortedMapIterable.class, TypeHandlerPair.OBJECT_OBJECT);
        add(ImmutableMap.class, TypeHandlerPair.OBJECT_OBJECT, MutableMap::toImmutable);
        add(ImmutableMapIterable.class, TypeHandlerPair.OBJECT_OBJECT, MutableMap::toImmutable);

        // 26-Sep-2025: [datatypes-collections#198] Sorted maps
        add(ImmutableSortedMap.class, TypeHandlerPair.COMPARABLE_OBJECT, MutableSortedMap::toImmutable);
        add(MutableSortedMap.class, TypeHandlerPair.COMPARABLE_OBJECT);
        add(SortedMapIterable.class, TypeHandlerPair.COMPARABLE_OBJECT);

        add(ObjectBooleanMap.class, TypeHandlerPair.OBJECT_BOOLEAN);
        add(MutableObjectBooleanMap.class, TypeHandlerPair.OBJECT_BOOLEAN);
        add(ImmutableObjectBooleanMap.class, TypeHandlerPair.OBJECT_BOOLEAN, ObjectBooleanMap::toImmutable);
        add(ObjectByteMap.class, TypeHandlerPair.OBJECT_BYTE);
        add(MutableObjectByteMap.class, TypeHandlerPair.OBJECT_BYTE);
        add(ImmutableObjectByteMap.class, TypeHandlerPair.OBJECT_BYTE, ObjectByteMap::toImmutable);
        add(ObjectShortMap.class, TypeHandlerPair.OBJECT_SHORT);
        add(MutableObjectShortMap.class, TypeHandlerPair.OBJECT_SHORT);
        add(ImmutableObjectShortMap.class, TypeHandlerPair.OBJECT_SHORT, ObjectShortMap::toImmutable);
        add(ObjectCharMap.class, TypeHandlerPair.OBJECT_CHAR);
        add(MutableObjectCharMap.class, TypeHandlerPair.OBJECT_CHAR);
        add(ImmutableObjectCharMap.class, TypeHandlerPair.OBJECT_CHAR, ObjectCharMap::toImmutable);
        add(ObjectIntMap.class, TypeHandlerPair.OBJECT_INT);
        add(MutableObjectIntMap.class, TypeHandlerPair.OBJECT_INT);
        add(ImmutableObjectIntMap.class, TypeHandlerPair.OBJECT_INT, ObjectIntMap::toImmutable);
        add(ObjectFloatMap.class, TypeHandlerPair.OBJECT_FLOAT);
        add(MutableObjectFloatMap.class, TypeHandlerPair.OBJECT_FLOAT);
        add(ImmutableObjectFloatMap.class, TypeHandlerPair.OBJECT_FLOAT, ObjectFloatMap::toImmutable);
        add(ObjectLongMap.class, TypeHandlerPair.OBJECT_LONG);
        add(MutableObjectLongMap.class, TypeHandlerPair.OBJECT_LONG);
        add(ImmutableObjectLongMap.class, TypeHandlerPair.OBJECT_LONG, ObjectLongMap::toImmutable);
        add(ObjectDoubleMap.class, TypeHandlerPair.OBJECT_DOUBLE);
        add(MutableObjectDoubleMap.class, TypeHandlerPair.OBJECT_DOUBLE);
        add(ImmutableObjectDoubleMap.class, TypeHandlerPair.OBJECT_DOUBLE, ObjectDoubleMap::toImmutable);

        add(ByteObjectMap.class, TypeHandlerPair.BYTE_OBJECT);
        add(MutableByteObjectMap.class, TypeHandlerPair.BYTE_OBJECT);
        add(ImmutableByteObjectMap.class, TypeHandlerPair.BYTE_OBJECT, ByteObjectMap::toImmutable);
        add(ShortObjectMap.class, TypeHandlerPair.SHORT_OBJECT);
        add(MutableShortObjectMap.class, TypeHandlerPair.SHORT_OBJECT);
        add(ImmutableShortObjectMap.class, TypeHandlerPair.SHORT_OBJECT, ShortObjectMap::toImmutable);
        add(CharObjectMap.class, TypeHandlerPair.CHAR_OBJECT);
        add(MutableCharObjectMap.class, TypeHandlerPair.CHAR_OBJECT);
        add(ImmutableCharObjectMap.class, TypeHandlerPair.CHAR_OBJECT, CharObjectMap::toImmutable);
        add(IntObjectMap.class, TypeHandlerPair.INT_OBJECT);
        add(MutableIntObjectMap.class, TypeHandlerPair.INT_OBJECT);
        add(ImmutableIntObjectMap.class, TypeHandlerPair.INT_OBJECT, IntObjectMap::toImmutable);
        add(FloatObjectMap.class, TypeHandlerPair.FLOAT_OBJECT);
        add(MutableFloatObjectMap.class, TypeHandlerPair.FLOAT_OBJECT);
        add(ImmutableFloatObjectMap.class, TypeHandlerPair.FLOAT_OBJECT, FloatObjectMap::toImmutable);
        add(LongObjectMap.class, TypeHandlerPair.LONG_OBJECT);
        add(MutableLongObjectMap.class, TypeHandlerPair.LONG_OBJECT);
        add(ImmutableLongObjectMap.class, TypeHandlerPair.LONG_OBJECT, LongObjectMap::toImmutable);
        add(DoubleObjectMap.class, TypeHandlerPair.DOUBLE_OBJECT);
        add(MutableDoubleObjectMap.class, TypeHandlerPair.DOUBLE_OBJECT);
        add(ImmutableDoubleObjectMap.class, TypeHandlerPair.DOUBLE_OBJECT, DoubleObjectMap::toImmutable);

        add(ByteBooleanMap.class, TypeHandlerPair.BYTE_BOOLEAN);
        add(MutableByteBooleanMap.class, TypeHandlerPair.BYTE_BOOLEAN);
        add(ImmutableByteBooleanMap.class, TypeHandlerPair.BYTE_BOOLEAN, ByteBooleanMap::toImmutable);
        add(ByteByteMap.class, TypeHandlerPair.BYTE_BYTE);
        add(MutableByteByteMap.class, TypeHandlerPair.BYTE_BYTE);
        add(ImmutableByteByteMap.class, TypeHandlerPair.BYTE_BYTE, ByteByteMap::toImmutable);
        add(ByteShortMap.class, TypeHandlerPair.BYTE_SHORT);
        add(MutableByteShortMap.class, TypeHandlerPair.BYTE_SHORT);
        add(ImmutableByteShortMap.class, TypeHandlerPair.BYTE_SHORT, ByteShortMap::toImmutable);
        add(ByteCharMap.class, TypeHandlerPair.BYTE_CHAR);
        add(MutableByteCharMap.class, TypeHandlerPair.BYTE_CHAR);
        add(ImmutableByteCharMap.class, TypeHandlerPair.BYTE_CHAR, ByteCharMap::toImmutable);
        add(ByteIntMap.class, TypeHandlerPair.BYTE_INT);
        add(MutableByteIntMap.class, TypeHandlerPair.BYTE_INT);
        add(ImmutableByteIntMap.class, TypeHandlerPair.BYTE_INT, ByteIntMap::toImmutable);
        add(ByteFloatMap.class, TypeHandlerPair.BYTE_FLOAT);
        add(MutableByteFloatMap.class, TypeHandlerPair.BYTE_FLOAT);
        add(ImmutableByteFloatMap.class, TypeHandlerPair.BYTE_FLOAT, ByteFloatMap::toImmutable);
        add(ByteLongMap.class, TypeHandlerPair.BYTE_LONG);
        add(MutableByteLongMap.class, TypeHandlerPair.BYTE_LONG);
        add(ImmutableByteLongMap.class, TypeHandlerPair.BYTE_LONG, ByteLongMap::toImmutable);
        add(ByteDoubleMap.class, TypeHandlerPair.BYTE_DOUBLE);
        add(MutableByteDoubleMap.class, TypeHandlerPair.BYTE_DOUBLE);
        add(ImmutableByteDoubleMap.class, TypeHandlerPair.BYTE_DOUBLE, ByteDoubleMap::toImmutable);
        add(ShortBooleanMap.class, TypeHandlerPair.SHORT_BOOLEAN);
        add(MutableShortBooleanMap.class, TypeHandlerPair.SHORT_BOOLEAN);
        add(ImmutableShortBooleanMap.class, TypeHandlerPair.SHORT_BOOLEAN, ShortBooleanMap::toImmutable);
        add(ShortByteMap.class, TypeHandlerPair.SHORT_BYTE);
        add(MutableShortByteMap.class, TypeHandlerPair.SHORT_BYTE);
        add(ImmutableShortByteMap.class, TypeHandlerPair.SHORT_BYTE, ShortByteMap::toImmutable);
        add(ShortShortMap.class, TypeHandlerPair.SHORT_SHORT);
        add(MutableShortShortMap.class, TypeHandlerPair.SHORT_SHORT);
        add(ImmutableShortShortMap.class, TypeHandlerPair.SHORT_SHORT, ShortShortMap::toImmutable);
        add(ShortCharMap.class, TypeHandlerPair.SHORT_CHAR);
        add(MutableShortCharMap.class, TypeHandlerPair.SHORT_CHAR);
        add(ImmutableShortCharMap.class, TypeHandlerPair.SHORT_CHAR, ShortCharMap::toImmutable);
        add(ShortIntMap.class, TypeHandlerPair.SHORT_INT);
        add(MutableShortIntMap.class, TypeHandlerPair.SHORT_INT);
        add(ImmutableShortIntMap.class, TypeHandlerPair.SHORT_INT, ShortIntMap::toImmutable);
        add(ShortFloatMap.class, TypeHandlerPair.SHORT_FLOAT);
        add(MutableShortFloatMap.class, TypeHandlerPair.SHORT_FLOAT);
        add(ImmutableShortFloatMap.class, TypeHandlerPair.SHORT_FLOAT, ShortFloatMap::toImmutable);
        add(ShortLongMap.class, TypeHandlerPair.SHORT_LONG);
        add(MutableShortLongMap.class, TypeHandlerPair.SHORT_LONG);
        add(ImmutableShortLongMap.class, TypeHandlerPair.SHORT_LONG, ShortLongMap::toImmutable);
        add(ShortDoubleMap.class, TypeHandlerPair.SHORT_DOUBLE);
        add(MutableShortDoubleMap.class, TypeHandlerPair.SHORT_DOUBLE);
        add(ImmutableShortDoubleMap.class, TypeHandlerPair.SHORT_DOUBLE, ShortDoubleMap::toImmutable);
        add(CharBooleanMap.class, TypeHandlerPair.CHAR_BOOLEAN);
        add(MutableCharBooleanMap.class, TypeHandlerPair.CHAR_BOOLEAN);
        add(ImmutableCharBooleanMap.class, TypeHandlerPair.CHAR_BOOLEAN, CharBooleanMap::toImmutable);
        add(CharByteMap.class, TypeHandlerPair.CHAR_BYTE);
        add(MutableCharByteMap.class, TypeHandlerPair.CHAR_BYTE);
        add(ImmutableCharByteMap.class, TypeHandlerPair.CHAR_BYTE, CharByteMap::toImmutable);
        add(CharShortMap.class, TypeHandlerPair.CHAR_SHORT);
        add(MutableCharShortMap.class, TypeHandlerPair.CHAR_SHORT);
        add(ImmutableCharShortMap.class, TypeHandlerPair.CHAR_SHORT, CharShortMap::toImmutable);
        add(CharCharMap.class, TypeHandlerPair.CHAR_CHAR);
        add(MutableCharCharMap.class, TypeHandlerPair.CHAR_CHAR);
        add(ImmutableCharCharMap.class, TypeHandlerPair.CHAR_CHAR, CharCharMap::toImmutable);
        add(CharIntMap.class, TypeHandlerPair.CHAR_INT);
        add(MutableCharIntMap.class, TypeHandlerPair.CHAR_INT);
        add(ImmutableCharIntMap.class, TypeHandlerPair.CHAR_INT, CharIntMap::toImmutable);
        add(CharFloatMap.class, TypeHandlerPair.CHAR_FLOAT);
        add(MutableCharFloatMap.class, TypeHandlerPair.CHAR_FLOAT);
        add(ImmutableCharFloatMap.class, TypeHandlerPair.CHAR_FLOAT, CharFloatMap::toImmutable);
        add(CharLongMap.class, TypeHandlerPair.CHAR_LONG);
        add(MutableCharLongMap.class, TypeHandlerPair.CHAR_LONG);
        add(ImmutableCharLongMap.class, TypeHandlerPair.CHAR_LONG, CharLongMap::toImmutable);
        add(CharDoubleMap.class, TypeHandlerPair.CHAR_DOUBLE);
        add(MutableCharDoubleMap.class, TypeHandlerPair.CHAR_DOUBLE);
        add(ImmutableCharDoubleMap.class, TypeHandlerPair.CHAR_DOUBLE, CharDoubleMap::toImmutable);
        add(IntBooleanMap.class, TypeHandlerPair.INT_BOOLEAN);
        add(MutableIntBooleanMap.class, TypeHandlerPair.INT_BOOLEAN);
        add(ImmutableIntBooleanMap.class, TypeHandlerPair.INT_BOOLEAN, IntBooleanMap::toImmutable);
        add(IntByteMap.class, TypeHandlerPair.INT_BYTE);
        add(MutableIntByteMap.class, TypeHandlerPair.INT_BYTE);
        add(ImmutableIntByteMap.class, TypeHandlerPair.INT_BYTE, IntByteMap::toImmutable);
        add(IntShortMap.class, TypeHandlerPair.INT_SHORT);
        add(MutableIntShortMap.class, TypeHandlerPair.INT_SHORT);
        add(ImmutableIntShortMap.class, TypeHandlerPair.INT_SHORT, IntShortMap::toImmutable);
        add(IntCharMap.class, TypeHandlerPair.INT_CHAR);
        add(MutableIntCharMap.class, TypeHandlerPair.INT_CHAR);
        add(ImmutableIntCharMap.class, TypeHandlerPair.INT_CHAR, IntCharMap::toImmutable);
        add(IntIntMap.class, TypeHandlerPair.INT_INT);
        add(MutableIntIntMap.class, TypeHandlerPair.INT_INT);
        add(ImmutableIntIntMap.class, TypeHandlerPair.INT_INT, IntIntMap::toImmutable);
        add(IntFloatMap.class, TypeHandlerPair.INT_FLOAT);
        add(MutableIntFloatMap.class, TypeHandlerPair.INT_FLOAT);
        add(ImmutableIntFloatMap.class, TypeHandlerPair.INT_FLOAT, IntFloatMap::toImmutable);
        add(IntLongMap.class, TypeHandlerPair.INT_LONG);
        add(MutableIntLongMap.class, TypeHandlerPair.INT_LONG);
        add(ImmutableIntLongMap.class, TypeHandlerPair.INT_LONG, IntLongMap::toImmutable);
        add(IntDoubleMap.class, TypeHandlerPair.INT_DOUBLE);
        add(MutableIntDoubleMap.class, TypeHandlerPair.INT_DOUBLE);
        add(ImmutableIntDoubleMap.class, TypeHandlerPair.INT_DOUBLE, IntDoubleMap::toImmutable);
        add(FloatBooleanMap.class, TypeHandlerPair.FLOAT_BOOLEAN);
        add(MutableFloatBooleanMap.class, TypeHandlerPair.FLOAT_BOOLEAN);
        add(ImmutableFloatBooleanMap.class, TypeHandlerPair.FLOAT_BOOLEAN, FloatBooleanMap::toImmutable);
        add(FloatByteMap.class, TypeHandlerPair.FLOAT_BYTE);
        add(MutableFloatByteMap.class, TypeHandlerPair.FLOAT_BYTE);
        add(ImmutableFloatByteMap.class, TypeHandlerPair.FLOAT_BYTE, FloatByteMap::toImmutable);
        add(FloatShortMap.class, TypeHandlerPair.FLOAT_SHORT);
        add(MutableFloatShortMap.class, TypeHandlerPair.FLOAT_SHORT);
        add(ImmutableFloatShortMap.class, TypeHandlerPair.FLOAT_SHORT, FloatShortMap::toImmutable);
        add(FloatCharMap.class, TypeHandlerPair.FLOAT_CHAR);
        add(MutableFloatCharMap.class, TypeHandlerPair.FLOAT_CHAR);
        add(ImmutableFloatCharMap.class, TypeHandlerPair.FLOAT_CHAR, FloatCharMap::toImmutable);
        add(FloatIntMap.class, TypeHandlerPair.FLOAT_INT);
        add(MutableFloatIntMap.class, TypeHandlerPair.FLOAT_INT);
        add(ImmutableFloatIntMap.class, TypeHandlerPair.FLOAT_INT, FloatIntMap::toImmutable);
        add(FloatFloatMap.class, TypeHandlerPair.FLOAT_FLOAT);
        add(MutableFloatFloatMap.class, TypeHandlerPair.FLOAT_FLOAT);
        add(ImmutableFloatFloatMap.class, TypeHandlerPair.FLOAT_FLOAT, FloatFloatMap::toImmutable);
        add(FloatLongMap.class, TypeHandlerPair.FLOAT_LONG);
        add(MutableFloatLongMap.class, TypeHandlerPair.FLOAT_LONG);
        add(ImmutableFloatLongMap.class, TypeHandlerPair.FLOAT_LONG, FloatLongMap::toImmutable);
        add(FloatDoubleMap.class, TypeHandlerPair.FLOAT_DOUBLE);
        add(MutableFloatDoubleMap.class, TypeHandlerPair.FLOAT_DOUBLE);
        add(ImmutableFloatDoubleMap.class, TypeHandlerPair.FLOAT_DOUBLE, FloatDoubleMap::toImmutable);
        add(LongBooleanMap.class, TypeHandlerPair.LONG_BOOLEAN);
        add(MutableLongBooleanMap.class, TypeHandlerPair.LONG_BOOLEAN);
        add(ImmutableLongBooleanMap.class, TypeHandlerPair.LONG_BOOLEAN, LongBooleanMap::toImmutable);
        add(LongByteMap.class, TypeHandlerPair.LONG_BYTE);
        add(MutableLongByteMap.class, TypeHandlerPair.LONG_BYTE);
        add(ImmutableLongByteMap.class, TypeHandlerPair.LONG_BYTE, LongByteMap::toImmutable);
        add(LongShortMap.class, TypeHandlerPair.LONG_SHORT);
        add(MutableLongShortMap.class, TypeHandlerPair.LONG_SHORT);
        add(ImmutableLongShortMap.class, TypeHandlerPair.LONG_SHORT, LongShortMap::toImmutable);
        add(LongCharMap.class, TypeHandlerPair.LONG_CHAR);
        add(MutableLongCharMap.class, TypeHandlerPair.LONG_CHAR);
        add(ImmutableLongCharMap.class, TypeHandlerPair.LONG_CHAR, LongCharMap::toImmutable);
        add(LongIntMap.class, TypeHandlerPair.LONG_INT);
        add(MutableLongIntMap.class, TypeHandlerPair.LONG_INT);
        add(ImmutableLongIntMap.class, TypeHandlerPair.LONG_INT, LongIntMap::toImmutable);
        add(LongFloatMap.class, TypeHandlerPair.LONG_FLOAT);
        add(MutableLongFloatMap.class, TypeHandlerPair.LONG_FLOAT);
        add(ImmutableLongFloatMap.class, TypeHandlerPair.LONG_FLOAT, LongFloatMap::toImmutable);
        add(LongLongMap.class, TypeHandlerPair.LONG_LONG);
        add(MutableLongLongMap.class, TypeHandlerPair.LONG_LONG);
        add(ImmutableLongLongMap.class, TypeHandlerPair.LONG_LONG, LongLongMap::toImmutable);
        add(LongDoubleMap.class, TypeHandlerPair.LONG_DOUBLE);
        add(MutableLongDoubleMap.class, TypeHandlerPair.LONG_DOUBLE);
        add(ImmutableLongDoubleMap.class, TypeHandlerPair.LONG_DOUBLE, LongDoubleMap::toImmutable);
        add(DoubleBooleanMap.class, TypeHandlerPair.DOUBLE_BOOLEAN);
        add(MutableDoubleBooleanMap.class, TypeHandlerPair.DOUBLE_BOOLEAN);
        add(ImmutableDoubleBooleanMap.class, TypeHandlerPair.DOUBLE_BOOLEAN, DoubleBooleanMap::toImmutable);
        add(DoubleByteMap.class, TypeHandlerPair.DOUBLE_BYTE);
        add(MutableDoubleByteMap.class, TypeHandlerPair.DOUBLE_BYTE);
        add(ImmutableDoubleByteMap.class, TypeHandlerPair.DOUBLE_BYTE, DoubleByteMap::toImmutable);
        add(DoubleShortMap.class, TypeHandlerPair.DOUBLE_SHORT);
        add(MutableDoubleShortMap.class, TypeHandlerPair.DOUBLE_SHORT);
        add(ImmutableDoubleShortMap.class, TypeHandlerPair.DOUBLE_SHORT, DoubleShortMap::toImmutable);
        add(DoubleCharMap.class, TypeHandlerPair.DOUBLE_CHAR);
        add(MutableDoubleCharMap.class, TypeHandlerPair.DOUBLE_CHAR);
        add(ImmutableDoubleCharMap.class, TypeHandlerPair.DOUBLE_CHAR, DoubleCharMap::toImmutable);
        add(DoubleIntMap.class, TypeHandlerPair.DOUBLE_INT);
        add(MutableDoubleIntMap.class, TypeHandlerPair.DOUBLE_INT);
        add(ImmutableDoubleIntMap.class, TypeHandlerPair.DOUBLE_INT, DoubleIntMap::toImmutable);
        add(DoubleFloatMap.class, TypeHandlerPair.DOUBLE_FLOAT);
        add(MutableDoubleFloatMap.class, TypeHandlerPair.DOUBLE_FLOAT);
        add(ImmutableDoubleFloatMap.class, TypeHandlerPair.DOUBLE_FLOAT, DoubleFloatMap::toImmutable);
        add(DoubleLongMap.class, TypeHandlerPair.DOUBLE_LONG);
        add(MutableDoubleLongMap.class, TypeHandlerPair.DOUBLE_LONG);
        add(ImmutableDoubleLongMap.class, TypeHandlerPair.DOUBLE_LONG, DoubleLongMap::toImmutable);
        add(DoubleDoubleMap.class, TypeHandlerPair.DOUBLE_DOUBLE);
        add(MutableDoubleDoubleMap.class, TypeHandlerPair.DOUBLE_DOUBLE);
        add(ImmutableDoubleDoubleMap.class, TypeHandlerPair.DOUBLE_DOUBLE, DoubleDoubleMap::toImmutable);
    }
}
