package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

public class RangeSetDeserializer
    extends StdDeserializer<RangeSet<Comparable<?>>>
    implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;

    // TODO: make immutable
    private JavaType genericRangeListType;

    public RangeSetDeserializer() {
        super(RangeSet.class);
    }
    
    @Override // since 2.12
    public LogicalType logicalType() {
        return LogicalType.Collection;
    }

    @Override
    public RangeSet<Comparable<?>> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (genericRangeListType == null) {
            return ctxt.reportBadDefinition(getValueType(ctxt),
"`RangeSetJsonSerializer` was not contextualized (no deserialize target type): " +
"you need to specify the generic type down to the generic parameter of `RangeSet`");
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
