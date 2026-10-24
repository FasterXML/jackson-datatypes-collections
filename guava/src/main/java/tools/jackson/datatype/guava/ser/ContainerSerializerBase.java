package tools.jackson.datatype.guava.ser;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.jsontype.TypeSerializer;
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
     * Method for locating serializer to use for content values, if it can be
     * determined statically: either content type is final, or it is explicitly
     * specified (for example, root type passed via {@code writerFor()}).
     *<p>
     * Note: static handling is not used for polymorphic content (one with
     * {@link TypeSerializer}) of non-final types, since the declared type may
     * only be a base type of actual values.
     *
     * @return Serializer to use for the content type if it is to be handled
     *   statically; {@code null} if it should be looked up dynamically (from
     *   the runtime type of each value)
     */
    protected ValueSerializer<Object> _findStaticContentSerializer(SerializationContext ctxt,
            JavaType contentType, TypeSerializer contentTypeSer, BeanProperty property)
    {
        if (contentType.isFinal()) {
            return ctxt.findContentValueSerializer(contentType, property);
        }
        if (contentTypeSer != null) {
            return null;
        }
        // `Object` is only a placeholder for "any value": resolving it statically
        // would yield the (empty) bean serializer and lose actual values [#244]
        if (contentType.useStaticType() && !contentType.isJavaLangObject()) {
            return ctxt.findContentValueSerializer(contentType, property);
        }
        return null;
    }
}
