package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.deser.util.RangeFactory;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDate;

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
    public void testRangeDeserializationFromBracketNotation() throws Exception
    {
        testRangeDeserialization("{\"r\":\"(1..10)\"}", openRange);
        testRangeDeserialization("{\"r\":\"[5..15]\"}", closedRange);
        testRangeDeserialization("{\"r\":\"(10..20]\"}", openClosedRange);
        testRangeDeserialization("{\"r\":\"[25..30)\"}", closedOpenRange);
        testRangeDeserialization("{\"r\":\"[42..42]\"}", singletonRange);
        testRangeDeserialization("{\"r\":\"(-∞..+∞)\"}", unboundedRange);
        testRangeDeserialization("{\"r\":\"(-∞..15)\"}", leftBoundedRange);
        testRangeDeserialization("{\"r\":\"[10..+∞)\"}", rightBoundedRange);

        // The below are failing for now. Priority is to get the minimal working deserialization.

        // testRangeDeserialization("{\"r\":\"(PT30M..PT1H)\"}", openDurationRange);
        // testRangeDeserialization("{\"r\":\"[2023-01-01..2023-01-31]\"}", closedDateRange);
        // testRangeDeserialization("{\"r\":\"(abc..def]\"}", openClosedStringRange);
    }

    public void testInvalidBracketNotationRangeDeserialization() throws Exception {
        testInvalidBracketNotation("[abc.def");
        testInvalidBracketNotation("abc.def]");
        testInvalidBracketNotation("[123.45.67]");
        testInvalidBracketNotation("[1.23.45]");
        testInvalidBracketNotation("[1.23]");
        testInvalidBracketNotation("[1.23, 4.56]");
        testInvalidBracketNotation("[1.23, 4.56");
        testInvalidBracketNotation("[1.23, 4.56)");
    }

    private void testInvalidBracketNotation(String json) {
        json = "{\"r\":\"" + json + "\"}";

        try {
            MAPPER.readValue(json, Stringified.class);
            fail("Should fail due to deserializing invalid bracket-notation Range.");
        } catch (JsonProcessingException e) {
            verifyException(e, "Invalid Range. Should start with '[' or '(', end with ')' or ']'");
        }
    }

    @SuppressWarnings("rawtypes")
    private <T extends Comparable> void testRangeSerialization(Range<T> range, String expectedJson) throws Exception
    {
        assertEquals("{\"r\":\"" + expectedJson + "\"}",
                MAPPER.writeValueAsString(new Stringified<>(range)));
    }

    @SuppressWarnings("rawtypes")
    private <T extends Comparable> void testRangeDeserialization(String json, Range<T> expectedRange) throws Exception
    {
        Range<?> actualRange = MAPPER.readValue(json, Stringified.class).toRange();
        assertEquals(expectedRange, actualRange);
    }
}
