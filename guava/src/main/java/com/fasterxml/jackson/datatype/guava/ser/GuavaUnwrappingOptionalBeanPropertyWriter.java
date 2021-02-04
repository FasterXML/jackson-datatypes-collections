package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.bean.UnwrappingBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.google.common.base.Optional;

public class GuavaUnwrappingOptionalBeanPropertyWriter extends UnwrappingBeanPropertyWriter
{
    private static final long serialVersionUID = 1L;

    public GuavaUnwrappingOptionalBeanPropertyWriter(BeanPropertyWriter base,
            NameTransformer transformer) {
        super(base, transformer);
    }

    protected GuavaUnwrappingOptionalBeanPropertyWriter(UnwrappingBeanPropertyWriter base,
            NameTransformer transformer, SerializedString name) {
        super(base, transformer, name);
    }

    @Override
    protected UnwrappingBeanPropertyWriter _new(NameTransformer transformer, SerializedString newName)
    {
        return new GuavaUnwrappingOptionalBeanPropertyWriter(this, transformer, newName);
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
