package tools.jackson.datatype.guava.ser;

import java.io.Serializable;
import java.util.List;

import com.google.common.base.Optional;

import tools.jackson.databind.*;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.ser.BeanPropertyWriter;
import tools.jackson.databind.ser.ValueSerializerModifier;
import tools.jackson.databind.ser.bean.UnwrappingBeanPropertyWriter;
import tools.jackson.databind.util.NameTransformer;

/**
 * {@link ValueSerializerModifier} needed to sneak in handler to exclude "absent"
 * optional values iff handling of "absent as nulls" is enabled.
 */
public class GuavaBeanSerializerModifier extends ValueSerializerModifier
    implements Serializable
{
    static final long serialVersionUID = 1L;
    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config,
            BeanDescription.Supplier beanDescRef, List<BeanPropertyWriter> beanProperties)
    {
        for (int i = 0; i < beanProperties.size(); ++i) {
            final BeanPropertyWriter writer = beanProperties.get(i);
            if (Optional.class.isAssignableFrom(writer.getType().getRawClass())) {
                // Since 2.17: Added to preserve UnwrappingBeanProperty name transformer.
                if (writer instanceof UnwrappingBeanPropertyWriter) {
                    final AnnotatedMember member = writer.getMember();
                    final AnnotationIntrospector intr = config.getAnnotationIntrospector();
                    final NameTransformer unwrapper = intr.findUnwrappingNameTransformer(config, member);
                    beanProperties.set(i, new GuavaUnwrappingOptionalBeanPropertyWriter(writer, unwrapper));
                } else {
                    beanProperties.set(i, new GuavaOptionalBeanPropertyWriter(writer));
                }
            }
        }
        return beanProperties;
    }
}
