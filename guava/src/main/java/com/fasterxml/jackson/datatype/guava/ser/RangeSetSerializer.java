package com.fasterxml.jackson.datatype.guava.ser;

import java.util.List;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

public class RangeSetSerializer extends JsonSerializer<RangeSet<Comparable<?>>>
{
    private final JavaType genericRangeListType;

    public RangeSetSerializer() {
        this(null);
    }

    protected RangeSetSerializer(JavaType grlType) {
        genericRangeListType = grlType;
    }

    @Override
    public void serialize(RangeSet<Comparable<?>> value, JsonGenerator gen, SerializerProvider serializers)
        throws JacksonException
    {
        if (genericRangeListType == null) {
            serializers.findValueSerializer(List.class).serialize(value.asRanges(), gen, serializers);
        } else {
            serializers.findValueSerializer(genericRangeListType).serialize(value.asRanges(), gen, serializers);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property)
    {
        if (property == null) {
            return this;
        }
        final JavaType grlType = prov.getTypeFactory()
                .constructCollectionType(List.class,
                        prov.getTypeFactory().constructParametricType(
                                Range.class, property.getType().containedType(0)));
        return new RangeSetSerializer(grlType);
    }
}
