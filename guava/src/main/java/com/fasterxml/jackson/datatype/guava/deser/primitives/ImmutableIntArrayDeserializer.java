package com.fasterxml.jackson.datatype.guava.deser.primitives;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.guava.util.ImmutablePrimitiveTypes;
import com.google.common.primitives.ImmutableIntArray;

public class ImmutableIntArrayDeserializer
        extends BaseImmutableArrayDeserializer<Integer, ImmutableIntArray, ImmutableIntArray.Builder> {
    public ImmutableIntArrayDeserializer() {
        super(ImmutablePrimitiveTypes.ImmutableIntArrayType, Integer.class);
    }

    @Override
    protected ImmutableIntArray.Builder createIntermediateCollection() {
        return ImmutableIntArray.builder();
    }

    @Override
    protected void collect(ImmutableIntArray.Builder intermediateBuilder, Integer value) {
        intermediateBuilder.add(value);
    }

    @Override
    protected ImmutableIntArray finish(ImmutableIntArray.Builder builder) {
        return builder.build();
    }

    @Override
    protected Integer asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getIntValue();
    }
}