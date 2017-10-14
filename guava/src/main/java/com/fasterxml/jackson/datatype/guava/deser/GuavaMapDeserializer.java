package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapType;

public abstract class GuavaMapDeserializer<T>
    extends JsonDeserializer<T>
    implements ContextualDeserializer
{
    protected final MapType _mapType;
    
    /**
     * Key deserializer used, if not null. If null, String from JSON
     * content is used as is.
     */
    protected KeyDeserializer _keyDeserializer;

    /**
     * Value deserializer.
     */
    protected JsonDeserializer<?> _valueDeserializer;

    /**
     * If value instances have polymorphic type information, this
     * is the type deserializer that can handle it
     */
    protected final TypeDeserializer _typeDeserializerForValue;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */
    
    protected GuavaMapDeserializer(MapType type, KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, JsonDeserializer<?> deser)
    {
        _mapType = type;
        _keyDeserializer = keyDeser;
        _typeDeserializerForValue = typeDeser;
        _valueDeserializer = deser;
    }

    /**
     * Overridable fluent factory method used for creating contextual
     * instances.
     */
    public abstract GuavaMapDeserializer<T> withResolved(KeyDeserializer keyDeser,
            TypeDeserializer typeDeser, JsonDeserializer<?> valueDeser);
    
    /*
    /**********************************************************
    /* Validation, post-processing
    /**********************************************************
     */

    /**
     * Method called to finalize setup of this deserializer,
     * after deserializer itself has been registered. This
     * is needed to handle recursive and transitive dependencies.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        KeyDeserializer keyDeser = _keyDeserializer;
        JsonDeserializer<?> deser = _valueDeserializer;
        TypeDeserializer typeDeser = _typeDeserializerForValue;
        // Do we need any contextualization?
        if ((keyDeser != null) && (deser != null) && (typeDeser == null)) { // nope
            return this;
        }
        if (keyDeser == null) {
            keyDeser = ctxt.findKeyDeserializer(_mapType.getKeyType(), property);
        }
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(_mapType.getContentType(), property);
        }
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        return withResolved(keyDeser, typeDeser, deser);
    }

    /*
    /**********************************************************
    /* Deserialization interface
    /**********************************************************
     */

    /**
     * Base implementation that does not assume specific type
     * inclusion mechanism. Sub-classes are expected to override
     * this method if they are to handle type information.
     */
    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws IOException
    {
        // note: call "...FromObject" because expected output structure
        // for value is JSON Object (regardless of contortions used for type id)
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        // Ok: must point to START_OBJECT or FIELD_NAME
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) { // If START_OBJECT, move to next; may also be END_OBJECT
            t = p.nextToken();
        }
        if (t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            return (T) ctxt.handleUnexpectedToken(_mapType.getRawClass(), p);
        }
        return _deserializeEntries(p, ctxt);
    }

    /*
    /**********************************************************************
    /* Abstract methods for impl classes
    /**********************************************************************
     */

    protected abstract T _deserializeEntries(JsonParser p, DeserializationContext ctxt)
        throws IOException;
}
