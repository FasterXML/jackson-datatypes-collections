package tools.jackson.datatype.guava.ser;

import java.lang.reflect.Modifier;
import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.impl.TypeWrappedSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

public class RangeSetSerializer extends StdSerializer<RangeSet<Comparable<?>>>
{
    private final ValueSerializer<Object> _serializer;

    private final ValueSerializer<Object> _rangeSerializer;

    public RangeSetSerializer() {
        super(RangeSet.class);
        _serializer = null;
        _rangeSerializer = null;
    }

    protected RangeSetSerializer(RangeSetSerializer base, ValueSerializer<Object> ser) {
        this(base, ser, base._rangeSerializer);
    }

    @SuppressWarnings("unchecked")
    protected RangeSetSerializer(RangeSetSerializer base, ValueSerializer<Object> ser,
            ValueSerializer<?> rangeSer) {
        super(base);
        _serializer = ser;
        _rangeSerializer = (ValueSerializer<Object>) rangeSer;
    }

    @Override
    public void serialize(RangeSet<Comparable<?>> value, JsonGenerator g,
            SerializationContext ctxt)
        throws JacksonException
    {
        if (_serializer == null) {
            ctxt.reportBadDefinition(handledType(), "Not contextualized to have value serializer");
        }
        _serializer.serialize(value.asRanges(), g, ctxt);
    }

    @Override
    public void serializeWithType(RangeSet<Comparable<?>> value, JsonGenerator g,
            SerializationContext ctxt, TypeSerializer typeSer)
        throws JacksonException
    {
        if (_rangeSerializer == null) {
            ctxt.reportBadDefinition(handledType(), "Not contextualized to have value serializer");
        }
        // Write the array directly (instead of delegating to the `Set` serializer) so that
        // both the type id and the current value refer to the `RangeSet`, not its `asRanges()` view
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(g, ctxt,
                typeSer.typeId(value, _typeIdClass(value), JsonToken.START_ARRAY));
        g.assignCurrentValue(value);
        for (Range<?> range : value.asRanges()) {
            _rangeSerializer.serialize(range, g, ctxt);
        }
        typeSer.writeTypeSuffix(g, ctxt, typeIdDef);
    }

    // Non-public implementations (like views returned by `TreeRangeSet.complement()`)
    // cannot be read back as such, so use the `RangeSet` interface as their type id
    private static Class<?> _typeIdClass(RangeSet<?> value) {
        Class<?> cls = value.getClass();
        return Modifier.isPublic(cls.getModifiers()) ? cls : RangeSet.class;
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property)
    {
        // 23-Jan-2021, tatu: Should really improve upon this to handle more complex
        //   values, but this simplified version passes existing unit tests so has to do.
       
        
        ValueSerializer<Object> ser = ctxt.findContentValueSerializer(
                ctxt.getTypeFactory().constructCollectionType(List.class, Range.class), property);
        if (property == null) {
            return new RangeSetSerializer(this, ser, ctxt.findTypedValueSerializer(Range.class, true));
        }
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
        return new RangeSetSerializer(this, ser, rangeSer);

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
