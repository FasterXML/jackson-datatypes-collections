package com.fasterxml.jackson.datatype.hppc.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;

/**
 * Base class for various container (~= Collection) serializers.
 */
public abstract class ContainerSerializerBase<T>
    extends ContainerSerializer<T>
{
    protected final String _schemeElementType;

    protected ContainerSerializerBase(Class<T> type, String schemaElementType)
    {
        super(type);
        _schemeElementType = schemaElementType;
    }

    protected ContainerSerializerBase(JavaType type, String schemaElementType)
    {
        super(type, null);
        _schemeElementType = schemaElementType;
    }

    protected ContainerSerializerBase(ContainerSerializerBase<?> src) {
        super(src._handledType);
        _schemeElementType = src._schemeElementType;
    }

    /*
    /**********************************************************
    /* Simple accessor overrides, defaults
    /**********************************************************
     */

    @Override
    public abstract boolean isEmpty(SerializerProvider provider, T value);

    @Override
    public JsonSerializer<?> getContentSerializer() {
        // We are not delegating, for most part, so while not dynamic claim we don't have it
        return null;
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer vts) {
        // May or may not be supportable, but for now fail loudly, not quietly
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaType getContentType() {
        // Not sure how to efficiently support this; could resolve types of course
        return null;
    }

    @Override
    public abstract void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException;

    /*
    /**********************************************************
    /* Serialization
    /**********************************************************
     */
    
    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider)
        throws IOException
    {
        gen.setCurrentValue(value);
        gen.writeStartArray();
        serializeContents(value, gen, provider);
        gen.writeEndArray();
    }

    protected abstract void serializeContents(T value, JsonGenerator gen, SerializerProvider provider)
            throws IOException;
    
    @Override
    public void serializeWithType(T value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer)
        throws IOException
    {
        gen.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                typeSer.typeId(value, JsonToken.START_ARRAY));
        serializeContents(value, gen, provider);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    /*
    /**********************************************************
    /* Helper methods for sub-classes
    /**********************************************************
     */
    
    protected JsonSerializer<?> getSerializer(JavaType type)
    {
        if (_handledType.isAssignableFrom(type.getRawClass())) {
            return this;
        }
        return null;
    }
}
