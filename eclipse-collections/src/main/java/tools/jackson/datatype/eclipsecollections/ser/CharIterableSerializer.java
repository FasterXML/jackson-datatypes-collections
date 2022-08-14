package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;

import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializerProvider;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.iterator.CharIterator;

public final class CharIterableSerializer extends StdSerializer<CharIterable> {
    public static final CharIterableSerializer INSTANCE = new CharIterableSerializer();

    protected CharIterableSerializer() {
        super(CharIterable.class);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, CharIterable value) {
        return value.isEmpty();
    }

    @Override
    public void serialize(CharIterable value, JsonGenerator gen, SerializerProvider provider)
            throws JacksonException
    {
        if (provider.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
            gen.writeStartArray();
            writeContentsAsArray(value, gen);
            gen.writeEndArray();
        } else {
            char[] chars = value.toArray();
            gen.writeString(chars, 0, chars.length);
        }
    }

    @Override
    public void serializeWithType(
            CharIterable value,
            JsonGenerator g,
            SerializerProvider ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        g.assignCurrentValue(value);
        WritableTypeId typeIdDef;
        if (ctxt.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
            typeIdDef = typeSer.writeTypePrefix(g, ctxt, typeSer.typeId(value, JsonToken.START_ARRAY));
            writeContentsAsArray(value, g);
        } else {
            typeIdDef = typeSer.writeTypePrefix(g, ctxt, typeSer.typeId(value, JsonToken.VALUE_STRING));
            char[] chars = value.toArray();
            g.writeString(chars, 0, chars.length);
        }
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    private void writeContentsAsArray(CharIterable value, JsonGenerator g)
        throws JacksonException
    {
        char[] buf = new char[1];
        CharIterator iterator = value.charIterator();
        while (iterator.hasNext()) {
            buf[0] = iterator.next();
            g.writeString(buf, 0, 1);
        }
    }
}
