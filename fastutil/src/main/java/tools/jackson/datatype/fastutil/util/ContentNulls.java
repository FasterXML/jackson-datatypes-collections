package tools.jackson.datatype.fastutil.util;

import com.fasterxml.jackson.annotation.Nulls;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.deser.impl.NullsConstantProvider;
import tools.jackson.databind.deser.impl.NullsFailProvider;

/**
 * Helper for handling {@code @JsonSetter(contentNulls = ...)} (and the matching default
 * setting) for elements of collections and values of maps the same way as
 * {@code jackson-databind} does for primitive arrays: only {@code SKIP} and {@code FAIL}
 * need special handling, others handle JSON nulls as specified by
 * {@link tools.jackson.databind.DeserializationFeature#FAIL_ON_NULL_FOR_PRIMITIVES}.
 *<p>
 * Internal API: not exported from the module.
 */
public final class ContentNulls
{
    private ContentNulls() { }

    /**
     * @param containerType Type of the collection or map, used for error messages
     *    (for root values)
     *
     * @return Handler that skips JSON nulls ({@code SKIP}) or fails on them
     *    ({@code FAIL}); {@code null} for other settings
     */
    public static NullValueProvider findNuller(DeserializationContext ctxt, BeanProperty property,
            JavaType containerType)
    {
        final Nulls nulls = (property == null)
                ? ctxt.getConfig().getDefaultNullHandling().getContentNulls()
                : property.getMetadata().getContentNulls();
        if (nulls == Nulls.SKIP) {
            return NullsConstantProvider.skipper();
        }
        if (nulls == Nulls.FAIL) {
            return (property == null)
                    ? NullsFailProvider.constructForRootValue(containerType.getContentType())
                    : NullsFailProvider.constructForProperty(property, property.getType().getContentType());
        }
        return null;
    }
}
