package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import com.google.common.primitives.ImmutableDoubleArray;

public final class ImmutableDoubleArrayDeserializer
    extends ImmutablePrimitiveArrayDeserializerBase<ImmutableDoubleArray, double[]>
{
    public ImmutableDoubleArrayDeserializer() {
        super(ImmutableDoubleArray.class, double[].class);
    }

    /**
     * @since 3.3
     */
    protected ImmutableDoubleArrayDeserializer(ImmutableDoubleArrayDeserializer src,
            ValueDeserializer<double[]> arrayDeserializer) {
        super(src, arrayDeserializer);
    }

    @Override
    protected ImmutableDoubleArrayDeserializer _withDelegate(ValueDeserializer<double[]> arrayDeserializer) {
        return new ImmutableDoubleArrayDeserializer(this, arrayDeserializer);
    }

    @Override
    protected ImmutableDoubleArray _fromArray(double[] array) {
        return ImmutableDoubleArray.copyOf(array);
    }

    @Override
    public ImmutableDoubleArray getEmptyValue(DeserializationContext ctxt) {
        return ImmutableDoubleArray.of();
    }
}
