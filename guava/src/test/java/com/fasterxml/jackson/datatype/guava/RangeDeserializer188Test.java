package com.fasterxml.jackson.datatype.guava;

import java.time.Duration;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.guava.deser.util.RangeFactory;

import com.google.common.collect.Range;

// Test for [dataformats-collections#118]
public class RangeDeserializer188Test extends ModuleTestBase
{
    // [dataformats-collections#118]
    static class Stringified<T extends Comparable<?>>
    {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Range<T> r;

        // Default constructor for Jackson deserialization
        public Stringified() { }

        public Stringified(Range<T> r) {
            this.r = r;
        }

        public Range<T> toRange() {
            return r;
        }
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    /*
     * Constant Ranges for Test Cases
     */
    private static final Range<Integer> openRange = RangeFactory.open(1, 10);
    private static final Range<Integer> closedRange = RangeFactory.closed(5, 15);
    private static final Range<Integer> openClosedRange = RangeFactory.openClosed(10, 20);
    private static final Range<Integer> closedOpenRange = RangeFactory.closedOpen(25, 30);
    private static final Range<Integer> singletonRange = RangeFactory.singleton(42);
    private static final Range<Integer> unboundedRange = RangeFactory.all();
    private static final Range<Duration> openDurationRange = RangeFactory.open(Duration.ofMinutes(30), Duration.ofMinutes(60));
    private static final Range<LocalDate> closedDateRange = RangeFactory.closed(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-31"));
    private static final Range<String> openClosedStringRange = RangeFactory.openClosed("abc", "def");
    private static final Range<Integer> leftBoundedRange = RangeFactory.lessThan(15);
    private static final Range<Integer> rightBoundedRange = RangeFactory.atLeast(10);

    // [dataformats-collections#118]
    public void testRangeSerializationToString() throws Exception
    {
        testRangeSerialization(openRange, "(1..10)");
        testRangeSerialization(closedRange, "[5..15]");
        testRangeSerialization(openClosedRange, "(10..20]");
        testRangeSerialization(closedOpenRange, "[25..30)");
        testRangeSerialization(singletonRange, "[42..42]");
        testRangeSerialization(unboundedRange, "(-∞..+∞)");
        testRangeSerialization(openDurationRange, "(PT30M..PT1H)");
        testRangeSerialization(closedDateRange, "[2023-01-01..2023-01-31]");
        testRangeSerialization(openClosedStringRange, "(abc..def]");
        testRangeSerialization(leftBoundedRange, "(-∞..15)");
        testRangeSerialization(rightBoundedRange, "[10..+∞)");
    }

    // [dataformats-collections#135]
    public void testIntRangeDeserializationFromBracketNotation() throws Exception
    {
        testIntRangeDeserialization("{\"r\":\"(1..10)\"}", openRange);
        testIntRangeDeserialization("{\"r\":\"[5..15]\"}", closedRange);
        testIntRangeDeserialization("{\"r\":\"(10..20]\"}", openClosedRange);
        testIntRangeDeserialization("{\"r\":\"[25..30)\"}", closedOpenRange);
        testIntRangeDeserialization("{\"r\":\"[42..42]\"}", singletonRange);
        testIntRangeDeserialization("{\"r\":\"(-∞..+∞)\"}", unboundedRange);
        testIntRangeDeserialization("{\"r\":\"(-∞..15)\"}", leftBoundedRange);
        testIntRangeDeserialization("{\"r\":\"[10..+∞)\"}", rightBoundedRange);
    }

    public void testStringRangeDeserializationFromBracketNotation() throws Exception
    {
        _testStringifiedRangeDeserialization("{\"r\":\"(abc..def]\"}",
                openClosedStringRange, String.class);
    }

    // Cannot implement here since `Duration` KeyDeserializer provided by Java 8 date/time module
    public void testDurationRangeDeserializationFromBracketNotation() throws Exception
    {
        // Would work if we had `KeyDeserializer` for Duration
        /*
        _testStringifiedRangeDeserialization("{\"r\":\"(PT30M..PT1H)\"}",
                openDurationRange, Duration.class);
                */
    }
                
    // Cannot implement here since `LocalDate` KeyDeserializer provided by Java 8 date/time module
    public void testLocalDateRangeDeserializationFromBracketNotation() throws Exception
    {
        /*
        _testStringifiedRangeDeserialization("{\"r\":\"[2023-01-01..2023-01-31]\"}",
                closedDateRange, LocalDate.class);
                */
    }

    public void testInvalidBracketNotationRangeDeserialization() throws Exception {
        // Fails due to open/close markers
        testInvalidStringifiedDeserialization("[1..2", RangeError.INVALID_BRACKET);
        testInvalidStringifiedDeserialization("1..2]", RangeError.INVALID_BRACKET);
        testInvalidStringifiedDeserialization("(123, 456", RangeError.INVALID_BRACKET);

        // Fails due to bad separator
        testInvalidStringifiedDeserialization("[123.45.67]", RangeError.GENERIC_INVALID);
        testInvalidStringifiedDeserialization("[1.23.45]", RangeError.GENERIC_INVALID);
        testInvalidStringifiedDeserialization("[1.23]", RangeError.GENERIC_INVALID);
        testInvalidStringifiedDeserialization("[1.23, 4.56]", RangeError.GENERIC_INVALID);
        testInvalidStringifiedDeserialization("[1.23, 4.56)", RangeError.GENERIC_INVALID);
    }

    private void testInvalidStringifiedDeserialization(String json, RangeError error) throws Exception {
        json = "{\"r\":\"" + json + "\"}";

        try {
            MAPPER.readValue(json,
                    new TypeReference<Stringified<Integer>>() {});
            fail("Should fail due to deserializing invalid bracket-notation Range.");
        } catch (InvalidFormatException e) {
            verifyException(e, error.getErrorMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    private <T extends Comparable> void testRangeSerialization(Range<T> range, String expectedJson) throws Exception
    {
        assertEquals("{\"r\":\"" + expectedJson + "\"}",
                MAPPER.writeValueAsString(new Stringified<>(range)));
    }

    private void testIntRangeDeserialization(String json,
            Range<?> expectedRange) throws Exception
    {
        _testStringifiedRangeDeserialization(json, expectedRange, Integer.class);
    }

    private void _testStringifiedRangeDeserialization(String json,
            Range<?> expectedRange,
            Class<?> endpointType) throws Exception
    {
        JavaType type = MAPPER.getTypeFactory()
                .constructParametricType(Stringified.class, endpointType);
        // Deserialize as Integer
        Stringified<?> stringified = MAPPER.readValue(json, type);
        assertEquals(expectedRange, stringified.toRange());
    }

    public enum RangeError {
        INVALID_BRACKET("Invalid Range: should start with '[' or '(', end with ')' or ']"),
        GENERIC_INVALID("Invalid bracket-notation representation");

        private final String errorMessage;

        RangeError(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
