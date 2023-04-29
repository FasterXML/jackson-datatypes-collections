package com.fasterxml.jackson.datatype.guava.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.cache.Cache;

import java.util.Map;

public class CacheSerializer extends StdSerializer<Cache<?, ?>>
{
    private static final long serialVersionUID = 1L;

    public CacheSerializer() {
        super(Cache.class, false);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Cache<?, ?> value) {
        // Since we serialize all as empty, let's claim we are always empty
        return true;
    }

    @Override
    public void serialize(Cache<?, ?> value, JsonGenerator gen, SerializerProvider provider)
        throws IOException
    {
        gen.writeStartObject(value);
        _writeContents(value, gen, provider);
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(Cache<?, ?> value, JsonGenerator gen, SerializerProvider ctxt,
            TypeSerializer typeSer)
        throws IOException
    {
        gen.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        _writeContents(value, gen, ctxt);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    // Just a stub in case we have time to implement proper (if optional) serialization
    protected void _writeContents(Cache<?, ?> value, JsonGenerator g, SerializerProvider ctxt)
        throws IOException
    {
        for (Map.Entry<?, ?> entry : value.asMap().entrySet()) {
                g.writeFieldName(String.valueOf(entry.getKey()));
                ctxt.defaultSerializeValue(entry.getValue(), g);    
        }
    }
}
