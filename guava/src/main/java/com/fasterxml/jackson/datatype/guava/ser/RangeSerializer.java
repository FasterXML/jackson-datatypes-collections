package com.fasterxml.jackson.datatype.guava.ser;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.guava.deser.util.RangeHelper;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

/**
 * Jackson serializer for a Guava {@link Range}.
 */
@SuppressWarnings("serial")
public class RangeSerializer extends StdSerializer<Range<?>>
    implements ContextualSerializer
{
    protected final JavaType _rangeType;

    protected final JsonSerializer<Object> _endpointSerializer;

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

    public RangeSerializer(JavaType type) { this(type, null); }

    @Deprecated // since 2.10
    public RangeSerializer(JavaType type, JsonSerializer<?> endpointSer)
    {
        this(type, endpointSer, RangeHelper.standardNames());
    }

    /**
     * @since 2.10
     *
     * @deprecated Since 2.17
     */
    @Deprecated
    public RangeSerializer(JavaType type, JsonSerializer<?> endpointSer,
            RangeHelper.RangeProperties fieldNames)
    {
        this(type, endpointSer, fieldNames, JsonFormat.Shape.ANY);
    }

    /**
     * @since 2.17
     */
    @SuppressWarnings("unchecked")
    public RangeSerializer(JavaType type, JsonSerializer<?> endpointSer,
                           RangeHelper.RangeProperties fieldNames, JsonFormat.Shape shape)
    {
        super(type);
        _rangeType = type;
        _endpointSerializer = (JsonSerializer<Object>) endpointSer;
        _fieldNames = fieldNames;
        _shape = shape;
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Range<?> value) {
        return value.isEmpty();
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        final JsonFormat.Value format = findFormatOverrides(prov, property, handledType());
        final JsonFormat.Shape shape = format.getShape();

        final PropertyNamingStrategy propertyNamingStrategy = prov.getConfig().getPropertyNamingStrategy();
        final RangeHelper.RangeProperties nameMapping = RangeHelper.getPropertyNames(prov.getConfig(), propertyNamingStrategy);
        JsonSerializer<?> endpointSer = _endpointSerializer;

        if (endpointSer == null) {
            JavaType endpointType = _rangeType.containedTypeOrUnknown(0);
            // let's not consider "untyped" (java.lang.Object) to be meaningful here...
            if (endpointType != null && !endpointType.hasRawClass(Object.class)) {
                JsonSerializer<?> ser = prov.findValueSerializer(endpointType, property);
                return new RangeSerializer(_rangeType, ser, nameMapping, shape);
            }
            /* 21-Sep-2014, tatu: Need to make sure all serializers get proper contextual
             *   access, in case they rely on annotations on properties... (or, more generally,
             *   in getting a chance to be contextualized)
             */
        } else if (endpointSer instanceof ContextualSerializer) {
            endpointSer = ((ContextualSerializer) endpointSer).createContextual(prov, property);
        }
        if ((endpointSer != _endpointSerializer) || (nameMapping != null)) {
            return new RangeSerializer(_rangeType, endpointSer, nameMapping, shape);
        }
        return this;
    }

    /*
    /**********************************************************
    /* Serialization methods
    /**********************************************************
     */

    @Override
    public void serialize(Range<?> value, JsonGenerator gen, SerializerProvider provider)
        throws IOException, JsonGenerationException
    {
        if (_shape == JsonFormat.Shape.STRING) {
            gen.writeString(getStringFormat(value));
        } else {
            gen.writeStartObject(value);
            _writeContents(value, gen, provider);
            gen.writeEndObject();
        }
    }

    @Override
    public void serializeWithType(Range<?> value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer)
        throws IOException
    {
        gen.assignCurrentValue(value);
        if (_shape == JsonFormat.Shape.STRING) {
            String rangeString = getStringFormat(value);
            WritableTypeId typeId = typeSer.writeTypeSuffix(gen,
                    typeSer.typeId(rangeString, JsonToken.VALUE_STRING)
            );
            typeSer.writeTypeSuffix(gen, typeId);
        } else {
            WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                    typeSer.typeId(value, JsonToken.START_OBJECT));
            _writeContents(value, gen, provider);
            typeSer.writeTypeSuffix(gen, typeIdDef);
        }
    }

    protected String getStringFormat(Range<?> range){
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

    protected void _writeContents(Range<?> value, JsonGenerator g, SerializerProvider provider)
        throws IOException
    {
        if (value.hasLowerBound()) {
            final String fieldName = _fieldNames.lowerEndpoint;
            if (_endpointSerializer != null) {
                g.writeFieldName(fieldName);
                _endpointSerializer.serialize(value.lowerEndpoint(), g, provider);
            } else {
                provider.defaultSerializeField(fieldName, value.lowerEndpoint(), g);
            }
            // 20-Mar-2016, tatu: Should not use default handling since it leads to
            //    [datatypes-collections#12] with default typing
            g.writeStringField(_fieldNames.lowerBoundType, value.lowerBoundType().name());
        }
        if (value.hasUpperBound()) {
            final String fieldName = _fieldNames.upperEndpoint;
            if (_endpointSerializer != null) {
                g.writeFieldName(fieldName);
                _endpointSerializer.serialize(value.upperEndpoint(), g, provider);
            } else {
                provider.defaultSerializeField(fieldName, value.upperEndpoint(), g);
            }
            // same as above; should always be just String so
            g.writeStringField(_fieldNames.upperBoundType, value.upperBoundType().name());
        }
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException
    {
        if (visitor != null) {
            JsonObjectFormatVisitor objectVisitor = visitor.expectObjectFormat(typeHint);
            if (objectVisitor != null) {
                if (_endpointSerializer != null) {
                    JavaType endpointType = _rangeType.containedType(0);
                    JavaType btType = visitor.getProvider().constructType(BoundType.class);
                    JsonSerializer<?> btSer = visitor.getProvider()
                            .findValueSerializer(btType, null);
                    objectVisitor.property(_fieldNames.lowerEndpoint, _endpointSerializer, endpointType);
                    objectVisitor.property(_fieldNames.lowerBoundType, btSer, btType);
                    objectVisitor.property(_fieldNames.upperEndpoint, _endpointSerializer, endpointType);
                    objectVisitor.property(_fieldNames.upperBoundType, btSer, btType);
                }
            }
        }
    }
}

