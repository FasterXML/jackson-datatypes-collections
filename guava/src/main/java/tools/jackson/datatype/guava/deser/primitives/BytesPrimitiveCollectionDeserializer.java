package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.util.ArrayBuilders;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Bytes;

import java.util.Collection;
import java.util.List;

public class BytesPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Byte, List<Byte>, Collection<Byte>> {
    public BytesPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.BytesType, Byte.class);
    }

    @Override
    protected Byte asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getByteValue();
    }

    @Override
    protected List<Byte> finish(Collection<Byte> bytes) {
        return Bytes.asList(Bytes.toArray(bytes));
    }

    // Overridden to collect into a primitive array, avoiding boxing every element
    @Override
    protected List<Byte> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        final ArrayBuilders.ByteBuilder builder = context.getArrayBuilders().getByteBuilder();
        byte[] chunk = builder.resetAndStart();
        int ix = 0;
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            byte value = parser.getByteValue();
            if (ix >= chunk.length) {
                chunk = builder.appendCompletedChunk(chunk, ix);
                ix = 0;
            }
            chunk[ix++] = value;
        }
        return Bytes.asList(builder.completeAndClearBuffer(chunk, ix));
    }
}
