package tools.jackson.datatype.guava.deser;

import java.util.Arrays;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import tools.jackson.core.*;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.util.ClassUtil;
import tools.jackson.datatype.guava.deser.util.RangeFactory;
import tools.jackson.datatype.guava.deser.util.RangeHelper;

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

    protected final ValueDeserializer<Object> _endpointDeserializer;
    protected final BoundType _defaultBoundType;

    protected final RangeHelper.RangeProperties _fieldNames;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */

    public RangeDeserializer(JavaType rangeType, BoundType defaultBoundType) {
        this(rangeType, null, defaultBoundType, RangeHelper.standardNames());
    }

    @SuppressWarnings("unchecked")
    protected RangeDeserializer(JavaType rangeType, ValueDeserializer<?> endpointDeser,
            BoundType defaultBoundType, RangeHelper.RangeProperties fieldNames)
    {
        super(rangeType);
        _rangeType = rangeType;
        _endpointDeserializer = (ValueDeserializer<Object>) endpointDeser;
        _defaultBoundType = defaultBoundType;
        _fieldNames = fieldNames;
    }

    @Override
    public JavaType getValueType() { return _rangeType; }

    @Override // since 2.12
    public LogicalType logicalType() {
        // 30-May-2020, tatu: Not 100% sure, but looks a bit like POJO so...
        return LogicalType.POJO;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property)
    {
        final RangeHelper.RangeProperties fieldNames = RangeHelper.getPropertyNames(ctxt.getConfig(),
                ctxt.getConfig().getPropertyNamingStrategy());
        ValueDeserializer<?> deser = _endpointDeserializer;
        if (deser == null) {
            JavaType endpointType = _rangeType.containedType(0);
            if (endpointType == null) { // should this ever occur?
                endpointType = TypeFactory.unknownType();
            }
            deser = ctxt.findContextualValueDeserializer(endpointType, property);
        } else {
            // 04-Sep-2019, tatu: If we already have a deserialize, should contextualize, right?
            deser = deser.createContextual(ctxt, property);
        }
        if ((deser != _endpointDeserializer) || (fieldNames != _fieldNames)) {
            return new RangeDeserializer(_rangeType, deser, _defaultBoundType,
                    fieldNames);
        }
        return this;
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

        for (; t != JsonToken.END_OBJECT; t = p.nextToken()) {
            expect(ctxt, JsonToken.PROPERTY_NAME, t);
            String fieldName = p.currentName();
//            try {
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
                /*
            } catch (IllegalStateException e) {
                // !!! 01-Oct-2016, tatu: Should figure out semantically better exception/reporting
                throw DatabindException.from(p, e.getMessage());
            }
            */
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

    private BoundType deserializeBoundType(DeserializationContext context, JsonParser p)
        throws JacksonException
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

    private Comparable<?> deserializeEndpoint(DeserializationContext context, JsonParser p)
        throws JacksonException
    {
        Object obj = _endpointDeserializer.deserialize(p, context);
        if (!(obj instanceof Comparable)) {
            // 01-Oct-2015, tatu: Not sure if it's data or definition problem... for now,
            //    assume definition, but may need to reconsider
            context.reportBadDefinition(_rangeType,
                    String.format(
                            "Field '%s' deserialized to a %s, which does not implement Comparable.",
                            p.currentName(),
                            ClassUtil.classNameOf(obj)));
        }
        return (Comparable<?>) obj;
    }

    private void expect(DeserializationContext context, JsonToken expected, JsonToken actual)
    {
        if (actual != expected) {
            context.reportInputMismatch(this, String.format("Problem deserializing %s: expecting %s, found %s",
                    handledType().getName(), expected, actual));
        }
    }
}
