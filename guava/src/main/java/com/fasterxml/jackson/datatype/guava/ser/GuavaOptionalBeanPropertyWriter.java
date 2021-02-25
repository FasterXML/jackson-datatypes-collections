package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.google.common.base.Optional;

public class GuavaOptionalBeanPropertyWriter extends BeanPropertyWriter
{
    private static final long serialVersionUID = 1;

    protected GuavaOptionalBeanPropertyWriter(BeanPropertyWriter base) {
        super(base);
    }

    protected GuavaOptionalBeanPropertyWriter(BeanPropertyWriter base, PropertyName newName) {
        super(base, newName);
    }

    @Override
    protected BeanPropertyWriter _new(PropertyName newName) {
        return new GuavaOptionalBeanPropertyWriter(this, newName);
    }

    @Override
    public BeanPropertyWriter unwrappingWriter(NameTransformer unwrapper) {
        return new GuavaUnwrappingOptionalBeanPropertyWriter(this, unwrapper);
    }

    @Override
    public void serializeAsProperty(Object bean, JsonGenerator gen, SerializerProvider prov)
        throws Exception
    {
        if (_nullSerializer == null) {
            Object value = get(bean);
            if (value == null || Optional.absent().equals(value)) {
                return;
            }
        }
        super.serializeAsProperty(bean, gen, prov);
    }
}
