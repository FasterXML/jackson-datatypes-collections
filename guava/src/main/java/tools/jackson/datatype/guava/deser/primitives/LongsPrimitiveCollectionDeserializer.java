package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Longs;

import java.util.Collection;
import java.util.List;

public class LongsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Long, List<Long>, Collection<Long>> {
    public LongsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.LongsType, Long.class);
    }

    @Override
    protected Long asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getLongValue();
    }

    @Override
    protected List<Long> finish(Collection<Long> longs) {
        return Longs.asList(Longs.toArray(longs));
    }
}
