package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import com.google.common.cache.Cache;

public class CacheSerializer extends StdSerializer<Cache<?, ?>>
{
    public CacheSerializer() {
        super(Cache.class);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Cache<?, ?> value) {
        // Since we serialize all as empty, let's claim we are always empty
        return true;
    }

    @Override
    public void serialize(Cache<?, ?> value, JsonGenerator gen, SerializerProvider provider)
        throws JacksonException
    {
        gen.writeStartObject(value);
        _writeContents(value, gen, provider);
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(Cache<?, ?> value, JsonGenerator gen, SerializerProvider ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        gen.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, ctxt,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        _writeContents(value, gen, ctxt);
        typeSer.writeTypeSuffix(gen, ctxt, typeIdDef);
    }

    // Just a stub in case we have time to implement proper (if optional) serialization
    protected void _writeContents(Cache<?, ?> value, JsonGenerator g, SerializerProvider provider)
        throws JacksonException
    {
    }    
}
