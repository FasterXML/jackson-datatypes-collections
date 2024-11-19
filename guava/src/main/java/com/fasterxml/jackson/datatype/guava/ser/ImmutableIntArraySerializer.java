package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.primitives.ImmutableIntArray;

import java.io.IOException;

public final class ImmutableIntArraySerializer extends StdSerializer<ImmutableIntArray> {

    public ImmutableIntArraySerializer() {
        super(ImmutableIntArray.class);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, ImmutableIntArray value) {
        return value == null || value.isEmpty();
    }

    @Override
    public void serialize(ImmutableIntArray value, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartArray();
        for (int i = 0; i < value.length(); i++) {
            generator.writeNumber(value.get(i));
        }
        generator.writeEndArray();
    }
}
