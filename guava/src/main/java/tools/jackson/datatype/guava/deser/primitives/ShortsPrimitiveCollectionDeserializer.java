package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
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
}
