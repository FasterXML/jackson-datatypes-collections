package com.fasterxml.jackson.datatype.guava.deser;

import java.util.List;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

public class RangeSetDeserializer
    extends StdDeserializer<RangeSet<Comparable<?>>>
{
    private final JavaType genericRangeListType;

    public RangeSetDeserializer() {
        super(RangeSet.class);
        genericRangeListType = null;
    }

    protected RangeSetDeserializer(JavaType grlType) {
        super(grlType);
        genericRangeListType = grlType;
    }

    @Override
    public LogicalType logicalType() {
        return LogicalType.Collection;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    {
        final JavaType genericType = ctxt.getContextualType().containedType(0);
        if (genericType == null) {
            return this;
        }
        JavaType grlType = ctxt.getTypeFactory().constructCollectionType(List.class,
                ctxt.getTypeFactory().constructParametricType(Range.class, genericType));
        return new RangeSetDeserializer(grlType);
    }

    @Override
    public RangeSet<Comparable<?>> deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (genericRangeListType == null) {
            ctxt.reportBadDefinition(RangeSet.class,
"RangeSetJsonSerializer was not contextualized (no deserialize target type). " +
"You need to specify the generic type down to the generic parameter of RangeSet.");
        }
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
