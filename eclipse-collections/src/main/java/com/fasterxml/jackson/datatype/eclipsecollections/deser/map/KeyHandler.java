package com.fasterxml.jackson.datatype.eclipsecollections.deser.map;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author yawkat
 */
public interface KeyHandler<K extends KeyHandler<K>> {
    K createContextualKey(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException;

    // TYPE key(DeserializationContext ctx, String key) throws IOException;
}
