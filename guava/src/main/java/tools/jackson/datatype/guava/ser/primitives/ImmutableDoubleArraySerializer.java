package tools.jackson.datatype.guava.ser.primitives;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ser.std.StdSerializer;
import com.google.common.primitives.ImmutableDoubleArray;

public final class ImmutableDoubleArraySerializer
    extends StdSerializer<ImmutableDoubleArray>
{
    public ImmutableDoubleArraySerializer() {
        super(ImmutableDoubleArray.class);
    }

    @Override
    public boolean isEmpty(SerializationContext ctxt, ImmutableDoubleArray value) {
        return value == null || value.isEmpty();
    }

    @Override
    public void serialize(ImmutableDoubleArray value, JsonGenerator generator,
            SerializationContext ctxt)
    {
        int len = value.length();
        if (len == 1 && ctxt.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) {
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
