package com.fasterxml.jackson.datatype.guava.deser;

import static java.util.Arrays.asList;

import java.io.IOException;

import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.TypeFactory;

import com.fasterxml.jackson.datatype.guava.deser.util.RangeFactory;
import com.fasterxml.jackson.datatype.guava.deser.util.RangeHelper;

/**
 * Jackson deserializer for a Guava {@link Range}.
 *<p>
 * TODO: I think it would make sense to reimplement this deserializer to
 * use Delegating Deserializer, using a POJO as an intermediate form (properties
 * could be of type {@link java.lang.Object})
 * This would also also simplify the implementation a bit.
 */
public class RangeDeserializer
    extends StdDeserializer<Range<?>>
{
    protected final JavaType _rangeType;

    protected final JsonDeserializer<Object> _endpointDeserializer;
    protected final BoundType _defaultBoundType;

    protected final RangeHelper.RangeProperties _fieldNames;
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public RangeDeserializer(JavaType rangeType, BoundType defaultBoundType) {
        this(rangeType, null, defaultBoundType, RangeHelper.standardNames());
    }

    @SuppressWarnings("unchecked")
    protected RangeDeserializer(JavaType rangeType, JsonDeserializer<?> endpointDeser,
            BoundType defaultBoundType, RangeHelper.RangeProperties fieldNames)
    {
        super(rangeType);
        _rangeType = rangeType;
        _endpointDeserializer = (JsonDeserializer<Object>) endpointDeser;
        _defaultBoundType = defaultBoundType;
        _fieldNames = fieldNames;
    }

    @Override
    public JavaType getValueType() { return _rangeType; }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        final RangeHelper.RangeProperties fieldNames = RangeHelper.getPropertyNames(ctxt.getConfig(),
                ctxt.getConfig().getPropertyNamingStrategy());
        JsonDeserializer<?> deser = _endpointDeserializer;
        if (deser == null) {
            JavaType endpointType = _rangeType.containedType(0);
            if (endpointType == null) { // should this ever occur?
                endpointType = TypeFactory.unknownType();
            }
            deser = ctxt.findContextualValueDeserializer(endpointType, property);
        } else if (deser instanceof ContextualDeserializer) {
            // 04-Sep-2019, tatu: If we already have a deserialize, should contextualize, right?
            deser = ((ContextualDeserializer) deser).createContextual(ctxt, property);
        }
        if ((deser != _endpointDeserializer) || (fieldNames != _fieldNames)) {
            return new RangeDeserializer(_rangeType, deser, _defaultBoundType, fieldNames);
        }
        return this;
    }

    /*
    /**********************************************************
    /* Actual deserialization
    /**********************************************************
     */

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws IOException
    {
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    @Override
    public Range<?> deserialize(JsonParser p, DeserializationContext context)
            throws IOException
    {
        // NOTE: either START_OBJECT _or_ FIELD_NAME fine; latter for polymorphic cases
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }

        Comparable<?> lowerEndpoint = null;
        Comparable<?> upperEndpoint = null;
        BoundType lowerBoundType = _defaultBoundType;
        BoundType upperBoundType = _defaultBoundType;

        for (; t != JsonToken.END_OBJECT; t = p.nextToken()) {
            expect(context, JsonToken.FIELD_NAME, t);
            String fieldName = p.currentName();
            try {
                if (fieldName.equals(_fieldNames.lowerEndpoint)) {
                    p.nextToken();
                    lowerEndpoint = deserializeEndpoint(context, p);
                } else if (fieldName.equals(_fieldNames.upperEndpoint)) {
                    p.nextToken();
                    upperEndpoint = deserializeEndpoint(context, p);
                } else if (fieldName.equals(_fieldNames.lowerBoundType)) {
                    p.nextToken();
                    lowerBoundType = deserializeBoundType(context, p);
                } else if (fieldName.equals(_fieldNames.upperBoundType)) {
                    p.nextToken();
                    upperBoundType = deserializeBoundType(context, p);
                } else {
                    // Note: should either return `true`, iff problem is handled (and
                    // content processed or skipped) or throw exception. So if we
                    // get back, we ought to be fine...
                    context.handleUnknownProperty(p, this, Range.class, fieldName);
                }
            } catch (IllegalStateException e) {
                // !!! 01-Oct-2016, tatu: Should figure out semantically better exception/reporting
                throw JsonMappingException.from(p, e.getMessage());
            }
        }
        try {
            if ((lowerEndpoint != null) && (upperEndpoint != null)) {
                Preconditions.checkState(lowerEndpoint.getClass() == upperEndpoint.getClass(),
                                         "Endpoint types are not the same - '%s' deserialized to [%s], and '%s' deserialized to [%s].",
                                         _fieldNames.lowerEndpoint,
                                         lowerEndpoint.getClass().getName(),
                                         _fieldNames.upperEndpoint,
                                         upperEndpoint.getClass().getName());
                Preconditions.checkState(lowerBoundType != null,
                                         "'%s' field found, but not '%s'",
                                         _fieldNames.lowerEndpoint,
                                         _fieldNames.lowerBoundType);
                Preconditions.checkState(upperBoundType != null,
                                         "'%s' field found, but not '%s'",
                                         _fieldNames.upperEndpoint,
                                         _fieldNames.upperBoundType);
                return RangeFactory.range(lowerEndpoint, lowerBoundType, upperEndpoint, upperBoundType);
            }
            if (lowerEndpoint != null) {
                Preconditions.checkState(lowerBoundType != null,
                                         "'%s' field found, but not '%s'",
                                         _fieldNames.lowerEndpoint,
                                         _fieldNames.lowerBoundType);
                return RangeFactory.downTo(lowerEndpoint, lowerBoundType);
            }
            if (upperEndpoint != null) {
                Preconditions.checkState(upperBoundType != null,
                                         "'%s' field found, but not '%s'",
                                         _fieldNames.lowerEndpoint);
                return RangeFactory.upTo(upperEndpoint, upperBoundType);
            }
            return RangeFactory.all();
        } catch (IllegalStateException e) {
            throw JsonMappingException.from(p, e.getMessage());
        }
    }

    private BoundType deserializeBoundType(DeserializationContext context, JsonParser p) throws IOException
    {
        expect(context, JsonToken.VALUE_STRING, p.currentToken());
        String name = p.getText();
        try {
            return BoundType.valueOf(name);
        } catch (IllegalArgumentException e) {
            return (BoundType) context.handleWeirdStringValue(BoundType.class, name,
                    "not a valid BoundType name (should be one oF: %s)",
                    asList(BoundType.values()));
        }
    }

    private Comparable<?> deserializeEndpoint(DeserializationContext context, JsonParser p) throws IOException
    {
        Object obj = _endpointDeserializer.deserialize(p, context);
        if (!(obj instanceof Comparable)) {
            // 01-Oct-2015, tatu: Not sure if it's data or definition problem... for now,
            //    assume definition, but may need to reconsider
            context.reportBadDefinition(_rangeType,
                    String.format(
                            "Field [%s] deserialized to [%s], which does not implement Comparable.",
                            p.currentName(), obj.getClass().getName()));
        }
        return (Comparable<?>) obj;
    }

    private void expect(DeserializationContext context, JsonToken expected, JsonToken actual) throws JsonMappingException
    {
        if (actual != expected) {
            context.reportInputMismatch(this, String.format("Problem deserializing %s: expecting %s, found %s",
                    handledType().getName(), expected, actual));
        }
    }
}
