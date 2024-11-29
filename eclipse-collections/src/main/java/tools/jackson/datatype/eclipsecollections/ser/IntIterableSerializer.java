package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;

import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.iterator.IntIterator;

public final class IntIterableSerializer extends EclipsePrimitiveIterableSerializer<IntIterable>
{
    public IntIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(IntIterable.class, elementType(int.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(IntIterable value, JsonGenerator gen)
        throws JacksonException
    {
        IntIterator iterator = value.intIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
