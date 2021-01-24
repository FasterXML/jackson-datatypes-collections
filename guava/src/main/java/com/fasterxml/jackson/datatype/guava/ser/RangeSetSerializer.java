package com.fasterxml.jackson.datatype.guava.ser;

import java.util.List;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import com.google.common.collect.RangeSet;

public class RangeSetSerializer extends StdSerializer<RangeSet<Comparable<?>>>
{
    private final JsonSerializer<Object> _serializer;

    public RangeSetSerializer() {
        super(RangeSet.class);
        _serializer = null;
    }

    protected RangeSetSerializer(RangeSetSerializer base, JsonSerializer<Object> ser) {
        super(base);
        _serializer = ser;
    }

    @Override
    public void serialize(RangeSet<Comparable<?>> value, JsonGenerator g,
            SerializerProvider ctxt)
        throws JacksonException
    {
        if (_serializer == null) {
            ctxt.reportBadDefinition(handledType(), "Not contextualized to have value serializer");
        }
        _serializer.serialize(value.asRanges(), g, ctxt);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider ctxt, BeanProperty property)
    {
        // 23-Jan-2021, tatu: Should really improve upon this to handle more complex
        //   values, but this simplified version passes existing unit tests so has to do.
       
        
        JsonSerializer<Object> ser = ctxt.findContentValueSerializer(List.class, property);
        return new RangeSetSerializer(this, ser);

        // Old (Jackson 2.x) implementation was along lines of
        /*
        if (property == null) {
            return this;
        }

        final JavaType grlType = prov.getTypeFactory()
                .constructCollectionType(List.class,
                        prov.getTypeFactory().constructParametricType(
                                Range.class, property.getType().containedType(0)));
        return new RangeSetSerializer(grlType);

        if (genericRangeListType == null) {
            ser = serializers.findValueSerializer(List.class);
        } else {
            serializers.findValueSerializer(genericRangeListType);
        }
        ser.serialize(value.asRanges(), gen, serializers);
        
        */
    }
}
