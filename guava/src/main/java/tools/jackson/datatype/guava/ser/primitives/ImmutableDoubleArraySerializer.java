package tools.jackson.datatype.guava.ser.primitives;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import com.google.common.primitives.ImmutableDoubleArray;

public final class ImmutableDoubleArraySerializer
    extends ImmutablePrimitiveArraySerializerBase<ImmutableDoubleArray>
{
    public ImmutableDoubleArraySerializer() {
        super(ImmutableDoubleArray.class, double.class);
    }

    /**
     * @since 3.3
     */
    protected ImmutableDoubleArraySerializer(ImmutableDoubleArraySerializer src,
            BeanProperty prop, Boolean unwrapSingle) {
        super(src, prop, unwrapSingle);
    }

    @Override
    protected ImmutableDoubleArraySerializer _withResolved(BeanProperty prop,
            Boolean unwrapSingle) {
        return new ImmutableDoubleArraySerializer(this, prop, unwrapSingle);
    }

    @Override
    protected int _length(ImmutableDoubleArray value) {
        return value.length();
    }

    @Override
    protected void _writeElement(JsonGenerator g, ImmutableDoubleArray value, int index) {
        g.writeNumber(value.get(index));
    }
}
