package com.fasterxml.jackson.datatype.primitive_collections_base.ser.map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;

/**
 * @author yawkat
 */
public abstract class PrimitiveMapSerializer<T> extends StdSerializer<T> {
    private static final long serialVersionUID = 0L;

    protected PrimitiveMapSerializer(Class<T> t) {
        super(t);
    }

    protected PrimitiveMapSerializer(JavaType type) {
        super(type);
    }

    @Override
    public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
            throws IOException {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(
                gen, typeSer.typeId(value, JsonToken.START_OBJECT));
        serializeEntries(value, gen, serializers);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject(value);
        serializeEntries(value, gen, provider);
        gen.writeEndObject();
    }

    protected abstract void serializeEntries(T value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException;
}
