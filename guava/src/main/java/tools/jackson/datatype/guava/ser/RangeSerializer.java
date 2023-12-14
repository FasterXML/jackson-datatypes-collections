package tools.jackson.datatype.guava.ser;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.*;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.*;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.datatype.guava.deser.util.RangeHelper;

/**
 * Jackson serializer for Guava Range objects with enhanced serialization capabilities.
 * When the range property is annotated with {@code @JsonFormat(JsonFormat.Shape.STRING)},
 * it generates bracket notation for a more concise and human-readable representation.
 * Otherwise, it defaults to a more verbose standard serialization, explicitly writing out endpoints and bound types.
 *
 * <p>
 * Usage Example for bracket notation:
 * <pre>{@code
 *   @JsonFormat(JsonFormat.Shape.STRING)
 *   private Range<Integer> r;
 * }</pre>
 * When the range field is annotated with {@code @JsonFormat(JsonFormat.Shape.STRING)}, the serializer
 * will output the range that would look something like: [X..Y] or (X..Y).
 * <br><br>
 * By default, when the range property lacks the string shape annotation,
 * the serializer will output the JSON in following manner:
 * <pre>{@code
 *   {
 *      "lowerEndpoint": X,
 *      "lowerBoundType": "CLOSED",
 *      "upperEndpoint": Y,
 *      "upperBoundType": "CLOSED"
 *   }
 * }</pre>
 */
public class RangeSerializer extends StdSerializer<Range<?>>
{
    protected final JavaType _rangeType;

    protected final ValueSerializer<Object> _endpointSerializer;

    protected final RangeHelper.RangeProperties _fieldNames;

    protected final JsonFormat.Shape _shape;

    /*
    /**********************************************************************
    /* Life-cycle
    /**********************************************************************
     */

    public RangeSerializer(JavaType type) {
        this(type, null, RangeHelper.standardNames(), JsonFormat.Shape.ANY);
    }

    @SuppressWarnings("unchecked")
    protected RangeSerializer(JavaType type, ValueSerializer<?> endpointSer,
            RangeHelper.RangeProperties fieldNames,
            JsonFormat.Shape shape)
    {
        super(type);
        _rangeType = type;
        _endpointSerializer = (ValueSerializer<Object>) endpointSer;
        _fieldNames = fieldNames;
        _shape = shape;
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Range<?> value) {
        return value.isEmpty();
    }

    @Override
    public ValueSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property)
    {
        final JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        final JsonFormat.Shape shape = format.getShape();

        final RangeHelper.RangeProperties fieldNames = RangeHelper.getPropertyNames(prov.getConfig(),
                prov.getConfig().getPropertyNamingStrategy());
        ValueSerializer<?> endpointSer = _endpointSerializer;
        if (endpointSer == null) {
            JavaType endpointType = _rangeType.containedTypeOrUnknown(0);
            // let's not consider "untyped" (java.lang.Object) to be meaningful here...
            if (endpointType != null && !endpointType.hasRawClass(Object.class)) {
                endpointSer = prov.findContentValueSerializer(endpointType, property);
            }
        } else {
            endpointSer = _endpointSerializer.createContextual(prov, property);
        }
        if ((endpointSer != _endpointSerializer)
                || (fieldNames != _fieldNames)
                || (shape != _shape)) {
            return new RangeSerializer(_rangeType, endpointSer, fieldNames, shape);
        }
        return this;
    }

    /*
    /**********************************************************************
    /* Serialization methods
    /**********************************************************************
     */

    @Override
    public void serialize(Range<?> value, JsonGenerator gen, SerializerProvider provider)
        throws JacksonException
    {
        if (_shape == JsonFormat.Shape.STRING) {
            gen.writeString(_getStringFormat(value));
        } else {
            gen.writeStartObject(value);
            _writeContents(value, gen, provider);
            gen.writeEndObject();
        }
    }

    @Override
    public void serializeWithType(Range<?> value, JsonGenerator gen, SerializerProvider ctxt,
            TypeSerializer typeSer)
        throws JacksonException
    {
        gen.assignCurrentValue(value);
        if (_shape == JsonFormat.Shape.STRING) {
            String rangeString = _getStringFormat(value);
            WritableTypeId typeId = typeSer.writeTypeSuffix(gen, ctxt,
                    typeSer.typeId(rangeString, JsonToken.VALUE_STRING)
            );
            typeSer.writeTypeSuffix(gen, ctxt, typeId);
        } else {
            WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, ctxt,
                    typeSer.typeId(value, JsonToken.START_OBJECT));
            _writeContents(value, gen, ctxt);
            typeSer.writeTypeSuffix(gen, ctxt, typeIdDef);
        }
    }

    private void _writeContents(Range<?> value, JsonGenerator g, SerializerProvider provider)
        throws JacksonException
    {
        if (value.hasLowerBound()) {
            if (_endpointSerializer != null) {
                g.writeName(_fieldNames.lowerEndpoint);
                _endpointSerializer.serialize(value.lowerEndpoint(), g, provider);
            } else {
                provider.defaultSerializeProperty(_fieldNames.lowerEndpoint, value.lowerEndpoint(), g);
            }
            // 20-Mar-2016, tatu: Should not use default handling since it leads to
            //    [datatypes-collections#12] with default typing
            g.writeStringProperty(_fieldNames.lowerBoundType, value.lowerBoundType().name());
        }
        if (value.hasUpperBound()) {
            if (_endpointSerializer != null) {
                g.writeName(_fieldNames.upperEndpoint);
                _endpointSerializer.serialize(value.upperEndpoint(), g, provider);
            } else {
                provider.defaultSerializeProperty(_fieldNames.upperEndpoint, value.upperEndpoint(), g);
            }
            // same as above; should always be just String so
            g.writeStringProperty(_fieldNames.upperBoundType, value.upperBoundType().name());
        }
    }

    private String _getStringFormat(Range<?> range){
        StringBuilder builder = new StringBuilder();

        if (range.hasLowerBound()) {
            builder.append(range.lowerBoundType() == BoundType.CLOSED ? '[' : '(').append(range.lowerEndpoint());
        } else {
            builder.append("(-∞");
        }

        builder.append("..");

        if (range.hasUpperBound()) {
            builder.append(range.upperEndpoint()).append(range.upperBoundType() == BoundType.CLOSED ? ']' : ')');
        } else {
            builder.append("+∞)");
        }

        return builder.toString();
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    {
        if (visitor != null) {
            JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);
            if (objectVisitor != null) {
                if (_endpointSerializer != null) {
                    JavaType endpointType = _rangeType.containedType(0);
                    JavaType btType = visitor.getProvider().constructType(BoundType.class);
                    // should probably keep track of `property`...
                    ValueSerializer<?> btSer = visitor.getProvider()
                            .findContentValueSerializer(btType, null);
                    objectVisitor.property(_fieldNames.lowerEndpoint, _endpointSerializer, endpointType);
                    objectVisitor.property(_fieldNames.lowerBoundType, btSer, btType);
                    objectVisitor.property(_fieldNames.upperEndpoint, _endpointSerializer, endpointType);
                    objectVisitor.property(_fieldNames.upperBoundType, btSer, btType);
                }
            }
        }
    }
}

