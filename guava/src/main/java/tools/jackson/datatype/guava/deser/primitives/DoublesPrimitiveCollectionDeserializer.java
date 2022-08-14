package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Doubles;

import java.util.Collection;
import java.util.List;

public class DoublesPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Double, List<Double>, Collection<Double>> {
    public DoublesPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.DoublesType, Double.class);
    }

    @Override
    protected Double asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getDoubleValue();
    }

    @Override
    protected List<Double> finish(Collection<Double> doubles) {
        return Doubles.asList(Doubles.toArray(doubles));
    }
}
