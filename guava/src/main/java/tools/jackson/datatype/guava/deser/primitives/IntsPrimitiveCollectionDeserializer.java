package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.util.ArrayBuilders;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Ints;

import java.util.Collection;
import java.util.List;

public class IntsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Integer, List<Integer>, Collection<Integer>> {
    public IntsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.IntsType, Integer.class);
    }

    @Override
    protected Integer asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getIntValue();
    }

    @Override
    protected List<Integer> finish(Collection<Integer> integers) {
        return Ints.asList(Ints.toArray(integers));
    }

    // Overridden to collect into a primitive array, avoiding boxing every element
    @Override
    protected List<Integer> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        final ArrayBuilders.IntBuilder builder = context.getArrayBuilders().getIntBuilder();
        int[] chunk = builder.resetAndStart();
        int ix = 0;
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            int value = parser.getIntValue();
            if (ix >= chunk.length) {
                chunk = builder.appendCompletedChunk(chunk, ix);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        return Ints.asList(builder.completeAndClearBuffer(chunk, ix));
    }
}
