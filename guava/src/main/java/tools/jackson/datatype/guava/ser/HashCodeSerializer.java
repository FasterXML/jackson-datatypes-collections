package tools.jackson.datatype.guava.ser;

import com.google.common.hash.HashCode;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.ToStringSerializerBase;

/**
 * Serializer for Guava {@link HashCode} values: writes hex String representation.
 *<p>
 * Type id (if any) is always written as that of {@link HashCode}, not of the actual
 * (non-public) implementation subtype, so that it stays readable regardless of
 * Guava internals (see [datatypes-collections#243]).
 */
public class HashCodeSerializer extends ToStringSerializerBase
{
    public final static HashCodeSerializer instance = new HashCodeSerializer();

    public HashCodeSerializer() { super(HashCode.class); }

    @Override
    public String valueToString(Object value) {
        return value.toString();
    }

    @Override
    public void serializeWithType(Object value, JsonGenerator g, SerializationContext ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, HashCode.class, JsonToken.VALUE_STRING));
        serialize(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }
}
