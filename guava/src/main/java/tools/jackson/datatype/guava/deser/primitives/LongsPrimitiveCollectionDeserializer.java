package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.util.ArrayBuilders;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Longs;

import java.util.Collection;
import java.util.List;

public class LongsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Long, List<Long>, Collection<Long>> {
    public LongsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.LongsType, Long.class);
    }

    @Override
    protected Long asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getLongValue();
    }

    @Override
    protected List<Long> finish(Collection<Long> longs) {
        return Longs.asList(Longs.toArray(longs));
    }

    // Overridden to collect into a primitive array, avoiding boxing every element
    @Override
    protected List<Long> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        final ArrayBuilders.LongBuilder builder = context.getArrayBuilders().getLongBuilder();
        long[] chunk = builder.resetAndStart();
        int ix = 0;
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            long value = parser.getLongValue();
            if (ix >= chunk.length) {
                chunk = builder.appendCompletedChunk(chunk, ix);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        return Longs.asList(builder.completeAndClearBuffer(chunk, ix));
    }
}
