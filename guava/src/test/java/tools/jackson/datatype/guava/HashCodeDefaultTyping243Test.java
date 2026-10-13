package tools.jackson.datatype.guava;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.google.common.hash.HashCode;

import tools.jackson.databind.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashCodeDefaultTyping243Test extends ModuleTestBase
{
    static class Wrapper {
        public Object value;
    }

    private final HashCode[] _values = {
        HashCode.fromInt(42), HashCode.fromLong(123456789L),
        HashCode.fromString("cafebabe12345678")
    };

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void roundTripRoot(DefaultTyping typing) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing).build();
        for (HashCode value : _values) {
            String json = mapper.writerFor(HashCode.class).writeValueAsString(value);
            assertEquals(value, mapper.readValue(json, HashCode.class));
            assertEquals(value, mapper.readValue(json, Object.class));
        }
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void roundTripProperty(DefaultTyping typing) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing).build();
        for (HashCode value : _values) {
            Wrapper input = new Wrapper();
            input.value = value;
            assertEquals(value, mapper.readValue(mapper.writeValueAsString(input), Wrapper.class).value);
        }
    }
}
