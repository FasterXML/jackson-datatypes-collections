package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Booleans;

import java.util.Collection;
import java.util.List;

public class BooleansPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Boolean, List<Boolean>, Collection<Boolean>> {

    private final ValueDeserializer<boolean[]> _arrayDeserializer;

    @SuppressWarnings("unchecked")
    public BooleansPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.BooleansType, Boolean.class);
        _arrayDeserializer = (ValueDeserializer<boolean[]>) PrimitiveArrayDeserializers.forType(boolean.class);
    }

    @Deprecated // since 3.3
    @Override
    protected Boolean asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getBooleanValue();
    }

    @Override
    protected Boolean asPrimitive(JsonParser parser, DeserializationContext context) throws JacksonException {
        return _parseBooleanPrimitive(parser, context);
    }

    @Override
    protected List<Boolean> finish(Collection<Boolean> booleans) {
        return Booleans.asList(Booleans.toArray(booleans));
    }

    /**
     * Delegates to databind's {@code boolean[]} deserializer to avoid boxing;
     * note that {@link #add}, {@link #asPrimitive} and {@link #finish} are
     * therefore only called for single-value input, not for JSON Arrays.
     */
    @Override
    protected List<Boolean> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        return Booleans.asList(_arrayDeserializer.deserialize(parser, context));
    }
}
