package tools.jackson.datatype.fastutil.util;

import java.util.Collection;
import java.util.Map;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.util.ClassUtil;

/**
 * Helper for merging deserialized content into existing containers (with
 * {@code @JsonMerge} or {@code ObjectReader.readerForUpdating()}): collections
 * get elements added, maps get entries added (replacing, not merging, existing values).
 *<p>
 * Internal API: not exported from the module.
 */
public final class Merging
{
    private Merging() { }

    @SuppressWarnings("unchecked")
    public static <C> C addAll(DeserializationContext ctxt, C intoValue, Collection<?> elements)
        throws JacksonException
    {
        try {
            ((Collection<Object>) intoValue).addAll(elements);
        } catch (UnsupportedOperationException e) {
            throw _mergeFailure(ctxt, intoValue, e);
        }
        return intoValue;
    }

    @SuppressWarnings("unchecked")
    public static <M> M putAll(DeserializationContext ctxt, M intoValue, Map<?, ?> entries)
        throws JacksonException
    {
        try {
            ((Map<Object, Object>) intoValue).putAll(entries);
        } catch (UnsupportedOperationException e) {
            throw _mergeFailure(ctxt, intoValue, e);
        }
        return intoValue;
    }

    // Existing value may be unmodifiable (like one from `IntLists.unmodifiable()`)
    private static DatabindException _mergeFailure(DeserializationContext ctxt, Object intoValue,
            UnsupportedOperationException e)
    {
        return DatabindException.from(ctxt, String.format(
                "Cannot merge into value of type %s (unmodifiable?): %s",
                ClassUtil.classNameOf(intoValue), ClassUtil.exceptionMessage(e)), e);
    }
}
