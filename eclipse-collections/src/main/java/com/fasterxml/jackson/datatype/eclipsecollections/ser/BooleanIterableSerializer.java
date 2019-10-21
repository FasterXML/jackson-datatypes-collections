package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.iterator.BooleanIterator;

import java.io.IOException;

public final class BooleanIterableSerializer extends EclipsePrimitiveIterableSerializer<BooleanIterable> {
    private static final JavaType ELEMENT_TYPE = TypeFactory.defaultInstance().constructType(boolean.class);

    public BooleanIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(BooleanIterable.class, ELEMENT_TYPE, property, unwrapSingle);
    }

    @Override
    protected BooleanIterableSerializer withResolved(BeanProperty property, Boolean unwrapSingle) {
        return new BooleanIterableSerializer(property, unwrapSingle);
    }

    @Override
    protected void serializeContents(BooleanIterable value, JsonGenerator gen) throws IOException {
        BooleanIterator iterator = value.booleanIterator();
        while (iterator.hasNext()) {
            gen.writeBoolean(iterator.next());
        }
    }
}
