package com.fasterxml.jackson.datatype.eclipsecollections.ser.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.datatype.primitive_collections_base.ser.map.PrimitiveRefMapSerializer;
import org.eclipse.collections.api.map.primitive.*;

import java.io.IOException;

/**
 * @author yawkat
 */
@SuppressWarnings({ "Duplicates", "NewClassNamingConvention" })
public final class PrimitiveRefMapSerializers {

    static <E extends Throwable> void rethrowUnchecked(IOException e) throws E {
        throw (E) e;
    }

    private PrimitiveRefMapSerializers() {
    }

    public static class Byte<V> extends PrimitiveRefMapSerializer<ByteObjectMap<V>, V> {
        private static final long serialVersionUID = 4L;

        public Byte(JavaType type, BeanProperty property, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
            super(type, property, vts, valueSerializer);
        }

        @Override
        protected void serializeEntries(ByteObjectMap<V> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    gen.writeFieldName(String.valueOf(k));
                    _serializeValue(v, gen, serializers);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }

        @Override
        protected PrimitiveRefMapSerializer<ByteObjectMap<V>, V> withResolved(
                TypeSerializer vts,
                BeanProperty property,
                JsonSerializer<Object> valueSerializer
        ) {
            return new Byte<>(_type, property, vts, valueSerializer);
        }
    }

    public static class Short<V> extends PrimitiveRefMapSerializer<ShortObjectMap<V>, V> {
        private static final long serialVersionUID = 4L;
        public Short(JavaType type, BeanProperty property, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
            
            super(type, property, vts, valueSerializer);
        }

        @Override
        protected void serializeEntries(ShortObjectMap<V> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    gen.writeFieldName(String.valueOf(k));
                    _serializeValue(v, gen, serializers);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }

        @Override
        protected PrimitiveRefMapSerializer<ShortObjectMap<V>, V> withResolved(
                TypeSerializer vts,
                BeanProperty property,
                JsonSerializer<Object> valueSerializer
        ) {
            return new Short<>(_type, property, vts, valueSerializer);
        }
    }

    public static class Char<V> extends PrimitiveRefMapSerializer<CharObjectMap<V>, V> {
        private static final long serialVersionUID = 4L;

        public Char(JavaType type, BeanProperty property, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
            super(type, property, vts, valueSerializer);
        }

        @Override
        protected void serializeEntries(CharObjectMap<V> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    gen.writeFieldName(String.valueOf(k));
                    _serializeValue(v, gen, serializers);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }

        @Override
        protected PrimitiveRefMapSerializer<CharObjectMap<V>, V> withResolved(
                TypeSerializer vts,
                BeanProperty property,
                JsonSerializer<Object> valueSerializer
        ) {
            return new Char<>(_type, property, vts, valueSerializer);
        }
    }

    public static class Int<V> extends PrimitiveRefMapSerializer<IntObjectMap<V>, V> {
        private static final long serialVersionUID = 4L;

        public Int(JavaType type, BeanProperty property, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
            super(type, property, vts, valueSerializer);
        }

        @Override
        protected void serializeEntries(IntObjectMap<V> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    gen.writeFieldName(String.valueOf(k));
                    _serializeValue(v, gen, serializers);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }

        @Override
        protected PrimitiveRefMapSerializer<IntObjectMap<V>, V> withResolved(
                TypeSerializer vts,
                BeanProperty property,
                JsonSerializer<Object> valueSerializer
        ) {
            return new Int<>(_type, property, vts, valueSerializer);
        }
    }

    public static class Float<V> extends PrimitiveRefMapSerializer<FloatObjectMap<V>, V> {
        private static final long serialVersionUID = 4L;

        public Float(JavaType type, BeanProperty property, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
            super(type, property, vts, valueSerializer);
        }

        @Override
        protected void serializeEntries(FloatObjectMap<V> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    gen.writeFieldName(String.valueOf(k));
                    _serializeValue(v, gen, serializers);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }

        @Override
        protected PrimitiveRefMapSerializer<FloatObjectMap<V>, V> withResolved(
                TypeSerializer vts,
                BeanProperty property,
                JsonSerializer<Object> valueSerializer
        ) {
            return new Float<>(_type, property, vts, valueSerializer);
        }
    }

    public static class Long<V> extends PrimitiveRefMapSerializer<LongObjectMap<V>, V> {
        private static final long serialVersionUID = 4L;

        public Long(JavaType type, BeanProperty property, TypeSerializer vts, JsonSerializer<Object> valueSerializer) {
            super(type, property, vts, valueSerializer);
        }

        @Override
        protected void serializeEntries(LongObjectMap<V> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    gen.writeFieldName(String.valueOf(k));
                    _serializeValue(v, gen, serializers);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }

        @Override
        protected PrimitiveRefMapSerializer<LongObjectMap<V>, V> withResolved(
                TypeSerializer vts,
                BeanProperty property,
                JsonSerializer<Object> valueSerializer
        ) {
            return new Long<>(_type, property, vts, valueSerializer);
        }
    }

    public static class Double<V> extends PrimitiveRefMapSerializer<DoubleObjectMap<V>, V> {
        private static final long serialVersionUID = 4L;

        public Double(
                JavaType type,
                BeanProperty property,
                TypeSerializer vts,
                JsonSerializer<Object> valueSerializer
        ) {
            super(type, property, vts, valueSerializer);
        }

        @Override
        protected void serializeEntries(DoubleObjectMap<V> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    gen.writeFieldName(String.valueOf(k));
                    _serializeValue(v, gen, serializers);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }

        @Override
        protected PrimitiveRefMapSerializer<DoubleObjectMap<V>, V> withResolved(
                TypeSerializer vts,
                BeanProperty property,
                JsonSerializer<Object> valueSerializer
        ) {
            return new Double<>(_type, property, vts, valueSerializer);
        }
    }
}
