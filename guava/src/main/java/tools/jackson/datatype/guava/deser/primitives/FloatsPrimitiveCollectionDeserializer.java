package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.util.ArrayBuilders;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Floats;

import java.util.Collection;
import java.util.List;

public class FloatsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Float, List<Float>, Collection<Float>> {
    public FloatsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.FloatsType, Float.class);
    }

    @Override
    protected Float asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getFloatValue();
    }

    @Override
    protected List<Float> finish(Collection<Float> floats) {
        return Floats.asList(Floats.toArray(floats));
    }

    // Overridden to collect into a primitive array, avoiding boxing every element
    @Override
    protected List<Float> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        final ArrayBuilders.FloatBuilder builder = context.getArrayBuilders().getFloatBuilder();
        float[] chunk = builder.resetAndStart();
        int ix = 0;
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            float value = parser.getFloatValue();
            if (ix >= chunk.length) {
                chunk = builder.appendCompletedChunk(chunk, ix);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        return Floats.asList(builder.completeAndClearBuffer(chunk, ix));
    }
}
