package tools.jackson.datatype.fastutil.util;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;

/**
 * Helper for coercion of empty (and blank) JSON Strings into containers, as
 * configured with {@code DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT} or
 * coercion configuration for the logical type of the deserializer (like
 * {@code LogicalType.Collection}), the same way as standard {@code jackson-databind}
 * deserializers for {@code java.util} containers do.
 *<p>
 * Internal API: not exported from the module.
 */
public final class EmptyStrings
{
    private EmptyStrings() { }

    /**
     * @return Coercion to apply, if the current token is an empty or blank JSON String
     *    and it is to be coerced (as {@code null} or empty container);
     *    {@code null} otherwise (including if coercion is not allowed: caller
     *    then handles the String as usual)
     */
    public static CoercionAction findCoercion(JsonParser p, DeserializationContext ctxt,
            ValueDeserializer<?> deser)
        throws JacksonException
    {
        if (!p.hasToken(JsonToken.VALUE_STRING)) {
            return null;
        }
        final String value = p.getString();
        final CoercionAction act;
        if (value.isEmpty()) {
            act = ctxt.findCoercionAction(deser.logicalType(), deser.handledType(),
                    CoercionInputShape.EmptyString);
        } else if (value.isBlank()) {
            act = ctxt.findCoercionFromBlankString(deser.logicalType(), deser.handledType(),
                    CoercionAction.Fail);
        } else {
            return null;
        }
        return (act == CoercionAction.Fail) ? null : act;
    }

    /**
     * @return Value for given coercion: empty container for
     *    {@link CoercionAction#AsEmpty}; {@code null} otherwise
     */
    public static Object coercedValue(DeserializationContext ctxt, ValueDeserializer<?> deser,
            CoercionAction act)
        throws JacksonException
    {
        return (act == CoercionAction.AsEmpty) ? deser.getEmptyValue(ctxt) : null;
    }
}
