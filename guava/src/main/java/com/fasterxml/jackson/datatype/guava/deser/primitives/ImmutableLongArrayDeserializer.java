package com.fasterxml.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.guava.util.ImmutablePrimitiveTypes;
import com.google.common.primitives.ImmutableLongArray;

public class ImmutableLongArrayDeserializer
        extends BaseImmutableArrayDeserializer<Long, ImmutableLongArray, ImmutableLongArray.Builder> {
    public ImmutableLongArrayDeserializer() {
        super(ImmutablePrimitiveTypes.ImmutableLongArrayType, Long.class);
    }

    @Override
    protected ImmutableLongArray.Builder createIntermediateCollection() {
        return ImmutableLongArray.builder();
    }

    @Override
    protected void collect(ImmutableLongArray.Builder intermediateBuilder, Long value) {
        intermediateBuilder.add(value);
    }

    @Override
    protected ImmutableLongArray finish(ImmutableLongArray.Builder builder) {
        return builder.build();
    }

    @Override
    protected Long asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getLongValue();
    }
}
