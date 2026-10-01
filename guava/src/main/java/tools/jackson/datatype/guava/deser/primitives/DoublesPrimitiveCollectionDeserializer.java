package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.util.ArrayBuilders;
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

    // Overridden to collect into a primitive array, avoiding boxing every element
    @Override
    protected List<Double> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        final ArrayBuilders.DoubleBuilder builder = context.getArrayBuilders().getDoubleBuilder();
        double[] chunk = builder.resetAndStart();
        int ix = 0;
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            double value = parser.getDoubleValue();
            if (ix >= chunk.length) {
                chunk = builder.appendCompletedChunk(chunk, ix);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        return Doubles.asList(builder.completeAndClearBuffer(chunk, ix));
    }
}
