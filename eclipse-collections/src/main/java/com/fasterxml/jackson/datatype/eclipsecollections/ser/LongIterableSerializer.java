package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;

import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.iterator.LongIterator;

public final class LongIterableSerializer extends PrimitiveIterableSerializer<LongIterable> {
    private static final long serialVersionUID = 1L;

    public LongIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(LongIterable.class, elementType(long.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(LongIterable value, JsonGenerator gen) throws IOException {
        LongIterator iterator = value.longIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
