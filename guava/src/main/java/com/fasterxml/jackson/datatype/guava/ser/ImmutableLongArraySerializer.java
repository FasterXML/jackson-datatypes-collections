package com.fasterxml.jackson.datatype.guava.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.primitives.ImmutableLongArray;

public final class ImmutableLongArraySerializer extends StdSerializer<ImmutableLongArray>
{
    private static final long serialVersionUID = 1L;

    public ImmutableLongArraySerializer() {
        super(ImmutableLongArray.class);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, ImmutableLongArray value) {
        return value == null || value.isEmpty();
    }

    @Override
    public void serialize(ImmutableLongArray value, JsonGenerator g, SerializerProvider ctxt)
        throws IOException
    {
        int len = value.length();
        if (len == 1 && ctxt.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) {
            g.writeNumber(value.get(0));
        } else {
            g.writeStartArray(value, len);
            for (int i = 0; i < len; i++) {
                g.writeNumber(value.get(i));
            }
            g.writeEndArray();
        }
    }
}
