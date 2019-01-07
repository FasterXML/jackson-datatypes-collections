package com.fasterxml.jackson.datatype.primitive_collections_base.ser.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;

import java.io.IOException;

/**
 * @author yawkat
 */
public abstract class PrimitiveRefMapSerializer<T, V>
        extends PrimitiveMapSerializer<T> {
    private static final long serialVersionUID = 0L;

    protected final JavaType _type;
    protected final BeanProperty _property;
    protected final TypeSerializer _valueTypeSerializer;
    protected final JsonSerializer<Object> _valueSerializer;

    protected PropertySerializerMap _dynamicValueSerializers = PropertySerializerMap.emptyForProperties();

    protected PrimitiveRefMapSerializer(
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
        JavaType containedType = _type.containedTypeOrUnknown(0);
        TypeSerializer vts = (_valueTypeSerializer == null)
                ? prov.findTypeSerializer(containedType) : _valueTypeSerializer;
        if (vts != null) {
            vts = vts.forProperty(prov, property);
        }
        JsonSerializer<Object> vs = ((_valueSerializer == null) && containedType.useStaticType())
                ? prov.findValueSerializer(containedType) : _valueSerializer;
        //noinspection ObjectEqualit
        if (vts == _valueTypeSerializer && vs == _valueSerializer) {
            return this;
        }
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
}
