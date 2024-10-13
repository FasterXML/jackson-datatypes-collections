package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.iterator.FloatIterator;

public final class FloatIterableSerializer extends EclipsePrimitiveIterableSerializer<FloatIterable>
{
    public FloatIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(FloatIterable.class, elementType(float.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(FloatIterable value, JsonGenerator gen)
        throws JacksonException
    {
        FloatIterator iterator = value.floatIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
