package com.fasterxml.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.guava.util.ImmutablePrimitiveTypes;
import com.google.common.primitives.ImmutableDoubleArray;

public class ImmutableDoubleArrayDeserializer
        extends BaseImmutableArrayDeserializer<Double, ImmutableDoubleArray, ImmutableDoubleArray.Builder> {
    public ImmutableDoubleArrayDeserializer() {
        super(ImmutablePrimitiveTypes.ImmutableDoubleArrayType, Double.class);
    }

    @Override
    protected ImmutableDoubleArray.Builder createIntermediateCollection() {
        return ImmutableDoubleArray.builder();
    }

    @Override
    protected void collect(ImmutableDoubleArray.Builder intermediateBuilder, Double value) {
        intermediateBuilder.add(value);
    }

    @Override
    protected ImmutableDoubleArray finish(ImmutableDoubleArray.Builder builder) {
        return builder.build();
    }

    @Override
    protected Double asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getDoubleValue();
    }
}
