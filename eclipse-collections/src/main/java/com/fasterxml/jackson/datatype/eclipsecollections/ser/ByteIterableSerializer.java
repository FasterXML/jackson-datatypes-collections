package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.SerializerProvider;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

import org.eclipse.collections.api.ByteIterable;

public final class ByteIterableSerializer extends StdSerializer<ByteIterable> {
    public static final ByteIterableSerializer INSTANCE = new ByteIterableSerializer();

    public ByteIterableSerializer() {
        super(ByteIterable.class);
    }

    @Override
    public void serialize(ByteIterable value, JsonGenerator g, SerializerProvider ctxt)
        throws JacksonException
    {
        byte[] arr = value.toArray();
        g.writeBinary(ctxt.getConfig().getBase64Variant(), arr, 0, arr.length);
    }

    @Override
    public void serializeWithType(
            ByteIterable value, JsonGenerator g, SerializerProvider ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, JsonToken.VALUE_EMBEDDED_OBJECT));
        byte[] arr = value.toArray();
        g.writeBinary(ctxt.getConfig().getBase64Variant(), arr, 0, arr.length);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }
}
