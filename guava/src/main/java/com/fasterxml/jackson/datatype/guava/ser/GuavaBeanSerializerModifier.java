package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.google.common.base.Optional;

import java.io.Serializable;
import java.util.List;

/**
 * {@link BeanSerializerModifier} needed to sneak in handler to exclude "absent"
 * optional values iff handling of "absent as nulls" is enabled.
 */
public class GuavaBeanSerializerModifier extends BeanSerializerModifier
    implements Serializable
{
    static final long serialVersionUID = 1L;
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
            BeanDescription beanDesc,
            List<BeanPropertyWriter> beanProperties)
    {
        for (int i = 0; i < beanProperties.size(); ++i) {
            final BeanPropertyWriter writer = beanProperties.get(i);
            if (Optional.class.isAssignableFrom(writer.getType().getRawClass())) {
                // Since 2.17: Added to preserve UnwrappingBeanProperty name transformer.
                if (writer instanceof UnwrappingBeanPropertyWriter) {
                    final AnnotatedMember member = writer.getMember();
                    final AnnotationIntrospector intr = config.getAnnotationIntrospector();
                    final NameTransformer unwrapper = intr.findUnwrappingNameTransformer(member);
                    beanProperties.set(i, new GuavaUnwrappingOptionalBeanPropertyWriter(writer, unwrapper));
                } else {
                    beanProperties.set(i, new GuavaOptionalBeanPropertyWriter(writer));
                }
            }
        }
        return beanProperties;
    }
}
