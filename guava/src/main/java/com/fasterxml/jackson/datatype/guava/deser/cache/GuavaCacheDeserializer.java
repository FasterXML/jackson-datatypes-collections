package com.fasterxml.jackson.datatype.guava.deser.cache;

import java.io.IOException;

import com.google.common.cache.Cache;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.util.ClassUtil;

public abstract class GuavaCacheDeserializer<T extends Cache<Object, Object>> 
    extends StdDeserializer<T> implements ContextualDeserializer 
{
    private static final long serialVersionUID = 1L;

    private final MapLikeType type;
    private final KeyDeserializer keyDeserializer;
    private final TypeDeserializer elementTypeDeserializer;
    private final JsonDeserializer<?> elementDeserializer;
    
    /*
     * @since 2.16 : in 3.x demote to `ContainerDeserializerBase`
     */
    private final NullValueProvider nullProvider;
    private final boolean skipNullValues;
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public GuavaCacheDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
        this(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, null);
    }
    
    public GuavaCacheDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer,
            NullValueProvider nvp)
    {
        super(type);
        this.type = type;
        this.keyDeserializer = keyDeserializer;
        this.elementTypeDeserializer = elementTypeDeserializer;
        this.elementDeserializer = elementDeserializer;
        this.nullProvider = nvp;
        skipNullValues = (nvp == null) ? false : NullsConstantProvider.isSkipper(nvp);
    }
    
    /*
    /**********************************************************
    /* Post-processing (contextualization)
    /**********************************************************
     */

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException 
    {
        KeyDeserializer kd = keyDeserializer;
        if (kd == null) {
            kd = ctxt.findKeyDeserializer(type.getKeyType(), property);
        }
        JsonDeserializer<?> valueDeser = elementDeserializer;
        final JavaType vt = type.getContentType();
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        } else { // if directly assigned, probably not yet contextual, so:
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
        }
        // Type deserializer is slightly different; must be passed, but needs to become contextual:
        TypeDeserializer vtd = elementTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        return _createContextual(type, kd, vtd, valueDeser, 
                findContentNullProvider(ctxt, property, valueDeser));
    }
    
    /*
    /**********************************************************************
    /* Abstract methods for subclasses
    /**********************************************************************
     */

    protected abstract T createCache();

    protected abstract JsonDeserializer<?> _createContextual(MapLikeType t, KeyDeserializer kd,
            TypeDeserializer vtd, JsonDeserializer<?> vd, NullValueProvider np);
    
    /*
    /**********************************************************************
    /* Implementations
    /**********************************************************************
     */

    @Override
    public LogicalType logicalType() {
        return LogicalType.Map;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return deserializeContents(p, ctxt);
    }

    private T deserializeContents(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        T cache = createCache();
        
        JsonToken currToken = p.currentToken();
        if (currToken != JsonToken.FIELD_NAME) {
            // 01-Mar-2023, tatu: [datatypes-collections#104] Handle empty Maps too
            if (currToken != JsonToken.END_OBJECT) {
                expect(p, JsonToken.START_OBJECT);
                currToken = p.nextToken();
            }
        }
        
        for (; currToken == JsonToken.FIELD_NAME; currToken = p.nextToken()) {
            final Object key;
            if (keyDeserializer != null) {
                key = keyDeserializer.deserializeKey(p.currentName(), ctxt);
            } else {
                key = p.currentName();
            }
            
            p.nextToken();
            
            final Object value;
            if (p.currentToken() == JsonToken.VALUE_NULL) {
                if (skipNullValues) {
                    continue;
                }
                value = nullProvider.getNullValue(ctxt);
            } else if (elementTypeDeserializer != null) {
                value = elementDeserializer.deserializeWithType(p, ctxt, elementTypeDeserializer);
            } else {
                value = elementDeserializer.deserialize(p, ctxt);
            }
            if (value == null) {
                _tryToAddNull(p, ctxt, cache, key);
                continue;
            }

            cache.put(key, value);
        }
        return cache;
    }

    private void expect(JsonParser p, JsonToken token) throws IOException {
        if (p.currentToken() != token) {
            throw new JsonMappingException(p, "Expecting " + token + " to start `Cache` value, found " + p.currentToken(),
                p.currentLocation());
        }
    }

    /**
     * Some/many Guava containers do not allow addition of {@code null} values,
     * so isolate handling here.
     *
     * @since 2.17
     */
    protected void _tryToAddNull(JsonParser p, DeserializationContext ctxt,
            T cache, Object key)
        throws IOException
    {
        // Ideally we'd have better idea of where nulls are accepted, but first
        // let's just produce something better than NPE:
        try {
            cache.put(key, null);
        } catch (NullPointerException e) {
            ctxt.handleUnexpectedToken(_valueType, JsonToken.VALUE_NULL, p,
                    "Guava `Cache` of type %s does not accept `null` values",
                    ClassUtil.classNameOf(cache));
        }
    }
}
