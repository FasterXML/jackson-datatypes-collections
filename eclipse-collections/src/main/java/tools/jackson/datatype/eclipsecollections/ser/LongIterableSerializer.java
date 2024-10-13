package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.iterator.LongIterator;

public final class LongIterableSerializer extends EclipsePrimitiveIterableSerializer<LongIterable>
{
    public LongIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(LongIterable.class, elementType(long.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(LongIterable value, JsonGenerator gen)
        throws JacksonException
    {
        LongIterator iterator = value.longIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
