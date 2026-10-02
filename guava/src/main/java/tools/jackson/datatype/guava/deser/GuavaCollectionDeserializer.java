package tools.jackson.datatype.guava.deser;

import java.util.Collection;
import java.util.function.ObjIntConsumer;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.*;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.deser.std.ContainerDeserializerBase;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.AccessPattern;
import tools.jackson.databind.util.ClassUtil;

/**
 * Base class for Guava-specific collection deserializers.
 */
public abstract class GuavaCollectionDeserializer<T>
    extends ContainerDeserializerBase<T>
{
    /**
     * Default maximum size (sum of element counts) of a
     * {@link com.google.common.collect.Multiset} read from entries.
     *
     * @since 3.3
     */
    public final static int DEFAULT_MAX_MULTISET_SIZE = 10_000_000;

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
        final ValueDeserializer<?> valueDes = _valueDeserializer;
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

    protected abstract T _createEmpty(DeserializationContext ctxt);

    protected abstract T _createWithSingleElement(DeserializationContext ctxt, Object value);

    /**
     * Helper method for reading {@link com.google.common.collect.Multiset} entries,
     * serialized as {@code {"element":...,"count":...}}, until the end of
     * enclosing JSON Array.
     *
     * @since 3.3
     */
    protected void _deserializeMultisetEntries(JsonParser p, DeserializationContext ctxt,
            ObjIntConsumer<Object> adder, int maxSize)
        throws JacksonException
    {
        int size = 0;
        while (p.nextToken() != JsonToken.END_ARRAY) {
            size += _deserializeMultisetEntry(p, ctxt, adder, size, maxSize);
        }
    }

    /**
     * Helper method for reading a single {@link com.google.common.collect.Multiset}
     * entry: parser is expected to point to {@code START_OBJECT} of the entry.
     *
     * @param sizeSoFar Number of elements (sum of counts) already read
     * @param maxSize Maximum number of elements (sum of counts) allowed
     *
     * @return Number of elements added (count of the entry, or 0 if skipped)
     *
     * @since 3.3
     */
    protected int _deserializeMultisetEntry(JsonParser p, DeserializationContext ctxt,
            ObjIntConsumer<Object> adder, int sizeSoFar, int maxSize)
        throws JacksonException
    {
        if (!p.hasToken(JsonToken.START_OBJECT)) {
            ctxt.reportInputMismatch(this,
"Unexpected token (%s) for `Multiset` entry: expected JSON Object with properties \"element\" and \"count\"",
                    p.currentToken());
        }
        Object element = null;
        boolean hasElement = false;
        boolean skipEntry = false;
        int count = 0;
        boolean hasCount = false;

        for (String name = p.nextName(); name != null; name = p.nextName()) {
            final JsonToken t = p.nextToken();
            if ("element".equals(name)) {
                hasElement = true;
                if (t == JsonToken.VALUE_NULL) {
                    skipEntry = _skipNullValues;
                    element = skipEntry ? null : _nullProvider.getNullValue(ctxt);
                } else if (_valueTypeDeserializer == null) {
                    element = _valueDeserializer.deserialize(p, ctxt);
                } else {
                    element = _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
                }
            } else if ("count".equals(name)) {
                if (t != JsonToken.VALUE_NUMBER_INT) {
                    ctxt.reportInputMismatch(this,
                            "Invalid `Multiset` entry \"count\": expected positive integer, got %s", t);
                }
                count = p.getIntValue();
                if (count < 1) {
                    ctxt.reportInputMismatch(this,
                            "Invalid `Multiset` entry \"count\": expected positive integer, got %d", count);
                }
                hasCount = true;
            } else {
                handleUnknownProperty(p, ctxt, handledType(), name);
            }
        }
        if (!hasElement) {
            ctxt.reportInputMismatch(this, "Invalid `Multiset` entry: missing \"element\" property");
        }
        if (!hasCount) {
            ctxt.reportInputMismatch(this, "Invalid `Multiset` entry: missing \"count\" property");
        }
        if (skipEntry) {
            return 0;
        }
        if ((long) sizeSoFar + count > maxSize) {
            ctxt.reportInputMismatch(this,
                    "`Multiset` size (%d) exceeds the maximum allowed (%d, from `GuavaModule.configureMaxMultisetSize()`)",
                    (long) sizeSoFar + count, maxSize);
        }
        try {
            adder.accept(element, count);
            return count;
        } catch (NullPointerException e) {
            if (element != null) {
                throw e;
            }
            ctxt.handleUnexpectedToken(_valueType, JsonToken.VALUE_NULL, p,
                    "Guava `Collection` of type %s does not accept `null` values",
                    ClassUtil.getTypeDescription(getValueType(ctxt)));
        } catch (ClassCastException | IllegalArgumentException e) {
            // elements of sorted Multiset not mutually comparable; or too many occurrences
            _reportMultisetFailure(ctxt, e);
        }
        return 0;
    }

    /**
     * @since 3.3
     */
    protected <R> R _reportMultisetFailure(DeserializationContext ctxt, RuntimeException e)
        throws JacksonException
    {
        String msg = e.getMessage();
        return ctxt.reportInputMismatch(this,
                "Failed to build `%s` from deserialized elements: %s",
                handledType().getSimpleName(),
                (msg == null) ? ClassUtil.nameOf(e.getClass()) : msg);
    }

    /**
     * Some/many Guava containers do not allow addition of {@code null} values,
     * so isolate handling here.
     *
     * @since 2.17
     */
    protected void _tryToAddNull(JsonParser p, DeserializationContext ctxt, Collection<?> set)
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
