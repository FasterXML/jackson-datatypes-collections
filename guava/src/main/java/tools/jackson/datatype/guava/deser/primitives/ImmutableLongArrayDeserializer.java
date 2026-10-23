package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.google.common.primitives.ImmutableLongArray;

public final class ImmutableLongArrayDeserializer
    extends ImmutablePrimitiveArrayDeserializerBase<ImmutableLongArray, long[]>
{
    public ImmutableLongArrayDeserializer() {
        super(ImmutableLongArray.class, long[].class);
    }

    /**
     * @since 3.3
     */
    protected ImmutableLongArrayDeserializer(ImmutableLongArrayDeserializer src,
            ValueDeserializer<long[]> arrayDeserializer) {
        super(src, arrayDeserializer);
    }

    @Override
    protected ImmutableLongArrayDeserializer _withDelegate(ValueDeserializer<long[]> arrayDeserializer) {
        return new ImmutableLongArrayDeserializer(this, arrayDeserializer);
    }

    @Override
    protected ImmutableLongArray _fromArray(long[] array) {
        return ImmutableLongArray.copyOf(array);
    }

    @Override
    public ImmutableLongArray getEmptyValue(DeserializationContext ctxt) {
        return ImmutableLongArray.of();
    }
}
