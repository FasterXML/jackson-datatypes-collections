package tools.jackson.datatype.guava;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.google.common.primitives.ImmutableDoubleArray;
import com.google.common.primitives.ImmutableIntArray;
import com.google.common.primitives.ImmutableLongArray;

import tools.jackson.databind.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImmutableArraysDefaultTyping242Test extends ModuleTestBase
{
    static class Wrapper {
        public Object value;
    }

    private final Object[] _values = {
        ImmutableIntArray.of(), ImmutableIntArray.of(1), ImmutableIntArray.of(1, 2),
        ImmutableLongArray.of(), ImmutableLongArray.of(1L), ImmutableLongArray.of(1L, Long.MAX_VALUE),
        ImmutableDoubleArray.of(), ImmutableDoubleArray.of(0.5), ImmutableDoubleArray.of(0.5, -2.5)
    };

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void roundTripRoot(DefaultTyping typing) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing).build();
        for (Object value : _values) {
            String json = mapper.writerFor(Object.class).writeValueAsString(value);
            assertEquals(value, mapper.readValue(json, Object.class));
        }
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void roundTripPropertyWithUnwrapping(DefaultTyping typing) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing)
                .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED).build();
        for (Object value : _values) {
            Wrapper input = new Wrapper();
            input.value = value;
            String json = mapper.writeValueAsString(input);
            assertEquals(value, mapper.readValue(json, Wrapper.class).value);
        }
    }
}
