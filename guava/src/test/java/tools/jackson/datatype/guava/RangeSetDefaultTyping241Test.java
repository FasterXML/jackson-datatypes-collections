package tools.jackson.datatype.guava;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.util.StdConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RangeSetDefaultTyping241Test extends ModuleTestBase
{
    static class Wrapper {
        public RangeSet<Integer> value;
    }

    static class ScalarWrapper<T extends Comparable<?>> {
        public RangeSet<T> value;
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

    static Stream<Arguments> scalarTyping() {
        return Stream.of(DefaultTyping.NON_FINAL, DefaultTyping.OBJECT_AND_NON_CONCRETE)
                .flatMap(typing -> Stream.of(JsonTypeInfo.As.WRAPPER_ARRAY,
                        JsonTypeInfo.As.WRAPPER_OBJECT, JsonTypeInfo.As.PROPERTY)
                        .map(inclusion -> Arguments.of(typing, inclusion)));
    }

    @ParameterizedTest
    @MethodSource("scalarTyping")
    public void roundTripDecimalRoot(DefaultTyping typing, JsonTypeInfo.As inclusion) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing, inclusion).build();
        TypeReference<RangeSet<BigDecimal>> type = new TypeReference<>() { };
        RangeSet<BigDecimal> input = ImmutableRangeSet.of(
                Range.closed(new BigDecimal("1.20"), new BigDecimal("5.30")));
        assertEquals(input, mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type));
    }

    @ParameterizedTest
    @MethodSource("scalarTyping")
    public void roundTripDecimalProperty(DefaultTyping typing, JsonTypeInfo.As inclusion) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing, inclusion).build();
        TypeReference<ScalarWrapper<BigDecimal>> type = new TypeReference<>() { };
        ScalarWrapper<BigDecimal> input = new ScalarWrapper<>();
        input.value = ImmutableRangeSet.of(Range.closed(new BigDecimal("1.20"), new BigDecimal("5.30")));
        assertEquals(input.value, mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type).value);
    }

    @ParameterizedTest
    @MethodSource("scalarTyping")
    public void roundTripDateRoot(DefaultTyping typing, JsonTypeInfo.As inclusion) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing, inclusion).build();
        TypeReference<RangeSet<Date>> type = new TypeReference<>() { };
        RangeSet<Date> input = ImmutableRangeSet.of(Range.closed(new Date(1), new Date(5)));
        assertEquals(input, mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type));
    }

    @ParameterizedTest
    @MethodSource("scalarTyping")
    public void roundTripDateProperty(DefaultTyping typing, JsonTypeInfo.As inclusion) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing, inclusion).build();
        TypeReference<ScalarWrapper<Date>> type = new TypeReference<>() { };
        ScalarWrapper<Date> input = new ScalarWrapper<>();
        input.value = ImmutableRangeSet.of(Range.closed(new Date(1), new Date(5)));
        assertEquals(input.value, mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type).value);
    }

    @Test
    public void declaredIntegerEndpointDoesNotRequireSubtypePermission() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("com.google.common.collect.").build(), DefaultTyping.NON_FINAL).build();
        TypeReference<RangeSet<Integer>> type = new TypeReference<>() { };
        RangeSet<Integer> input = ImmutableRangeSet.of(Range.closed(1, 5));
        assertEquals(input, mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type));
    }

    @Test
    public void plainDateDoesNotRequirePolymorphicPermission() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .denyForExactBaseType(Date.class).build(), DefaultTyping.NON_FINAL).build();
        assertEquals(Range.atLeast(new Date(1)), mapper.readValue(
                a2q("{'lowerEndpoint':1,'lowerBoundType':'CLOSED'}"),
                new TypeReference<Range<Date>>() { }));
    }

    @Test
    public void typedScalarStillValidatesSubtype() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder().build(),
                        DefaultTyping.NON_FINAL).build();
        assertThrows(InvalidTypeIdException.class, () -> mapper.readValue(
                a2q("{'lowerEndpoint':['java.math.BigDecimal',1.20],'lowerBoundType':'CLOSED'}"),
                new TypeReference<Range<BigDecimal>>() { }));
    }

    @ParameterizedTest
    @ValueSource(strings = { "1.20", "'1.20'", "[1.20]", "['1.20']" })
    public void scalarCoercionWithDefaultTyping(String endpoint) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL)
                .enable(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS).build();
        assertEquals(Range.atLeast(new BigDecimal("1.20")), mapper.readValue(
                a2q("{'lowerEndpoint':" + endpoint + ",'lowerBoundType':'CLOSED'}"),
                new TypeReference<Range<BigDecimal>>() { }));
    }

    @Test
    public void emptyScalarArrayKeepsCoercionSetting() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL)
                .withCoercionConfig(BigDecimal.class,
                        config -> config.setCoercion(CoercionInputShape.EmptyArray, CoercionAction.AsEmpty))
                .build();
        assertEquals(Range.atLeast(BigDecimal.ZERO), mapper.readValue(
                a2q("{'lowerEndpoint':[],'lowerBoundType':'CLOSED'}"),
                new TypeReference<Range<BigDecimal>>() { }));
    }

    @ParameterizedTest
    @ValueSource(strings = { "[1.20]", "['1.20']" })
    public void scalarArrayStillRequiresUnwrapping(String endpoint) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL).build();
        assertThrows(MismatchedInputException.class, () -> mapper.readValue(
                a2q("{'lowerEndpoint':" + endpoint + ",'lowerBoundType':'CLOSED'}"),
                new TypeReference<Range<BigDecimal>>() { }));
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
