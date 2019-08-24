package com.fasterxml.jackson.datatype.guava.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import java.util.Optional;

/**
 * Jackson serializer for a Guava {@link Range}.
 */
@SuppressWarnings("serial")
public class RangeSerializer extends StdSerializer<Range<?>>
{
    protected final JavaType _rangeType;

    protected final JsonSerializer<Object> _endpointSerializer;

    private PropertyNamingStrategy _propertyNamingStrategy;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public RangeSerializer(JavaType type) { this(type, null); }

    public RangeSerializer(JavaType type, PropertyNamingStrategy propertyNamingStrategy) {
        this(type, null, propertyNamingStrategy);
    }

    @SuppressWarnings("unchecked")
    public RangeSerializer(JavaType type, JsonSerializer<?> endpointSer, PropertyNamingStrategy propertyNamingStrategy)
    {
        super(type);
        _rangeType = type;
        _endpointSerializer = (JsonSerializer<Object>) endpointSer;
        _propertyNamingStrategy = propertyNamingStrategy;
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
                JsonSerializer<?> ser = prov.findSecondaryPropertySerializer(endpointType, property);
                return new RangeSerializer(_rangeType, ser, prov.getConfig().getPropertyNamingStrategy());
            }
            /* 21-Sep-2014, tatu: Need to make sure all serializers get proper contextual
             *   access, in case they rely on annotations on properties... (or, more generally,
             *   in getting a chance to be contextualized)
             */
        } else {
            JsonSerializer<?> cs = _endpointSerializer.createContextual(prov, property);
            if (cs != _endpointSerializer) {
                return new RangeSerializer(_rangeType, cs, prov.getConfig().getPropertyNamingStrategy());
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
        PropertyNamingStrategy propertyNamingStrategy =
                Optional.ofNullable(_propertyNamingStrategy)
                        .orElse(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        if (value.hasLowerBound()) {
            if (_endpointSerializer != null) {
                g.writeFieldName(propertyNamingStrategy.nameForField(provider.getConfig(), null, "lowerEndpoint"));
                _endpointSerializer.serialize(value.lowerEndpoint(), g, provider);
            } else {
                provider.defaultSerializeField(propertyNamingStrategy.nameForField(provider.getConfig(), null, "lowerEndpoint"), value.lowerEndpoint(), g);
            }
            // 20-Mar-2016, tatu: Should not use default handling since it leads to
            //    [datatypes-collections#12] with default typing
            g.writeStringField(propertyNamingStrategy.nameForField(provider.getConfig(), null, "lowerBoundType"), value.lowerBoundType().name());
        }
        if (value.hasUpperBound()) {
            if (_endpointSerializer != null) {
                g.writeFieldName(propertyNamingStrategy.nameForField(provider.getConfig(), null, "upperEndpoint"));
                _endpointSerializer.serialize(value.upperEndpoint(), g, provider);
            } else {
                provider.defaultSerializeField(propertyNamingStrategy.nameForField(provider.getConfig(), null, "upperEndpoint"), value.upperEndpoint(), g);
            }
            // same as above; should always be just String so
            g.writeStringField(propertyNamingStrategy.nameForField(provider.getConfig(), null, "upperBoundType"), value.upperBoundType().name());
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
                    objectVisitor.property("lowerEndpoint", _endpointSerializer, endpointType);
                    objectVisitor.property("lowerBoundType", btSer, btType);
                    objectVisitor.property("upperEndpoint", _endpointSerializer, endpointType);
                    objectVisitor.property("upperBoundType", btSer, btType);
                }
            }
        }
    }
}

