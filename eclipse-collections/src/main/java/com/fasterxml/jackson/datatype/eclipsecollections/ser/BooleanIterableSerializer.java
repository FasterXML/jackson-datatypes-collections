package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.iterator.BooleanIterator;

public final class BooleanIterableSerializer extends PrimitiveIterableSerializer<BooleanIterable> {
    private static final long serialVersionUID = 1L;

    public BooleanIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(BooleanIterable.class, elementType(boolean.class), property, unwrapSingle);
    }

    @Override
    protected void serializeContents(BooleanIterable value, JsonGenerator gen) throws IOException {
        BooleanIterator iterator = value.booleanIterator();
        while (iterator.hasNext()) {
            gen.writeBoolean(iterator.next());
        }
    }
}
