package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;

import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.iterator.IntIterator;

public final class IntIterableSerializer extends PrimitiveIterableSerializer<IntIterable> {
    private static final long serialVersionUID = 1L;

    public IntIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(IntIterable.class, elementType(int.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(IntIterable value, JsonGenerator gen) throws IOException {
        IntIterator iterator = value.intIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
