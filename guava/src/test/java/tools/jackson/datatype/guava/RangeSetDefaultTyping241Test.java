package tools.jackson.datatype.guava;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.util.StdConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class RangeSetDefaultTyping241Test extends ModuleTestBase
{
    static class Wrapper {
        public RangeSet<Integer> value;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    static class TypedRange { }

    static class FormattedWrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public RangeSet<Integer> value;
    }

    static class ConvertedWrapper {
        @JsonSerialize(contentConverter = RangeToString.class)
        @JsonDeserialize(contentConverter = StringToRange.class)
        public RangeSet<Integer> value;
    }

    static class CustomSerializerWrapper {
        @JsonSerialize(contentUsing = CurrentValueRangeSerializer.class)
        @JsonDeserialize(contentConverter = StringToRange.class)
        public RangeSet<Integer> value;
    }

    static class CurrentValueRangeSerializer extends StdSerializer<Range<Integer>> {
        CurrentValueRangeSerializer() {
            super(Range.class);
        }

        @Override
        public void serialize(Range<Integer> value, JsonGenerator g, SerializationContext ctxt) {
            assertInstanceOf(RangeSet.class, g.currentValue());
            g.writeString(value.lowerEndpoint() + ":" + value.upperEndpoint());
        }
    }

    static class RangeToString extends StdConverter<Range<Integer>, String> {
        @Override
        public String convert(Range<Integer> value) {
            return value.lowerEndpoint() + ":" + value.upperEndpoint();
        }
    }

    static class StringToRange extends StdConverter<String, Range<Integer>> {
        @Override
        public Range<Integer> convert(String value) {
            String[] bounds = value.split(":");
            return Range.closed(Integer.valueOf(bounds[0]), Integer.valueOf(bounds[1]));
        }
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

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void roundTripFormattedProperty(DefaultTyping typing) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing).build();
        FormattedWrapper input = new FormattedWrapper();
        input.value = ImmutableRangeSet.of(Range.closed(1, 5));
        assertEquals(input.value,
                mapper.readValue(mapper.writeValueAsString(input), FormattedWrapper.class).value);
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void roundTripConvertedProperty(DefaultTyping typing) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing).build();
        ConvertedWrapper input = new ConvertedWrapper();
        input.value = ImmutableRangeSet.of(Range.closed(1, 5));
        assertEquals(input.value,
                mapper.readValue(mapper.writeValueAsString(input), ConvertedWrapper.class).value);
    }

    @Test
    public void customSerializerSeesRangeSetAsCurrentValue() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.OBJECT_AND_NON_CONCRETE)
                .build();
        CustomSerializerWrapper input = new CustomSerializerWrapper();
        input.value = ImmutableRangeSet.<Integer>builder()
                .add(Range.closed(1, 5)).add(Range.closed(10, 20)).build();
        assertEquals(input.value,
                mapper.readValue(mapper.writeValueAsString(input), CustomSerializerWrapper.class).value);
    }

    @Test
    public void roundTripWithRangeTypeIds() {
        ObjectMapper mapper = builderWithModule()
                .addMixIn(Range.class, TypedRange.class)
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.OBJECT_AND_NON_CONCRETE)
                .build();
        TypeReference<RangeSet<Integer>> type = new TypeReference<>() { };
        RangeSet<Integer> input = ImmutableRangeSet.of(Range.closed(1, 5));
        assertEquals(input, mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type));
        Wrapper wrapper = new Wrapper();
        wrapper.value = input;
        assertEquals(input, mapper.readValue(mapper.writeValueAsString(wrapper), Wrapper.class).value);
    }
}
