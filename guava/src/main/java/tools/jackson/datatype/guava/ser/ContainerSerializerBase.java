package tools.jackson.datatype.guava.ser;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.ser.std.StdContainerSerializer;

/**
 * Base class for Guava container serializers that have an explicit content type
 * ({@link CacheSerializer}, {@link MultimapSerializer}, {@link RangeMapSerializer},
 * {@link TableSerializer}),
 * for sharing handling of that content type.
 */
abstract class ContainerSerializerBase<T>
    extends StdContainerSerializer<T>
{
    protected ContainerSerializerBase(JavaType type, BeanProperty property) {
        super(type, property);
    }

    protected ContainerSerializerBase(ContainerSerializerBase<?> src) {
        super(src);
    }

    protected ContainerSerializerBase(ContainerSerializerBase<?> src, BeanProperty property) {
        super(src, property);
    }

    /**
     * Method for locating serializer to use for statically typed content, if
     * content type is explicitly specified (for example, root type passed
     * via {@code writerFor()}).
     *
     * @return Serializer to use for the content type if it is to be handled
     *   statically; {@code null} if it should be looked up dynamically (from
     *   the runtime type of each value)
     */
    protected ValueSerializer<Object> _findStaticContentSerializer(SerializationContext ctxt,
            JavaType contentType, BeanProperty property)
    {
        // `Object` is only a placeholder for "any value": resolving it statically
        // would yield the (empty) bean serializer and lose actual values [#244]
        if (contentType.useStaticType() && !contentType.isJavaLangObject()) {
            return ctxt.findContentValueSerializer(contentType, property);
        }
        return null;
    }
}
