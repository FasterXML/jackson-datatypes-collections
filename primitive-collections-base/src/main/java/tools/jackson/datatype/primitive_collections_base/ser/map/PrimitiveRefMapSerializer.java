package tools.jackson.datatype.primitive_collections_base.ser.map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.*;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.impl.PropertySerializerMap;

/**
 * @author yawkat
 */
public abstract class PrimitiveRefMapSerializer<T, V>
        extends PrimitiveMapSerializer<T>
{
    protected final JavaType _type;
    protected final BeanProperty _property;
    protected final TypeSerializer _valueTypeSerializer;
    protected final ValueSerializer<Object> _valueSerializer;

    protected PropertySerializerMap _dynamicValueSerializers = PropertySerializerMap.emptyForProperties();

    protected PrimitiveRefMapSerializer(
            JavaType type, BeanProperty property,
            TypeSerializer vts, ValueSerializer<Object> valueSerializer
    ) {
        super(type);
        _type = type;
        _property = property;
        _valueTypeSerializer = vts;
        _valueSerializer = valueSerializer;
    }

    protected abstract PrimitiveRefMapSerializer<T, V> withResolved(
            TypeSerializer vts, BeanProperty property, ValueSerializer<Object> valueSerializer
    );

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property)
    {
        JavaType containedType = _type.containedTypeOrUnknown(0);
        TypeSerializer vts = (_valueTypeSerializer == null)
                ? ctxt.findTypeSerializer(containedType) : _valueTypeSerializer;
        if (vts != null) {
            vts = vts.forProperty(ctxt, property);
        }
        ValueSerializer<Object> vs = ((_valueSerializer == null) && containedType.useStaticType())
                ? ctxt.findValueSerializer(containedType) : _valueSerializer;
        //noinspection ObjectEqualit
        if (vts == _valueTypeSerializer && vs == _valueSerializer) {
            return this;
        }
        return withResolved(vts, property, vs);
    }

    protected void _serializeValue(V value, JsonGenerator gen, SerializationContext ctxt)
        throws JacksonException
    {
        ValueSerializer<Object> valueSer = _valueSerializer;
        if (valueSer == null) {
            Class<?> cc = value.getClass();
            valueSer = _dynamicValueSerializers.serializerFor(cc);
            if (valueSer == null) {
                valueSer = _findAndAddDynamic(_dynamicValueSerializers, ctxt.constructType(cc), ctxt);
            }
        }
        if (_valueTypeSerializer == null) {
            valueSer.serialize(value, gen, ctxt);
        } else {
            valueSer.serializeWithType(value, gen, ctxt, _valueTypeSerializer);
        }
    }

    protected final ValueSerializer<Object> _findAndAddDynamic(
            PropertySerializerMap map, JavaType type, SerializationContext ctxt)
    {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(
                type, ctxt, _property);
        if (map != result.map) {
            _dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }
}
