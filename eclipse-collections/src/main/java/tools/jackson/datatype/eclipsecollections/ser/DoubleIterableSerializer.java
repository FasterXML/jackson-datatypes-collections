package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;

import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.iterator.DoubleIterator;

public final class DoubleIterableSerializer extends EclipsePrimitiveIterableSerializer<DoubleIterable>
{
    public DoubleIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(DoubleIterable.class, elementType(double.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(DoubleIterable value, JsonGenerator gen)
        throws JacksonException
    {
        DoubleIterator iterator = value.doubleIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
