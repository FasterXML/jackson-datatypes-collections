package tools.jackson.datatype.guava.deser;

import java.util.Collection;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.*;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.deser.std.ContainerDeserializerBase;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.AccessPattern;
import tools.jackson.databind.util.ClassUtil;
import tools.jackson.datatype.guava.util.NullElementHelper;

/**
 * Base class for Guava-specific collection deserializers.
 */
public abstract class GuavaCollectionDeserializer<T>
    extends ContainerDeserializerBase<T>
{
    /**
     * Marker value returned by {@link #_deserializeElement} for elements
     * to skip.
     */
    protected static final Object SKIPPED_ELEMENT = new Object();

    /**
     * Deserializer used for values contained in collection being deserialized;
     * either assigned on constructor, or during resolve().
     */
    protected final ValueDeserializer<?> _valueDeserializer;

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
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
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
            ValueDeserializer<?> valueDeser, TypeDeserializer typeDeser, 
            NullValueProvider nuller, Boolean unwrapSingle);

    /**
     * Method called to finalize setup of this deserializer,
     * after deserializer itself has been registered. This
     * is needed to handle recursive and transitive dependencies.
     */
    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property)
    {
        Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class,
                JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        ValueDeserializer<?> valueDeser = _valueDeserializer;
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
    public ValueDeserializer<Object> getContentDeserializer() {
        return (ValueDeserializer<Object>) _valueDeserializer;
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
        throws JacksonException
    {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
            throws JacksonException
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
        return (T) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
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

    protected abstract T _deserializeContents(JsonParser p, DeserializationContext ctxt)
        throws JacksonException;

    /**
     * Method used to support implicit coercion from a single non-array value
     * into single-element collection.
     */
    protected T _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        final JsonToken t = p.currentToken();
        Object value = _deserializeElement(p, ctxt, t);
        if (value != SKIPPED_ELEMENT) {
            // Avoid lambda allocation for the common non-null case
            if (value != null) {
                return _createWithSingleElement(ctxt, value);
            }
            T result = _addElement(p, ctxt, p.currentToken(), null,
                    v -> _createWithSingleElement(ctxt, v));
            // `null` if element dropped by problem handler
            if (result != null) {
                return result;
            }
        }
        return _createEmpty(ctxt);
    }

    /**
     * Helper method for deserializing a single element, given its first token.
     * Only JSON {@code null} tokens are skipped (if so configured): {@code null}
     * returned by value deserializer (or null provider) is returned as-is.
     *
     * @return Element deserialized (possibly {@code null}), or {@link #SKIPPED_ELEMENT}
     *    if element is to be skipped
     */
    protected Object _deserializeElement(JsonParser p, DeserializationContext ctxt,
            JsonToken t)
        throws JacksonException
    {
        if (t == JsonToken.VALUE_NULL) {
            if (_skipNullValues) {
                return SKIPPED_ELEMENT;
            }
            return _nullProvider.getNullValue(ctxt);
        }
        if (_valueTypeDeserializer == null) {
            return _valueDeserializer.deserialize(p, ctxt);
        }
        return _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
    }

    /**
     * Helper method for adding an element using given adder; {@code null}s that
     * the container does not accept are reported as problems.
     *
     * @param t Token parser is currently positioned at (for structured values,
     *    the matching {@code END_OBJECT} / {@code END_ARRAY}, not the start token)
     *
     * @return Result of adder, or {@code null} if element was dropped by
     *    a problem handler
     */
    protected <R> R _addElement(JsonParser p, DeserializationContext ctxt, JsonToken t,
            Object value, Function<Object, R> adder)
        throws JacksonException
    {
        return NullElementHelper.addElement(p, ctxt, t, _containerType.getContentType(),
                "Guava `Collection` of type "+ClassUtil.getTypeDescription(_containerType),
                value, adder);
    }

    protected abstract T _createEmpty(DeserializationContext ctxt);

    protected abstract T _createWithSingleElement(DeserializationContext ctxt, Object value);

    /**
     * Helper method for checking whether element type is untyped ({@code Object}),
     * in which case sorted containers need to use {@link UntypedElementComparator}
     * instead of natural ordering.
     */
    protected boolean _hasUntypedElements() {
        return _containerType.getContentType().hasRawClass(Object.class);
    }

    /**
     * Helper method for reporting failure to build a container, typically a sorted
     * one failing (with {@link ClassCastException}) due to elements that are not
     * mutually comparable.
     */
    protected <R> R _reportBuildFailure(DeserializationContext ctxt, String msg)
        throws JacksonException
    {
        return ctxt.reportInputMismatch(this,
                "Failed to build %s from deserialized elements: %s",
                ClassUtil.getTypeDescription(_containerType), msg);
    }

    /**
     * Some/many Guava containers do not allow addition of {@code null} values,
     * so isolate handling here.
     *
     * @since 2.17
     */
    protected void _tryToAddNull(JsonParser p, DeserializationContext ctxt, Collection<?> set)
    {
        _addElement(p, ctxt, p.currentToken(), null, v -> {
            @SuppressWarnings("unchecked")
            Collection<Object> coll = (Collection<Object>) set;
            return coll.add(v);
        });
    }
}
