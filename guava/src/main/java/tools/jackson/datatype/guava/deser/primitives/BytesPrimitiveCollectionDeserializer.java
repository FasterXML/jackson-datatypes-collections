package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Bytes;

import java.util.Collection;
import java.util.List;

public class BytesPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Byte, List<Byte>, Collection<Byte>> {
    private final ValueDeserializer<byte[]> _arrayDeserializer;

    @SuppressWarnings("unchecked")
    public BytesPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.BytesType, Byte.class);
        _arrayDeserializer = (ValueDeserializer<byte[]>) PrimitiveArrayDeserializers.forType(byte.class);
    }

    @Deprecated // since 3.3
    @Override
    protected Byte asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getByteValue();
    }

    @Override
    protected Byte asPrimitive(JsonParser parser, DeserializationContext context) throws JacksonException {
        // same as databind's byte[] deserializer: allows unsigned values up to 255
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return parser.getByteValue();
        }
        return _parseBytePrimitive(parser, context);
    }

    @Override
    protected List<Byte> finish(Collection<Byte> bytes) {
        return Bytes.asList(Bytes.toArray(bytes));
    }

    /**
     * Delegates to databind's {@code byte[]} deserializer to avoid boxing;
     * note that {@link #add}, {@link #asPrimitive} and {@link #finish} are
     * therefore only called for single-value input, not for JSON Arrays.
     */
    @Override
    protected List<Byte> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        return Bytes.asList(_arrayDeserializer.deserialize(parser, context));
    }
}
