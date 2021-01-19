package com.fasterxml.jackson.datatype.primitive_collections_base.ser.map;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.*;

/**
 * @author yawkat
 */
public abstract class RefPrimitiveMapSerializer<T, K> extends PrimitiveMapSerializer<T>
{
    protected final JavaType _type;
    protected final BeanProperty _property;
    protected final JsonSerializer<Object> _keySerializer;

    protected RefPrimitiveMapSerializer(JavaType type, BeanProperty property, JsonSerializer<Object> keySerializer) {
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
    {
        JsonSerializer<Object> ks = _keySerializer == null ?
                prov.findKeySerializer(_type.containedTypeOrUnknown(0), property) :
                _keySerializer;
        return withResolved(property, ks);
    }

    protected void _serializeKey(K key, JsonGenerator gen, SerializerProvider provider)
        throws JacksonException
    {
        if (key == null) {
            provider.findNullKeySerializer(_type.getKeyType(), _property)
                    .serialize(null, gen, provider);
        } else {
            _keySerializer.serialize(key, gen, provider);
        }
    }
}
