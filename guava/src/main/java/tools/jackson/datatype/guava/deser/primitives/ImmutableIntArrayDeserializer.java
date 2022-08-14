package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.datatype.guava.util.ImmutablePrimitiveTypes;

import com.google.common.primitives.ImmutableIntArray;

public class ImmutableIntArrayDeserializer
        extends BaseImmutableArrayDeserializer<Integer, ImmutableIntArray, ImmutableIntArray.Builder> {
    public ImmutableIntArrayDeserializer() {
        super(ImmutablePrimitiveTypes.ImmutableIntArrayType, Integer.class);
    }

    @Override
    protected ImmutableIntArray.Builder createIntermediateCollection() {
        return ImmutableIntArray.builder();
    }

    @Override
    protected void collect(ImmutableIntArray.Builder intermediateBuilder, Integer value) {
        intermediateBuilder.add(value);
    }

    @Override
    protected ImmutableIntArray finish(ImmutableIntArray.Builder builder) {
        return builder.build();
    }

    @Override
    protected Integer asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getIntValue();
    }
}
