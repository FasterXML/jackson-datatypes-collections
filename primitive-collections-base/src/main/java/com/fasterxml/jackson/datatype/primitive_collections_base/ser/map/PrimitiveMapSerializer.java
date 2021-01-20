package com.fasterxml.jackson.datatype.primitive_collections_base.ser.map;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * @author yawkat
 */
public abstract class PrimitiveMapSerializer<T> extends StdSerializer<T>
{
    protected PrimitiveMapSerializer(Class<T> t) {
        super(t);
    }

    protected PrimitiveMapSerializer(JavaType type) {
        super(type);
    }

    @Override
    public void serializeWithType(T value, JsonGenerator gen, SerializerProvider ctxt, TypeSerializer typeSer)
        throws JacksonException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(
                gen, ctxt, typeSer.typeId(value, JsonToken.START_OBJECT));
        serializeEntries(value, gen, ctxt);
        typeSer.writeTypeSuffix(gen, ctxt, typeIdDef);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider ctxt)
        throws JacksonException
    {
        gen.writeStartObject(value);
        serializeEntries(value, gen, ctxt);
        gen.writeEndObject();
    }

    protected abstract void serializeEntries(T value, JsonGenerator gen, SerializerProvider serializers)
        throws JacksonException;
}
