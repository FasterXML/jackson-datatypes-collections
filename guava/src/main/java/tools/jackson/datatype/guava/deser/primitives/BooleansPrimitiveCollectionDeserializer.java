package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
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
}
