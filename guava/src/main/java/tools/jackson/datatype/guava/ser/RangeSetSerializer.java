package tools.jackson.datatype.guava.ser;

import java.util.Set;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.impl.TypeWrappedSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

public class RangeSetSerializer extends StdSerializer<RangeSet<Comparable<?>>>
{
    /**
     * Serializer for individual {@link Range} elements (wrapped with
     * type serializer, if {@code Range}s have type ids).
     */
    private final ValueSerializer<Object> _rangeSerializer;

    public RangeSetSerializer() {
        super(RangeSet.class);
        _rangeSerializer = null;
    }

    /**
     * @param rangeSer Serializer for individual {@link Range} elements
     *    (since 3.3; was serializer for {@code List} of ranges before)
     */
    @SuppressWarnings("unchecked")
    protected RangeSetSerializer(RangeSetSerializer base, ValueSerializer<?> rangeSer) {
        super(base);
        _rangeSerializer = (ValueSerializer<Object>) rangeSer;
    }

    @Override
    public void serialize(RangeSet<Comparable<?>> value, JsonGenerator g,
            SerializationContext ctxt)
        throws JacksonException
    {
        Set<Range<Comparable<?>>> ranges = value.asRanges();
        // Same as databind `CollectionSerializer`
        if ((ranges.size() == 1) && ctxt.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) {
            _writeRanges(value, g, ctxt);
            return;
        }
        g.writeStartArray(value, ranges.size());
        _writeRanges(value, g, ctxt);
        g.writeEndArray();
    }

    @Override
    public void serializeWithType(RangeSet<Comparable<?>> value, JsonGenerator g,
            SerializationContext ctxt, TypeSerializer typeSer)
        throws JacksonException
    {
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, _typeIdClass(value), JsonToken.START_ARRAY));
        g.assignCurrentValue(value);
        _writeRanges(value, g, ctxt);
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    private void _writeRanges(RangeSet<Comparable<?>> value, JsonGenerator g,
            SerializationContext ctxt)
        throws JacksonException
    {
        if (_rangeSerializer == null) {
            ctxt.reportBadDefinition(handledType(), "Not contextualized to have value serializer");
        }
        for (Range<?> range : value.asRanges()) {
            _rangeSerializer.serialize(range, g, ctxt);
        }
    }

    // Deserializer always builds an `ImmutableRangeSet`, so other implementations
    // (`TreeRangeSet`, views like `complement()`) use the `RangeSet` interface as type id
    private static Class<?> _typeIdClass(RangeSet<?> value) {
        return (value instanceof ImmutableRangeSet<?>) ? value.getClass() : RangeSet.class;
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property)
    {
        JavaType rangeType = ctxt.constructType(Range.class);
        ValueSerializer<?> rangeSer = findAnnotatedContentSerializer(ctxt, property);
        rangeSer = findContextualConvertingSerializer(ctxt, property, rangeSer);
        if (rangeSer == null) {
            rangeSer = ctxt.findContentValueSerializer(rangeType, property);
        } else {
            rangeSer = ctxt.handleSecondaryContextualization(rangeSer, property);
        }
        TypeSerializer typeSer = ctxt.findTypeSerializer(rangeType);
        if (typeSer != null) {
            rangeSer = new TypeWrappedSerializer(typeSer.forProperty(ctxt, property), rangeSer);
        }
        return new RangeSetSerializer(this, rangeSer);
    }
}
