package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;

import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.iterator.ShortIterator;

public final class ShortIterableSerializer extends PrimitiveIterableSerializer<ShortIterable> {
    private static final long serialVersionUID = 1L;

    public ShortIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(ShortIterable.class, elementType(short.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(ShortIterable value, JsonGenerator gen) throws IOException {
        ShortIterator iterator = value.shortIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
