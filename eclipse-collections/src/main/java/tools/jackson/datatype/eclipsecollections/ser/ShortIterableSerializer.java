package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;

import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.iterator.ShortIterator;

public final class ShortIterableSerializer extends EclipsePrimitiveIterableSerializer<ShortIterable>
{
    public ShortIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(ShortIterable.class, elementType(short.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(ShortIterable value, JsonGenerator gen)
        throws JacksonException
    {
        ShortIterator iterator = value.shortIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
