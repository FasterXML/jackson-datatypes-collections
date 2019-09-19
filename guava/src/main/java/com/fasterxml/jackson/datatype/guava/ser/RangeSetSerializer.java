package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

import java.io.IOException;
import java.util.List;

public class RangeSetSerializer
    extends JsonSerializer<RangeSet<Comparable<?>>>
    implements ContextualSerializer
{
    private JavaType genericRangeListType;

    @Override
    public void serialize(RangeSet<Comparable<?>> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (genericRangeListType == null) {
            serializers.findValueSerializer(List.class).serialize(value.asRanges(), gen, serializers);
        } else {
            serializers.findValueSerializer(genericRangeListType).serialize(value.asRanges(), gen, serializers);
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) {
        if (property == null) return this;
        final RangeSetSerializer serializer = new RangeSetSerializer();
        serializer.genericRangeListType = prov.getTypeFactory()
                .constructCollectionType(List.class,
                        prov.getTypeFactory().constructParametricType(
                                Range.class, property.getType().containedType(0)));
        return serializer;
    }
}
