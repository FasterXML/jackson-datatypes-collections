package tools.jackson.datatype.guava;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.ser.std.StdSerializer;
import tools.jackson.databind.util.StdConverter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RangeSetDefaultTyping241Test extends ModuleTestBase
{
    static class Wrapper {
        public RangeSet<Integer> value;
    }

    static class ObjectWrapper {
        public Object value;
    }

    static class ScalarWrapper<T extends Comparable<?>> {
        public RangeSet<T> value;
    }

    static class BeanEndpoint implements Comparable<BeanEndpoint> {
        public String name;
        public int value;

        public BeanEndpoint() { }

        BeanEndpoint(String name, int value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public int compareTo(BeanEndpoint other) {
            return Integer.compare(value, other.value);
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof BeanEndpoint && value == ((BeanEndpoint) other).value
                    && Objects.equals(name, ((BeanEndpoint) other).name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }
    }

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({ "name", "value" })
    static class ArrayEndpoint extends BeanEndpoint {
        public ArrayEndpoint() { }

        ArrayEndpoint(String name, int value) {
            super(name, value);
        }
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

    static Stream<Arguments> objectEndpoints() {
        return scalarTyping().flatMap(args -> Stream.of(BeanEndpoint.class, ArrayEndpoint.class)
                .map(type -> Arguments.of(args.get()[0], args.get()[1], type)));
    }

    private BeanEndpoint endpoint(Class<? extends BeanEndpoint> type, String name, int value) {
        return type == ArrayEndpoint.class ? new ArrayEndpoint(name, value) : new BeanEndpoint(name, value);
    }

    @ParameterizedTest
    @MethodSource("objectEndpoints")
    public void roundTripObjectEndpointRoot(DefaultTyping typing, JsonTypeInfo.As inclusion,
            Class<? extends BeanEndpoint> endpointType) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing, inclusion).build();
        JavaType type = mapper.getTypeFactory().constructParametricType(RangeSet.class, endpointType);
        RangeSet<BeanEndpoint> input = ImmutableRangeSet.of(Range.closed(
                endpoint(endpointType, "a", 1), endpoint(endpointType, "b", 5)));
        assertEquals(input, mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type));
    }

    @ParameterizedTest
    @MethodSource("objectEndpoints")
    public void roundTripObjectEndpointProperty(DefaultTyping typing, JsonTypeInfo.As inclusion,
            Class<? extends BeanEndpoint> endpointType) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing, inclusion).build();
        JavaType type = mapper.getTypeFactory().constructParametricType(ScalarWrapper.class, endpointType);
        ScalarWrapper<BeanEndpoint> input = new ScalarWrapper<>();
        input.value = ImmutableRangeSet.of(Range.closed(
                endpoint(endpointType, "a", 1), endpoint(endpointType, "b", 5)));
        ScalarWrapper<?> output = mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type);
        assertEquals(input.value, output.value);
    }

    @ParameterizedTest
    @MethodSource("objectEndpoints")
    public void plainRangeWithObjectEndpointsKeepsItsFormat(DefaultTyping typing, JsonTypeInfo.As inclusion,
            Class<? extends BeanEndpoint> endpointType) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing, inclusion).build();
        JavaType type = mapper.getTypeFactory().constructParametricType(Range.class, endpointType);
        Range<BeanEndpoint> input = Range.closed(
                endpoint(endpointType, "a", 1), endpoint(endpointType, "b", 5));
        assertEquals(input, mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type));
    }

    // Endpoints are written without type ids, so JSON from other producers reads as well
    @Test
    public void readUntypedObjectEndpoints() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL).build();
        RangeSet<BeanEndpoint> result = mapper.readValue(
                a2q("[{'lowerEndpoint':{'name':'a','value':1},'lowerBoundType':'CLOSED'}]"),
                new TypeReference<ImmutableRangeSet<BeanEndpoint>>() { });
        assertEquals(ImmutableRangeSet.of(Range.atLeast(new BeanEndpoint("a", 1))), result);
    }

    // `Range` with unknown endpoint type (here, held in an `Object` property) also writes
    // endpoints without type ids; previously it wrote ids that could not be read back
    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void untypedRangeEndpointsWrittenWithoutTypeIds(DefaultTyping typing) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing).build();
        ObjectWrapper input = new ObjectWrapper();
        input.value = Range.closed(new BigDecimal("1.5"), new BigDecimal("2.5"));
        String json = mapper.writeValueAsString(input);
        assertFalse(json.contains("java.math.BigDecimal"), json);
        ObjectWrapper output = mapper.readValue(json, ObjectWrapper.class);
        // Endpoint type is not known when reading, so numbers come back as natural types
        assertEquals(Range.closed(1.5, 2.5), output.value);
    }

    // Numeric endpoints that read back as different types are widened across ranges
    @Test
    public void mixedNumberTypesAcrossRanges() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL).build();
        TreeRangeSet<Long> set = TreeRangeSet.create();
        set.add(Range.closed(1L, 2L));
        set.add(Range.atLeast(5_000_000_000L));
        ObjectWrapper input = new ObjectWrapper();
        input.value = set;
        ObjectWrapper output = mapper.readValue(mapper.writeValueAsString(input), ObjectWrapper.class);
        assertEquals(set, output.value);
    }

    // Ranges with incompatible (non-numeric) endpoint types must fail cleanly
    @Test
    public void mixedEndpointTypesAcrossRanges() {
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> mapperWithModule().readValue(
                        a2q("[{'lowerEndpoint':1,'lowerBoundType':'CLOSED'},"
                                + "{'lowerEndpoint':'a','lowerBoundType':'CLOSED'}]"),
                        new TypeReference<RangeSet<?>>() { }));
        verifyException(e, "Failed to build");
    }

    // Invalid (empty) range must be reported with its index
    @Test
    public void emptyRangeReportedWithIndex() {
        DatabindException e = assertThrows(DatabindException.class,
                () -> mapperWithModule().readValue(
                        a2q("[{'lowerEndpoint':1,'lowerBoundType':'CLOSED','upperEndpoint':2,'upperBoundType':'CLOSED'},"
                                + "{'lowerEndpoint':1,'lowerBoundType':'OPEN','upperEndpoint':1,'upperBoundType':'CLOSED'}]"),
                        new TypeReference<RangeSet<Integer>>() { }));
        verifyException(e, "range must not be empty");
        assertEquals(1, e.getPath().get(0).getIndex());
    }

    // Unwrapped single range must also see `RangeSet` as current value
    @Test
    public void customSerializerSeesRangeSetAsCurrentValueUnwrapped() {
        ObjectMapper mapper = builderWithModule()
                .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED).build();
        CustomSerializerWrapper input = new CustomSerializerWrapper();
        input.value = ImmutableRangeSet.of(Range.closed(1, 5));
        assertEquals(a2q("{'value':'1:5'}"), mapper.writeValueAsString(input));
    }

    // Deserializer always builds `ImmutableRangeSet`, so other implementations use `RangeSet` id
    @Test
    public void treeRangeSetUsesRangeSetTypeId() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL).build();
        TreeRangeSet<Integer> set = TreeRangeSet.create();
        set.add(Range.closed(1, 5));
        Wrapper input = new Wrapper();
        input.value = set;
        String json = mapper.writeValueAsString(input);
        assertTrue(json.contains("\"com.google.common.collect.RangeSet\""), json);
        assertEquals(set, mapper.readValue(json, Wrapper.class).value);

        input.value = ImmutableRangeSet.copyOf(set);
        json = mapper.writeValueAsString(input);
        assertTrue(json.contains("\"com.google.common.collect.ImmutableRangeSet\""), json);
    }

    // Non-public views (like `complement()`) must be written with a readable type id
    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void roundTripRangeSetViews(DefaultTyping typing) {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing).build();
        TreeRangeSet<Integer> base = TreeRangeSet.create();
        base.add(Range.closed(1, 5));
        base.add(Range.open(10, 20));
        for (RangeSet<Integer> view : List.of(base.complement(), base.subRangeSet(Range.closed(3, 15)))) {
            Wrapper input = new Wrapper();
            input.value = view;
            assertEquals(view, mapper.readValue(mapper.writeValueAsString(input), Wrapper.class).value);
        }
    }

    @Test
    public void roundTripCalendarRoot() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL).build();
        TypeReference<RangeSet<Calendar>> type = new TypeReference<>() { };
        Calendar lower = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        lower.setTimeInMillis(1L);
        Calendar upper = (Calendar) lower.clone();
        upper.setTimeInMillis(5L);
        RangeSet<Calendar> input = ImmutableRangeSet.of(Range.closed(lower, upper));
        RangeSet<Calendar> result = mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type);
        Range<Calendar> range = result.span();
        assertEquals(1L, range.lowerEndpoint().getTimeInMillis());
        assertEquals(5L, range.upperEndpoint().getTimeInMillis());
    }

    @Test
    public void emptySetDoesNotRequireEndpointSubtypePermission() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(BasicPolymorphicTypeValidator.builder()
                        .denyForExactBaseType(BeanEndpoint.class)
                        .allowIfSubType("com.google.common.collect.").build(), DefaultTyping.NON_FINAL).build();
        TypeReference<RangeSet<BeanEndpoint>> type = new TypeReference<>() { };
        RangeSet<BeanEndpoint> input = ImmutableRangeSet.of();
        assertEquals(input, mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type));
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
