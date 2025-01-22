package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;

import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.iterator.DoubleIterator;

public final class DoubleIterableSerializer extends PrimitiveIterableSerializer<DoubleIterable> {
    private static final long serialVersionUID = 1L;

    public DoubleIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(DoubleIterable.class, elementType(double.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(DoubleIterable value, JsonGenerator gen) throws IOException {
        DoubleIterator iterator = value.doubleIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
