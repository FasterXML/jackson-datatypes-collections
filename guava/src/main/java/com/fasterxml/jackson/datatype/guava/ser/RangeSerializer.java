package com.fasterxml.jackson.datatype.guava.ser;

import static java.util.Arrays.asList;

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
import java.util.HashMap;
import java.util.Map;

/**
 * Jackson serializer for a Guava {@link Range}.
 */
@SuppressWarnings("serial")
public class RangeSerializer extends StdSerializer<Range<?>>
{
    protected final JavaType _rangeType;

    protected final JsonSerializer<Object> _endpointSerializer;

    private final Map<String, String> _fieldNames;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public RangeSerializer(JavaType type) { this(type, null); }

    public RangeSerializer(JavaType type, JsonSerializer<?> endpointSer) {
        this(type, endpointSer, new HashMap<>());
    }

    @SuppressWarnings("unchecked")
    public RangeSerializer(JavaType type, JsonSerializer<?> endpointSer, Map<String, String> fieldNames)
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
        PropertyNamingStrategy propertyNamingStrategy = prov.getConfig().getPropertyNamingStrategy();
        if (_endpointSerializer == null) {
            JavaType endpointType = _rangeType.containedTypeOrUnknown(0);
            // let's not consider "untyped" (java.lang.Object) to be meaningful here...
            if (endpointType != null && !endpointType.hasRawClass(Object.class)) {
                JsonSerializer<?> ser = prov.findSecondaryPropertySerializer(endpointType, property);
                if (propertyNamingStrategy != null) {
                    return new RangeSerializer(_rangeType, ser, getPropertyNames(prov.getConfig(), propertyNamingStrategy));
                } else {
                    return new RangeSerializer(_rangeType, ser);
                }
            }
            /* 21-Sep-2014, tatu: Need to make sure all serializers get proper contextual
             *   access, in case they rely on annotations on properties... (or, more generally,
             *   in getting a chance to be contextualized)
             */
        } else {
            JsonSerializer<?> cs = _endpointSerializer.createContextual(prov, property);
            if (cs != _endpointSerializer) {
                if (propertyNamingStrategy != null) {
                    return new RangeSerializer(_rangeType, cs, getPropertyNames(prov.getConfig(), propertyNamingStrategy));
                } else {
                    return new RangeSerializer(_rangeType, cs);
                }
            }
        }
        if (propertyNamingStrategy != null) {
            return new RangeSerializer(_rangeType, _endpointSerializer, getPropertyNames(prov.getConfig(), propertyNamingStrategy));
        }
        return this;
    }

    private Map<String, String> getPropertyNames(SerializationConfig config, PropertyNamingStrategy propertyNamingStrategy) {
        final HashMap<String, String> fieldNames = new HashMap<>();
        for (String field : asList("lowerEndpoint", "upperEndpoint", "lowerBoundType", "upperBoundType")) {
            fieldNames.put(field, propertyNamingStrategy.nameForField(config, null, field));
        }
        return fieldNames;
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
                g.writeFieldName(getFieldName("lowerEndpoint"));
                _endpointSerializer.serialize(value.lowerEndpoint(), g, provider);
            } else {
                provider.defaultSerializeField(getFieldName("lowerEndpoint"), value.lowerEndpoint(), g);
            }
            // 20-Mar-2016, tatu: Should not use default handling since it leads to
            //    [datatypes-collections#12] with default typing
            g.writeStringField(getFieldName("lowerBoundType"), value.lowerBoundType().name());
        }
        if (value.hasUpperBound()) {
            if (_endpointSerializer != null) {
                g.writeFieldName(getFieldName("upperEndpoint"));
                _endpointSerializer.serialize(value.upperEndpoint(), g, provider);
            } else {
                provider.defaultSerializeField(getFieldName("upperEndpoint"), value.upperEndpoint(), g);
            }
            // same as above; should always be just String so
            g.writeStringField(getFieldName("upperBoundType"), value.upperBoundType().name());
        }
    }

    private String getFieldName(String fieldName) {
        String name = _fieldNames.get(fieldName);
        return name == null ? fieldName : name;
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
                    objectVisitor.property(getFieldName("lowerEndpoint"), _endpointSerializer, endpointType);
                    objectVisitor.property(getFieldName("lowerBoundType"), btSer, btType);
                    objectVisitor.property(getFieldName("upperEndpoint"), _endpointSerializer, endpointType);
                    objectVisitor.property(getFieldName("upperBoundType"), btSer, btType);
                }
            }
        }
    }
}

