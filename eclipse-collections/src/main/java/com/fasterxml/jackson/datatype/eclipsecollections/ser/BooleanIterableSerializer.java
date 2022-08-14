package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.type.TypeFactory;

import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.iterator.BooleanIterator;

public final class BooleanIterableSerializer extends EclipsePrimitiveIterableSerializer<BooleanIterable>
{
    private static final JavaType ELEMENT_TYPE = TypeFactory.defaultInstance().constructType(boolean.class);

    public BooleanIterableSerializer(BeanProperty property, Boolean unwrapSingle) {
        super(BooleanIterable.class, ELEMENT_TYPE, property, unwrapSingle);
    }

    @Override
    protected BooleanIterableSerializer withResolved(BeanProperty property, Boolean unwrapSingle) {
        return new BooleanIterableSerializer(property, unwrapSingle);
    }

    @Override
    protected void serializeContents(BooleanIterable value, JsonGenerator gen)
        throws JacksonException
    {
        BooleanIterator iterator = value.booleanIterator();
        while (iterator.hasNext()) {
            gen.writeBoolean(iterator.next());
        }
    }
}
