package com.fasterxml.jackson.datatype.guava.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public RangeSerializer(JavaType type) { this(type, null); }

    @SuppressWarnings("unchecked")
    public RangeSerializer(JavaType type, JsonSerializer<?> endpointSer)
    {
        super(type);
        _rangeType = type;
        _endpointSerializer = (JsonSerializer<Object>) endpointSer;
    }

    @Override
    @Deprecated
    public boolean isEmpty(Range<?> value) {
        return isEmpty(null, value);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Range<?> value) {
        return value.isEmpty();
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        if (_endpointSerializer == null) {
            JavaType endpointType = _rangeType.containedTypeOrUnknown(0);
            // let's not consider "untyped" (java.lang.Object) to be meaningful here...
            if (endpointType != null && !endpointType.hasRawClass(Object.class)) {
                JsonSerializer<?> ser = prov.findValueSerializer(endpointType, property);
                return new RangeSerializer(_rangeType, ser);
            }
            /* 21-Sep-2014, tatu: Need to make sure all serializers get proper contextual
             *   access, in case they rely on annotations on properties... (or, more generally,
             *   in getting a chance to be contextualized)
             */
        } else if (_endpointSerializer instanceof ContextualSerializer) {
            JsonSerializer<?> cs = ((ContextualSerializer)_endpointSerializer).createContextual(prov, property);
            if (cs != _endpointSerializer) {
                return new RangeSerializer(_rangeType, cs);
            }
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
        gen.writeStartObject();
        _writeContents(value, gen, provider);
        gen.writeEndObject();

    }

    @Override
    public void serializeWithType(Range<?> value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer)
        throws IOException
    {
        // Will be serialized as a JSON Object, so:
        typeSer.writeTypePrefixForObject(value, gen);
        _writeContents(value, gen, provider);
        typeSer.writeTypeSuffixForObject(value, gen);
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
                    objectVisitor.property("lowerEndpoint", _endpointSerializer, endpointType);
                    objectVisitor.property("lowerBoundType", btSer, btType);
                    objectVisitor.property("upperEndpoint", _endpointSerializer, endpointType);
                    objectVisitor.property("upperBoundType", btSer, btType);
                }
            }
        }
    }

    private void _writeContents(Range<?> value, JsonGenerator g, SerializerProvider provider)
        throws IOException
    {
        if (value.hasLowerBound()) {
            if (_endpointSerializer != null) {
                g.writeFieldName("lowerEndpoint");
                _endpointSerializer.serialize(value.lowerEndpoint(), g, provider);
            } else {
                provider.defaultSerializeField("lowerEndpoint", value.lowerEndpoint(), g);
            }
            provider.defaultSerializeField("lowerBoundType", value.lowerBoundType(), g);
        }
        if (value.hasUpperBound()) {
            if (_endpointSerializer != null) {
                g.writeFieldName("upperEndpoint");
                _endpointSerializer.serialize(value.upperEndpoint(), g, provider);
            } else {
                provider.defaultSerializeField("upperEndpoint", value.upperEndpoint(), g);
            }
            provider.defaultSerializeField("upperBoundType", value.upperBoundType(), g);
        }
    }
}
