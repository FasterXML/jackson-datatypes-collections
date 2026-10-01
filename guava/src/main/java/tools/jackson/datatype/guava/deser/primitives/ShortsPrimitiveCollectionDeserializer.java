package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.util.ArrayBuilders;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Shorts;

import java.util.Collection;
import java.util.List;

public class ShortsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Short, List<Short>, Collection<Short>> {
    public ShortsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.ShortsType, Short.class);
    }

    @Override
    protected Short asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getShortValue();
    }

    @Override
    protected List<Short> finish(Collection<Short> shorts) {
        return Shorts.asList(Shorts.toArray(shorts));
    }

    // Overridden to collect into a primitive array, avoiding boxing every element
    @Override
    protected List<Short> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        final ArrayBuilders.ShortBuilder builder = context.getArrayBuilders().getShortBuilder();
        short[] chunk = builder.resetAndStart();
        int ix = 0;
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            short value = parser.getShortValue();
            if (ix >= chunk.length) {
                chunk = builder.appendCompletedChunk(chunk, ix);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        return Shorts.asList(builder.completeAndClearBuffer(chunk, ix));
    }
}
