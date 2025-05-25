package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;

import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.iterator.FloatIterator;

public final class FloatIterableSerializer extends PrimitiveIterableSerializer<FloatIterable> {
    private static final long serialVersionUID = 1L;

    public FloatIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(FloatIterable.class, elementType(float.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(FloatIterable value, JsonGenerator gen) throws IOException {
        FloatIterator iterator = value.floatIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
