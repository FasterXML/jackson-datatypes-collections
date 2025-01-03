package tools.jackson.datatype.guava.deser;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import tools.jackson.core.*;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.ClassUtil;
import tools.jackson.datatype.guava.deser.util.RangeFactory;
import tools.jackson.datatype.guava.deser.util.RangeHelper;

/**
 * Jackson deserializer for a Guava {@link Range}.
 *<p>
 * TODO: I think it would make sense to re-implement this deserializer to
 * use Delegating Deserializer, using a POJO as an intermediate form (properties
 * could be of type {@link java.lang.Object})
 * This would also also simplify the implementation a bit.
 */
public class RangeDeserializer
    extends StdDeserializer<Range<?>>
{
    protected final static Pattern PATTERN_DOUBLE_DOT = Pattern.compile("\\.\\.");

    protected final JavaType _rangeType;

    /**
     * Deserializer used when getting JSON Object representation
     */
    protected final ValueDeserializer<Object> _endpointDeserializer;

    /**
     * Deserializer used when getting JSON String (inline) representation.
     *
     * @since 2.17
     */
    protected final KeyDeserializer _fromStringDeserializer;

    protected final BoundType _defaultBoundType;

    protected final RangeHelper.RangeProperties _fieldNames;

    /**
     * @since 2.17
     */
    protected final JsonFormat.Shape _shape;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */

    public RangeDeserializer(JavaType rangeType, BoundType defaultBoundType) {
        this(rangeType, null, defaultBoundType, RangeHelper.standardNames(),
                JsonFormat.Shape.ANY, null);
    }

    @SuppressWarnings("unchecked")
    protected RangeDeserializer(JavaType rangeType, ValueDeserializer<?> endpointDeser,
            BoundType defaultBoundType, RangeHelper.RangeProperties fieldNames,
            JsonFormat.Shape shape, KeyDeserializer fromStringDeserializer)
    {
        super(rangeType);
        _rangeType = rangeType;
        _endpointDeserializer = (ValueDeserializer<Object>) endpointDeser;
        _fromStringDeserializer = fromStringDeserializer;
        _defaultBoundType = defaultBoundType;
        _fieldNames = fieldNames;
        _shape = shape;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property)
    {
        final JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
        final JsonFormat.Shape shape = format.getShape();

        final RangeHelper.RangeProperties fieldNames = RangeHelper.getPropertyNames(ctxt.getConfig(),
                ctxt.getConfig().getPropertyNamingStrategy());
        final JavaType endpointType = _rangeType.containedTypeOrUnknown(0);
        ValueDeserializer<?> deser = _endpointDeserializer;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(endpointType, property);
        } else {
            // 04-Sep-2019, tatu: If we already have a deserialize, should contextualize, right?
            deser = deser.createContextual(ctxt, property);
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
    /**********************************************************************
    /* Standard accessors
    /**********************************************************************
     */

    @Override
    public JavaType getValueType() { return _rangeType; }

    @Override // since 2.12
    public LogicalType logicalType() {
        // 30-May-2020, tatu: Not 100% sure, but looks a bit like POJO so...
        return LogicalType.POJO;
    }

    /*
    /**********************************************************************
    /* Actual deserialization
    /**********************************************************************
     */

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    @Override
    public Range<?> deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        // NOTE: either START_OBJECT _or_ PROPERTY_NAME fine; latter for polymorphic cases
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) {
            t = p.nextToken();
        }

        Comparable<?> lowerEndpoint = null;
        Comparable<?> upperEndpoint = null;
        BoundType lowerBoundType = _defaultBoundType;
        BoundType upperBoundType = _defaultBoundType;

        if (_shape == JsonFormat.Shape.STRING) {
            expect(ctxt, JsonToken.VALUE_STRING, t);
            return deserializeRangeFromString(ctxt, p);
        }
        for (; t != JsonToken.END_OBJECT; t = p.nextToken()) {
            expect(ctxt, JsonToken.PROPERTY_NAME, t);
            String fieldName = p.currentName();
            if (fieldName.equals(_fieldNames.lowerEndpoint)) {
                p.nextToken();
                lowerEndpoint = deserializeEndpoint(ctxt, p);
            } else if (fieldName.equals(_fieldNames.upperEndpoint)) {
                p.nextToken();
                upperEndpoint = deserializeEndpoint(ctxt, p);
            } else if (fieldName.equals(_fieldNames.lowerBoundType)) {
                p.nextToken();
                lowerBoundType = deserializeBoundType(ctxt, p);
            } else if (fieldName.equals(_fieldNames.upperBoundType)) {
                p.nextToken();
                upperBoundType = deserializeBoundType(ctxt, p);
            } else {
                // Note: should either return `true`, iff problem is handled (and
                // content processed or skipped) or throw exception. So if we
                // get back, we ought to be fine...
                ctxt.handleUnknownProperty(p, this, Range.class, fieldName);
            }
        }

        if ((lowerEndpoint != null) && (upperEndpoint != null)) {
            if (lowerEndpoint.getClass() != upperEndpoint.getClass()) {
                return ctxt.reportBadDefinition(getValueType(ctxt), String.format(
"Endpoint types are not the same - '%s' deserialized to [%s], and '%s' deserialized to [%s].",
                        _fieldNames.lowerEndpoint,
                        lowerEndpoint.getClass().getName(),
                        _fieldNames.upperEndpoint,
                        upperEndpoint.getClass().getName()));
            }
            if (lowerBoundType == null) {
                return ctxt.reportInputMismatch(getValueType(ctxt), String.format(
                    "'%s' field found, but not '%s'",
                    _fieldNames.lowerEndpoint,
                    _fieldNames.lowerBoundType));
            }
            if (upperBoundType == null) {
                return ctxt.reportInputMismatch(getValueType(ctxt), String.format(
                    "'%s' field found, but not '%s'",
                    _fieldNames.upperEndpoint,
                    _fieldNames.upperBoundType));
            }
            return RangeFactory.range(lowerEndpoint, lowerBoundType, upperEndpoint, upperBoundType);
        }

        if (lowerEndpoint != null) {
            if (lowerBoundType == null) {
                return ctxt.reportInputMismatch(getValueType(ctxt), String.format(
                        "'%s' field found, but not '%s'",
                        _fieldNames.lowerEndpoint,
                        _fieldNames.lowerBoundType));
            }
            return RangeFactory.downTo(lowerEndpoint, lowerBoundType);
        }
        if (upperEndpoint != null) {
            if (upperBoundType == null) {
                return ctxt.reportInputMismatch(getValueType(ctxt), String.format(
                        "'%s' field found, but not '%s'",
                        _fieldNames.lowerEndpoint));
            }
            return RangeFactory.upTo(upperEndpoint, upperBoundType);
        }
        return RangeFactory.all();
    }

    private Range<?> deserializeRangeFromString(DeserializationContext context, JsonParser p)
        throws JacksonException
    {
        String rangeInterval = p.getString();

        if (rangeInterval.isEmpty()) {
            return null;
        }

        if (isValidBracketNotation(rangeInterval)) {
            BoundType lowerBoundType = rangeInterval.startsWith("[") ? BoundType.CLOSED : BoundType.OPEN;
            BoundType upperBoundType = rangeInterval.endsWith("]") ? BoundType.CLOSED : BoundType.OPEN;

            rangeInterval = rangeInterval.substring(1, rangeInterval.length() - 1);
            String[] parts = PATTERN_DOUBLE_DOT.split(rangeInterval);

            if (parts.length == 2) {
                boolean isLowerInfinite = parts[0].equals("-∞");
                boolean isUpperInfinite = parts[1].equals("+∞");

                if (isLowerInfinite && isUpperInfinite) {
                    return RangeFactory.all();
                } else if (isLowerInfinite) {
                    return RangeFactory.upTo(deserializeStringified(context, parts[1]), upperBoundType);
                } else if (isUpperInfinite) {
                    return RangeFactory.downTo(deserializeStringified(context, parts[0]), lowerBoundType);
                } else {
                    return RangeFactory.range(deserializeStringified(context, parts[0]),
                            lowerBoundType,
                            deserializeStringified(context, parts[1]),
                            upperBoundType);
                }
            }
        } else {
            String msg = "Invalid Range: should start with '[' or '(', end with ')' or ']'";
            return (Range<?>) context.handleWeirdStringValue(handledType(), rangeInterval, msg);
        }

        // Give generic failure if no specific reason can be given.
        // Although most likely will happen because `..` is absent, since we are validating brackets above.
        return (Range<?>) context.handleWeirdStringValue(handledType(), rangeInterval,
                "Invalid bracket-notation representation (possibly missing \"..\" delimiter in your Stringified Range)");
    }

    private BoundType deserializeBoundType(DeserializationContext context, JsonParser p)
        throws JacksonException
    {
        expect(context, JsonToken.VALUE_STRING, p.currentToken());
        String name = p.getString();
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

    private Comparable<?> deserializeEndpoint(DeserializationContext context, JsonParser p)
        throws JacksonException
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

    private Comparable<?> deserializeStringified(DeserializationContext context, String value)
        throws JacksonException
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

    private void expect(DeserializationContext context, JsonToken expected, JsonToken actual)
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
