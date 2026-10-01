package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.util.ArrayBuilders;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Booleans;

import java.util.Collection;
import java.util.List;

public class BooleansPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Boolean, List<Boolean>, Collection<Boolean>> {

    public BooleansPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.BooleansType, Boolean.class);
    }

    @Override
    protected Boolean asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getBooleanValue();
    }

    @Override
    protected List<Boolean> finish(Collection<Boolean> booleans) {
        return Booleans.asList(Booleans.toArray(booleans));
    }

    // Overridden to collect into a primitive array, avoiding boxing every element
    @Override
    protected List<Boolean> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        final ArrayBuilders.BooleanBuilder builder = context.getArrayBuilders().getBooleanBuilder();
        boolean[] chunk = builder.resetAndStart();
        int ix = 0;
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            boolean value = parser.getBooleanValue();
            if (ix >= chunk.length) {
                chunk = builder.appendCompletedChunk(chunk, ix);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        return Booleans.asList(builder.completeAndClearBuffer(chunk, ix));
    }
}
