package com.fasterxml.jackson.datatype.primitive_collections_base.deser.map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;

import java.io.IOException;

public interface TypeHandlerPair<M, K extends KeyHandler<K>, V extends ValueHandler<V>> {
    K keyHandler(JavaType type);

    V valueHandler(JavaType type);

    M createEmpty();

    void add(M target, K kh, V vh, DeserializationContext ctx, String k, JsonParser v)
            throws IOException;
}
