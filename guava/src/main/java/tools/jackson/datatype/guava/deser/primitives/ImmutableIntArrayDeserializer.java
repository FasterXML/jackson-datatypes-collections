package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.google.common.primitives.ImmutableIntArray;

public final class ImmutableIntArrayDeserializer
    extends ImmutablePrimitiveArrayDeserializerBase<ImmutableIntArray, int[]>
{
    public ImmutableIntArrayDeserializer() {
        super(ImmutableIntArray.class, int[].class);
    }

    /**
     * @since 3.3
     */
    protected ImmutableIntArrayDeserializer(ImmutableIntArrayDeserializer src,
            ValueDeserializer<int[]> arrayDeserializer) {
        super(src, arrayDeserializer);
    }

    @Override
    protected ImmutableIntArrayDeserializer _withDelegate(ValueDeserializer<int[]> arrayDeserializer) {
        return new ImmutableIntArrayDeserializer(this, arrayDeserializer);
    }

    @Override
    protected ImmutableIntArray _fromArray(int[] array) {
        return ImmutableIntArray.copyOf(array);
    }

    @Override
    public ImmutableIntArray getEmptyValue(DeserializationContext ctxt) {
        return ImmutableIntArray.of();
    }
}
