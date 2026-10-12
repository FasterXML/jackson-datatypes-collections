package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Ints;

import java.util.Collection;
import java.util.List;

public class IntsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Integer, List<Integer>, Collection<Integer>> {
    private final ValueDeserializer<int[]> _arrayDeserializer;

    @SuppressWarnings("unchecked")
    public IntsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.IntsType, Integer.class);
        _arrayDeserializer = (ValueDeserializer<int[]>) PrimitiveArrayDeserializers.forType(int.class);
    }

    @Deprecated // since 3.3
    @Override
    protected Integer asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getIntValue();
    }

    @Override
    protected Integer asPrimitive(JsonParser parser, DeserializationContext context) throws JacksonException {
        return _parseIntPrimitive(parser, context);
    }

    @Override
    protected List<Integer> finish(Collection<Integer> integers) {
        return Ints.asList(Ints.toArray(integers));
    }

    /**
     * Delegates to databind's {@code int[]} deserializer to avoid boxing;
     * note that {@link #add}, {@link #asPrimitive} and {@link #finish} are
     * therefore only called for single-value input, not for JSON Arrays.
     */
    @Override
    protected List<Integer> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        return Ints.asList(_arrayDeserializer.deserialize(parser, context));
    }
}
