package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.iterator.IntIterator;

public final class IntIterableSerializer extends EclipsePrimitiveIterableSerializer<IntIterable> {
    private static final JavaType ELEMENT_TYPE = TypeFactory.defaultInstance().constructType(int.class);

    public IntIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(IntIterable.class, ELEMENT_TYPE, property, unwrapSingle);
    }

    @Override
    protected IntIterableSerializer withResolved(BeanProperty property, Boolean unwrapSingle) {
        return new IntIterableSerializer(property, unwrapSingle);
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
