package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.databind.*;

import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.ReferenceTypeSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.google.common.base.Optional;

public class GuavaOptionalSerializer
    extends ReferenceTypeSerializer<Optional<?>> // since 2.9
{
    private static final long serialVersionUID = 1L;

    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public GuavaOptionalSerializer(ReferenceType fullType, boolean staticTyping,
            TypeSerializer vts, JsonSerializer<Object> ser)
    {
        super(fullType, staticTyping, vts, ser);
    }

    public GuavaOptionalSerializer(GuavaOptionalSerializer base, BeanProperty property,
            TypeSerializer vts, JsonSerializer<?> valueSer, NameTransformer unwrapper,
            Object suppressableValue, boolean suppressNulls)
    {
        super(base, property, vts, valueSer, unwrapper,
                suppressableValue, suppressNulls);
    }


    @Override
    protected ReferenceTypeSerializer<Optional<?>> withResolved(BeanProperty prop,
            TypeSerializer vts, JsonSerializer<?> valueSer,
            NameTransformer unwrapper)
    {
        if ((_property == prop)
                && (_valueTypeSerializer == vts) && (_valueSerializer == valueSer)
                && (_unwrapper == unwrapper)) {
            return this;
        }
        return new GuavaOptionalSerializer(this, prop, vts, valueSer, unwrapper,
                _suppressableValue, _suppressNulls);
    }

    @Override
    public ReferenceTypeSerializer<Optional<?>> withContentInclusion(Object suppressableValue,
            boolean suppressNulls)
    {
        return new GuavaOptionalSerializer(this, _property, _valueTypeSerializer,
                _valueSerializer, _unwrapper,
                suppressableValue, suppressNulls);
    }
    
    /*
    /**********************************************************
    /* Abstract method impls
    /**********************************************************
     */

    @Override
    protected boolean _isValuePresent(Optional<?> value) {
        return value.isPresent();
    }

    @Override
    protected Object _getReferenced(Optional<?> value) {
        return value.get();
    }

    @Override
    protected Object _getReferencedIfPresent(Optional<?> value) {
        return value.isPresent() ? value.get() : null;
    }    
}
