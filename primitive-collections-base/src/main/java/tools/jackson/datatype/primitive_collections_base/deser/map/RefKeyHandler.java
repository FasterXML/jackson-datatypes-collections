package tools.jackson.datatype.primitive_collections_base.deser.map;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.deser.ContextualKeyDeserializer;

/**
 * @author yawkat
 */
public class RefKeyHandler implements KeyHandler<RefKeyHandler>
{
    private final JavaType _keyType;
    private final KeyDeserializer _keyDeserializer;

    public RefKeyHandler(JavaType keyType, KeyDeserializer _keyDeserializer) {
        if (keyType == null) {
            throw new IllegalArgumentException("keyType == null");
        }

        this._keyType = keyType;
        this._keyDeserializer = _keyDeserializer;
    }

    @Override
    public RefKeyHandler createContextualKey(DeserializationContext ctxt, BeanProperty property)
    {
        KeyDeserializer keyDeser = _keyDeserializer;
        if (keyDeser == null) {
            keyDeser = ctxt.findKeyDeserializer(_keyType, property);
        } else if (keyDeser instanceof ContextualKeyDeserializer ckd) {
            // if directly assigned (custom deserializer), probably not yet contextual
            keyDeser = ckd.createContextual(ctxt, property);
        }
        //noinspection ObjectEquality
        return (keyDeser == _keyDeserializer) ? this : new RefKeyHandler(_keyType, keyDeser);
    }

    public Object key(DeserializationContext ctx, String key) throws JacksonException {
        return _keyDeserializer.deserializeKey(key, ctx);
    }
}
