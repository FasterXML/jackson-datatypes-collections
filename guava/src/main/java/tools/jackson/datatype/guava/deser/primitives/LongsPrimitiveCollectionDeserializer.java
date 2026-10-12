package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Longs;

import java.util.Collection;
import java.util.List;

public class LongsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Long, List<Long>, Collection<Long>> {
    private final ValueDeserializer<long[]> _arrayDeserializer;

    @SuppressWarnings("unchecked")
    public LongsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.LongsType, Long.class);
        _arrayDeserializer = (ValueDeserializer<long[]>) PrimitiveArrayDeserializers.forType(long.class);
    }

    @Deprecated // since 3.3
    @Override
    protected Long asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getLongValue();
    }

    @Override
    protected Long asPrimitive(JsonParser parser, DeserializationContext context) throws JacksonException {
        return _parseLongPrimitive(parser, context);
    }

    @Override
    protected List<Long> finish(Collection<Long> longs) {
        return Longs.asList(Longs.toArray(longs));
    }

    /**
     * Delegates to databind's {@code long[]} deserializer to avoid boxing;
     * note that {@link #add}, {@link #asPrimitive} and {@link #finish} are
     * therefore only called for single-value input, not for JSON Arrays.
     */
    @Override
    protected List<Long> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        return Longs.asList(_arrayDeserializer.deserialize(parser, context));
    }
}
