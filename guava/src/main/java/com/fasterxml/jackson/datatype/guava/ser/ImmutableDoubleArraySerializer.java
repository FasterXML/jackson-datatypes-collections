package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.primitives.ImmutableDoubleArray;

import java.io.IOException;

public final class ImmutableDoubleArraySerializer extends StdSerializer<ImmutableDoubleArray> {

    public ImmutableDoubleArraySerializer() {
        super(ImmutableDoubleArray.class);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, ImmutableDoubleArray value) {
        return value == null || value.isEmpty();
    }

    @Override
    public void serialize(ImmutableDoubleArray value, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        int len = value.length();
        if (len== 1 && serializerProvider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) {
            generator.writeNumber(value.get(0));
        } else {
            generator.writeStartArray(value, len);
            for (int i = 0; i < len; i++) {
                generator.writeNumber(value.get(i));
            }
            generator.writeEndArray();
        }
    }
}
