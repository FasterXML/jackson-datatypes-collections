package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.iterator.BooleanIterator;

public final class BooleanIterableSerializer extends EclipsePrimitiveIterableSerializer<BooleanIterable>
{
    public BooleanIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(BooleanIterable.class, elementType(boolean.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(BooleanIterable value, JsonGenerator gen)
        throws JacksonException
    {
        BooleanIterator iterator = value.booleanIterator();
        while (iterator.hasNext()) {
            gen.writeBoolean(iterator.next());
        }
    }
}
