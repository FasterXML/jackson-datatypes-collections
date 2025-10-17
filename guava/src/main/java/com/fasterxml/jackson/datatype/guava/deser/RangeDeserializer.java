package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
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
    implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;

    // @since 2.17
    protected final static Pattern PATTERN_DOUBLE_DOT = Pattern.compile("\\.\\.");

    protected final JavaType _rangeType;

    /**
     * Deserializer used when getting JSON Object representation
     */
    protected final JsonDeserializer<Object> _endpointDeserializer;

    /**
     * Deserializer used when getting JSON String (inline) representation.
     *
     * @since 2.17
     */
    protected final KeyDeserializer _fromStringDeserializer;
    
    protected final BoundType _defaultBoundType;

    /**
     * @since 2.10
     */
    protected final RangeHelper.RangeProperties _fieldNames;

    /**
     * @since 2.17
     */
    protected final JsonFormat.Shape _shape;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public RangeDeserializer(BoundType defaultBoundType, JavaType rangeType) {
        this(rangeType, null, defaultBoundType, RangeHelper.standardNames(),
                JsonFormat.Shape.ANY, null);
    }

    @Deprecated // since 2.12
    public RangeDeserializer(JavaType rangeType, JsonDeserializer<?> endpointDeser) {
        this(rangeType, endpointDeser, null);
    }

    @Deprecated // since 2.10
    public RangeDeserializer(JavaType rangeType, JsonDeserializer<?> endpointDeser,
            BoundType defaultBoundType)
    {
        this(rangeType, endpointDeser, defaultBoundType, RangeHelper.standardNames());
    }

    /**
     * @since 2.10
     * @deprecated Since 2.17 use other constructor(s)
     */
    @Deprecated // since 2.17
    protected RangeDeserializer(JavaType rangeType, JsonDeserializer<?> endpointDeser,
            BoundType defaultBoundType, RangeHelper.RangeProperties fieldNames)
    {
        this(rangeType, endpointDeser, defaultBoundType, fieldNames,
                JsonFormat.Shape.ANY, null);
    }

    /**
     * @since 2.17
     */
    @SuppressWarnings("unchecked")
    protected RangeDeserializer(JavaType rangeType, JsonDeserializer<?> endpointDeser,
            BoundType defaultBoundType, RangeHelper.RangeProperties fieldNames,
            JsonFormat.Shape shape, KeyDeserializer fromStringDeserializer)
    {
        super(rangeType);
        _rangeType = rangeType;
        _endpointDeserializer = (JsonDeserializer<Object>) endpointDeser;
        _fromStringDeserializer = fromStringDeserializer;
        _defaultBoundType = defaultBoundType;
        _fieldNames = fieldNames;
        _shape = shape;
    }

    @Override
    public JavaType getValueType() { return _rangeType; }

    @Override // since 2.12
    public LogicalType logicalType() {
        // 30-May-2020, tatu: Not 100% sure, but looks a bit like POJO so...
        return LogicalType.POJO;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException
    {
        final JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        final JsonFormat.Shape shape = format.getShape();

        final RangeHelper.RangeProperties fieldNames = RangeHelper.getPropertyNames(ctxt.getConfig(),
                    ctxt.getConfig().getPropertyNamingStrategy());
        JavaType endpointType = _rangeType.containedType(0);
        if (endpointType == null) { // should this ever occur?
            endpointType = TypeFactory.unknownType();
        }
        JsonDeserializer<?> deser = _endpointDeserializer;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(endpointType, property);
        } else if (deser instanceof ContextualDeserializer) {
            // 04-Sep-2019, tatu: If we already have a deserialize, should contextualize, right?
            deser = ((ContextualDeserializer) deser).createContextual(ctxt, property);
        }
        KeyDeserializer kd = _fromStringDeserializer;
        if (shape == JsonFormat.Shape.STRING) {
            kd = ctxt.findKeyDeserializer(endpointType, property);
        }
        if ((deser != _endpointDeserializer) || (fieldNames != _fieldNames)
                || (shape != _shape) || (kd != _fromStringDeserializer)) {
            return new RangeDeserializer(_rangeType, deser, _defaultBoundType, fieldNames,
                    shape, kd);
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

        if (_shape == JsonFormat.Shape.STRING) {
            expect(context, JsonToken.VALUE_STRING, t);
            return deserializeRangeFromString(context, p);
        }
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
                return context.reportBadDefinition(handledType(), e.getMessage());
            }
        }
        try {
            if ((lowerEndpoint != null) && (upperEndpoint != null)) {
                Preconditions.checkState(lowerEndpoint.getClass() == upperEndpoint.getClass(),
                        "Endpoint types are not the same - 'lowerEndpoint' deserialized to [%s], and 'upperEndpoint' deserialized to [%s].",
                        lowerEndpoint.getClass().getName(),
                        upperEndpoint.getClass().getName());
                Preconditions.checkState(lowerBoundType != null, "'lowerEndpoint' field found, but not 'lowerBoundType'");
                Preconditions.checkState(upperBoundType != null, "'upperEndpoint' field found, but not 'upperBoundType'");
                return RangeFactory.range(lowerEndpoint, lowerBoundType, upperEndpoint, upperBoundType);
            }
            if (lowerEndpoint != null) {
                Preconditions.checkState(lowerBoundType != null, "'lowerEndpoint' field found, but not 'lowerBoundType'");
                return RangeFactory.downTo(lowerEndpoint, lowerBoundType);
            }
            if (upperEndpoint != null) {
                Preconditions.checkState(upperBoundType != null, "'upperEndpoint' field found, but not 'upperBoundType'");
                return RangeFactory.upTo(upperEndpoint, upperBoundType);
            }
            return RangeFactory.all();
        } catch (IllegalStateException e) {
            return context.reportBadDefinition(getValueType(context), e.getMessage());
        }
    }

    private Range<?> deserializeRangeFromString(DeserializationContext context, JsonParser p)
            throws IOException
    {
        String rangeInterval = p.getText();

        if (rangeInterval.isEmpty()) {
            return null;
        }

        return RangeHelper.getRangeFromString(rangeInterval, context, _fromStringDeserializer, _rangeType, handledType());
    }

    private BoundType deserializeBoundType(DeserializationContext context, JsonParser p) throws IOException
    {
        expect(context, JsonToken.VALUE_STRING, p.currentToken());
        String name = p.getText();
        if (name == null) {
            name = "";
        }
        switch (name) {
        case "OPEN":
            return BoundType.OPEN;
        case "CLOSED":
            return BoundType.CLOSED;
        }
        if (context.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)) {
            if (name.equalsIgnoreCase("open")) {
                return BoundType.OPEN;
            }
            if (name.equalsIgnoreCase("closed")) {
                return BoundType.CLOSED;
            }
        }
        return (BoundType) context.handleWeirdStringValue(BoundType.class, name,
                "not a valid BoundType name (should be one of: %s)",
                Arrays.asList(BoundType.values()));
    }

    private Comparable<?> deserializeEndpoint(DeserializationContext context, JsonParser p) throws IOException
    {
        Object obj = _endpointDeserializer.deserialize(p, context);
        if (!(obj instanceof Comparable)) {
            // 01-Oct-2015, tatu: Not sure if it's data or definition problem... for now,
            //    assume definition, but may need to reconsider
            context.reportBadDefinition(_rangeType,
                    String.format(
                            "Field '%s' deserialized to a %s, which does not implement `Comparable`",
                            p.currentName(),
                            ClassUtil.classNameOf(obj)));
        }
        return (Comparable<?>) obj;
    }

    private Comparable<?> deserializeStringified(DeserializationContext context, String value) throws IOException
    {
        Object obj = _fromStringDeserializer.deserializeKey(value, context);
        if (!(obj instanceof Comparable)) {
            // 02-Jan-2024, tatu: Not sure this is possible but let's double-check
            context.reportBadDefinition(_rangeType,
                    String.format(
                            "Stringified endpoint '%s' deserialized to a %s, which does not implement `Comparable`",
                            value,
                            ClassUtil.classNameOf(obj)));
        }
        return (Comparable<?>) obj;
    }
    
    private void expect(DeserializationContext context, JsonToken expected, JsonToken actual) throws JsonMappingException
    {
        if (actual != expected) {
            context.reportInputMismatch(this, String.format("Problem deserializing %s: expecting %s, found %s",
                    ClassUtil.getTypeDescription(getValueType()), expected, actual));
        }
    }

    private boolean isValidBracketNotation(String range) {
        char first = range.charAt(0);
        char last = range.charAt(range.length() - 1);

        return (first == '[' || first == '(') && (last == ']' || last == ')');
    }
}
