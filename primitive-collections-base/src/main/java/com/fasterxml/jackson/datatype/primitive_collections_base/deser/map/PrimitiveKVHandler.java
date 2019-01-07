package com.fasterxml.jackson.datatype.primitive_collections_base.deser.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;

/**
 * @author yawkat
 */
public class PrimitiveKVHandler<H extends KeyHandler<H> & ValueHandler<H>> implements KeyHandler<H>, ValueHandler<H> {
    @SuppressWarnings("unchecked")
    @Override
    public H createContextualKey(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        return (H) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public H createContextualValue(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        return (H) this;
    }

    public static final class Boolean extends PrimitiveKVHandler<Boolean> {
        public static final Boolean INSTANCE = new Boolean();

        public boolean value(DeserializationContext ctx, JsonParser parser) throws IOException {
            return parser.getBooleanValue();
        }
    }

    public static final class Byte extends PrimitiveKVHandler<Byte> {
        public static final Byte INSTANCE = new Byte();

        public byte key(DeserializationContext ctx, String key) {
            return java.lang.Byte.parseByte(key);
        }

        public byte value(DeserializationContext ctx, JsonParser parser) throws IOException {
            return parser.getByteValue();
        }
    }

    public static final class Short extends PrimitiveKVHandler<Short> {
        public static final Short INSTANCE = new Short();

        public short key(DeserializationContext ctx, String key) {
            return java.lang.Short.parseShort(key);
        }

        public short value(DeserializationContext ctx, JsonParser parser) throws IOException {
            return parser.getShortValue();
        }
    }

    public static final class Char extends PrimitiveKVHandler<Char> {
        public static final Char INSTANCE = new Char();

        public char key(DeserializationContext ctx, String key) throws JsonMappingException {
            if (key.length() != 1) {
                ctx.reportInputMismatch(char.class,
                                        "Cannot convert a JSON String of length %d into a char key of map",
                                        key.length());
            }
            return key.charAt(0);
        }

        public char value(DeserializationContext ctx, JsonParser parser) throws IOException {
            String valueAsString = parser.getValueAsString();
            if (valueAsString.length() != 1) {
                ctx.reportInputMismatch(char.class,
                                        "Cannot convert a JSON String of length %d into a char element of map",
                                        valueAsString.length());
            }
            return valueAsString.charAt(0);
        }
    }

    public static final class Int extends PrimitiveKVHandler<Int> {
        public static final Int INSTANCE = new Int();

        public int key(DeserializationContext ctx, String key) {
            return Integer.parseInt(key);
        }

        public int value(DeserializationContext ctx, JsonParser parser) throws IOException {
            return parser.getIntValue();
        }
    }

    public static final class Float extends PrimitiveKVHandler<Float> {
        public static final Float INSTANCE = new Float();

        public float key(DeserializationContext ctx, String key) {
            return java.lang.Float.parseFloat(key);
        }

        public float value(DeserializationContext ctx, JsonParser parser) throws IOException {
            return parser.getFloatValue();
        }
    }

    public static final class Long extends PrimitiveKVHandler<Long> {
        public static final Long INSTANCE = new Long();

        public long key(DeserializationContext ctx, String key) {
            return java.lang.Long.parseLong(key);
        }

        public long value(DeserializationContext ctx, JsonParser parser) throws IOException {
            return parser.getLongValue();
        }
    }

    public static final class Double extends PrimitiveKVHandler<Double> {
        public static final Double INSTANCE = new Double();

        public double key(DeserializationContext ctx, String key) {
            return java.lang.Double.parseDouble(key);
        }

        public double value(DeserializationContext ctx, JsonParser parser) throws IOException {
            return parser.getDoubleValue();
        }
    }
}
