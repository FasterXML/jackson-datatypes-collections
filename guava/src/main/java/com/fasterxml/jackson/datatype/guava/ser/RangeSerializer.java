package com.fasterxml.jackson.datatype.guava.ser;

import java.io.IOException;

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
     */
    @SuppressWarnings("unchecked")
    public RangeSerializer(JavaType type, JsonSerializer<?> endpointSer,
            RangeHelper.RangeProperties fieldNames)
    {
        super(type);
        _rangeType = type;
        _endpointSerializer = (JsonSerializer<Object>) endpointSer;
        _fieldNames = fieldNames;
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Range<?> value) {
        return value.isEmpty();
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        final PropertyNamingStrategy propertyNamingStrategy = prov.getConfig().getPropertyNamingStrategy();
        final RangeHelper.RangeProperties nameMapping = RangeHelper.getPropertyNames(prov.getConfig(), propertyNamingStrategy);
        JsonSerializer<?> endpointSer = _endpointSerializer;

        if (endpointSer == null) {
            JavaType endpointType = _rangeType.containedTypeOrUnknown(0);
            // let's not consider "untyped" (java.lang.Object) to be meaningful here...
            if (endpointType != null && !endpointType.hasRawClass(Object.class)) {
                JsonSerializer<?> ser = prov.findValueSerializer(endpointType, property);
                return new RangeSerializer(_rangeType, ser, nameMapping);
            }
            /* 21-Sep-2014, tatu: Need to make sure all serializers get proper contextual
             *   access, in case they rely on annotations on properties... (or, more generally,
             *   in getting a chance to be contextualized)
             */
        } else if (endpointSer instanceof ContextualSerializer) {
            endpointSer = ((ContextualSerializer) endpointSer).createContextual(prov, property);
        }
        if ((endpointSer != _endpointSerializer) || (nameMapping != null)) {
            return new RangeSerializer(_rangeType, endpointSer, nameMapping);
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
        gen.writeStartObject(value);
        _writeContents(value, gen, provider);
        gen.writeEndObject();

    }

    @Override
    public void serializeWithType(Range<?> value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer)
        throws IOException
    {
        gen.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        _writeContents(value, gen, provider);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    private void _writeContents(Range<?> value, JsonGenerator g, SerializerProvider provider)
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

