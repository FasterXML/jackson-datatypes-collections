package com.fasterxml.jackson.datatype.primitive_collections_base.deser.map;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.KeyDeserializer;

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
        //noinspection VariableNotUsedInsideIf
        return _keyDeserializer == null ?
                new RefKeyHandler(_keyType, ctxt.findKeyDeserializer(_keyType, property)) :
                this;
    }

    public Object key(DeserializationContext ctx, String key) throws JacksonException {
        return _keyDeserializer.deserializeKey(key, ctx);
    }
}
