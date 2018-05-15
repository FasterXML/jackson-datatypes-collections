package com.fasterxml.jackson.datatype.eclipsecollections.ser.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.api.map.primitive.ByteBooleanMap;
import org.eclipse.collections.api.map.primitive.ByteByteMap;
import org.eclipse.collections.api.map.primitive.ByteCharMap;
import org.eclipse.collections.api.map.primitive.ByteDoubleMap;
import org.eclipse.collections.api.map.primitive.ByteFloatMap;
import org.eclipse.collections.api.map.primitive.ByteIntMap;
import org.eclipse.collections.api.map.primitive.ByteLongMap;
import org.eclipse.collections.api.map.primitive.ByteShortMap;
import org.eclipse.collections.api.map.primitive.CharBooleanMap;
import org.eclipse.collections.api.map.primitive.CharByteMap;
import org.eclipse.collections.api.map.primitive.CharCharMap;
import org.eclipse.collections.api.map.primitive.CharDoubleMap;
import org.eclipse.collections.api.map.primitive.CharFloatMap;
import org.eclipse.collections.api.map.primitive.CharIntMap;
import org.eclipse.collections.api.map.primitive.CharLongMap;
import org.eclipse.collections.api.map.primitive.CharShortMap;
import org.eclipse.collections.api.map.primitive.DoubleBooleanMap;
import org.eclipse.collections.api.map.primitive.DoubleByteMap;
import org.eclipse.collections.api.map.primitive.DoubleCharMap;
import org.eclipse.collections.api.map.primitive.DoubleDoubleMap;
import org.eclipse.collections.api.map.primitive.DoubleFloatMap;
import org.eclipse.collections.api.map.primitive.DoubleIntMap;
import org.eclipse.collections.api.map.primitive.DoubleLongMap;
import org.eclipse.collections.api.map.primitive.DoubleShortMap;
import org.eclipse.collections.api.map.primitive.FloatBooleanMap;
import org.eclipse.collections.api.map.primitive.FloatByteMap;
import org.eclipse.collections.api.map.primitive.FloatCharMap;
import org.eclipse.collections.api.map.primitive.FloatDoubleMap;
import org.eclipse.collections.api.map.primitive.FloatFloatMap;
import org.eclipse.collections.api.map.primitive.FloatIntMap;
import org.eclipse.collections.api.map.primitive.FloatLongMap;
import org.eclipse.collections.api.map.primitive.FloatShortMap;
import org.eclipse.collections.api.map.primitive.IntBooleanMap;
import org.eclipse.collections.api.map.primitive.IntByteMap;
import org.eclipse.collections.api.map.primitive.IntCharMap;
import org.eclipse.collections.api.map.primitive.IntDoubleMap;
import org.eclipse.collections.api.map.primitive.IntFloatMap;
import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.api.map.primitive.IntLongMap;
import org.eclipse.collections.api.map.primitive.IntShortMap;
import org.eclipse.collections.api.map.primitive.LongBooleanMap;
import org.eclipse.collections.api.map.primitive.LongByteMap;
import org.eclipse.collections.api.map.primitive.LongCharMap;
import org.eclipse.collections.api.map.primitive.LongDoubleMap;
import org.eclipse.collections.api.map.primitive.LongFloatMap;
import org.eclipse.collections.api.map.primitive.LongIntMap;
import org.eclipse.collections.api.map.primitive.LongLongMap;
import org.eclipse.collections.api.map.primitive.LongShortMap;
import org.eclipse.collections.api.map.primitive.ShortBooleanMap;
import org.eclipse.collections.api.map.primitive.ShortByteMap;
import org.eclipse.collections.api.map.primitive.ShortCharMap;
import org.eclipse.collections.api.map.primitive.ShortDoubleMap;
import org.eclipse.collections.api.map.primitive.ShortFloatMap;
import org.eclipse.collections.api.map.primitive.ShortIntMap;
import org.eclipse.collections.api.map.primitive.ShortLongMap;
import org.eclipse.collections.api.map.primitive.ShortShortMap;

/**
 * @author yawkat
 */
@SuppressWarnings("Duplicates")
public final class PrimitivePrimitiveMapSerializers {
    private PrimitivePrimitiveMapSerializers() {
    }

    public static Map<Class<? extends PrimitiveIterable>, PrimitiveMapSerializer<?>> getInstances() {
        return Collections.unmodifiableMap(INSTANCES);
    }

    private static final Map<Class<? extends PrimitiveIterable>, PrimitiveMapSerializer<?>> INSTANCES = new HashMap<>();
    //region Byte -> Primitive
    public static final PrimitiveMapSerializer<ByteBooleanMap> BYTE_BOOLEAN =
            new PrimitiveMapSerializer<ByteBooleanMap>(ByteBooleanMap.class) {
                @Override
                protected void serializeEntries(ByteBooleanMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeBoolean(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ByteByteMap> BYTE_BYTE =
            new PrimitiveMapSerializer<ByteByteMap>(ByteByteMap.class) {
                @Override
                protected void serializeEntries(ByteByteMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ByteShortMap> BYTE_SHORT =
            new PrimitiveMapSerializer<ByteShortMap>(ByteShortMap.class) {
                @Override
                protected void serializeEntries(ByteShortMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ByteCharMap> BYTE_CHAR =
            new PrimitiveMapSerializer<ByteCharMap>(ByteCharMap.class) {
                @Override
                protected void serializeEntries(ByteCharMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeString(new char[]{v}, 0, 1);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ByteIntMap> BYTE_INT =
            new PrimitiveMapSerializer<ByteIntMap>(ByteIntMap.class) {
                @Override
                protected void serializeEntries(ByteIntMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ByteFloatMap> BYTE_FLOAT =
            new PrimitiveMapSerializer<ByteFloatMap>(ByteFloatMap.class) {
                @Override
                protected void serializeEntries(ByteFloatMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ByteLongMap> BYTE_LONG =
            new PrimitiveMapSerializer<ByteLongMap>(ByteLongMap.class) {
                @Override
                protected void serializeEntries(ByteLongMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ByteDoubleMap> BYTE_DOUBLE =
            new PrimitiveMapSerializer<ByteDoubleMap>(ByteDoubleMap.class) {
                @Override
                protected void serializeEntries(ByteDoubleMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    //endregion
    //region Short -> Primitive
    public static final PrimitiveMapSerializer<ShortBooleanMap> SHORT_BOOLEAN =
            new PrimitiveMapSerializer<ShortBooleanMap>(ShortBooleanMap.class) {
                @Override
                protected void serializeEntries(ShortBooleanMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeBoolean(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ShortByteMap> SHORT_BYTE =
            new PrimitiveMapSerializer<ShortByteMap>(ShortByteMap.class) {
                @Override
                protected void serializeEntries(ShortByteMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ShortShortMap> SHORT_SHORT =
            new PrimitiveMapSerializer<ShortShortMap>(ShortShortMap.class) {
                @Override
                protected void serializeEntries(ShortShortMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ShortCharMap> SHORT_CHAR =
            new PrimitiveMapSerializer<ShortCharMap>(ShortCharMap.class) {
                @Override
                protected void serializeEntries(ShortCharMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeString(new char[]{v}, 0, 1);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ShortIntMap> SHORT_INT =
            new PrimitiveMapSerializer<ShortIntMap>(ShortIntMap.class) {
                @Override
                protected void serializeEntries(ShortIntMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ShortFloatMap> SHORT_FLOAT =
            new PrimitiveMapSerializer<ShortFloatMap>(ShortFloatMap.class) {
                @Override
                protected void serializeEntries(ShortFloatMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ShortLongMap> SHORT_LONG =
            new PrimitiveMapSerializer<ShortLongMap>(ShortLongMap.class) {
                @Override
                protected void serializeEntries(ShortLongMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<ShortDoubleMap> SHORT_DOUBLE =
            new PrimitiveMapSerializer<ShortDoubleMap>(ShortDoubleMap.class) {
                @Override
                protected void serializeEntries(ShortDoubleMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    //endregion
    //region Char -> Primitive
    public static final PrimitiveMapSerializer<CharBooleanMap> CHAR_BOOLEAN =
            new PrimitiveMapSerializer<CharBooleanMap>(CharBooleanMap.class) {
                @Override
                protected void serializeEntries(CharBooleanMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeBoolean(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<CharByteMap> CHAR_BYTE =
            new PrimitiveMapSerializer<CharByteMap>(CharByteMap.class) {
                @Override
                protected void serializeEntries(CharByteMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<CharShortMap> CHAR_SHORT =
            new PrimitiveMapSerializer<CharShortMap>(CharShortMap.class) {
                @Override
                protected void serializeEntries(CharShortMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<CharCharMap> CHAR_CHAR =
            new PrimitiveMapSerializer<CharCharMap>(CharCharMap.class) {
                @Override
                protected void serializeEntries(CharCharMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeString(new char[]{v}, 0, 1);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<CharIntMap> CHAR_INT =
            new PrimitiveMapSerializer<CharIntMap>(CharIntMap.class) {
                @Override
                protected void serializeEntries(CharIntMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<CharFloatMap> CHAR_FLOAT =
            new PrimitiveMapSerializer<CharFloatMap>(CharFloatMap.class) {
                @Override
                protected void serializeEntries(CharFloatMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<CharLongMap> CHAR_LONG =
            new PrimitiveMapSerializer<CharLongMap>(CharLongMap.class) {
                @Override
                protected void serializeEntries(CharLongMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<CharDoubleMap> CHAR_DOUBLE =
            new PrimitiveMapSerializer<CharDoubleMap>(CharDoubleMap.class) {
                @Override
                protected void serializeEntries(CharDoubleMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    //endregion
    //region Int -> Primitive
    public static final PrimitiveMapSerializer<IntBooleanMap> INT_BOOLEAN =
            new PrimitiveMapSerializer<IntBooleanMap>(IntBooleanMap.class) {
                @Override
                protected void serializeEntries(IntBooleanMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeBoolean(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<IntByteMap> INT_BYTE =
            new PrimitiveMapSerializer<IntByteMap>(IntByteMap.class) {
                @Override
                protected void serializeEntries(IntByteMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<IntShortMap> INT_SHORT =
            new PrimitiveMapSerializer<IntShortMap>(IntShortMap.class) {
                @Override
                protected void serializeEntries(IntShortMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<IntCharMap> INT_CHAR =
            new PrimitiveMapSerializer<IntCharMap>(IntCharMap.class) {
                @Override
                protected void serializeEntries(IntCharMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeString(new char[]{v}, 0, 1);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<IntIntMap> INT_INT =
            new PrimitiveMapSerializer<IntIntMap>(IntIntMap.class) {
                @Override
                protected void serializeEntries(IntIntMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<IntFloatMap> INT_FLOAT =
            new PrimitiveMapSerializer<IntFloatMap>(IntFloatMap.class) {
                @Override
                protected void serializeEntries(IntFloatMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<IntLongMap> INT_LONG =
            new PrimitiveMapSerializer<IntLongMap>(IntLongMap.class) {
                @Override
                protected void serializeEntries(IntLongMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<IntDoubleMap> INT_DOUBLE =
            new PrimitiveMapSerializer<IntDoubleMap>(IntDoubleMap.class) {
                @Override
                protected void serializeEntries(IntDoubleMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    //endregion
    //region Float -> Primitive
    public static final PrimitiveMapSerializer<FloatBooleanMap> FLOAT_BOOLEAN =
            new PrimitiveMapSerializer<FloatBooleanMap>(FloatBooleanMap.class) {
                @Override
                protected void serializeEntries(FloatBooleanMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeBoolean(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<FloatByteMap> FLOAT_BYTE =
            new PrimitiveMapSerializer<FloatByteMap>(FloatByteMap.class) {
                @Override
                protected void serializeEntries(FloatByteMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<FloatShortMap> FLOAT_SHORT =
            new PrimitiveMapSerializer<FloatShortMap>(FloatShortMap.class) {
                @Override
                protected void serializeEntries(FloatShortMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<FloatCharMap> FLOAT_CHAR =
            new PrimitiveMapSerializer<FloatCharMap>(FloatCharMap.class) {
                @Override
                protected void serializeEntries(FloatCharMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeString(new char[]{v}, 0, 1);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<FloatIntMap> FLOAT_INT =
            new PrimitiveMapSerializer<FloatIntMap>(FloatIntMap.class) {
                @Override
                protected void serializeEntries(FloatIntMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<FloatFloatMap> FLOAT_FLOAT =
            new PrimitiveMapSerializer<FloatFloatMap>(FloatFloatMap.class) {
                @Override
                protected void serializeEntries(FloatFloatMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<FloatLongMap> FLOAT_LONG =
            new PrimitiveMapSerializer<FloatLongMap>(FloatLongMap.class) {
                @Override
                protected void serializeEntries(FloatLongMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<FloatDoubleMap> FLOAT_DOUBLE =
            new PrimitiveMapSerializer<FloatDoubleMap>(FloatDoubleMap.class) {
                @Override
                protected void serializeEntries(FloatDoubleMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    //endregion
    //region Long -> Primitive
    public static final PrimitiveMapSerializer<LongBooleanMap> LONG_BOOLEAN =
            new PrimitiveMapSerializer<LongBooleanMap>(LongBooleanMap.class) {
                @Override
                protected void serializeEntries(LongBooleanMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeBoolean(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<LongByteMap> LONG_BYTE =
            new PrimitiveMapSerializer<LongByteMap>(LongByteMap.class) {
                @Override
                protected void serializeEntries(LongByteMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<LongShortMap> LONG_SHORT =
            new PrimitiveMapSerializer<LongShortMap>(LongShortMap.class) {
                @Override
                protected void serializeEntries(LongShortMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<LongCharMap> LONG_CHAR =
            new PrimitiveMapSerializer<LongCharMap>(LongCharMap.class) {
                @Override
                protected void serializeEntries(LongCharMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeString(new char[]{v}, 0, 1);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<LongIntMap> LONG_INT =
            new PrimitiveMapSerializer<LongIntMap>(LongIntMap.class) {
                @Override
                protected void serializeEntries(LongIntMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<LongFloatMap> LONG_FLOAT =
            new PrimitiveMapSerializer<LongFloatMap>(LongFloatMap.class) {
                @Override
                protected void serializeEntries(LongFloatMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<LongLongMap> LONG_LONG =
            new PrimitiveMapSerializer<LongLongMap>(LongLongMap.class) {
                @Override
                protected void serializeEntries(LongLongMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<LongDoubleMap> LONG_DOUBLE =
            new PrimitiveMapSerializer<LongDoubleMap>(LongDoubleMap.class) {
                @Override
                protected void serializeEntries(LongDoubleMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    //endregion
    //region Double -> Primitive
    public static final PrimitiveMapSerializer<DoubleBooleanMap> DOUBLE_BOOLEAN =
            new PrimitiveMapSerializer<DoubleBooleanMap>(DoubleBooleanMap.class) {
                @Override
                protected void serializeEntries(DoubleBooleanMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeBoolean(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<DoubleByteMap> DOUBLE_BYTE =
            new PrimitiveMapSerializer<DoubleByteMap>(DoubleByteMap.class) {
                @Override
                protected void serializeEntries(DoubleByteMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<DoubleShortMap> DOUBLE_SHORT =
            new PrimitiveMapSerializer<DoubleShortMap>(DoubleShortMap.class) {
                @Override
                protected void serializeEntries(DoubleShortMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<DoubleCharMap> DOUBLE_CHAR =
            new PrimitiveMapSerializer<DoubleCharMap>(DoubleCharMap.class) {
                @Override
                protected void serializeEntries(DoubleCharMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeString(new char[]{v}, 0, 1);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<DoubleIntMap> DOUBLE_INT =
            new PrimitiveMapSerializer<DoubleIntMap>(DoubleIntMap.class) {
                @Override
                protected void serializeEntries(DoubleIntMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<DoubleFloatMap> DOUBLE_FLOAT =
            new PrimitiveMapSerializer<DoubleFloatMap>(DoubleFloatMap.class) {
                @Override
                protected void serializeEntries(DoubleFloatMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<DoubleLongMap> DOUBLE_LONG =
            new PrimitiveMapSerializer<DoubleLongMap>(DoubleLongMap.class) {
                @Override
                protected void serializeEntries(DoubleLongMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    public static final PrimitiveMapSerializer<DoubleDoubleMap> DOUBLE_DOUBLE =
            new PrimitiveMapSerializer<DoubleDoubleMap>(DoubleDoubleMap.class) {
                @Override
                protected void serializeEntries(DoubleDoubleMap value, JsonGenerator gen, SerializerProvider serializers) {
                    value.forEachKeyValue((k, v) -> {
                        try {
                            gen.writeFieldName(String.valueOf(k));
                            gen.writeNumber(v);
                        } catch (IOException e) {
                            rethrowUnchecked(e);
                        }
                    });
                }
            };
    //endregion

    static {
        INSTANCES.put(ByteBooleanMap.class, BYTE_BOOLEAN);
        INSTANCES.put(ByteByteMap.class, BYTE_BYTE);
        INSTANCES.put(ByteShortMap.class, BYTE_SHORT);
        INSTANCES.put(ByteCharMap.class, BYTE_CHAR);
        INSTANCES.put(ByteIntMap.class, BYTE_INT);
        INSTANCES.put(ByteFloatMap.class, BYTE_FLOAT);
        INSTANCES.put(ByteLongMap.class, BYTE_LONG);
        INSTANCES.put(ByteDoubleMap.class, BYTE_DOUBLE);
        INSTANCES.put(ShortBooleanMap.class, SHORT_BOOLEAN);
        INSTANCES.put(ShortByteMap.class, SHORT_BYTE);
        INSTANCES.put(ShortShortMap.class, SHORT_SHORT);
        INSTANCES.put(ShortCharMap.class, SHORT_CHAR);
        INSTANCES.put(ShortIntMap.class, SHORT_INT);
        INSTANCES.put(ShortFloatMap.class, SHORT_FLOAT);
        INSTANCES.put(ShortLongMap.class, SHORT_LONG);
        INSTANCES.put(ShortDoubleMap.class, SHORT_DOUBLE);
        INSTANCES.put(CharBooleanMap.class, CHAR_BOOLEAN);
        INSTANCES.put(CharByteMap.class, CHAR_BYTE);
        INSTANCES.put(CharShortMap.class, CHAR_SHORT);
        INSTANCES.put(CharCharMap.class, CHAR_CHAR);
        INSTANCES.put(CharIntMap.class, CHAR_INT);
        INSTANCES.put(CharFloatMap.class, CHAR_FLOAT);
        INSTANCES.put(CharLongMap.class, CHAR_LONG);
        INSTANCES.put(CharDoubleMap.class, CHAR_DOUBLE);
        INSTANCES.put(IntBooleanMap.class, INT_BOOLEAN);
        INSTANCES.put(IntByteMap.class, INT_BYTE);
        INSTANCES.put(IntShortMap.class, INT_SHORT);
        INSTANCES.put(IntCharMap.class, INT_CHAR);
        INSTANCES.put(IntIntMap.class, INT_INT);
        INSTANCES.put(IntFloatMap.class, INT_FLOAT);
        INSTANCES.put(IntLongMap.class, INT_LONG);
        INSTANCES.put(IntDoubleMap.class, INT_DOUBLE);
        INSTANCES.put(FloatBooleanMap.class, FLOAT_BOOLEAN);
        INSTANCES.put(FloatByteMap.class, FLOAT_BYTE);
        INSTANCES.put(FloatShortMap.class, FLOAT_SHORT);
        INSTANCES.put(FloatCharMap.class, FLOAT_CHAR);
        INSTANCES.put(FloatIntMap.class, FLOAT_INT);
        INSTANCES.put(FloatFloatMap.class, FLOAT_FLOAT);
        INSTANCES.put(FloatLongMap.class, FLOAT_LONG);
        INSTANCES.put(FloatDoubleMap.class, FLOAT_DOUBLE);
        INSTANCES.put(LongBooleanMap.class, LONG_BOOLEAN);
        INSTANCES.put(LongByteMap.class, LONG_BYTE);
        INSTANCES.put(LongShortMap.class, LONG_SHORT);
        INSTANCES.put(LongCharMap.class, LONG_CHAR);
        INSTANCES.put(LongIntMap.class, LONG_INT);
        INSTANCES.put(LongFloatMap.class, LONG_FLOAT);
        INSTANCES.put(LongLongMap.class, LONG_LONG);
        INSTANCES.put(LongDoubleMap.class, LONG_DOUBLE);
        INSTANCES.put(DoubleBooleanMap.class, DOUBLE_BOOLEAN);
        INSTANCES.put(DoubleByteMap.class, DOUBLE_BYTE);
        INSTANCES.put(DoubleShortMap.class, DOUBLE_SHORT);
        INSTANCES.put(DoubleCharMap.class, DOUBLE_CHAR);
        INSTANCES.put(DoubleIntMap.class, DOUBLE_INT);
        INSTANCES.put(DoubleFloatMap.class, DOUBLE_FLOAT);
        INSTANCES.put(DoubleLongMap.class, DOUBLE_LONG);
        INSTANCES.put(DoubleDoubleMap.class, DOUBLE_DOUBLE);
    }
}
