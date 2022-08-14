package com.fasterxml.jackson.datatype.primitive_collections_base.deser.map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;

/**
 * @author yawkat
 */
public class RefValueHandler implements ValueHandler<RefValueHandler>
{
    private final JavaType _valueType;
    private final ValueDeserializer<?> _valueDeserializer;
    private final TypeDeserializer _typeDeserializerForValue;

    public RefValueHandler(
            JavaType valueType,
            ValueDeserializer<?> _valueDeserializer,
            TypeDeserializer _typeDeserializerForValue
    ) {
        if (valueType == null) { throw new IllegalArgumentException("valueType == null"); }

        this._valueType = valueType;
        this._valueDeserializer = _valueDeserializer;
        this._typeDeserializerForValue = _typeDeserializerForValue;
    }

    @Override
    public RefValueHandler createContextualValue(DeserializationContext ctxt, BeanProperty property)
    {
        ValueDeserializer<?> deser;
        if (_valueDeserializer == null) {
            deser = ctxt.findContextualValueDeserializer(_valueType, property);
        } else {
            deser = _valueDeserializer;
        }
        TypeDeserializer typeDeser = _typeDeserializerForValue == null ?
                ctxt.findTypeDeserializer(_valueType) : _typeDeserializerForValue;
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        //noinspection ObjectEquality
        if (deser == _valueDeserializer && typeDeser == _typeDeserializerForValue) {
            return this;
        }
        return new RefValueHandler(_valueType, deser, typeDeser);
    }

    public Object value(DeserializationContext ctx, JsonParser parser)
        throws JacksonException
    {
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return _valueDeserializer.getNullValue(ctx);
        }
        if (_typeDeserializerForValue == null) {
            return _valueDeserializer.deserialize(parser, ctx);
        }
        return _valueDeserializer.deserializeWithType(parser, ctx, _typeDeserializerForValue);
    }
}
