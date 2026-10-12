package tools.jackson.datatype.guava.util;

import java.util.function.Function;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;

/**
 * Shared helper for adding {@code null} elements (or values) into Guava containers,
 * many of which do not accept them.
 *<p>
 * NOTE: internal helper; package not exported by module, not part of
 * the public API.
 */
public final class NullElementHelper
{
    private NullElementHelper() { }

    /**
     * Method that applies given "adder" to given value. If value is {@code null}
     * and adder fails with {@link NullPointerException} (container does not accept
     * {@code null}s), problem is reported via
     * {@link DeserializationContext#handleUnexpectedToken}: this allows a
     * {@link tools.jackson.databind.deser.DeserializationProblemHandler} to provide
     * a replacement value (which is then added), or to drop the element by returning
     * {@code null}; without a handler, a
     * {@link tools.jackson.databind.exc.MismatchedInputException} is thrown.
     *<p>
     * Callers must pass the token parser is currently positioned at
     * ({@code p.currentToken()}), not the token value started at: for structured
     * values, value deserializer has already advanced parser to the matching
     * {@code END_OBJECT} / {@code END_ARRAY}, and a problem handler may want to
     * access the parser.
     *
     * @param t Token parser is currently positioned at (for structured values,
     *    the matching {@code END_OBJECT} / {@code END_ARRAY}, not the start token)
     * @param elementType Type of elements (or values) of container
     * @param containerDesc Description of the container for the error message
     *
     * @return Result of adder, or {@code null} if element was dropped
     */
    public static <R> R addElement(JsonParser p, DeserializationContext ctxt, JsonToken t,
            JavaType elementType, String containerDesc,
            Object value, Function<Object, R> adder)
        throws JacksonException
    {
        if (value == null) {
            try {
                return adder.apply(null);
            } catch (NullPointerException e) {
                value = handleNull(p, ctxt, t, elementType, containerDesc);
                if (value == null) {
                    return null;
                }
            }
        }
        return adder.apply(value);
    }

    /**
     * Method for reporting a {@code null} element (or value) that the container
     * does not accept, for cases where caller knows this without trying to add it.
     *
     * @param t Token parser is currently positioned at; see
     *    {@link #addElement} for details
     * @return Replacement value given by a problem handler, or {@code null} if
     *    element is to be dropped
     */
    public static Object handleNull(JsonParser p, DeserializationContext ctxt, JsonToken t,
            JavaType elementType, String containerDesc)
        throws JacksonException
    {
        return ctxt.handleUnexpectedToken(elementType, t, p,
                "%s does not accept `null` values", containerDesc);
    }
}
