package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Shorts;

import java.util.Collection;
import java.util.List;

public class ShortsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Short, List<Short>, Collection<Short>> {
    private final ValueDeserializer<short[]> _arrayDeserializer;

    @SuppressWarnings("unchecked")
    public ShortsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.ShortsType, Short.class);
        _arrayDeserializer = (ValueDeserializer<short[]>) PrimitiveArrayDeserializers.forType(short.class);
    }

    @Deprecated // since 3.3
    @Override
    protected Short asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getShortValue();
    }

    @Override
    protected Short asPrimitive(JsonParser parser, DeserializationContext context) throws JacksonException {
        return _parseShortPrimitive(parser, context);
    }

    @Override
    protected List<Short> finish(Collection<Short> shorts) {
        return Shorts.asList(Shorts.toArray(shorts));
    }

    /**
     * Delegates to databind's {@code short[]} deserializer to avoid boxing;
     * note that {@link #add}, {@link #asPrimitive} and {@link #finish} are
     * therefore only called for single-value input, not for JSON Arrays.
     */
    @Override
    protected List<Short> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        return Shorts.asList(_arrayDeserializer.deserialize(parser, context));
    }
}
