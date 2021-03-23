package com.fasterxml.jackson.datatype.guava.ser.primitives;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.datatype.guava.util.ImmutablePrimitiveTypes;
import com.google.common.primitives.ImmutableLongArray;

public class ImmutableLongArraySerializer extends BaseImmutableArraySerializer<ImmutableLongArray> {

    public ImmutableLongArraySerializer() {
        super(ImmutablePrimitiveTypes.ImmutablePrimitiveArrays.LONG);
    }

    @Override
    protected void writeArray(ImmutableLongArray immutableArray, JsonGenerator gen) {
        if (!immutableArray.isEmpty()) {
            gen.writeArray(immutableArray.toArray(), 0, immutableArray.length());
        }
    }

}
