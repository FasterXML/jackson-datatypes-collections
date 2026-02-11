package tools.jackson.datatype.primitive_collections_base.deser.map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;

/**
 * @author yawkat
 */
public class PrimitiveKVHandler<H extends KeyHandler<H> & ValueHandler<H>> implements KeyHandler<H>, ValueHandler<H>
{
    @SuppressWarnings("unchecked")
    @Override
    public H createContextualKey(DeserializationContext ctxt, BeanProperty property) {
        return (H) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public H createContextualValue(DeserializationContext ctxt, BeanProperty property) {
        return (H) this;
    }

    public static final class Boolean extends PrimitiveKVHandler<Boolean>
    {
        public static final Boolean INSTANCE = new Boolean();

        public boolean value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getBooleanValue();
        }
    }

    public static final class Byte extends PrimitiveKVHandler<Byte> {
        public static final Byte INSTANCE = new Byte();

        public byte key(DeserializationContext ctx, String key) throws JsonMappingException {
            try {
                return java.lang.Byte.parseByte(key);
            } catch (NumberFormatException e) {
                return ctx.reportInputMismatch(byte.class,
                    "Cannot parse '%s' as byte value for map key", key);
            }
        }

        public byte value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getByteValue();
        }
    }

    public static final class Short extends PrimitiveKVHandler<Short> {
        public static final Short INSTANCE = new Short();

        public short key(DeserializationContext ctx, String key) throws JsonMappingException {
            try {
                return java.lang.Short.parseShort(key);
            } catch (NumberFormatException e) {
                return ctx.reportInputMismatch(short.class,
                    "Cannot parse '%s' as short value for map key", key);
            }
        }

        public short value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getShortValue();
        }
    }

    public static final class Char extends PrimitiveKVHandler<Char>
    {
        public static final Char INSTANCE = new Char();

        public char key(DeserializationContext ctx, String key)
        {
            if (key.length() != 1) {
                ctx.reportInputMismatch(char.class,
                                        "Cannot convert a JSON String of length %d into a char key of map",
                                        key.length());
            }
            return key.charAt(0);
        }

        public char value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            String valueAsString = parser.getValueAsString();
            if (valueAsString == null) {
                ctx.reportInputMismatch(char.class,
                        "Cannot convert a JSON Null into a char element of map");
            }

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

        public int key(DeserializationContext ctx, String key) throws JsonMappingException {
            try {
                return Integer.parseInt(key);
            } catch (NumberFormatException e) {
                return ctx.reportInputMismatch(int.class,
                    "Cannot parse '%s' as int value for map key", key);
            }
        }

        public int value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getIntValue();
        }
    }

    public static final class Float extends PrimitiveKVHandler<Float> {
        public static final Float INSTANCE = new Float();

        public float key(DeserializationContext ctx, String key) throws JsonMappingException {
            try {
                return java.lang.Float.parseFloat(key);
            } catch (NumberFormatException e) {
                return ctx.reportInputMismatch(float.class,
                    "Cannot parse '%s' as float value for map key", key);
            }
        }

        public float value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getFloatValue();
        }
    }

    public static final class Long extends PrimitiveKVHandler<Long> {
        public static final Long INSTANCE = new Long();

        public long key(DeserializationContext ctx, String key) throws JsonMappingException {
            try {
                return java.lang.Long.parseLong(key);
            } catch (NumberFormatException e) {
                return ctx.reportInputMismatch(long.class,
                    "Cannot parse '%s' as long value for map key", key);
            }
        }

        public long value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getLongValue();
        }
    }

    public static final class Double extends PrimitiveKVHandler<Double> {
        public static final Double INSTANCE = new Double();

        public double key(DeserializationContext ctx, String key) throws JsonMappingException {
            try {
                return java.lang.Double.parseDouble(key);
            } catch (NumberFormatException e) {
                return ctx.reportInputMismatch(double.class,
                    "Cannot parse '%s' as double value for map key", key);
            }
        }

        public double value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getDoubleValue();
        }
    }
}
