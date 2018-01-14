package com.fasterxml.jackson.datatype.pcollections.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.pcollections.PCollection;

public abstract class PCollectionsCollectionDeserializer<T extends PCollection<Object>>
    extends StdDeserializer<T>
{
    private static final long serialVersionUID = 1L;

    protected final CollectionType _containerType;

    /**
     * Deserializer used for values contained in collection being deserialized;
     * either assigned on constructor, or during resolve().
     */
    protected final JsonDeserializer<?> _valueDeserializer;

    /**
     * If value instances have polymorphic type information, this
     * is the type deserializer that can deserialize required type
     * information
     */
    protected final TypeDeserializer _typeDeserializerForValue;

    protected PCollectionsCollectionDeserializer(CollectionType type,
            TypeDeserializer typeDeser, JsonDeserializer<?> deser)
    {
        super(type);
        _containerType = type;
        _typeDeserializerForValue = typeDeser;
        _valueDeserializer = deser;
    }

    protected abstract T createEmptyCollection();

    /**
     * Overridable fluent factory method used for creating contextual
     * instances.
     */
    public abstract PCollectionsCollectionDeserializer<T> withResolved(
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
        JsonDeserializer<?> deser = _valueDeserializer;
        TypeDeserializer typeDeser = _typeDeserializerForValue;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(_containerType.getContentType(), property);
        }
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        if (deser == _valueDeserializer && typeDeser == _typeDeserializerForValue) {
            return this;
        }
        return withResolved(typeDeser, deser);
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
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        // Should usually point to START_ARRAY
        if (p.isExpectedStartArrayToken()) {
            return _deserializeContents(p, ctxt);
        }
        // But may support implicit arrays from single values?
        if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            return _deserializeFromSingleValue(p, ctxt);
        }
        return (T) ctxt.handleUnexpectedToken(handledType(), p);
    }

    protected T _deserializeContents(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        JsonDeserializer<?> valueDes = _valueDeserializer;
        JsonToken t;
        final TypeDeserializer typeDeser = _typeDeserializerForValue;
        // No way to pass actual type parameter; but does not matter, just
        // compiler-time fluff:
        T collection = createEmptyCollection();

        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            Object value;

            if (t == JsonToken.VALUE_NULL) {
                value = null;
            } else if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
            } else {
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
            // .plus is always overridden to return the correct subclass
            @SuppressWarnings("unchecked")
            T newCollection = (T) collection.plus(value);
            collection = newCollection;
        }
        return collection;
    }

    protected T _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
            throws IOException
    {
        JsonDeserializer<?> valueDes = _valueDeserializer;
        final TypeDeserializer typeDeser = _typeDeserializerForValue;
        JsonToken t = p.currentToken();

        Object value;
        
        if (t == JsonToken.VALUE_NULL) {
            value = null;
        } else if (typeDeser == null) {
            value = valueDes.deserialize(p, ctxt);
        } else {
            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
        }
        @SuppressWarnings("unchecked")
        T result = (T) createEmptyCollection().plus(value);
        return result;
    }
}