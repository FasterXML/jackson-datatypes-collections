package tools.jackson.datatype.guava.ser.primitives;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import com.google.common.primitives.ImmutableLongArray;

public final class ImmutableLongArraySerializer extends StdSerializer<ImmutableLongArray>
{
    public ImmutableLongArraySerializer() {
        super(ImmutableLongArray.class);
    }

    @Override
    public boolean isEmpty(SerializationContext ctxt, ImmutableLongArray value) {
        return value == null || value.isEmpty();
    }

    @Override
    public void serialize(ImmutableLongArray value, JsonGenerator g, SerializationContext ctxt)
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

    @Override
    public void serializeWithType(ImmutableLongArray value, JsonGenerator generator,
            SerializationContext ctxt, TypeSerializer typeSer)
    {
        WritableTypeId typeId = typeSer.writeTypePrefix(generator, ctxt,
                typeSer.typeId(value, JsonToken.START_ARRAY));
        for (int i = 0, len = value.length(); i < len; i++) {
            generator.writeNumber(value.get(i));
        }
        typeSer.writeTypeSuffix(generator, ctxt, typeId);
    }
}
