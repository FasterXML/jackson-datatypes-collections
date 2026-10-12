package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Chars;

import java.util.Collection;
import java.util.List;

public class CharsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Character, List<Character>, Collection<Character>> {
    private final ValueDeserializer<char[]> _arrayDeserializer;

    @SuppressWarnings("unchecked")
    public CharsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.CharsType, Character.class);
        _arrayDeserializer = (ValueDeserializer<char[]>) PrimitiveArrayDeserializers.forType(char.class);
    }

    @Deprecated // since 3.3
    @Override
    protected Character asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getValueAsString().charAt(0);
    }

    @Override
    protected Character asPrimitive(JsonParser parser, DeserializationContext context) throws JacksonException {
        if (parser.hasToken(JsonToken.VALUE_NULL)) {
            return (char) _verifyNullForPrimitive(context, parser, '\0');
        }
        final String str = parser.hasToken(JsonToken.VALUE_STRING)
                ? parser.getString()
                : ((CharSequence) context.handleUnexpectedToken(Character.TYPE, parser)).toString();
        if (str.length() != 1) {
            context.reportInputMismatch(this,
                    "Cannot convert a JSON String of length %d into a `char`", str.length());
        }
        return str.charAt(0);
    }

    @Override
    protected List<Character> finish(Collection<Character> characters) {
        return Chars.asList(Chars.toArray(characters));
    }

    /**
     * Delegates to databind's {@code char[]} deserializer to avoid boxing;
     * note that {@link #add}, {@link #asPrimitive} and {@link #finish} are
     * therefore only called for single-value input, not for JSON Arrays.
     */
    @Override
    protected List<Character> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        return Chars.asList(_arrayDeserializer.deserialize(parser, context));
    }
}
