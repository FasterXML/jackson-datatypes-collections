package com.fasterxml.jackson.datatype.guava;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import com.fasterxml.jackson.datatype.guava.deser.util.RangeFactory;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

/**
 * Unit tests to verify serialization of Guava {@link Range}s.
 */
public class TestRange extends ModuleTestBase {

    private final ObjectMapper MAPPER = mapperWithModule();

    protected static class UntypedWrapper
    {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object range;

        public UntypedWrapper() { }
        public UntypedWrapper(Range<?> r) { range = r; }
    }

    static class Wrapped {
        public Range<Integer> r;

        public Wrapped() { }
        public Wrapped(Range<Integer> r) { this.r = r; }
    }
    
    /**
     * This test is present so that we know if either Jackson's handling of Range
     * or Guava's implementation of Range changes.
     * @throws Exception
     */
    public void testSerializationWithoutModule() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Range<Integer> range = RangeFactory.closed(1, 10);
        String json = mapper.writeValueAsString(range);
        assertEquals("{\"empty\":false}", json);
    }

    public void testSerialization() throws Exception
    {
        testSerialization(MAPPER, RangeFactory.open(1, 10));
        testSerialization(MAPPER, RangeFactory.openClosed(1, 10));
        testSerialization(MAPPER, RangeFactory.closedOpen(1, 10));
        testSerialization(MAPPER, RangeFactory.closed(1, 10));
        testSerialization(MAPPER, RangeFactory.atLeast(1));
        testSerialization(MAPPER, RangeFactory.greaterThan(1));
        testSerialization(MAPPER, RangeFactory.atMost(10));
        testSerialization(MAPPER, RangeFactory.lessThan(10));
        testSerialization(MAPPER, RangeFactory.all());
        testSerialization(MAPPER, RangeFactory.singleton(1));
    }

    public void testSerializationWithPropertyNamingStrategy() throws Exception
    {
        ObjectMapper mappper = builderWithModule().propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).build();
        testSerialization(mappper, RangeFactory.open(1, 10));
        testSerialization(mappper, RangeFactory.openClosed(1, 10));
        testSerialization(mappper, RangeFactory.closedOpen(1, 10));
        testSerialization(mappper, RangeFactory.closed(1, 10));
        testSerialization(mappper, RangeFactory.atLeast(1));
        testSerialization(mappper, RangeFactory.greaterThan(1));
        testSerialization(mappper, RangeFactory.atMost(10));
        testSerialization(mappper, RangeFactory.lessThan(10));
        testSerialization(mappper, RangeFactory.all());
        testSerialization(mappper, RangeFactory.singleton(1));
    }

    public void testWrappedSerialization() throws Exception
    {
        testSerializationWrapped(MAPPER, RangeFactory.open(1, 10));
        testSerializationWrapped(MAPPER, RangeFactory.openClosed(1, 10));
        testSerializationWrapped(MAPPER, RangeFactory.closedOpen(1, 10));
        testSerializationWrapped(MAPPER, RangeFactory.closed(1, 10));
        testSerializationWrapped(MAPPER, RangeFactory.atLeast(1));
        testSerializationWrapped(MAPPER, RangeFactory.greaterThan(1));
        testSerializationWrapped(MAPPER, RangeFactory.atMost(10));
        testSerializationWrapped(MAPPER, RangeFactory.lessThan(10));
        testSerializationWrapped(MAPPER, RangeFactory.singleton(1));
    }
    
    public void testDeserialization() throws Exception
    {
        String json = MAPPER.writeValueAsString(RangeFactory.open(1, 10));
        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) MAPPER.readValue(json, Range.class);
        assertNotNull(r);
        assertEquals(Integer.valueOf(1), r.lowerEndpoint());
        assertEquals(Integer.valueOf(10), r.upperEndpoint());
    }
    
    private void testSerialization(ObjectMapper objectMapper, Range<?> range) throws IOException
    {
        String json = objectMapper.writeValueAsString(range);
        Range<?> rangeClone = objectMapper.readValue(json, Range.class);
        assertEquals(rangeClone, range);
    }

    private void testSerializationWrapped(ObjectMapper objectMapper, Range<Integer> range) throws IOException
    {
        String json = objectMapper.writeValueAsString(new Wrapped(range));
        Wrapped result = objectMapper.readValue(json, Wrapped.class);
        assertEquals(range, result.r);
    }
    
    public void testUntyped() throws Exception
    {
        // Default settings do not allow possibly unsafe base type
        final ObjectMapper polyMapper = builderWithModule()
                .polymorphicTypeValidator(BasicPolymorphicTypeValidator
                        .builder()
                        .allowIfBaseType(Object.class)
                        .build()
                ).build();

        String json = polyMapper.writerWithDefaultPrettyPrinter().writeValueAsString(new UntypedWrapper(RangeFactory.open(1, 10)));
        UntypedWrapper out = polyMapper.readValue(json, UntypedWrapper.class);
        assertNotNull(out);
        assertEquals(Range.class, out.range.getClass());
    }

    public void testDefaultBoundTypeNoBoundTypeInformed() throws Exception
    {
        String json = "{\"lowerEndpoint\": 2, \"upperEndpoint\": 3}";

        try {
            MAPPER.readValue(json, Range.class);
            fail("Should have failed");
        } catch (MismatchedInputException e) {
            verifyException(e, "'lowerEndpoint' field found, but not 'lowerBoundType'");
        }
    }

    public void testDefaultBoundTypeNoBoundTypeInformedWithClosedConfigured() throws Exception
    {
        String json = "{\"lowerEndpoint\": 2, \"upperEndpoint\": 3}";

        GuavaModule mod = new GuavaModule().defaultBoundType(BoundType.CLOSED);
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(mod)
                .build();

        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) mapper.readValue(json, Range.class);

        assertEquals(Integer.valueOf(2), r.lowerEndpoint());
        assertEquals(Integer.valueOf(3), r.upperEndpoint());
        assertEquals(BoundType.CLOSED, r.lowerBoundType());
        assertEquals(BoundType.CLOSED, r.upperBoundType());
    }

    public void testDefaultBoundTypeOnlyLowerBoundTypeInformed() throws Exception
    {
        String json = "{\"lowerEndpoint\": 2, \"lowerBoundType\": \"OPEN\", \"upperEndpoint\": 3}";

        try {
            MAPPER.readValue(json, Range.class);
            fail("Should have failed");
        } catch (MismatchedInputException e) {
            verifyException(e, "'upperEndpoint' field found, but not 'upperBoundType'");
        }
    }

    public void testDefaultBoundTypeOnlyLowerBoundTypeInformedWithClosedConfigured() throws Exception
    {
        String json = "{\"lowerEndpoint\": 2, \"lowerBoundType\": \"OPEN\", \"upperEndpoint\": 3}";

        GuavaModule mod = new GuavaModule().defaultBoundType(BoundType.CLOSED);
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(mod)
                .build();
        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) mapper.readValue(json, Range.class);

        assertEquals(Integer.valueOf(2), r.lowerEndpoint());
        assertEquals(Integer.valueOf(3), r.upperEndpoint());
        assertEquals(BoundType.OPEN, r.lowerBoundType());
        assertEquals(BoundType.CLOSED, r.upperBoundType());
    }

    public void testDefaultBoundTypeOnlyUpperBoundTypeInformed() throws Exception
    {
        String json = "{\"lowerEndpoint\": 2, \"upperEndpoint\": 3, \"upperBoundType\": \"OPEN\"}";

        try {
            MAPPER.readValue(json, Range.class);
            fail("Should have failed");
        } catch (MismatchedInputException e) {
            verifyException(e, "'lowerEndpoint' field found, but not 'lowerBoundType'");
        }
    }

    public void testDefaultBoundTypeOnlyUpperBoundTypeInformedWithClosedConfigured() throws Exception
    {
        String json = "{\"lowerEndpoint\": 1, \"upperEndpoint\": 3, \"upperBoundType\": \"OPEN\"}";

        GuavaModule mod = new GuavaModule().defaultBoundType(BoundType.CLOSED);
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(mod)
                .build();
        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) mapper.readValue(json, Range.class);

        assertEquals(Integer.valueOf(1), r.lowerEndpoint());
        assertEquals(Integer.valueOf(3), r.upperEndpoint());
        assertEquals(BoundType.CLOSED, r.lowerBoundType());
        assertEquals(BoundType.OPEN, r.upperBoundType());
    }

    public void testDefaultBoundTypeBothBoundTypesOpen() throws Exception
    {
        String json = "{\"lowerEndpoint\": 2, \"lowerBoundType\": \"OPEN\", \"upperEndpoint\": 3, \"upperBoundType\": \"OPEN\"}";
        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) MAPPER.readValue(json, Range.class);

        assertEquals(Integer.valueOf(2), r.lowerEndpoint());
        assertEquals(Integer.valueOf(3), r.upperEndpoint());

        assertEquals(BoundType.OPEN, r.lowerBoundType());
        assertEquals(BoundType.OPEN, r.upperBoundType());
    }

    public void testDefaultBoundTypeBothBoundTypesOpenWithClosedConfigured() throws Exception
    {
        String json = "{\"lowerEndpoint\": 1, \"lowerBoundType\": \"OPEN\", \"upperEndpoint\": 3, \"upperBoundType\": \"OPEN\"}";

        GuavaModule mod = new GuavaModule().defaultBoundType(BoundType.CLOSED);
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(mod)
                .build();
        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) mapper.readValue(json, Range.class);

        assertEquals(Integer.valueOf(1), r.lowerEndpoint());
        assertEquals(Integer.valueOf(3), r.upperEndpoint());

        assertEquals(BoundType.OPEN, r.lowerBoundType());
        assertEquals(BoundType.OPEN, r.upperBoundType());
    }

    public void testDefaultBoundTypeBothBoundTypesClosed() throws Exception
    {
        String json = "{\"lowerEndpoint\": 1, \"lowerBoundType\": \"CLOSED\", \"upperEndpoint\": 3, \"upperBoundType\": \"CLOSED\"}";
        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) MAPPER.readValue(json, Range.class);

        assertEquals(Integer.valueOf(1), r.lowerEndpoint());
        assertEquals(Integer.valueOf(3), r.upperEndpoint());

        assertEquals(BoundType.CLOSED, r.lowerBoundType());
        assertEquals(BoundType.CLOSED, r.upperBoundType());
    }

    public void testDefaultBoundTypeBothBoundTypesClosedWithOpenConfigured() throws Exception
    {
        String json = "{\"lowerEndpoint\": 12, \"lowerBoundType\": \"CLOSED\", \"upperEndpoint\": 33, \"upperBoundType\": \"CLOSED\"}";

        GuavaModule mod = new GuavaModule().defaultBoundType(BoundType.CLOSED);
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(mod)
                .build();
        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) mapper.readValue(json, Range.class);

        assertEquals(Integer.valueOf(12), r.lowerEndpoint());
        assertEquals(Integer.valueOf(33), r.upperEndpoint());

        assertEquals(BoundType.CLOSED, r.lowerBoundType());
        assertEquals(BoundType.CLOSED, r.upperBoundType());
    }

    public void testSnakeCaseNamingStrategy() throws Exception
    {
        String json = "{\"lower_endpoint\": 12, \"lower_bound_type\": \"CLOSED\", \"upper_endpoint\": 33, \"upper_bound_type\": \"CLOSED\"}";

        GuavaModule mod = new GuavaModule().defaultBoundType(BoundType.CLOSED);
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(mod)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();

        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) mapper.readValue(json, Range.class);

        assertEquals(Integer.valueOf(12), r.lowerEndpoint());
        assertEquals(Integer.valueOf(33), r.upperEndpoint());

        assertEquals(BoundType.CLOSED, r.lowerBoundType());
        assertEquals(BoundType.CLOSED, r.upperBoundType());
    }

    // [datatypes-collections#12]
    public void testRangeWithDefaultTyping() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL)
                .build();
        Range<Integer> input = RangeFactory.closed(1, 10);
        String json = mapper.writeValueAsString(input);

        Range<Integer> result = mapper.readValue(json,
                new TypeReference<Range<Integer>>() { });
        assertNotNull(result);

        assertEquals(Integer.valueOf(1), result.lowerEndpoint());
        assertEquals(Integer.valueOf(10), result.upperEndpoint());

        assertEquals(BoundType.CLOSED, result.lowerBoundType());
        assertEquals(BoundType.CLOSED, result.upperBoundType());
    }
}
