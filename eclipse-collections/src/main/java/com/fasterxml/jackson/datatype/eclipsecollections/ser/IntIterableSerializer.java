package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.iterator.IntIterator;

public final class IntIterableSerializer extends EclipsePrimitiveIterableSerializer<IntIterable> {
    private static final JavaType ELEMENT_TYPE = TypeFactory.defaultInstance().constructType(int.class);

    public IntIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(IntIterable.class, ELEMENT_TYPE, property, unwrapSingle);
    }

    @Override
    protected IntIterableSerializer withResolved(BeanProperty property, Boolean unwrapSingle) {
        return new IntIterableSerializer(property, unwrapSingle);
    }

    @Override
    protected void serializeContents(IntIterable value, JsonGenerator gen) throws IOException {
        IntIterator iterator = value.intIterator();
        while (iterator.hasNext()) {
            gen.writeNumber(iterator.next());
        }
    }
}
