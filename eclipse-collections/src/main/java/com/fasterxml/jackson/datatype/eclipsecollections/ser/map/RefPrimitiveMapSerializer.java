package com.fasterxml.jackson.datatype.eclipsecollections.ser.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.api.map.primitive.ObjectBooleanMap;
import org.eclipse.collections.api.map.primitive.ObjectByteMap;
import org.eclipse.collections.api.map.primitive.ObjectCharMap;
import org.eclipse.collections.api.map.primitive.ObjectDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectFloatMap;
import org.eclipse.collections.api.map.primitive.ObjectIntMap;
import org.eclipse.collections.api.map.primitive.ObjectLongMap;
import org.eclipse.collections.api.map.primitive.ObjectShortMap;

/**
 * @author yawkat
 */
@SuppressWarnings({ "Duplicates", "NewClassNamingConvention" })
public abstract class RefPrimitiveMapSerializer<T extends PrimitiveIterable, K>
        extends PrimitiveMapSerializer<T>
{
    protected final JavaType _type;
    protected final BeanProperty _property;
    protected final JsonSerializer<Object> _keySerializer;

    private RefPrimitiveMapSerializer(JavaType type, BeanProperty property, JsonSerializer<Object> keySerializer) {
        super(type);
        this._type = type;
        this._property = property;
        this._keySerializer = keySerializer;
    }

    protected abstract RefPrimitiveMapSerializer<T, K> withResolved(
            BeanProperty property, JsonSerializer<Object> keySerializer
    );

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
            throws JsonMappingException {
        JsonSerializer<Object> ks = _keySerializer == null ?
                prov.findKeySerializer(_type.containedTypeOrUnknown(0), property) :
                _keySerializer;
        return withResolved(property, ks);
    }

    protected void _serializeKey(K key, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (key == null) {
            provider.findNullKeySerializer(_type.getKeyType(), _property)
                    .serialize(null, gen, provider);
        } else {
            _keySerializer.serialize(key, gen, provider);
        }
    }

    public static final class Boolean<K> extends RefPrimitiveMapSerializer<ObjectBooleanMap<K>, K> {
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
