package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Doubles;

import java.util.Collection;
import java.util.List;

public class DoublesPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Double, List<Double>, Collection<Double>> {
    private final ValueDeserializer<double[]> _arrayDeserializer;

    @SuppressWarnings("unchecked")
    public DoublesPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.DoublesType, Double.class);
        _arrayDeserializer = (ValueDeserializer<double[]>) PrimitiveArrayDeserializers.forType(double.class);
    }

    @Deprecated // since 3.3
    @Override
    protected Double asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getDoubleValue();
    }

    @Override
    protected Double asPrimitive(JsonParser parser, DeserializationContext context) throws JacksonException {
        return _parseDoublePrimitive(parser, context);
    }

    @Override
    protected List<Double> finish(Collection<Double> doubles) {
        return Doubles.asList(Doubles.toArray(doubles));
    }

    /**
     * Delegates to databind's {@code double[]} deserializer to avoid boxing;
     * note that {@link #add}, {@link #asPrimitive} and {@link #finish} are
     * therefore only called for single-value input, not for JSON Arrays.
     */
    @Override
    protected List<Double> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        return Doubles.asList(_arrayDeserializer.deserialize(parser, context));
    }
}
