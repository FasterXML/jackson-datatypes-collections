package com.fasterxml.jackson.datatype.guava.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.guava.deser.util.RangeHelper;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

/**
 * Jackson serializer for a Guava {@link Range}.
 */
public class RangeSerializer extends StdSerializer<Range<?>>
{
    protected final JavaType _rangeType;

    protected final JsonSerializer<Object> _endpointSerializer;

    private final RangeHelper.RangeProperties _fieldNames;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public RangeSerializer(JavaType type) { this(type, null, RangeHelper.standardNames()); }

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
        final RangeHelper.RangeProperties fieldNames = RangeHelper.getPropertyNames(prov.getConfig(),
                prov.getConfig().getPropertyNamingStrategy());
        JsonSerializer<?> endpointSer = _endpointSerializer;
        if (endpointSer == null) {
            JavaType endpointType = _rangeType.containedTypeOrUnknown(0);
            // let's not consider "untyped" (java.lang.Object) to be meaningful here...
            if (endpointType != null && !endpointType.hasRawClass(Object.class)) {
                endpointSer = prov.findSecondaryPropertySerializer(endpointType, property);
            }
        } else {
            endpointSer = _endpointSerializer.createContextual(prov, property);
        }
        if ((endpointSer != _endpointSerializer) || (fieldNames != _fieldNames)) {
            return new RangeSerializer(_rangeType, endpointSer, fieldNames);
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
        gen.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        _writeContents(value, gen, provider);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    private void _writeContents(Range<?> value, JsonGenerator g, SerializerProvider provider)
        throws IOException
    {
        if (value.hasLowerBound()) {
            if (_endpointSerializer != null) {
                g.writeFieldName(_fieldNames.lowerEndpoint);
                _endpointSerializer.serialize(value.lowerEndpoint(), g, provider);
            } else {
                provider.defaultSerializeField(_fieldNames.lowerEndpoint, value.lowerEndpoint(), g);
            }
            // 20-Mar-2016, tatu: Should not use default handling since it leads to
            //    [datatypes-collections#12] with default typing
            g.writeStringField(_fieldNames.lowerBoundType, value.lowerBoundType().name());
        }
        if (value.hasUpperBound()) {
            if (_endpointSerializer != null) {
                g.writeFieldName(_fieldNames.upperEndpoint);
                _endpointSerializer.serialize(value.upperEndpoint(), g, provider);
            } else {
                provider.defaultSerializeField(_fieldNames.upperEndpoint, value.upperEndpoint(), g);
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
                    // should probably keep track of `property`...
                    JsonSerializer<?> btSer = visitor.getProvider()
                            .findSecondaryPropertySerializer(btType, null);
                    objectVisitor.property(_fieldNames.lowerEndpoint, _endpointSerializer, endpointType);
                    objectVisitor.property(_fieldNames.lowerBoundType, btSer, btType);
                    objectVisitor.property(_fieldNames.upperEndpoint, _endpointSerializer, endpointType);
                    objectVisitor.property(_fieldNames.upperBoundType, btSer, btType);
                }
            }
        }
    }
}

