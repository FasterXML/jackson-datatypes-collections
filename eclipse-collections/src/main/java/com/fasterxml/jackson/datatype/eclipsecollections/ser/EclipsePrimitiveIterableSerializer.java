package com.fasterxml.jackson.datatype.eclipsecollections.ser;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.primitive_collections_base.ser.PrimitiveIterableSerializer;
import org.eclipse.collections.api.PrimitiveIterable;

public abstract class EclipsePrimitiveIterableSerializer<C extends PrimitiveIterable>
        extends PrimitiveIterableSerializer<C> {

    protected EclipsePrimitiveIterableSerializer(Class<C> type, JavaType elementType,
            BeanProperty property, Boolean unwrapSingle) {
        super(type, elementType, property, unwrapSingle);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, C value) {
        return value.isEmpty();
    }

    @Override
    public boolean hasSingleElement(C value) {
        if (value != null) {
            return value.size() == 1;
        }
        return false;
    }
}
