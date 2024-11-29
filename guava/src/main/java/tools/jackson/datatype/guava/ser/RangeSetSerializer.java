package tools.jackson.datatype.guava.ser;

import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.std.StdSerializer;

import com.google.common.collect.RangeSet;

public class RangeSetSerializer extends StdSerializer<RangeSet<Comparable<?>>>
{
    private final ValueSerializer<Object> _serializer;

    public RangeSetSerializer() {
        super(RangeSet.class);
        _serializer = null;
    }

    protected RangeSetSerializer(RangeSetSerializer base, ValueSerializer<Object> ser) {
        super(base);
        _serializer = ser;
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
    public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property)
    {
        // 23-Jan-2021, tatu: Should really improve upon this to handle more complex
        //   values, but this simplified version passes existing unit tests so has to do.
       
        
        ValueSerializer<Object> ser = ctxt.findContentValueSerializer(List.class, property);
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
