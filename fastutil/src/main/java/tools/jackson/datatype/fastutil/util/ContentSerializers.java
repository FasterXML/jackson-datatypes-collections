package tools.jackson.datatype.fastutil.util;

import tools.jackson.databind.AnnotationIntrospector;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.introspect.AnnotatedMember;

/**
 * Helper for serializers of primitive containers, which need to defer to the
 * standard Jackson serializers when a property specifies handlers for boxed values.
 *<p>
 * Internal API: not exported from the module.
 */
public final class ContentSerializers
{
    private ContentSerializers() { }

    /**
     * @return Whether given property specifies a custom serializer
     *    ({@code @JsonSerialize(contentUsing = ...)}) or converter
     *    ({@code contentConverter}) for elements (or map values)
     */
    public static boolean hasCustomContentSerialization(SerializationContext ctxt,
            BeanProperty property)
    {
        final AnnotatedMember member = (property == null) ? null : property.getMember();
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        if (member == null || intr == null) {
            return false;
        }
        return (intr.findContentSerializer(ctxt.getConfig(), member) != null)
                || (intr.findSerializationContentConverter(ctxt.getConfig(), member) != null);
    }
}
