package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Floats;

import java.util.Collection;
import java.util.List;

public class FloatsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Float, List<Float>, Collection<Float>> {
    private final ValueDeserializer<float[]> _arrayDeserializer;

    @SuppressWarnings("unchecked")
    public FloatsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.FloatsType, Float.class);
        _arrayDeserializer = (ValueDeserializer<float[]>) PrimitiveArrayDeserializers.forType(float.class);
    }

    @Deprecated // since 3.3
    @Override
    protected Float asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getFloatValue();
    }

    @Override
    protected Float asPrimitive(JsonParser parser, DeserializationContext context) throws JacksonException {
        return _parseFloatPrimitive(parser, context);
    }

    @Override
    protected List<Float> finish(Collection<Float> floats) {
        return Floats.asList(Floats.toArray(floats));
    }

    /**
     * Delegates to databind's {@code float[]} deserializer to avoid boxing;
     * note that {@link #add}, {@link #asPrimitive} and {@link #finish} are
     * therefore only called for single-value input, not for JSON Arrays.
     */
    @Override
    protected List<Float> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        return Floats.asList(_arrayDeserializer.deserialize(parser, context));
    }
}
