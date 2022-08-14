package tools.jackson.datatype.guava.ser;

import tools.jackson.databind.*;

import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.ReferenceTypeSerializer;
import tools.jackson.databind.type.ReferenceType;
import tools.jackson.databind.util.NameTransformer;
import com.google.common.base.Optional;

public class GuavaOptionalSerializer
    extends ReferenceTypeSerializer<Optional<?>> // since 2.9
{
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public GuavaOptionalSerializer(ReferenceType fullType, boolean staticTyping,
            TypeSerializer vts, ValueSerializer<Object> ser)
    {
        super(fullType, staticTyping, vts, ser);
    }

    public GuavaOptionalSerializer(GuavaOptionalSerializer base, BeanProperty property,
            TypeSerializer vts, ValueSerializer<?> valueSer, NameTransformer unwrapper,
            Object suppressableValue, boolean suppressNulls)
    {
        super(base, property, vts, valueSer, unwrapper,
                suppressableValue, suppressNulls);
    }


    @Override
    protected ReferenceTypeSerializer<Optional<?>> withResolved(BeanProperty prop,
            TypeSerializer vts, ValueSerializer<?> valueSer,
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
