package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.iterator.FloatIterator;

public final class FloatIterableSerializer extends EclipsePrimitiveIterableSerializer<FloatIterable> {
    private static final JavaType ELEMENT_TYPE = TypeFactory.defaultInstance().constructType(float.class);

    public FloatIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(FloatIterable.class, ELEMENT_TYPE, property, unwrapSingle);
    }

    @Override
    protected FloatIterableSerializer withResolved(BeanProperty property, Boolean unwrapSingle) {
        return new FloatIterableSerializer(property, unwrapSingle);
    }

    @Override
    protected void serializeContents(FloatIterable value, JsonGenerator gen) throws IOException {
        FloatIterator iterator = value.floatIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
