package tools.jackson.datatype.guava;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.datatype.guava.deser.util.RangeFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to verify serialization of Guava {@link Range}s.
 */
public class RangeTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    protected static class UntypedWrapper
    {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object range;

        public UntypedWrapper() { }
        public UntypedWrapper(Range<?> r) { range = r; }
    }

    static class WildcardWrapper {
        public Range<?> r;
    }

    static class StringRangeWrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Range<Integer> r;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    static class TypedRangeMixin { }

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
    @Test
    public void testSerializationWithoutModule() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Range<Integer> range = RangeFactory.closed(1, 10);
        String json = mapper.writeValueAsString(range);
        assertEquals("{\"empty\":false}", json);
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
    public void testDeserialization() throws Exception
    {
        String json = MAPPER.writeValueAsString(RangeFactory.open(1, 10));
        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) MAPPER.readValue(json, Range.class);
        assertNotNull(r);
        assertEquals(Integer.valueOf(1), r.lowerEndpoint());
        assertEquals(Integer.valueOf(10), r.upperEndpoint());
    }
    
    private void testSerialization(ObjectMapper objectMapper, Range<?> range) throws Exception
    {
        String json = objectMapper.writeValueAsString(range);
        Range<?> rangeClone = objectMapper.readValue(json, Range.class);
        assertEquals(rangeClone, range);
    }

    private void testSerializationWrapped(ObjectMapper objectMapper, Range<Integer> range) throws Exception
    {
        String json = objectMapper.writeValueAsString(new Wrapped(range));
        Wrapped result = objectMapper.readValue(json, Wrapped.class);
        assertEquals(range, result.r);
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    // [datatypes-collections#218]: only upperEndpoint, missing upperBoundType
    @Test
    public void testUpperEndpointOnlyMissingBoundType() throws Exception
    {
        String json = "{\"upperEndpoint\": 5}";

        try {
            MAPPER.readValue(json, Range.class);
            fail("Should have failed");
        } catch (MismatchedInputException e) {
            verifyException(e, "'upperEndpoint' field found, but not 'upperBoundType'");
        }
    }

    // [datatypes-collections#218]: only lowerEndpoint, missing lowerBoundType
    @Test
    public void testLowerEndpointOnlyMissingBoundType() throws Exception
    {
        String json = "{\"lowerEndpoint\": 5}";

        try {
            MAPPER.readValue(json, Range.class);
            fail("Should have failed");
        } catch (MismatchedInputException e) {
            verifyException(e, "'lowerEndpoint' field found, but not 'lowerBoundType'");
        }
    }

    @Test
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

    @Test
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

    @Test
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

    @Test
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

    @Test
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
    @Test
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

    // error case from OSS-Fuzz: https://issues.oss-fuzz.com/u/1/issues/469521785?pli=1
    @Test
    public void testWrongOrdering() throws Exception
    {
        String json = a2q("{'lowerEndpoint': 32, 'lowerBoundType': 'OPEN', 'upperEndpoint': 0, 'upperBoundType': 'OPEN'}");

        try {
            MAPPER.readValue(json, Range.class);
            fail("Should have failed");
        } catch (MismatchedInputException e) {
            verifyException(e, "Failed to construct `Range`");
            verifyException(e, "from (32/'open', 0/'open')");
            verifyException(e, "Invalid range: (32..0)");
        }
    }


    // `Range<?>` endpoints are written by their runtime type, read as "natural" types
    @Test
    public void testWildcardRange() throws Exception
    {
        WildcardWrapper input = new WildcardWrapper();
        input.r = RangeFactory.closed(new BigDecimal("1.5"), new BigDecimal("2.5"));
        String json = MAPPER.writeValueAsString(input);
        assertEquals(a2q("{'r':{'lowerEndpoint':1.5,'lowerBoundType':'CLOSED',"
                + "'upperEndpoint':2.5,'upperBoundType':'CLOSED'}}"), json);
        assertEquals(RangeFactory.closed(1.5, 2.5), MAPPER.readValue(json, WildcardWrapper.class).r);

        input.r = RangeFactory.atLeast("abc");
        assertEquals(input.r, MAPPER.readValue(MAPPER.writeValueAsString(input), WildcardWrapper.class).r);

        // Same with default typing: no endpoint type ids
        ObjectMapper typed = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL).build();
        json = typed.writeValueAsString(input);
        assertFalse(json.contains("java.lang.String"), json);
        assertEquals(input.r, typed.readValue(json, WildcardWrapper.class).r);
    }

    // Numeric endpoints read as different "natural" types are widened to a common type
    @Test
    public void testWildcardRangeMixedNumbers() throws Exception
    {
        assertEquals(RangeFactory.closed(1.0, 2.5), _readWildcard("1", "2.5"));
        assertEquals(RangeFactory.closed(1L, 5_000_000_000L), _readWildcard("1", "5000000000"));
        assertEquals(RangeFactory.closed(BigInteger.ONE, new BigInteger("99999999999999999999")),
                _readWildcard("1", "99999999999999999999"));
        ObjectMapper mapper = builderWithModule()
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS).build();
        assertEquals(RangeFactory.closed(BigDecimal.ONE, new BigDecimal("2.5")),
                mapper.readValue(a2q("{'r':{'lowerEndpoint':1,'lowerBoundType':'CLOSED',"
                        + "'upperEndpoint':2.5,'upperBoundType':'CLOSED'}}"), WildcardWrapper.class).r);
    }

    // Non-finite `Double` cannot be widened to `BigDecimal`: must fail cleanly
    @Test
    public void testWildcardRangeNaNWithBigDecimal() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new GuavaModule())
                .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
                .enable(JsonReadFeature.ALLOW_NON_NUMERIC_NUMBERS)
                .build();
        InvalidDefinitionException e = assertThrows(InvalidDefinitionException.class,
                () -> mapper.readValue(a2q("{'r':{'lowerEndpoint':NaN,'lowerBoundType':'CLOSED',"
                        + "'upperEndpoint':1.5,'upperBoundType':'CLOSED'}}"), WildcardWrapper.class));
        verifyException(e, "Endpoint types are not the same");
    }

    // STRING shape must write type id (if any) and the range String
    @Test
    public void testStringShapeWithTypeId() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .addMixIn(Range.class, TypedRangeMixin.class)
                .build();
        StringRangeWrapper input = new StringRangeWrapper();
        input.r = RangeFactory.closed(1, 5);
        String json = mapper.writeValueAsString(input);
        assertEquals(a2q("{'r':['com.google.common.collect.Range','[1..5]']}"), json);
        assertEquals(input.r, mapper.readValue(json, StringRangeWrapper.class).r);
    }

    private Range<?> _readWildcard(String lower, String upper) throws Exception
    {
        return MAPPER.readValue(a2q("{'r':{'lowerEndpoint':" + lower + ",'lowerBoundType':'CLOSED',"
                + "'upperEndpoint':" + upper + ",'upperBoundType':'CLOSED'}}"), WildcardWrapper.class).r;
    }

}
