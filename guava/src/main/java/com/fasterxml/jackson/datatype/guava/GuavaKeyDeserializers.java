package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.KeyDeserializers;
import com.fasterxml.jackson.datatype.guava.deser.RangeKeyDeserializer;
import com.google.common.collect.Range;

import java.io.Serializable;

public class GuavaKeyDeserializers
        implements Serializable,
        KeyDeserializers {
    static final long serialVersionUID = 1L;

    @Override
    public KeyDeserializer findKeyDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
        if (type.isTypeOrSubTypeOf(Range.class)) {
            return new RangeKeyDeserializer(type);
        }
        return null;
    }
}
