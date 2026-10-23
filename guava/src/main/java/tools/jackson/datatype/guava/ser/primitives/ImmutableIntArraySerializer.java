package tools.jackson.datatype.guava.ser.primitives;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import com.google.common.primitives.ImmutableIntArray;

public final class ImmutableIntArraySerializer
    extends ImmutablePrimitiveArraySerializerBase<ImmutableIntArray>
{
    public ImmutableIntArraySerializer() {
        super(ImmutableIntArray.class, int.class);
    }

    /**
     * @since 3.3
     */
    protected ImmutableIntArraySerializer(ImmutableIntArraySerializer src,
            BeanProperty prop, Boolean unwrapSingle) {
        super(src, prop, unwrapSingle);
    }

    @Override
    protected ImmutableIntArraySerializer _withResolved(BeanProperty prop,
            Boolean unwrapSingle) {
        return new ImmutableIntArraySerializer(this, prop, unwrapSingle);
    }

    @Override
    protected int _length(ImmutableIntArray value) {
        return value.length();
    }

    @Override
    protected void _writeElement(JsonGenerator g, ImmutableIntArray value, int index) {
        g.writeNumber(value.get(index));
    }
}
