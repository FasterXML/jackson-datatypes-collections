package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.iterator.CharIterator;

public final class CharIterableSerializer extends StdSerializer<CharIterable> {
    private static final long serialVersionUID = 1L;

    public static final CharIterableSerializer INSTANCE = new CharIterableSerializer();

    protected CharIterableSerializer() {
        super(CharIterable.class);
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, CharIterable value) {
        return value.isEmpty();
    }

    @Override
    public void serialize(CharIterable value, JsonGenerator gen, SerializerProvider provider) throws IOException {
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
            SerializerProvider provider,
            TypeSerializer typeSer
    ) throws IOException {
        g.assignCurrentValue(value);
        WritableTypeId typeIdDef;
        if (provider.isEnabled(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)) {
            typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.START_ARRAY));
            writeContentsAsArray(value, g);
        } else {
            typeIdDef = typeSer.writeTypePrefix(g, typeSer.typeId(value, JsonToken.VALUE_STRING));
            char[] chars = value.toArray();
            g.writeString(chars, 0, chars.length);
        }
        typeSer.writeTypeSuffix(g, typeIdDef);
    }

    private void writeContentsAsArray(CharIterable value, JsonGenerator g) throws IOException {
        char[] buf = new char[1];
        CharIterator iterator = value.charIterator();
        while (iterator.hasNext()) {
            buf[0] = iterator.next();
            g.writeString(buf, 0, 1);
        }
    }
}
