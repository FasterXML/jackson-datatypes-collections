package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.core.JsonGenerator;
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
        generator.writeStartArray();
        for (int i = 0; i < value.length(); i++) {
            generator.writeNumber(value.get(i));
        }
        generator.writeEndArray();
    }
}
