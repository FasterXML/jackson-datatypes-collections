package com.fasterxml.jackson.datatype.guava.ser.primitives;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.guava.util.ImmutablePrimitiveTypes;

public abstract class BaseImmutableArraySerializer<ImmutableArray> extends StdSerializer<ImmutableArray> {
    protected BaseImmutableArraySerializer(ImmutablePrimitiveTypes.ImmutablePrimitiveArrays immutableArrayType) {
        super(immutableArrayType.type());
    }

    @Override
    public final void serialize(ImmutableArray immutableArray, JsonGenerator gen, SerializerProvider provider) throws JacksonException {
        if (immutableArray == null) {
            provider.defaultSerializeNullValue(gen);
        } else {
            writeArray(immutableArray, gen);
        }
    }

    protected abstract void writeArray(ImmutableArray immutableArray, JsonGenerator gen);
}
