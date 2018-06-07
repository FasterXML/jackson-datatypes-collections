package com.fasterxml.jackson.datatype.eclipsecollections.ser.map;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;

import org.eclipse.collections.api.map.primitive.ByteObjectMap;
import org.eclipse.collections.api.map.primitive.CharObjectMap;
import org.eclipse.collections.api.map.primitive.DoubleObjectMap;
import org.eclipse.collections.api.map.primitive.FloatObjectMap;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.LongObjectMap;
import org.eclipse.collections.api.map.primitive.PrimitiveObjectMap;
import org.eclipse.collections.api.map.primitive.ShortObjectMap;

/**
 * @author yawkat
 */
@SuppressWarnings({ "Duplicates", "NewClassNamingConvention" })
public abstract class PrimitiveRefMapSerializer<T extends PrimitiveObjectMap<V>, V>
        extends PrimitiveMapSerializer<T>
{
    private static final long serialVersionUID = 3L;

    protected final JavaType _type;
    protected final BeanProperty _property;
    protected final TypeSerializer _valueTypeSerializer;
    protected final JsonSerializer<Object> _valueSerializer;

    protected PropertySerializerMap _dynamicValueSerializers = PropertySerializerMap.emptyForProperties();

    public PrimitiveRefMapSerializer(
            JavaType type, BeanProperty property,
            TypeSerializer vts, JsonSerializer<Object> valueSerializer
    ) {
        super(type);
        _type = type;
        _property = property;
        _valueTypeSerializer = vts;
        _valueSerializer = valueSerializer;
    }

    protected abstract PrimitiveRefMapSerializer<T, V> withResolved(
            TypeSerializer vts, BeanProperty property, JsonSerializer<Object> valueSerializer
    );

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
            throws JsonMappingException {
        TypeSerializer vts = this._valueTypeSerializer == null ? null
                : this._valueTypeSerializer.forProperty(prov, property);
        JavaType containedType = _type.containedTypeOrUnknown(0);
        JsonSerializer<Object> vs = this._valueSerializer == null && containedType.useStaticType() ?
                prov.findValueSerializer(containedType) :
                this._valueSerializer;
        //noinspection ObjectEquality
        if (vts == _valueTypeSerializer && vs == _valueSerializer) { return this; }
        return withResolved(vts, property, vs);
    }

    protected void _serializeValue(V value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        JsonSerializer<Object> valueSer = _valueSerializer;
        if (valueSer == null) {
            Class<?> cc = value.getClass();
            valueSer = _dynamicValueSerializers.serializerFor(cc);
            if (valueSer == null) {
                valueSer = _findAndAddDynamic(_dynamicValueSerializers, serializers.constructType(cc), serializers);
            }
        }
        if (_valueTypeSerializer == null) {
            valueSer.serialize(value, gen, serializers);
        } else {
            valueSer.serializeWithType(value, gen, serializers, _valueTypeSerializer);
        }
    }

    protected final JsonSerializer<Object> _findAndAddDynamic(
            PropertySerializerMap map, JavaType type, SerializerProvider provider
    ) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(
                type, provider, _property);
        if (map != result.map) {
            _dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }

    public static class Byte<V> extends PrimitiveRefMapSerializer<ByteObjectMap<V>, V> {
        private static final long serialVersionUID = 3L;

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
        private static final long serialVersionUID = 3L;
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
        private static final long serialVersionUID = 3L;

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
        private static final long serialVersionUID = 3L;

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
        private static final long serialVersionUID = 3L;

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
        private static final long serialVersionUID = 3L;

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
        private static final long serialVersionUID = 3L;

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
