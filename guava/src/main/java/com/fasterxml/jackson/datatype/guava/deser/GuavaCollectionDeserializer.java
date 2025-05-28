package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.fasterxml.jackson.databind.util.ClassUtil;

/**
 * Base class for Guava-specific collection deserializers.
 */
public abstract class GuavaCollectionDeserializer<T>
    extends ContainerDeserializerBase<T>
    implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;

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
    protected final TypeDeserializer _valueTypeDeserializer;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    protected GuavaCollectionDeserializer(JavaType selfType,
            JsonDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle)
    {
        super(selfType, nuller, unwrapSingle);
        _valueTypeDeserializer = typeDeser;
        _valueDeserializer = deser;
    }

    /**
     * Overridable fluent factory method used for creating contextual
     * instances.
     */
    public abstract GuavaCollectionDeserializer<T> withResolved(
            JsonDeserializer<?> valueDeser, TypeDeserializer typeDeser, 
            NullValueProvider nuller, Boolean unwrapSingle);

    /**
     * Method called to finalize setup of this deserializer,
     * after deserializer itself has been registered. This
     * is needed to handle recursive and transitive dependencies.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class,
                JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        JsonDeserializer<?> valueDeser = _valueDeserializer;
        valueDeser = findConvertingContentDeserializer(ctxt, property, valueDeser);
        TypeDeserializer valueTypeDeser = _valueTypeDeserializer;
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(_containerType.getContentType(), property);
        }
        if (valueTypeDeser != null) {
            valueTypeDeser = valueTypeDeser.forProperty(property);
        }

        NullValueProvider nuller = findContentNullProvider(ctxt, property, valueDeser);

        if ( (unwrapSingle != _unwrapSingle)
                || (nuller != _nullProvider)
                || (valueDeser != _valueDeserializer)
                || (valueTypeDeser != _valueTypeDeserializer)) {
            return withResolved(valueDeser, valueTypeDeser, nuller, unwrapSingle);
        }
        return this;
    }

    /*
    /**********************************************************
    /* Base class method implementations
    /**********************************************************
     */

    @SuppressWarnings("unchecked")
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return (JsonDeserializer<Object>) _valueDeserializer;
    }

    @Override // since 2.12
    public LogicalType logicalType() {
        return LogicalType.Collection;
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
        final boolean canWrap = (_unwrapSingle == Boolean.TRUE) ||
                ((_unwrapSingle == null) && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (canWrap) {
            return _deserializeFromSingleValue(p, ctxt);
        }
        // Otherwise, we have a problem
        return (T) ctxt.handleUnexpectedToken(_valueClass, p);
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
    public abstract Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException;

    protected abstract T _deserializeContents(JsonParser p, DeserializationContext ctxt)
            throws IOException;

    /**
     * Method used to support implicit coercion from a single non-array value
     * into single-element collection.
     * 
     * @since 2.3
     */
    protected T _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        final JsonDeserializer<?> valueDes = _valueDeserializer;
        final TypeDeserializer typeDeser = _valueTypeDeserializer;
        final JsonToken t = p.currentToken();

        final Object value;

        if (t == JsonToken.VALUE_NULL) {
            if (_skipNullValues) {
                return _createEmpty(ctxt);
            }
            value = _nullProvider.getNullValue(ctxt);
        } else if (typeDeser == null) {
            value = valueDes.deserialize(p, ctxt);
        } else {
            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
        }
        return _createWithSingleElement(ctxt, value);

    }

    // Note: 'throws IOException' dropped from 2.10.5
    protected abstract T _createEmpty(DeserializationContext ctxt);

    // Note: 'throws IOException' dropped from 2.12.0
    protected abstract T _createWithSingleElement(DeserializationContext ctxt, Object value);

    /**
     * Some/many Guava containers do not allow addition of {@code null} values,
     * so isolate handling here.
     *
     * @since 2.17
     */
    protected void _tryToAddNull(JsonParser p, DeserializationContext ctxt, Collection<?> set)
        throws IOException
    {
        // Ideally we'd have better idea of where nulls are accepted, but first
        // let's just produce something better than NPE:
        try {
            set.add(null);
        } catch (NullPointerException e) {
            ctxt.handleUnexpectedToken(_valueType, JsonToken.VALUE_NULL, p,
                    "Guava `Collection` of type %s does not accept `null` values",
                    ClassUtil.getTypeDescription(getValueType(ctxt)));
        }
    }
}
