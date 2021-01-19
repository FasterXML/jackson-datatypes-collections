package com.fasterxml.jackson.datatype.primitive_collections_base.deser.map;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;

/**
 * @author yawkat
 */
public interface KeyHandler<K extends KeyHandler<K>> {
    K createContextualKey(DeserializationContext ctxt, BeanProperty property);

    // TYPE key(DeserializationContext ctx, String key);
}
