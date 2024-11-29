package tools.jackson.datatype.primitive_collections_base.ser.map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.*;

/**
 * @author yawkat
 */
public abstract class RefPrimitiveMapSerializer<T, K> extends PrimitiveMapSerializer<T>
{
    protected final JavaType _type;
    protected final BeanProperty _property;
    protected final ValueSerializer<Object> _keySerializer;

    protected RefPrimitiveMapSerializer(JavaType type, BeanProperty property, ValueSerializer<Object> keySerializer) {
        super(type);
        this._type = type;
        this._property = property;
        this._keySerializer = keySerializer;
    }

    protected abstract RefPrimitiveMapSerializer<T, K> withResolved(
            BeanProperty property, ValueSerializer<Object> keySerializer
    );

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property)
    {
        ValueSerializer<Object> ks = _keySerializer == null ?
                ctxt.findKeySerializer(_type.containedTypeOrUnknown(0), property) :
                _keySerializer;
        return withResolved(property, ks);
    }

    protected void _serializeKey(K key, JsonGenerator gen, SerializationContext ctxt)
        throws JacksonException
    {
        if (key == null) {
            ctxt.findNullKeySerializer(_type.getKeyType(), _property)
                    .serialize(null, gen, ctxt);
        } else {
            _keySerializer.serialize(key, gen, ctxt);
        }
    }
}
