package com.fasterxml.jackson.datatype.pcollections.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import org.pcollections.PMap;

public abstract class PCollectionsMapDeserializer<T extends PMap<Object, Object>>
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
    
    protected PCollectionsMapDeserializer(MapType type, KeyDeserializer keyDeser,
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
    public abstract PCollectionsMapDeserializer<T> withResolved(KeyDeserializer keyDeser,
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
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws IOException, JsonProcessingException
    {
        // note: call "...FromObject" because expected output structure
        // for value is JSON Object (regardless of contortions used for type id)
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }
    
    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        // Ok: must point to START_OBJECT or FIELD_NAME
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) { // If START_OBJECT, move to next; may also be END_OBJECT
            t = jp.nextToken();
        }
        if (t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            throw ctxt.mappingException(_mapType.getRawClass());
        }
        return _deserializeEntries(jp, ctxt);
    }

    protected abstract T createEmptyMap();

    protected T _deserializeEntries(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException
    {
        final KeyDeserializer keyDes = _keyDeserializer;
        final JsonDeserializer<?> valueDes = _valueDeserializer;
        final TypeDeserializer typeDeser = _typeDeserializerForValue;

        T map = createEmptyMap();
        for (; p.getCurrentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
            // Must point to field name now
            String fieldName = p.getCurrentName();
            Object key = (keyDes == null) ? fieldName : keyDes.deserializeKey(fieldName, ctxt);
            // And then the value...
            JsonToken t = p.nextToken();
            // 28-Nov-2010, tatu: Should probably support "ignorable properties" in future...
            Object value;
            if (t == JsonToken.VALUE_NULL) {
                map = _handleNull(ctxt, key, _valueDeserializer, map);
                continue;
            }
            if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
            } else {
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
            @SuppressWarnings("unchecked")
            T newMap = (T) map.plus(key, value);
            map = newMap;
        }
        return map;
    }

    /**
     * Overridable helper method called when a JSON null value is encountered.
     * Since PCollections Maps typically do not allow null values, special handling
     * is needed; default is to simply ignore and skip such values, but alternative
     * could be to throw an exception.
     */
    protected T _handleNull(DeserializationContext ctxt, Object key,
            JsonDeserializer<?> valueDeser,
            T map) throws IOException
    {
        // TODO: allow reporting problem via a feature, in future?

        // Actually, first, see if there's an alternative to Java null
        Object nvl = valueDeser.getNullValue(ctxt);
        if (nvl != null) {
            @SuppressWarnings("unchecked")
            T newMap = (T) map.plus(key, nvl);
            return newMap;
        } else {
            return map;
        }
    }
}
