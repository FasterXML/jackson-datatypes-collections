package com.fasterxml.jackson.datatype.primitive_collections_base.deser.map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;

public interface TypeHandlerPair<M, K extends KeyHandler<K>, V extends ValueHandler<V>>
{
    K keyHandler(JavaType type);

    V valueHandler(JavaType type);

    M createEmpty();

    void add(M target, K kh, V vh, DeserializationContext ctx, String k, JsonParser v)
        throws JacksonException;
}
