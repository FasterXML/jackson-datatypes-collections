package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

import java.io.IOException;
import java.util.List;

public class RangeSetDeserializer extends JsonDeserializer<RangeSet<Comparable<?>>> {
    private JavaType genericRangeListType;

    @Override // since 2.12
    public LogicalType logicalType() {
        return LogicalType.Collection;
    }

    @Override
    public RangeSet<Comparable<?>> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (genericRangeListType == null) {
            throw new JsonParseException(p, "RangeSetJsonSerializer was not contextualized (no deserialize target type). " +
                    "You need to specify the generic type down to the generic parameter of RangeSet.");
        } else {
            @SuppressWarnings("unchecked") final Iterable<Range<Comparable<?>>> ranges
                    = (Iterable<Range<Comparable<?>>>) ctxt
                    .findContextualValueDeserializer(genericRangeListType, null).deserialize(p, ctxt);
            ImmutableRangeSet.Builder<Comparable<?>> builder = ImmutableRangeSet.builder();
            for (Range<Comparable<?>> range : ranges) {
                builder.add(range);
            }
            return builder.build();
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        final JavaType genericType = ctxt.getContextualType().containedType(0);
        if (genericType == null) return this;
        final RangeSetDeserializer deserializer = new RangeSetDeserializer();
        deserializer.genericRangeListType = ctxt.getTypeFactory().constructCollectionType(List.class,
                ctxt.getTypeFactory().constructParametricType(Range.class, genericType));
        return deserializer;
    }
}
