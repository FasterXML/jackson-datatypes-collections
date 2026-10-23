package tools.jackson.datatype.fastutil.util;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.util.ClassUtil;

/**
 * Helper for deserializing maps from JSON Strings, for deserializers that do not
 * extend {@code StdDeserializer}: same as its {@code _deserializeFromStringForContainer()}
 * (used for {@code java.util} maps), so empty (and blank) Strings may be coerced as
 * configured with {@code DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT} or
 * coercion configuration for {@code LogicalType.Map}; other Strings are not accepted.
 *<p>
 * Collection deserializers extend {@code StdDeserializer} and use it directly.
 *<p>
 * Internal API: not exported from the module.
 */
public final class EmptyStrings
{
    private EmptyStrings() { }

    /**
     * Called when the current token is a JSON String.
     *
     * @return {@code null} or empty container, depending on configured coercion
     */
    public static Object deserializeFromString(JsonParser p, DeserializationContext ctxt,
            ValueDeserializer<?> deser)
        throws JacksonException
    {
        final Class<?> rawTargetType = deser.handledType();
        final JavaType targetType = ctxt.constructType(rawTargetType);
        final String value = p.getValueAsString();
        // Some parsers (like Ion, for corrupt content) may report VALUE_STRING without text
        if (value == null) {
            return ctxt.handleUnexpectedToken(targetType, p);
        }
        final CoercionAction act;
        if (value.isEmpty()) {
            act = ctxt.findCoercionAction(deser.logicalType(), rawTargetType,
                    CoercionInputShape.EmptyString);
        } else if (value.isBlank()) {
            act = ctxt.findCoercionFromBlankString(deser.logicalType(), rawTargetType,
                    CoercionAction.Fail);
        } else {
            return ctxt.handleUnexpectedToken(targetType, p);
        }
        switch (act) {
        case AsEmpty:
            return deser.getEmptyValue(ctxt);
        case Fail:
            ctxt.reportBadCoercion(deser, rawTargetType, value,
                    "Cannot coerce %s to element of %s (but could if coercion was enabled using `CoercionConfig`)",
                    value.isEmpty() ? "empty String (\"\")" : "blank String (all whitespace)",
                    ClassUtil.getClassDescription(rawTargetType));
            return null; // never gets here
        case TryConvert: // empty or null, typically: assume "as null"
        case AsNull:
        default:
            return null;
        }
    }
}
