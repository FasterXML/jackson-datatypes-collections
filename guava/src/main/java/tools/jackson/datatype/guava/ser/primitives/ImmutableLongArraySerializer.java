package tools.jackson.datatype.guava.ser.primitives;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.BeanProperty;
import com.google.common.primitives.ImmutableLongArray;

public final class ImmutableLongArraySerializer
    extends ImmutablePrimitiveArraySerializerBase<ImmutableLongArray>
{
    public ImmutableLongArraySerializer() {
        super(ImmutableLongArray.class, long.class);
    }

    protected ImmutableLongArraySerializer(ImmutableLongArraySerializer src,
            BeanProperty prop, Boolean unwrapSingle) {
        super(src, prop, unwrapSingle);
    }

    @Override
    protected ImmutableLongArraySerializer _withResolved(BeanProperty prop,
            Boolean unwrapSingle) {
        return new ImmutableLongArraySerializer(this, prop, unwrapSingle);
    }

    @Override
    protected int _length(ImmutableLongArray value) {
        return value.length();
    }

    @Override
    protected void _writeElement(JsonGenerator g, ImmutableLongArray value, int index) {
        g.writeNumber(value.get(index));
    }
}
