package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.iterator.FloatIterator;

public final class FloatIterableSerializer extends EclipsePrimitiveIterableSerializer<FloatIterable> {
    private static final JavaType ELEMENT_TYPE = TypeFactory.defaultInstance().constructType(float.class);

    public FloatIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(FloatIterable.class, ELEMENT_TYPE, property, unwrapSingle);
    }

    @Override
    protected FloatIterableSerializer withResolved(BeanProperty property, Boolean unwrapSingle) {
        return new FloatIterableSerializer(property, unwrapSingle);
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
