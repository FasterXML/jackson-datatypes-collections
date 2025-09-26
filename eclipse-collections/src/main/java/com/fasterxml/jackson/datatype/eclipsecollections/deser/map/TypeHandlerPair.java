package com.fasterxml.jackson.datatype.eclipsecollections.deser.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import java.io.IOException;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
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
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.SortedMaps;
import org.eclipse.collections.impl.factory.primitive.ByteBooleanMaps;
import org.eclipse.collections.impl.factory.primitive.ByteByteMaps;
import org.eclipse.collections.impl.factory.primitive.ByteCharMaps;
import org.eclipse.collections.impl.factory.primitive.ByteDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.ByteFloatMaps;
import org.eclipse.collections.impl.factory.primitive.ByteIntMaps;
import org.eclipse.collections.impl.factory.primitive.ByteLongMaps;
import org.eclipse.collections.impl.factory.primitive.ByteObjectMaps;
import org.eclipse.collections.impl.factory.primitive.ByteShortMaps;
import org.eclipse.collections.impl.factory.primitive.CharBooleanMaps;
import org.eclipse.collections.impl.factory.primitive.CharByteMaps;
import org.eclipse.collections.impl.factory.primitive.CharCharMaps;
import org.eclipse.collections.impl.factory.primitive.CharDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.CharFloatMaps;
import org.eclipse.collections.impl.factory.primitive.CharIntMaps;
import org.eclipse.collections.impl.factory.primitive.CharLongMaps;
import org.eclipse.collections.impl.factory.primitive.CharObjectMaps;
import org.eclipse.collections.impl.factory.primitive.CharShortMaps;
import org.eclipse.collections.impl.factory.primitive.DoubleBooleanMaps;
import org.eclipse.collections.impl.factory.primitive.DoubleByteMaps;
import org.eclipse.collections.impl.factory.primitive.DoubleCharMaps;
import org.eclipse.collections.impl.factory.primitive.DoubleDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.DoubleFloatMaps;
import org.eclipse.collections.impl.factory.primitive.DoubleIntMaps;
import org.eclipse.collections.impl.factory.primitive.DoubleLongMaps;
import org.eclipse.collections.impl.factory.primitive.DoubleObjectMaps;
import org.eclipse.collections.impl.factory.primitive.DoubleShortMaps;
import org.eclipse.collections.impl.factory.primitive.FloatBooleanMaps;
import org.eclipse.collections.impl.factory.primitive.FloatByteMaps;
import org.eclipse.collections.impl.factory.primitive.FloatCharMaps;
import org.eclipse.collections.impl.factory.primitive.FloatDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.FloatFloatMaps;
import org.eclipse.collections.impl.factory.primitive.FloatIntMaps;
import org.eclipse.collections.impl.factory.primitive.FloatLongMaps;
import org.eclipse.collections.impl.factory.primitive.FloatObjectMaps;
import org.eclipse.collections.impl.factory.primitive.FloatShortMaps;
import org.eclipse.collections.impl.factory.primitive.IntBooleanMaps;
import org.eclipse.collections.impl.factory.primitive.IntByteMaps;
import org.eclipse.collections.impl.factory.primitive.IntCharMaps;
import org.eclipse.collections.impl.factory.primitive.IntDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.IntFloatMaps;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;
import org.eclipse.collections.impl.factory.primitive.IntLongMaps;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.IntShortMaps;
import org.eclipse.collections.impl.factory.primitive.LongBooleanMaps;
import org.eclipse.collections.impl.factory.primitive.LongByteMaps;
import org.eclipse.collections.impl.factory.primitive.LongCharMaps;
import org.eclipse.collections.impl.factory.primitive.LongDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.LongFloatMaps;
import org.eclipse.collections.impl.factory.primitive.LongIntMaps;
import org.eclipse.collections.impl.factory.primitive.LongLongMaps;
import org.eclipse.collections.impl.factory.primitive.LongObjectMaps;
import org.eclipse.collections.impl.factory.primitive.LongShortMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectBooleanMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectByteMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectCharMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectFloatMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectLongMaps;
import org.eclipse.collections.impl.factory.primitive.ObjectShortMaps;
import org.eclipse.collections.impl.factory.primitive.ShortBooleanMaps;
import org.eclipse.collections.impl.factory.primitive.ShortByteMaps;
import org.eclipse.collections.impl.factory.primitive.ShortCharMaps;
import org.eclipse.collections.impl.factory.primitive.ShortDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.ShortFloatMaps;
import org.eclipse.collections.impl.factory.primitive.ShortIntMaps;
import org.eclipse.collections.impl.factory.primitive.ShortLongMaps;
import org.eclipse.collections.impl.factory.primitive.ShortObjectMaps;
import org.eclipse.collections.impl.factory.primitive.ShortShortMaps;

/**
 * @author yawkat
 */
interface TypeHandlerPair<M, K extends KeyHandler<K>, V extends ValueHandler<V>> {
    K keyHandler(JavaType type);

    V valueHandler(JavaType type);

    M createEmpty();

    void add(M target, K kh, V vh, DeserializationContext ctx, String k, JsonParser v) throws IOException;

    TypeHandlerPair<MutableMap<Object, Object>, RefKeyHandler, RefValueHandler> OBJECT_OBJECT =
            new TypeHandlerPair<MutableMap<Object, Object>, RefKeyHandler, RefValueHandler>() {
                @Override
                public RefKeyHandler keyHandler(JavaType type) {
                    return new RefKeyHandler(type, null);
                }

                @Override
                public RefValueHandler valueHandler(JavaType type) {
                    return new RefValueHandler(type, null, null);
                }

                @Override
                public MutableMap<Object, Object> createEmpty() {
                    return Maps.mutable.empty();
                }

                @Override
                public void add(
                        MutableMap<Object, Object> target,
                        RefKeyHandler kh, RefValueHandler vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    // @since 2.21
    TypeHandlerPair<MutableSortedMap<Comparable<?>, Object>, RefKeyHandler, RefValueHandler> COMPARABLE_OBJECT =
            new TypeHandlerPair<MutableSortedMap<Comparable<?>, Object>, RefKeyHandler, RefValueHandler>() {
                @Override
                public RefKeyHandler keyHandler(JavaType type) {
                    return new RefKeyHandler(type, null);
                }

                @Override
                public RefValueHandler valueHandler(JavaType type) {
                    return new RefValueHandler(type, null, null);
                }

                @Override
                public MutableSortedMap<Comparable<?>, Object> createEmpty() {
                    return SortedMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableSortedMap<Comparable<?>, Object> target,
                        RefKeyHandler kh, RefValueHandler vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put((Comparable<?>) kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    //region Object -> Primitive
    TypeHandlerPair<MutableObjectBooleanMap<Object>, RefKeyHandler, PrimitiveKVHandler.Boolean> OBJECT_BOOLEAN =
            new TypeHandlerPair<MutableObjectBooleanMap<Object>, RefKeyHandler, PrimitiveKVHandler.Boolean>() {
                @Override
                public RefKeyHandler keyHandler(JavaType type) {
                    return new RefKeyHandler(type, null);
                }

                @Override
                public PrimitiveKVHandler.Boolean valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Boolean.INSTANCE;
                }

                @Override
                public MutableObjectBooleanMap<Object> createEmpty() {
                    return ObjectBooleanMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableObjectBooleanMap<Object> target,
                        RefKeyHandler kh, PrimitiveKVHandler.Boolean vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableObjectByteMap<Object>, RefKeyHandler, PrimitiveKVHandler.Byte> OBJECT_BYTE =
            new TypeHandlerPair<MutableObjectByteMap<Object>, RefKeyHandler, PrimitiveKVHandler.Byte>() {
                @Override
                public RefKeyHandler keyHandler(JavaType type) {
                    return new RefKeyHandler(type, null);
                }

                @Override
                public PrimitiveKVHandler.Byte valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public MutableObjectByteMap<Object> createEmpty() {
                    return ObjectByteMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableObjectByteMap<Object> target,
                        RefKeyHandler kh, PrimitiveKVHandler.Byte vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableObjectShortMap<Object>, RefKeyHandler, PrimitiveKVHandler.Short> OBJECT_SHORT =
            new TypeHandlerPair<MutableObjectShortMap<Object>, RefKeyHandler, PrimitiveKVHandler.Short>() {
                @Override
                public RefKeyHandler keyHandler(JavaType type) {
                    return new RefKeyHandler(type, null);
                }

                @Override
                public PrimitiveKVHandler.Short valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public MutableObjectShortMap<Object> createEmpty() {
                    return ObjectShortMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableObjectShortMap<Object> target,
                        RefKeyHandler kh, PrimitiveKVHandler.Short vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableObjectCharMap<Object>, RefKeyHandler, PrimitiveKVHandler.Char> OBJECT_CHAR =
            new TypeHandlerPair<MutableObjectCharMap<Object>, RefKeyHandler, PrimitiveKVHandler.Char>() {
                @Override
                public RefKeyHandler keyHandler(JavaType type) {
                    return new RefKeyHandler(type, null);
                }

                @Override
                public PrimitiveKVHandler.Char valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public MutableObjectCharMap<Object> createEmpty() {
                    return ObjectCharMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableObjectCharMap<Object> target,
                        RefKeyHandler kh, PrimitiveKVHandler.Char vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableObjectIntMap<Object>, RefKeyHandler, PrimitiveKVHandler.Int> OBJECT_INT =
            new TypeHandlerPair<MutableObjectIntMap<Object>, RefKeyHandler, PrimitiveKVHandler.Int>() {
                @Override
                public RefKeyHandler keyHandler(JavaType type) {
                    return new RefKeyHandler(type, null);
                }

                @Override
                public PrimitiveKVHandler.Int valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public MutableObjectIntMap<Object> createEmpty() {
                    return ObjectIntMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableObjectIntMap<Object> target,
                        RefKeyHandler kh, PrimitiveKVHandler.Int vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableObjectFloatMap<Object>, RefKeyHandler, PrimitiveKVHandler.Float> OBJECT_FLOAT =
            new TypeHandlerPair<MutableObjectFloatMap<Object>, RefKeyHandler, PrimitiveKVHandler.Float>() {
                @Override
                public RefKeyHandler keyHandler(JavaType type) {
                    return new RefKeyHandler(type, null);
                }

                @Override
                public PrimitiveKVHandler.Float valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public MutableObjectFloatMap<Object> createEmpty() {
                    return ObjectFloatMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableObjectFloatMap<Object> target,
                        RefKeyHandler kh, PrimitiveKVHandler.Float vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableObjectLongMap<Object>, RefKeyHandler, PrimitiveKVHandler.Long> OBJECT_LONG =
            new TypeHandlerPair<MutableObjectLongMap<Object>, RefKeyHandler, PrimitiveKVHandler.Long>() {
                @Override
                public RefKeyHandler keyHandler(JavaType type) {
                    return new RefKeyHandler(type, null);
                }

                @Override
                public PrimitiveKVHandler.Long valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public MutableObjectLongMap<Object> createEmpty() {
                    return ObjectLongMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableObjectLongMap<Object> target,
                        RefKeyHandler kh, PrimitiveKVHandler.Long vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableObjectDoubleMap<Object>, RefKeyHandler, PrimitiveKVHandler.Double> OBJECT_DOUBLE =
            new TypeHandlerPair<MutableObjectDoubleMap<Object>, RefKeyHandler, PrimitiveKVHandler.Double>() {
                @Override
                public RefKeyHandler keyHandler(JavaType type) {
                    return new RefKeyHandler(type, null);
                }

                @Override
                public PrimitiveKVHandler.Double valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public MutableObjectDoubleMap<Object> createEmpty() {
                    return ObjectDoubleMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableObjectDoubleMap<Object> target,
                        RefKeyHandler kh, PrimitiveKVHandler.Double vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    //endregion

    //region Primitive -> Object
    TypeHandlerPair<MutableByteObjectMap<Object>, PrimitiveKVHandler.Byte, RefValueHandler> BYTE_OBJECT =
            new TypeHandlerPair<MutableByteObjectMap<Object>, PrimitiveKVHandler.Byte, RefValueHandler>() {
                @Override
                public PrimitiveKVHandler.Byte keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public RefValueHandler valueHandler(JavaType type) {
                    return new RefValueHandler(type, null, null);
                }

                @Override
                public MutableByteObjectMap<Object> createEmpty() {
                    return ByteObjectMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableByteObjectMap<Object> target,
                        PrimitiveKVHandler.Byte kh, RefValueHandler vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableShortObjectMap<Object>, PrimitiveKVHandler.Short, RefValueHandler> SHORT_OBJECT =
            new TypeHandlerPair<MutableShortObjectMap<Object>, PrimitiveKVHandler.Short, RefValueHandler>() {
                @Override
                public PrimitiveKVHandler.Short keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public RefValueHandler valueHandler(JavaType type) {
                    return new RefValueHandler(type, null, null);
                }

                @Override
                public MutableShortObjectMap<Object> createEmpty() {
                    return ShortObjectMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableShortObjectMap<Object> target,
                        PrimitiveKVHandler.Short kh, RefValueHandler vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableCharObjectMap<Object>, PrimitiveKVHandler.Char, RefValueHandler> CHAR_OBJECT =
            new TypeHandlerPair<MutableCharObjectMap<Object>, PrimitiveKVHandler.Char, RefValueHandler>() {
                @Override
                public PrimitiveKVHandler.Char keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public RefValueHandler valueHandler(JavaType type) {
                    return new RefValueHandler(type, null, null);
                }

                @Override
                public MutableCharObjectMap<Object> createEmpty() {
                    return CharObjectMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableCharObjectMap<Object> target,
                        PrimitiveKVHandler.Char kh, RefValueHandler vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableIntObjectMap<Object>, PrimitiveKVHandler.Int, RefValueHandler> INT_OBJECT =
            new TypeHandlerPair<MutableIntObjectMap<Object>, PrimitiveKVHandler.Int, RefValueHandler>() {
                @Override
                public PrimitiveKVHandler.Int keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public RefValueHandler valueHandler(JavaType type) {
                    return new RefValueHandler(type, null, null);
                }

                @Override
                public MutableIntObjectMap<Object> createEmpty() {
                    return IntObjectMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableIntObjectMap<Object> target,
                        PrimitiveKVHandler.Int kh, RefValueHandler vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableFloatObjectMap<Object>, PrimitiveKVHandler.Float, RefValueHandler> FLOAT_OBJECT =
            new TypeHandlerPair<MutableFloatObjectMap<Object>, PrimitiveKVHandler.Float, RefValueHandler>() {
                @Override
                public PrimitiveKVHandler.Float keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public RefValueHandler valueHandler(JavaType type) {
                    return new RefValueHandler(type, null, null);
                }

                @Override
                public MutableFloatObjectMap<Object> createEmpty() {
                    return FloatObjectMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableFloatObjectMap<Object> target,
                        PrimitiveKVHandler.Float kh, RefValueHandler vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableLongObjectMap<Object>, PrimitiveKVHandler.Long, RefValueHandler> LONG_OBJECT =
            new TypeHandlerPair<MutableLongObjectMap<Object>, PrimitiveKVHandler.Long, RefValueHandler>() {
                @Override
                public PrimitiveKVHandler.Long keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public RefValueHandler valueHandler(JavaType type) {
                    return new RefValueHandler(type, null, null);
                }

                @Override
                public MutableLongObjectMap<Object> createEmpty() {
                    return LongObjectMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableLongObjectMap<Object> target,
                        PrimitiveKVHandler.Long kh, RefValueHandler vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    TypeHandlerPair<MutableDoubleObjectMap<Object>, PrimitiveKVHandler.Double, RefValueHandler> DOUBLE_OBJECT =
            new TypeHandlerPair<MutableDoubleObjectMap<Object>, PrimitiveKVHandler.Double, RefValueHandler>() {
                @Override
                public PrimitiveKVHandler.Double keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public RefValueHandler valueHandler(JavaType type) {
                    return new RefValueHandler(type, null, null);
                }

                @Override
                public MutableDoubleObjectMap<Object> createEmpty() {
                    return DoubleObjectMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableDoubleObjectMap<Object> target,
                        PrimitiveKVHandler.Double kh, RefValueHandler vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    //endregion

    //region Byte -> Primitive

    TypeHandlerPair<MutableByteBooleanMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Boolean> BYTE_BOOLEAN =
            new TypeHandlerPair<MutableByteBooleanMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Boolean>() {
                @Override
                public PrimitiveKVHandler.Byte keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Boolean valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Boolean.INSTANCE;
                }

                @Override
                public MutableByteBooleanMap createEmpty() {
                    return ByteBooleanMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableByteBooleanMap target,
                        PrimitiveKVHandler.Byte kh, PrimitiveKVHandler.Boolean vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableByteByteMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Byte> BYTE_BYTE =
            new TypeHandlerPair<MutableByteByteMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Byte>() {
                @Override
                public PrimitiveKVHandler.Byte keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Byte valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public MutableByteByteMap createEmpty() {
                    return ByteByteMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableByteByteMap target,
                        PrimitiveKVHandler.Byte kh, PrimitiveKVHandler.Byte vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableByteShortMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Short> BYTE_SHORT =
            new TypeHandlerPair<MutableByteShortMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Short>() {
                @Override
                public PrimitiveKVHandler.Byte keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Short valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public MutableByteShortMap createEmpty() {
                    return ByteShortMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableByteShortMap target,
                        PrimitiveKVHandler.Byte kh, PrimitiveKVHandler.Short vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableByteCharMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Char> BYTE_CHAR =
            new TypeHandlerPair<MutableByteCharMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Char>() {
                @Override
                public PrimitiveKVHandler.Byte keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Char valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public MutableByteCharMap createEmpty() {
                    return ByteCharMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableByteCharMap target,
                        PrimitiveKVHandler.Byte kh, PrimitiveKVHandler.Char vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableByteIntMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Int> BYTE_INT =
            new TypeHandlerPair<MutableByteIntMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Int>() {
                @Override
                public PrimitiveKVHandler.Byte keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Int valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public MutableByteIntMap createEmpty() {
                    return ByteIntMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableByteIntMap target,
                        PrimitiveKVHandler.Byte kh, PrimitiveKVHandler.Int vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableByteFloatMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Float> BYTE_FLOAT =
            new TypeHandlerPair<MutableByteFloatMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Float>() {
                @Override
                public PrimitiveKVHandler.Byte keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Float valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public MutableByteFloatMap createEmpty() {
                    return ByteFloatMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableByteFloatMap target,
                        PrimitiveKVHandler.Byte kh, PrimitiveKVHandler.Float vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableByteLongMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Long> BYTE_LONG =
            new TypeHandlerPair<MutableByteLongMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Long>() {
                @Override
                public PrimitiveKVHandler.Byte keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Long valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public MutableByteLongMap createEmpty() {
                    return ByteLongMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableByteLongMap target,
                        PrimitiveKVHandler.Byte kh, PrimitiveKVHandler.Long vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableByteDoubleMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Double> BYTE_DOUBLE =
            new TypeHandlerPair<MutableByteDoubleMap, PrimitiveKVHandler.Byte, PrimitiveKVHandler.Double>() {
                @Override
                public PrimitiveKVHandler.Byte keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Double valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public MutableByteDoubleMap createEmpty() {
                    return ByteDoubleMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableByteDoubleMap target,
                        PrimitiveKVHandler.Byte kh, PrimitiveKVHandler.Double vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    //endregion
    //region Short -> Primitive

    TypeHandlerPair<MutableShortBooleanMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Boolean> SHORT_BOOLEAN =
            new TypeHandlerPair<MutableShortBooleanMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Boolean>() {
                @Override
                public PrimitiveKVHandler.Short keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Boolean valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Boolean.INSTANCE;
                }

                @Override
                public MutableShortBooleanMap createEmpty() {
                    return ShortBooleanMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableShortBooleanMap target,
                        PrimitiveKVHandler.Short kh, PrimitiveKVHandler.Boolean vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableShortByteMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Byte> SHORT_BYTE =
            new TypeHandlerPair<MutableShortByteMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Byte>() {
                @Override
                public PrimitiveKVHandler.Short keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Byte valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public MutableShortByteMap createEmpty() {
                    return ShortByteMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableShortByteMap target,
                        PrimitiveKVHandler.Short kh, PrimitiveKVHandler.Byte vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableShortShortMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Short> SHORT_SHORT =
            new TypeHandlerPair<MutableShortShortMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Short>() {
                @Override
                public PrimitiveKVHandler.Short keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Short valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public MutableShortShortMap createEmpty() {
                    return ShortShortMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableShortShortMap target,
                        PrimitiveKVHandler.Short kh, PrimitiveKVHandler.Short vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableShortCharMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Char> SHORT_CHAR =
            new TypeHandlerPair<MutableShortCharMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Char>() {
                @Override
                public PrimitiveKVHandler.Short keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Char valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public MutableShortCharMap createEmpty() {
                    return ShortCharMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableShortCharMap target,
                        PrimitiveKVHandler.Short kh, PrimitiveKVHandler.Char vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableShortIntMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Int> SHORT_INT =
            new TypeHandlerPair<MutableShortIntMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Int>() {
                @Override
                public PrimitiveKVHandler.Short keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Int valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public MutableShortIntMap createEmpty() {
                    return ShortIntMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableShortIntMap target,
                        PrimitiveKVHandler.Short kh, PrimitiveKVHandler.Int vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableShortFloatMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Float> SHORT_FLOAT =
            new TypeHandlerPair<MutableShortFloatMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Float>() {
                @Override
                public PrimitiveKVHandler.Short keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Float valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public MutableShortFloatMap createEmpty() {
                    return ShortFloatMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableShortFloatMap target,
                        PrimitiveKVHandler.Short kh, PrimitiveKVHandler.Float vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableShortLongMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Long> SHORT_LONG =
            new TypeHandlerPair<MutableShortLongMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Long>() {
                @Override
                public PrimitiveKVHandler.Short keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Long valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public MutableShortLongMap createEmpty() {
                    return ShortLongMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableShortLongMap target,
                        PrimitiveKVHandler.Short kh, PrimitiveKVHandler.Long vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableShortDoubleMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Double> SHORT_DOUBLE =
            new TypeHandlerPair<MutableShortDoubleMap, PrimitiveKVHandler.Short, PrimitiveKVHandler.Double>() {
                @Override
                public PrimitiveKVHandler.Short keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Double valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public MutableShortDoubleMap createEmpty() {
                    return ShortDoubleMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableShortDoubleMap target,
                        PrimitiveKVHandler.Short kh, PrimitiveKVHandler.Double vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    //endregion
    //region Char -> Primitive

    TypeHandlerPair<MutableCharBooleanMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Boolean> CHAR_BOOLEAN =
            new TypeHandlerPair<MutableCharBooleanMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Boolean>() {
                @Override
                public PrimitiveKVHandler.Char keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Boolean valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Boolean.INSTANCE;
                }

                @Override
                public MutableCharBooleanMap createEmpty() {
                    return CharBooleanMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableCharBooleanMap target,
                        PrimitiveKVHandler.Char kh, PrimitiveKVHandler.Boolean vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableCharByteMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Byte> CHAR_BYTE =
            new TypeHandlerPair<MutableCharByteMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Byte>() {
                @Override
                public PrimitiveKVHandler.Char keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Byte valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public MutableCharByteMap createEmpty() {
                    return CharByteMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableCharByteMap target,
                        PrimitiveKVHandler.Char kh, PrimitiveKVHandler.Byte vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableCharShortMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Short> CHAR_SHORT =
            new TypeHandlerPair<MutableCharShortMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Short>() {
                @Override
                public PrimitiveKVHandler.Char keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Short valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public MutableCharShortMap createEmpty() {
                    return CharShortMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableCharShortMap target,
                        PrimitiveKVHandler.Char kh, PrimitiveKVHandler.Short vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableCharCharMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Char> CHAR_CHAR =
            new TypeHandlerPair<MutableCharCharMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Char>() {
                @Override
                public PrimitiveKVHandler.Char keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Char valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public MutableCharCharMap createEmpty() {
                    return CharCharMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableCharCharMap target,
                        PrimitiveKVHandler.Char kh, PrimitiveKVHandler.Char vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableCharIntMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Int> CHAR_INT =
            new TypeHandlerPair<MutableCharIntMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Int>() {
                @Override
                public PrimitiveKVHandler.Char keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Int valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public MutableCharIntMap createEmpty() {
                    return CharIntMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableCharIntMap target,
                        PrimitiveKVHandler.Char kh, PrimitiveKVHandler.Int vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableCharFloatMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Float> CHAR_FLOAT =
            new TypeHandlerPair<MutableCharFloatMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Float>() {
                @Override
                public PrimitiveKVHandler.Char keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Float valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public MutableCharFloatMap createEmpty() {
                    return CharFloatMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableCharFloatMap target,
                        PrimitiveKVHandler.Char kh, PrimitiveKVHandler.Float vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableCharLongMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Long> CHAR_LONG =
            new TypeHandlerPair<MutableCharLongMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Long>() {
                @Override
                public PrimitiveKVHandler.Char keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Long valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public MutableCharLongMap createEmpty() {
                    return CharLongMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableCharLongMap target,
                        PrimitiveKVHandler.Char kh, PrimitiveKVHandler.Long vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableCharDoubleMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Double> CHAR_DOUBLE =
            new TypeHandlerPair<MutableCharDoubleMap, PrimitiveKVHandler.Char, PrimitiveKVHandler.Double>() {
                @Override
                public PrimitiveKVHandler.Char keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Double valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public MutableCharDoubleMap createEmpty() {
                    return CharDoubleMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableCharDoubleMap target,
                        PrimitiveKVHandler.Char kh, PrimitiveKVHandler.Double vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    //endregion
    //region Int -> Primitive

    TypeHandlerPair<MutableIntBooleanMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Boolean> INT_BOOLEAN =
            new TypeHandlerPair<MutableIntBooleanMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Boolean>() {
                @Override
                public PrimitiveKVHandler.Int keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Boolean valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Boolean.INSTANCE;
                }

                @Override
                public MutableIntBooleanMap createEmpty() {
                    return IntBooleanMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableIntBooleanMap target,
                        PrimitiveKVHandler.Int kh, PrimitiveKVHandler.Boolean vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableIntByteMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Byte> INT_BYTE =
            new TypeHandlerPair<MutableIntByteMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Byte>() {
                @Override
                public PrimitiveKVHandler.Int keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Byte valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public MutableIntByteMap createEmpty() {
                    return IntByteMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableIntByteMap target,
                        PrimitiveKVHandler.Int kh, PrimitiveKVHandler.Byte vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableIntShortMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Short> INT_SHORT =
            new TypeHandlerPair<MutableIntShortMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Short>() {
                @Override
                public PrimitiveKVHandler.Int keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Short valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public MutableIntShortMap createEmpty() {
                    return IntShortMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableIntShortMap target,
                        PrimitiveKVHandler.Int kh, PrimitiveKVHandler.Short vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableIntCharMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Char> INT_CHAR =
            new TypeHandlerPair<MutableIntCharMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Char>() {
                @Override
                public PrimitiveKVHandler.Int keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Char valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public MutableIntCharMap createEmpty() {
                    return IntCharMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableIntCharMap target,
                        PrimitiveKVHandler.Int kh, PrimitiveKVHandler.Char vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableIntIntMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Int> INT_INT =
            new TypeHandlerPair<MutableIntIntMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Int>() {
                @Override
                public PrimitiveKVHandler.Int keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Int valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public MutableIntIntMap createEmpty() {
                    return IntIntMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableIntIntMap target,
                        PrimitiveKVHandler.Int kh, PrimitiveKVHandler.Int vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableIntFloatMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Float> INT_FLOAT =
            new TypeHandlerPair<MutableIntFloatMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Float>() {
                @Override
                public PrimitiveKVHandler.Int keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Float valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public MutableIntFloatMap createEmpty() {
                    return IntFloatMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableIntFloatMap target,
                        PrimitiveKVHandler.Int kh, PrimitiveKVHandler.Float vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableIntLongMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Long> INT_LONG =
            new TypeHandlerPair<MutableIntLongMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Long>() {
                @Override
                public PrimitiveKVHandler.Int keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Long valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public MutableIntLongMap createEmpty() {
                    return IntLongMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableIntLongMap target,
                        PrimitiveKVHandler.Int kh, PrimitiveKVHandler.Long vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableIntDoubleMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Double> INT_DOUBLE =
            new TypeHandlerPair<MutableIntDoubleMap, PrimitiveKVHandler.Int, PrimitiveKVHandler.Double>() {
                @Override
                public PrimitiveKVHandler.Int keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Double valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public MutableIntDoubleMap createEmpty() {
                    return IntDoubleMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableIntDoubleMap target,
                        PrimitiveKVHandler.Int kh, PrimitiveKVHandler.Double vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    //endregion
    //region Float -> Primitive

    TypeHandlerPair<MutableFloatBooleanMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Boolean> FLOAT_BOOLEAN =
            new TypeHandlerPair<MutableFloatBooleanMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Boolean>() {
                @Override
                public PrimitiveKVHandler.Float keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Boolean valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Boolean.INSTANCE;
                }

                @Override
                public MutableFloatBooleanMap createEmpty() {
                    return FloatBooleanMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableFloatBooleanMap target,
                        PrimitiveKVHandler.Float kh, PrimitiveKVHandler.Boolean vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableFloatByteMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Byte> FLOAT_BYTE =
            new TypeHandlerPair<MutableFloatByteMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Byte>() {
                @Override
                public PrimitiveKVHandler.Float keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Byte valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public MutableFloatByteMap createEmpty() {
                    return FloatByteMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableFloatByteMap target,
                        PrimitiveKVHandler.Float kh, PrimitiveKVHandler.Byte vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableFloatShortMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Short> FLOAT_SHORT =
            new TypeHandlerPair<MutableFloatShortMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Short>() {
                @Override
                public PrimitiveKVHandler.Float keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Short valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public MutableFloatShortMap createEmpty() {
                    return FloatShortMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableFloatShortMap target,
                        PrimitiveKVHandler.Float kh, PrimitiveKVHandler.Short vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableFloatCharMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Char> FLOAT_CHAR =
            new TypeHandlerPair<MutableFloatCharMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Char>() {
                @Override
                public PrimitiveKVHandler.Float keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Char valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public MutableFloatCharMap createEmpty() {
                    return FloatCharMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableFloatCharMap target,
                        PrimitiveKVHandler.Float kh, PrimitiveKVHandler.Char vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableFloatIntMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Int> FLOAT_INT =
            new TypeHandlerPair<MutableFloatIntMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Int>() {
                @Override
                public PrimitiveKVHandler.Float keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Int valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public MutableFloatIntMap createEmpty() {
                    return FloatIntMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableFloatIntMap target,
                        PrimitiveKVHandler.Float kh, PrimitiveKVHandler.Int vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableFloatFloatMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Float> FLOAT_FLOAT =
            new TypeHandlerPair<MutableFloatFloatMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Float>() {
                @Override
                public PrimitiveKVHandler.Float keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Float valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public MutableFloatFloatMap createEmpty() {
                    return FloatFloatMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableFloatFloatMap target,
                        PrimitiveKVHandler.Float kh, PrimitiveKVHandler.Float vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableFloatLongMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Long> FLOAT_LONG =
            new TypeHandlerPair<MutableFloatLongMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Long>() {
                @Override
                public PrimitiveKVHandler.Float keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Long valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public MutableFloatLongMap createEmpty() {
                    return FloatLongMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableFloatLongMap target,
                        PrimitiveKVHandler.Float kh, PrimitiveKVHandler.Long vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableFloatDoubleMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Double> FLOAT_DOUBLE =
            new TypeHandlerPair<MutableFloatDoubleMap, PrimitiveKVHandler.Float, PrimitiveKVHandler.Double>() {
                @Override
                public PrimitiveKVHandler.Float keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Double valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public MutableFloatDoubleMap createEmpty() {
                    return FloatDoubleMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableFloatDoubleMap target,
                        PrimitiveKVHandler.Float kh, PrimitiveKVHandler.Double vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    //endregion
    //region Long -> Primitive

    TypeHandlerPair<MutableLongBooleanMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Boolean> LONG_BOOLEAN =
            new TypeHandlerPair<MutableLongBooleanMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Boolean>() {
                @Override
                public PrimitiveKVHandler.Long keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Boolean valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Boolean.INSTANCE;
                }

                @Override
                public MutableLongBooleanMap createEmpty() {
                    return LongBooleanMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableLongBooleanMap target,
                        PrimitiveKVHandler.Long kh, PrimitiveKVHandler.Boolean vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableLongByteMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Byte> LONG_BYTE =
            new TypeHandlerPair<MutableLongByteMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Byte>() {
                @Override
                public PrimitiveKVHandler.Long keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Byte valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public MutableLongByteMap createEmpty() {
                    return LongByteMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableLongByteMap target,
                        PrimitiveKVHandler.Long kh, PrimitiveKVHandler.Byte vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableLongShortMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Short> LONG_SHORT =
            new TypeHandlerPair<MutableLongShortMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Short>() {
                @Override
                public PrimitiveKVHandler.Long keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Short valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public MutableLongShortMap createEmpty() {
                    return LongShortMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableLongShortMap target,
                        PrimitiveKVHandler.Long kh, PrimitiveKVHandler.Short vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableLongCharMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Char> LONG_CHAR =
            new TypeHandlerPair<MutableLongCharMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Char>() {
                @Override
                public PrimitiveKVHandler.Long keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Char valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public MutableLongCharMap createEmpty() {
                    return LongCharMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableLongCharMap target,
                        PrimitiveKVHandler.Long kh, PrimitiveKVHandler.Char vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableLongIntMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Int> LONG_INT =
            new TypeHandlerPair<MutableLongIntMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Int>() {
                @Override
                public PrimitiveKVHandler.Long keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Int valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public MutableLongIntMap createEmpty() {
                    return LongIntMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableLongIntMap target,
                        PrimitiveKVHandler.Long kh, PrimitiveKVHandler.Int vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableLongFloatMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Float> LONG_FLOAT =
            new TypeHandlerPair<MutableLongFloatMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Float>() {
                @Override
                public PrimitiveKVHandler.Long keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Float valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public MutableLongFloatMap createEmpty() {
                    return LongFloatMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableLongFloatMap target,
                        PrimitiveKVHandler.Long kh, PrimitiveKVHandler.Float vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableLongLongMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Long> LONG_LONG =
            new TypeHandlerPair<MutableLongLongMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Long>() {
                @Override
                public PrimitiveKVHandler.Long keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Long valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public MutableLongLongMap createEmpty() {
                    return LongLongMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableLongLongMap target,
                        PrimitiveKVHandler.Long kh, PrimitiveKVHandler.Long vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableLongDoubleMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Double> LONG_DOUBLE =
            new TypeHandlerPair<MutableLongDoubleMap, PrimitiveKVHandler.Long, PrimitiveKVHandler.Double>() {
                @Override
                public PrimitiveKVHandler.Long keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Double valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public MutableLongDoubleMap createEmpty() {
                    return LongDoubleMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableLongDoubleMap target,
                        PrimitiveKVHandler.Long kh, PrimitiveKVHandler.Double vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    //endregion
    //region Double -> Primitive

    TypeHandlerPair<MutableDoubleBooleanMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Boolean> DOUBLE_BOOLEAN =
            new TypeHandlerPair<MutableDoubleBooleanMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Boolean>() {
                @Override
                public PrimitiveKVHandler.Double keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Boolean valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Boolean.INSTANCE;
                }

                @Override
                public MutableDoubleBooleanMap createEmpty() {
                    return DoubleBooleanMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableDoubleBooleanMap target,
                        PrimitiveKVHandler.Double kh, PrimitiveKVHandler.Boolean vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableDoubleByteMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Byte> DOUBLE_BYTE =
            new TypeHandlerPair<MutableDoubleByteMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Byte>() {
                @Override
                public PrimitiveKVHandler.Double keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Byte valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Byte.INSTANCE;
                }

                @Override
                public MutableDoubleByteMap createEmpty() {
                    return DoubleByteMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableDoubleByteMap target,
                        PrimitiveKVHandler.Double kh, PrimitiveKVHandler.Byte vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableDoubleShortMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Short> DOUBLE_SHORT =
            new TypeHandlerPair<MutableDoubleShortMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Short>() {
                @Override
                public PrimitiveKVHandler.Double keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Short valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Short.INSTANCE;
                }

                @Override
                public MutableDoubleShortMap createEmpty() {
                    return DoubleShortMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableDoubleShortMap target,
                        PrimitiveKVHandler.Double kh, PrimitiveKVHandler.Short vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableDoubleCharMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Char> DOUBLE_CHAR =
            new TypeHandlerPair<MutableDoubleCharMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Char>() {
                @Override
                public PrimitiveKVHandler.Double keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Char valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Char.INSTANCE;
                }

                @Override
                public MutableDoubleCharMap createEmpty() {
                    return DoubleCharMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableDoubleCharMap target,
                        PrimitiveKVHandler.Double kh, PrimitiveKVHandler.Char vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableDoubleIntMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Int> DOUBLE_INT =
            new TypeHandlerPair<MutableDoubleIntMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Int>() {
                @Override
                public PrimitiveKVHandler.Double keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Int valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Int.INSTANCE;
                }

                @Override
                public MutableDoubleIntMap createEmpty() {
                    return DoubleIntMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableDoubleIntMap target,
                        PrimitiveKVHandler.Double kh, PrimitiveKVHandler.Int vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableDoubleFloatMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Float> DOUBLE_FLOAT =
            new TypeHandlerPair<MutableDoubleFloatMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Float>() {
                @Override
                public PrimitiveKVHandler.Double keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Float valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Float.INSTANCE;
                }

                @Override
                public MutableDoubleFloatMap createEmpty() {
                    return DoubleFloatMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableDoubleFloatMap target,
                        PrimitiveKVHandler.Double kh, PrimitiveKVHandler.Float vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableDoubleLongMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Long> DOUBLE_LONG =
            new TypeHandlerPair<MutableDoubleLongMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Long>() {
                @Override
                public PrimitiveKVHandler.Double keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Long valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Long.INSTANCE;
                }

                @Override
                public MutableDoubleLongMap createEmpty() {
                    return DoubleLongMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableDoubleLongMap target,
                        PrimitiveKVHandler.Double kh, PrimitiveKVHandler.Long vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };

    TypeHandlerPair<MutableDoubleDoubleMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Double> DOUBLE_DOUBLE =
            new TypeHandlerPair<MutableDoubleDoubleMap, PrimitiveKVHandler.Double, PrimitiveKVHandler.Double>() {
                @Override
                public PrimitiveKVHandler.Double keyHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public PrimitiveKVHandler.Double valueHandler(JavaType type) {
                    return PrimitiveKVHandler.Double.INSTANCE;
                }

                @Override
                public MutableDoubleDoubleMap createEmpty() {
                    return DoubleDoubleMaps.mutable.empty();
                }

                @Override
                public void add(
                        MutableDoubleDoubleMap target,
                        PrimitiveKVHandler.Double kh, PrimitiveKVHandler.Double vh,
                        DeserializationContext ctx, String k, JsonParser v
                ) throws IOException {
                    target.put(kh.key(ctx, k), vh.value(ctx, v));
                }
            };
    //endregion
}
