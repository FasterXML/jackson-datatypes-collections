package tools.jackson.datatype.fastutil.deser.map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.datatype.primitive_collections_base.deser.map.ValueHandler;

/**
 * Handlers for primitive values of fastutil maps. Values are parsed the same way as
 * elements of fastutil collections (and primitive values by {@code jackson-databind}):
 * applying coercion configuration, and handling JSON nulls as specified by
 * {@link tools.jackson.databind.DeserializationFeature#FAIL_ON_NULL_FOR_PRIMITIVES}.
 *<p>
 * Handlers extend {@link StdDeserializer} only to use its parsing helper methods.
 */
@SuppressWarnings({ "serial", "NewClassNamingConvention" })
final class PrimitiveValueHandlers
{
    private PrimitiveValueHandlers() { }

    abstract static class Base<V extends Base<V>> extends StdDeserializer<Object>
        implements ValueHandler<V>
    {
        private final LogicalType _logicalType;

        protected Base(Class<?> primitiveType, LogicalType logicalType) {
            super(primitiveType);
            _logicalType = logicalType;
        }

        @SuppressWarnings("unchecked")
        @Override
        public V createContextualValue(DeserializationContext ctxt, BeanProperty property) {
            return (V) this;
        }

        @Override
        public LogicalType logicalType() {
            return _logicalType;
        }
    }

    static final class Boolean extends Base<Boolean> {
        static final Boolean INSTANCE = new Boolean();

        private Boolean() { super(java.lang.Boolean.TYPE, LogicalType.Boolean); }

        boolean value(DeserializationContext ctxt, JsonParser p) throws JacksonException {
            return _parseBooleanPrimitive(p, ctxt);
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            return value(ctxt, p);
        }
    }

    static final class Byte extends Base<Byte> {
        static final Byte INSTANCE = new Byte();

        private Byte() { super(java.lang.Byte.TYPE, LogicalType.Integer); }

        byte value(DeserializationContext ctxt, JsonParser p) throws JacksonException {
            return _parseBytePrimitive(p, ctxt);
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            return value(ctxt, p);
        }
    }

    static final class Short extends Base<Short> {
        static final Short INSTANCE = new Short();

        private Short() { super(java.lang.Short.TYPE, LogicalType.Integer); }

        short value(DeserializationContext ctxt, JsonParser p) throws JacksonException {
            return _parseShortPrimitive(p, ctxt);
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            return value(ctxt, p);
        }
    }

    static final class Char extends Base<Char> {
        static final Char INSTANCE = new Char();

        private Char() { super(java.lang.Character.TYPE, LogicalType.Textual); }

        char value(DeserializationContext ctxt, JsonParser p) throws JacksonException {
            // no parsing helper for `char` in `StdDeserializer`: handle the same way as
            // `jackson-databind` does for `char` (except for no coercion from numbers)
            if (p.hasToken(JsonToken.VALUE_NULL)) {
                _verifyNullForPrimitive(ctxt);
                return '\0';
            }
            if (!p.hasToken(JsonToken.VALUE_STRING)) {
                return (java.lang.Character) ctxt.handleUnexpectedToken(java.lang.Character.TYPE, p);
            }
            String str = p.getString();
            if (str.length() != 1) {
                return (java.lang.Character) ctxt.handleWeirdStringValue(java.lang.Character.TYPE, str,
                        "Cannot convert a JSON String of length %d into a `char` map value",
                        str.length());
            }
            return str.charAt(0);
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            return value(ctxt, p);
        }
    }

    static final class Int extends Base<Int> {
        static final Int INSTANCE = new Int();

        private Int() { super(java.lang.Integer.TYPE, LogicalType.Integer); }

        int value(DeserializationContext ctxt, JsonParser p) throws JacksonException {
            return _parseIntPrimitive(p, ctxt);
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            return value(ctxt, p);
        }
    }

    static final class Long extends Base<Long> {
        static final Long INSTANCE = new Long();

        private Long() { super(java.lang.Long.TYPE, LogicalType.Integer); }

        long value(DeserializationContext ctxt, JsonParser p) throws JacksonException {
            return _parseLongPrimitive(p, ctxt);
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            return value(ctxt, p);
        }
    }

    static final class Float extends Base<Float> {
        static final Float INSTANCE = new Float();

        private Float() { super(java.lang.Float.TYPE, LogicalType.Float); }

        float value(DeserializationContext ctxt, JsonParser p) throws JacksonException {
            return _parseFloatPrimitive(p, ctxt);
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            return value(ctxt, p);
        }
    }

    static final class Double extends Base<Double> {
        static final Double INSTANCE = new Double();

        private Double() { super(java.lang.Double.TYPE, LogicalType.Float); }

        double value(DeserializationContext ctxt, JsonParser p) throws JacksonException {
            return _parseDoublePrimitive(p, ctxt);
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
            return value(ctxt, p);
        }
    }
}
