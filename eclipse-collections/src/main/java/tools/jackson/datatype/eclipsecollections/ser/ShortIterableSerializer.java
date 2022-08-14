package tools.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.iterator.ShortIterator;

public final class ShortIterableSerializer extends EclipsePrimitiveIterableSerializer<ShortIterable> {
    private static final JavaType ELEMENT_TYPE = TypeFactory.defaultInstance().constructType(short.class);

    public ShortIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(ShortIterable.class, ELEMENT_TYPE, property, unwrapSingle);
    }

    @Override
    protected ShortIterableSerializer withResolved(BeanProperty property, Boolean unwrapSingle) {
        return new ShortIterableSerializer(property, unwrapSingle);
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
