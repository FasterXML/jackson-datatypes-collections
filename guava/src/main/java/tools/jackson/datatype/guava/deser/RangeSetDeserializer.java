package tools.jackson.datatype.guava.deser;

import java.util.Collection;
import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.util.ClassUtil;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

public class RangeSetDeserializer
    extends StdDeserializer<RangeSet<?>>
{
    private final ValueDeserializer<Object> _deserializer;

    public RangeSetDeserializer() {
        super(RangeSet.class);
        _deserializer = null;
    }

    protected RangeSetDeserializer(RangeSetDeserializer base,
            ValueDeserializer<Object> deser)
    {
        super(base);
        _deserializer = deser;
    }

    @Override
    public LogicalType logicalType() {
        return LogicalType.Collection;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    {
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
        ValueDeserializer<Object> deser = ctxt.findContextualValueDeserializer(genericType, property);
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
    public RangeSet<?> deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (_deserializer == null) {
            ctxt.reportBadDefinition(handledType(),
"Not contextualized to have value deserializer or value type of `RangeSet` was not available via type parameters");
        }
        final Collection<?> ranges = (Collection<?>) _deserializer.deserialize(p, ctxt);
        ImmutableRangeSet.Builder<Comparable<?>> builder = ImmutableRangeSet.builder();
        for (Object ob : ranges) {
            _addRange(p, ctxt, builder, ob);
        }
        try {
            return builder.build();
        } catch (IllegalArgumentException e) {
            return ctxt.reportInputMismatch(this,
                    "Failed to build %s from deserialized entries: %s",
                    ClassUtil.getTypeDescription(getValueType(ctxt)), e.getMessage());
        }
    }

    /**
     * Some/many Guava containers do not allow addition of {@code null} values,
     * so isolate handling here.
     */
    protected void _tryToAddNull(JsonParser p, DeserializationContext ctxt,
            ImmutableRangeSet.Builder<Comparable<?>> builder)
        throws JacksonException
    {
        _addRange(p, ctxt, builder, null);
    }

    private void _addRange(JsonParser p, DeserializationContext ctxt,
            ImmutableRangeSet.Builder<Comparable<?>> builder, Object range)
        throws JacksonException
    {
        // Ranges were already deserialized (and JSON nulls resolved) by the
        // List deserializer, so actual token is not known: report as JSON null
        NullElementHelper.addElement(p, ctxt, JsonToken.VALUE_NULL,
                ctxt.constructType(Range.class), "`RangeSet`",
                range, r -> {
                    @SuppressWarnings("unchecked")
                    Range<Comparable<?>> typed = (Range<Comparable<?>>) r;
                    return builder.add(typed);
                });
    }
}
