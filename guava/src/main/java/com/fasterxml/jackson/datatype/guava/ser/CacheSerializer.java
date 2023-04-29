package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import com.google.common.cache.Cache;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CacheSerializer extends StdSerializer<Cache<?, ?>>
    implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;

    /**
     * Set of entries to omit during serialization, if any
     *
     * @since 2.16
     */
    protected Set<String> _ignoredEntries;
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */
    
    public CacheSerializer(Set<String> ignored) {
        super(Cache.class, false);
        _ignoredEntries = ignored;
    }

    private JsonSerializer<?> withResolved(Set<String> ignored) {
        return new CacheSerializer(ignored);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        final AnnotatedMember propertyAcc = (property == null) ? null : property.getMember();

        // ignores
        Set<String> ignored = _ignoredEntries;
        if (intr != null && propertyAcc != null) {
            JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnoralByName(provider.getConfig(), propertyAcc);
            if (ignorals != null) {
                Set<String> newIgnored = ignorals.findIgnoredForSerialization();
                if ((newIgnored != null) && !newIgnored.isEmpty()) {
                    ignored = (ignored == null) ? new HashSet<>() : new HashSet<>(ignored);
                    ignored.addAll(newIgnored);
                }
            }
        }
        return withResolved(ignored);
    }
    
    /*
    /**********************************************************
    /* Abstract method implementations
    /**********************************************************
     */

    @Override
    public boolean isEmpty(SerializerProvider prov, Cache<?, ?> value) {
        return value != null && value.size() != 0;
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
    
    /*
    /**********************************************************
    /* Internal helper methods
    /**********************************************************
     */

    protected void _writeContents(Cache<?, ?> cache, JsonGenerator gen, SerializerProvider provider)
        throws IOException
    {
        final Set<String> ignored = _ignoredEntries;
        for (Map.Entry<?, ?> entry : cache.asMap().entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            // ignore? 
            if (ignored != null && ignored.contains(key.toString())) {
                continue;
            }
            gen.writeFieldName(key.toString());
            provider.defaultSerializeValue(value, gen);
        }
    }
}
