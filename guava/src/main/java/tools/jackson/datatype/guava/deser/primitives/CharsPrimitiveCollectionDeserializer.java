package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.datatype.guava.util.PrimitiveTypes;

import com.google.common.primitives.Chars;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CharsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Character, List<Character>, Collection<Character>> {
    public CharsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.CharsType, Character.class);
    }

    @Override
    protected Character asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getValueAsString().charAt(0);
    }

    @Override
    protected List<Character> finish(Collection<Character> characters) {
        return Chars.asList(Chars.toArray(characters));
    }

    // Overridden to collect into a primitive array, avoiding boxing every element
    @Override
    protected List<Character> _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        char[] chars = new char[16];
        int ix = 0;
        while (parser.nextToken() != JsonToken.END_ARRAY) {
            char value = parser.getValueAsString().charAt(0);
            if (ix >= chars.length) {
                chars = Arrays.copyOf(chars, chars.length << 1);
            }
            chars[ix++] = value;
        }
        return Chars.asList(Arrays.copyOf(chars, ix));
    }
}
