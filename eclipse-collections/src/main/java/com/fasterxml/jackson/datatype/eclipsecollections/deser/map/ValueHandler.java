package com.fasterxml.jackson.datatype.eclipsecollections.deser.map;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author yawkat
 */
public interface ValueHandler<V extends ValueHandler<V>> {
    V createContextualValue(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException;

    // TYPE value(DeserializationContext ctx, JsonParser parser) throws IOException
}
