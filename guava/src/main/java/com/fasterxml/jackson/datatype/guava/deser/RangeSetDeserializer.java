package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

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

/**
 * Backported all implementations from Jackson 3.0, except following parts.
 * 
 * <ul>
 *     <li>class declaration</li>
 *     <li>JsonDeserializer in 2.x -> ValueDeserializer in 3.0</li>
 * </ul>
 * 
 * @since 2.16 
 */
public class RangeSetDeserializer
    extends StdDeserializer<RangeSet<?>>
    implements ContextualDeserializer
{
    private final JsonDeserializer<Object> _deserializer;
    
    public RangeSetDeserializer() {
        super(RangeSet.class);
        _deserializer = null;
    }

    protected RangeSetDeserializer(RangeSetDeserializer base,
            JsonDeserializer<Object> deser)
    {
        super(base);
        _deserializer = deser;
    }
    
    @Override // since 2.12
    public LogicalType logicalType() {
        return LogicalType.Collection;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        JavaType genericType = _findType(ctxt, ctxt.getContextualType());
        if (genericType == null) {
            if (property != null) {
                genericType = _findType(ctxt, property.getType());
            }
            // Cannot locate generic type to use? Leave as-is, fail on attempt to deserialize
            if (genericType == null) {
                return this;
            }
        }
        JsonDeserializer<Object> deser = ctxt.findContextualValueDeserializer(genericType, property);
        return new RangeSetDeserializer(this, deser);
    }

    private JavaType _findType(DeserializationContext ctxt, JavaType base)
    {
        Class<?> raw = base.getRawClass();
        final TypeFactory tf = ctxt.getTypeFactory();
        if (RangeSet.class.isAssignableFrom(raw)) {
            JavaType valueType = tf.findFirstTypeParameter(base, RangeSet.class);
            if (valueType != null) {
                JavaType rangeType = tf.constructParametricType(Range.class, valueType);
                return tf.constructCollectionType(List.class, rangeType);
            }
        }
        return null;
    }

    @Override
    public RangeSet<Comparable<?>> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException 
    {
        if (_deserializer == null) {
            ctxt.reportBadDefinition(handledType(),
                    "Not contextualized to have value deserializer or value type of `RangeSet` was not available via type parameters");
        }
        final Collection<?> ranges = (Collection<?>) _deserializer.deserialize(p, ctxt);
        ImmutableRangeSet.Builder<Comparable<?>> builder = ImmutableRangeSet.builder();
        for (Object ob : ranges) {
            @SuppressWarnings("unchecked")
            Range<Comparable<?>> range = (Range<Comparable<?>>) ob;
            builder.add(range);
        }
        return builder.build();
    }
}
