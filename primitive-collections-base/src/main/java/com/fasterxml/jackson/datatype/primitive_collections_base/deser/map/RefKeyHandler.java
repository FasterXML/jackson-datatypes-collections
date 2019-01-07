package com.fasterxml.jackson.datatype.primitive_collections_base.deser.map;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import java.io.IOException;

/**
 * @author yawkat
 */
public class RefKeyHandler implements KeyHandler<RefKeyHandler> {
    private final JavaType _keyType;
    private final KeyDeserializer _keyDeserializer;

    public RefKeyHandler(JavaType keyType, KeyDeserializer _keyDeserializer) {
        if (keyType == null) { throw new IllegalArgumentException("keyType == null"); }

        this._keyType = keyType;
        this._keyDeserializer = _keyDeserializer;
    }

    @Override
    public RefKeyHandler createContextualKey(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        //noinspection VariableNotUsedInsideIf
        return _keyDeserializer == null ?
                new RefKeyHandler(_keyType, ctxt.findKeyDeserializer(_keyType, property)) :
                this;
    }

    public Object key(DeserializationContext ctx, String key) throws IOException {
        return _keyDeserializer.deserializeKey(key, ctx);
    }
}
