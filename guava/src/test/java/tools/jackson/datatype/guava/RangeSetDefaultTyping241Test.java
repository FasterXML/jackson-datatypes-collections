package tools.jackson.datatype.guava;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RangeSetDefaultTyping241Test extends ModuleTestBase
{
    static class Wrapper {
        public RangeSet<Integer> value;
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void roundTripRoot(DefaultTyping typing) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing).build();
        TypeReference<RangeSet<Integer>> type = new TypeReference<>() { };
        RangeSet<Integer> mutable = TreeRangeSet.create();
        mutable.add(Range.closed(1, 5));
        mutable.add(Range.open(10, 20));
        for (RangeSet<Integer> input : List.of(mutable, ImmutableRangeSet.copyOf(mutable),
                ImmutableRangeSet.<Integer>of())) {
            String json = mapper.writerFor(type).writeValueAsString(input);
            assertEquals(input, mapper.readValue(json, type));
        }
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void roundTripPropertyWithUnwrapping(DefaultTyping typing) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing)
                .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED).build();
        Wrapper input = new Wrapper();
        input.value = ImmutableRangeSet.of(Range.closed(1, 5));
        assertEquals(input.value, mapper.readValue(mapper.writeValueAsString(input), Wrapper.class).value);
    }
}
