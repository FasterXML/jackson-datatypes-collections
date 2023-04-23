package tools.jackson.datatype.guava.deser;

import tools.jackson.core.*;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.ContextualKeyDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.deser.std.ContainerDeserializerBase;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.AccessPattern;

public abstract class GuavaMapDeserializer<T>
    extends ContainerDeserializerBase<T>
{
    /**
     * Key deserializer used, if not null. If null, String from JSON
     * content is used as is.
     */
    protected KeyDeserializer _keyDeserializer;

    /**
     * Value deserializer.
     */
    protected ValueDeserializer<?> _valueDeserializer;

    /**
     * If value instances have polymorphic type information, this
     * is the type deserializer that can handle it
     */
    protected final TypeDeserializer _valueTypeDeserializer;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */
    
    protected GuavaMapDeserializer(JavaType type, KeyDeserializer keyDeser,
            ValueDeserializer<?> valueDeser, TypeDeserializer valueTypeDeser,
            NullValueProvider nuller)
    {
        super(type, nuller, null);
        _keyDeserializer = keyDeser;
        _valueDeserializer = valueDeser;
        _valueTypeDeserializer = valueTypeDeser;
    }

    /**
     * Overridable fluent factory method used for creating contextual
     * instances.
     */
    public abstract GuavaMapDeserializer<T> withResolved(KeyDeserializer keyDeser,
            ValueDeserializer<?> valueDeser, TypeDeserializer valueTypeDeser,
            NullValueProvider nuller);

    /*
    /**********************************************************************
    /* Abstract method impl
    /**********************************************************************
     */

    @SuppressWarnings("unchecked")
    @Override
    public ValueDeserializer<Object> getContentDeserializer() {
        return (ValueDeserializer<Object>) _valueDeserializer;
    }

    @Override // since 2.12
    public LogicalType logicalType() {
        return LogicalType.Map;
    }

    /*
    /**********************************************************************
    /* Validation, post-processing
    /**********************************************************************
     */

    /**
     * Method called to finalize setup of this deserializer,
     * after deserializer itself has been registered. This
     * is needed to handle recursive and transitive dependencies.
     */
    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property)
    {
        KeyDeserializer keyDeser = _keyDeserializer;
        ValueDeserializer<?> valueDeser = _valueDeserializer;
        valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
        TypeDeserializer valueTypeDeser = _valueTypeDeserializer;

        // First: fetch and/or contextualize deserializers (key, value, value type)
        if (keyDeser == null) {
            keyDeser = ctxt.findKeyDeserializer(_containerType.getKeyType(), property);
        } else {
            if (keyDeser instanceof ContextualKeyDeserializer) {
                keyDeser = ((ContextualKeyDeserializer) keyDeser).createContextual(ctxt, property);
            }
        }
        final JavaType vt = _containerType.getContentType();
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        } else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
        }
        if (valueTypeDeser != null) {
            valueTypeDeser = valueTypeDeser.forProperty(property);
        }

        // Then other handlers

        NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);

        // !!! 08-Aug-2019, tatu: TODO: null skipping? Ignored properties?
        
        if ((_keyDeserializer == keyDeser) && (_valueDeserializer == valueDeser)
                && (_valueTypeDeserializer == valueTypeDeser)
                && (_nullProvider == nuller)
                ) {
            return this;
        }
        
        return withResolved(keyDeser, valueDeser, valueTypeDeser, nuller);
    }

    /*
    /**********************************************************************
    /* Deserialization interface
    /**********************************************************************
     */

    /**
     * Base implementation that does not assume specific type
     * inclusion mechanism. Sub-classes are expected to override
     * this method if they are to handle type information.
     */
    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        // note: call "...FromObject" because expected output structure
        // for value is JSON Object (regardless of contortions used for type id)
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
    {
        // Ok: must point to START_OBJECT or PROPERTY_NAME
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) { // If START_OBJECT, move to next; may also be END_OBJECT
            t = p.nextToken();
        }
        if (t != JsonToken.PROPERTY_NAME && t != JsonToken.END_OBJECT) {
            return (T) ctxt.handleUnexpectedToken(_containerType, p);
        }
        return _deserializeEntries(p, ctxt);
    }

    /*
    /**********************************************************************
    /* Abstract methods for impl classes
    /**********************************************************************
     */

    // Force abstract-ness for subclasses
    @Override
    public abstract AccessPattern getEmptyAccessPattern();

    // Force abstract-ness for subclasses
    @Override
    public abstract Object getEmptyValue(DeserializationContext ctxt);

    protected abstract T _deserializeEntries(JsonParser p, DeserializationContext ctxt)
        throws JacksonException;
}
