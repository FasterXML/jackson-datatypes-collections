package tools.jackson.datatype.primitive_collections_base.deser.map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.io.NumberInput;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.util.ClassUtil;

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

    /*
    /**********************************************************************
    /* Helper methods for keys: parse and report problems the same
    /* way as `jackson-databind` does for `java.util.Map` keys
    /* (see `JDKKeyDeserializer`), except that `float` keys are parsed
    /* directly (not as `double` narrowed to `float`)
    /**********************************************************************
     */

    /**
     * @since 3.3
     */
    protected static int _parseIntKey(DeserializationContext ctxt, Class<?> keyClass, String key)
        throws JacksonException
    {
        // `NumberInput.parseInt()` does not check for empty String
        if (key.isEmpty()) {
            return _weirdNumberKey(ctxt, keyClass, key, "not a valid representation (empty String)").intValue();
        }
        try {
            return NumberInput.parseInt(key);
        } catch (NumberFormatException e) {
            return _invalidNumberKey(ctxt, keyClass, key, e).intValue();
        }
    }

    /**
     * @since 3.3
     */
    protected static long _parseLongKey(DeserializationContext ctxt, Class<?> keyClass, String key)
        throws JacksonException
    {
        // `NumberInput.parseLong()` does not check for empty String
        if (key.isEmpty()) {
            return _weirdNumberKey(ctxt, keyClass, key, "not a valid representation (empty String)").longValue();
        }
        try {
            return NumberInput.parseLong(key);
        } catch (NumberFormatException e) {
            return _invalidNumberKey(ctxt, keyClass, key, e).longValue();
        }
    }

    /**
     * @since 3.3
     */
    protected static float _parseFloatKey(DeserializationContext ctxt, Class<?> keyClass, String key)
        throws JacksonException
    {
        try {
            return NumberInput.parseFloat(key, false);
        } catch (NumberFormatException e) {
            return _invalidNumberKey(ctxt, keyClass, key, e).floatValue();
        }
    }

    /**
     * @since 3.3
     */
    protected static double _parseDoubleKey(DeserializationContext ctxt, Class<?> keyClass, String key)
        throws JacksonException
    {
        try {
            return NumberInput.parseDouble(key, false);
        } catch (NumberFormatException e) {
            return _invalidNumberKey(ctxt, keyClass, key, e).doubleValue();
        }
    }

    /**
     * @since 3.3
     */
    protected static Number _invalidNumberKey(DeserializationContext ctxt, Class<?> keyClass, String key,
            NumberFormatException e)
        throws JacksonException
    {
        return _weirdNumberKey(ctxt, keyClass, key, "not a valid representation, problem: (%s) %s",
                e.getClass().getName(), ClassUtil.exceptionMessage(e));
    }

    /**
     * Helper method that lets {@link tools.jackson.databind.deser.DeserializationProblemHandler}s
     * provide a replacement key (via {@link DeserializationContext#handleWeirdKey}), or
     * otherwise throws {@link tools.jackson.databind.exc.InvalidFormatException}.
     *
     * @param keyClass Wrapper type of the primitive key ({@code Integer.class} for {@code int}
     *    and so on), so that handlers may return boxed values
     *
     * @return Replacement key (never {@code null}), of type {@code keyClass}
     *
     * @since 3.3
     */
    protected static Object _weirdKey(DeserializationContext ctxt, Class<?> keyClass, String key,
            String msg, Object... msgArgs)
        throws JacksonException
    {
        Object result = ctxt.handleWeirdKey(keyClass, key, msg, msgArgs);
        // Unlike for `java.util.Map`, `null` cannot be used as primitive key
        if (result == null) {
            return ctxt.reportInputMismatch(keyClass,
                    "Cannot use `null` as %s map key (replacement for String \"%s\")",
                    ClassUtil.nameOf(ClassUtil.primitiveType(keyClass)), key);
        }
        return result;
    }

    /**
     * @since 3.3
     */
    protected static Number _weirdNumberKey(DeserializationContext ctxt, Class<?> keyClass, String key,
            String msg, Object... msgArgs)
        throws JacksonException
    {
        return (Number) _weirdKey(ctxt, keyClass, key, msg, msgArgs);
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

        public byte key(DeserializationContext ctx, String key) throws JacksonException {
            int value = _parseIntKey(ctx, java.lang.Byte.class, key);
            // allow range up to 255, inclusive (to support "unsigned" byte), as databind does
            if (value < java.lang.Byte.MIN_VALUE || value > 255) {
                return _weirdNumberKey(ctx, java.lang.Byte.class, key,
                        "overflow, value cannot be represented as 8-bit value").byteValue();
            }
            return (byte) value;
        }

        public byte value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getByteValue();
        }
    }

    public static final class Short extends PrimitiveKVHandler<Short> {
        public static final Short INSTANCE = new Short();

        public short key(DeserializationContext ctx, String key) throws JacksonException {
            int value = _parseIntKey(ctx, java.lang.Short.class, key);
            if (value < java.lang.Short.MIN_VALUE || value > java.lang.Short.MAX_VALUE) {
                return _weirdNumberKey(ctx, java.lang.Short.class, key,
                        "overflow, value cannot be represented as 16-bit value").shortValue();
            }
            return (short) value;
        }

        public short value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getShortValue();
        }
    }

    public static final class Char extends PrimitiveKVHandler<Char>
    {
        public static final Char INSTANCE = new Char();

        public char key(DeserializationContext ctx, String key) throws JacksonException
        {
            if (key.length() != 1) {
                return (java.lang.Character) _weirdKey(ctx, java.lang.Character.class, key,
                        "can only convert 1-character Strings");
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

        public int key(DeserializationContext ctx, String key) throws JacksonException {
            return _parseIntKey(ctx, java.lang.Integer.class, key);
        }

        public int value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getIntValue();
        }
    }

    public static final class Float extends PrimitiveKVHandler<Float> {
        public static final Float INSTANCE = new Float();

        public float key(DeserializationContext ctx, String key) throws JacksonException {
            // NOTE: databind parses `float` keys as `double` and narrows, but that
            // double rounding may produce wrong results; so parse directly
            return _parseFloatKey(ctx, java.lang.Float.class, key);
        }

        public float value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getFloatValue();
        }
    }

    public static final class Long extends PrimitiveKVHandler<Long> {
        public static final Long INSTANCE = new Long();

        public long key(DeserializationContext ctx, String key) throws JacksonException {
            return _parseLongKey(ctx, java.lang.Long.class, key);
        }

        public long value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getLongValue();
        }
    }

    public static final class Double extends PrimitiveKVHandler<Double> {
        public static final Double INSTANCE = new Double();

        public double key(DeserializationContext ctx, String key) throws JacksonException {
            return _parseDoubleKey(ctx, java.lang.Double.class, key);
        }

        public double value(DeserializationContext ctx, JsonParser parser) throws JacksonException {
            return parser.getDoubleValue();
        }
    }
}
