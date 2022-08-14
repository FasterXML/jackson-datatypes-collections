package tools.jackson.datatype.primitive_collections_base.ser.map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializerProvider;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

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
