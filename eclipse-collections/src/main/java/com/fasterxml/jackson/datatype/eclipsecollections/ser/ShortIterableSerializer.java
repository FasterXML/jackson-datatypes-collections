package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
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
    protected void serializeContents(ShortIterable value, JsonGenerator gen) throws IOException {
        ShortIterator iterator = value.shortIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
