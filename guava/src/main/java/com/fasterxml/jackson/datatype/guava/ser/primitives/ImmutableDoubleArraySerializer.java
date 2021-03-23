package com.fasterxml.jackson.datatype.guava.ser.primitives;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.datatype.guava.util.ImmutablePrimitiveTypes;
import com.google.common.primitives.ImmutableDoubleArray;

public class ImmutableDoubleArraySerializer extends BaseImmutableArraySerializer<ImmutableDoubleArray> {

    public ImmutableDoubleArraySerializer() {
        super(ImmutablePrimitiveTypes.ImmutablePrimitiveArrays.DOUBLE);
    }

    @Override
    protected void writeArray(ImmutableDoubleArray immutableArray, JsonGenerator gen) {
        if (!immutableArray.isEmpty()) {
            gen.writeArray(immutableArray.toArray(), 0, immutableArray.length());
        }
    }

}
