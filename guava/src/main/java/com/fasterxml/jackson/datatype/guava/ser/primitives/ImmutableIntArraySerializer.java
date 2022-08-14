package com.fasterxml.jackson.datatype.guava.ser.primitives;

import tools.jackson.core.JsonGenerator;
import com.fasterxml.jackson.datatype.guava.util.ImmutablePrimitiveTypes;
import com.google.common.primitives.ImmutableIntArray;

public class ImmutableIntArraySerializer extends BaseImmutableArraySerializer<ImmutableIntArray> {

    public ImmutableIntArraySerializer() {
        super(ImmutablePrimitiveTypes.ImmutablePrimitiveArrays.INT);
    }

    @Override
    protected void writeArray(ImmutableIntArray immutableArray, JsonGenerator gen) {
        if (!immutableArray.isEmpty()) {
            gen.writeArray(immutableArray.toArray(), 0, immutableArray.length());
        }
    }

}
