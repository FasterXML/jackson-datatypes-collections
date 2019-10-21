package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.iterator.DoubleIterator;

import java.io.IOException;

public final class DoubleIterableSerializer extends EclipsePrimitiveIterableSerializer<DoubleIterable> {
    private static final JavaType ELEMENT_TYPE = TypeFactory.defaultInstance().constructType(double.class);

    public DoubleIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(DoubleIterable.class, ELEMENT_TYPE, property, unwrapSingle);
    }

    @Override
    protected DoubleIterableSerializer withResolved(BeanProperty property, Boolean unwrapSingle) {
        return new DoubleIterableSerializer(property, unwrapSingle);
    }

    @Override
    protected void serializeContents(DoubleIterable value, JsonGenerator gen) throws IOException {
        DoubleIterator iterator = value.doubleIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
