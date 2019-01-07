package com.fasterxml.jackson.datatype.eclipsecollections.ser.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.primitive_collections_base.ser.map.RefPrimitiveMapSerializer;
import org.eclipse.collections.api.map.primitive.*;

import java.io.IOException;

import static com.fasterxml.jackson.datatype.eclipsecollections.ser.map.PrimitiveRefMapSerializers.rethrowUnchecked;

/**
 * @author yawkat
 */
@SuppressWarnings({ "Duplicates", "NewClassNamingConvention" })
public final class RefPrimitiveMapSerializers {
    private RefPrimitiveMapSerializers() {
    }

    public static final class Boolean<K> extends RefPrimitiveMapSerializer<ObjectBooleanMap<K>, K> {
        private static final long serialVersionUID = 4L;

        public Boolean(JavaType type, BeanProperty property, JsonSerializer<Object> keySerializer) {
            super(type, property, keySerializer);
        }

        @Override
        protected RefPrimitiveMapSerializer<ObjectBooleanMap<K>, K> withResolved(
                BeanProperty property, JsonSerializer<Object> keySerializer
        ) {
            return new Boolean<>(_type, property, keySerializer);
        }

        @Override
        protected void serializeEntries(ObjectBooleanMap<K> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    _serializeKey(k, gen, serializers);
                    gen.writeBoolean(v);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }
    }

    public static final class Byte<K> extends RefPrimitiveMapSerializer<ObjectByteMap<K>, K> {
        private static final long serialVersionUID = 4L;

        public Byte(JavaType type, BeanProperty property, JsonSerializer<Object> keySerializer) {
            super(type, property, keySerializer);
        }

        @Override
        protected RefPrimitiveMapSerializer<ObjectByteMap<K>, K> withResolved(
                BeanProperty property, JsonSerializer<Object> keySerializer
        ) {
            return new Byte<>(_type, property, keySerializer);
        }

        @Override
        protected void serializeEntries(ObjectByteMap<K> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    _serializeKey(k, gen, serializers);
                    gen.writeNumber(v);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }
    }

    public static final class Short<K> extends RefPrimitiveMapSerializer<ObjectShortMap<K>, K> {
        private static final long serialVersionUID = 4L;

        public Short(JavaType type, BeanProperty property, JsonSerializer<Object> keySerializer) {
            super(type, property, keySerializer);
        }

        @Override
        protected RefPrimitiveMapSerializer<ObjectShortMap<K>, K> withResolved(
                BeanProperty property, JsonSerializer<Object> keySerializer
        ) {
            return new Short<>(_type, property, keySerializer);
        }

        @Override
        protected void serializeEntries(ObjectShortMap<K> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    _serializeKey(k, gen, serializers);
                    gen.writeNumber(v);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }
    }

    public static final class Char<K> extends RefPrimitiveMapSerializer<ObjectCharMap<K>, K> {
        private static final long serialVersionUID = 4L;

        public Char(JavaType type, BeanProperty property, JsonSerializer<Object> keySerializer) {
            super(type, property, keySerializer);
        }

        @Override
        protected RefPrimitiveMapSerializer<ObjectCharMap<K>, K> withResolved(
                BeanProperty property, JsonSerializer<Object> keySerializer
        ) {
            return new Char<>(_type, property, keySerializer);
        }

        @Override
        protected void serializeEntries(ObjectCharMap<K> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    _serializeKey(k, gen, serializers);
                    gen.writeString(new char[]{ v }, 0, 1);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }
    }

    public static final class Int<K> extends RefPrimitiveMapSerializer<ObjectIntMap<K>, K> {
        private static final long serialVersionUID = 4L;

        public Int(JavaType type, BeanProperty property, JsonSerializer<Object> keySerializer) {
            super(type, property, keySerializer);
        }

        @Override
        protected RefPrimitiveMapSerializer<ObjectIntMap<K>, K> withResolved(
                BeanProperty property, JsonSerializer<Object> keySerializer
        ) {
            return new Int<>(_type, property, keySerializer);
        }

        @Override
        protected void serializeEntries(ObjectIntMap<K> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    _serializeKey(k, gen, serializers);
                    gen.writeNumber(v);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }
    }

    public static final class Float<K> extends RefPrimitiveMapSerializer<ObjectFloatMap<K>, K> {
        private static final long serialVersionUID = 4L;

        public Float(JavaType type, BeanProperty property, JsonSerializer<Object> keySerializer) {
            super(type, property, keySerializer);
        }

        @Override
        protected RefPrimitiveMapSerializer<ObjectFloatMap<K>, K> withResolved(
                BeanProperty property, JsonSerializer<Object> keySerializer
        ) {
            return new Float<>(_type, property, keySerializer);
        }

        @Override
        protected void serializeEntries(ObjectFloatMap<K> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    _serializeKey(k, gen, serializers);
                    gen.writeNumber(v);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }
    }

    public static final class Long<K> extends RefPrimitiveMapSerializer<ObjectLongMap<K>, K> {
        private static final long serialVersionUID = 4L;

        public Long(JavaType type, BeanProperty property, JsonSerializer<Object> keySerializer) {
            super(type, property, keySerializer);
        }

        @Override
        protected RefPrimitiveMapSerializer<ObjectLongMap<K>, K> withResolved(
                BeanProperty property, JsonSerializer<Object> keySerializer
        ) {
            return new Long<>(_type, property, keySerializer);
        }

        @Override
        protected void serializeEntries(ObjectLongMap<K> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    _serializeKey(k, gen, serializers);
                    gen.writeNumber(v);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }
    }

    public static final class Double<K> extends RefPrimitiveMapSerializer<ObjectDoubleMap<K>, K> {
        private static final long serialVersionUID = 4L;

        public Double(JavaType type, BeanProperty property, JsonSerializer<Object> keySerializer) {
            super(type, property, keySerializer);
        }

        @Override
        protected RefPrimitiveMapSerializer<ObjectDoubleMap<K>, K> withResolved(
                BeanProperty property, JsonSerializer<Object> keySerializer
        ) {
            return new Double<>(_type, property, keySerializer);
        }

        @Override
        protected void serializeEntries(ObjectDoubleMap<K> value, JsonGenerator gen, SerializerProvider serializers) {
            value.forEachKeyValue((k, v) -> {
                try {
                    _serializeKey(k, gen, serializers);
                    gen.writeNumber(v);
                } catch (IOException e) {
                    rethrowUnchecked(e);
                }
            });
        }
    }
}
