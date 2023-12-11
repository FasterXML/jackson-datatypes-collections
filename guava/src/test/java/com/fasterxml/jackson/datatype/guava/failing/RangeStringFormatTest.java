package com.fasterxml.jackson.datatype.guava.failing;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;
import com.fasterxml.jackson.datatype.guava.deser.util.RangeFactory;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDate;

public class RangeStringFormatTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    static class Stringified<T extends Comparable<?>>
    {

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Range<T> r;

        public Stringified() {
        }

        public Stringified(Range<T> r) {
            this.r = r;
        }

        public Range<T> toRange() {
            return r;
        }
    }

    public void testRangeSerializationToString() throws Exception
    {
        // Test open range serialization
        Range<Integer> openRange = RangeFactory.open(1, 10);
        assertEquals(MAPPER.writeValueAsString(new Stringified<>(openRange)), "{\"r\":\"(1..10)\"}");

        // Test closed range serialization
        Range<Integer> closedRange = RangeFactory.closed(5, 15);
        assertEquals(MAPPER.writeValueAsString(new Stringified<>(closedRange)), "{\"r\":\"[5..15]\"}");

        // Test open-closed range serialization
        Range<Integer> openClosedRange = RangeFactory.openClosed(10, 20);
        assertEquals(MAPPER.writeValueAsString(new Stringified<>(openClosedRange)), "{\"r\":\"(10..20]\"}");

        // Test closed-open range serialization
        Range<Integer> closedOpenRange = RangeFactory.closedOpen(25, 30);
        assertEquals(MAPPER.writeValueAsString(new Stringified<>(closedOpenRange)), "{\"r\":\"[25..30)\"}");

        // Test single point range serialization
        Range<Integer> singletonRange = RangeFactory.singleton(42);
        assertEquals(MAPPER.writeValueAsString(new Stringified<>(singletonRange)), "{\"r\":\"[42..42]\"}");

        // Test unbounded range serialization
        Range<Integer> unboundedRange = RangeFactory.all();
        assertEquals(MAPPER.writeValueAsString(new Stringified<>(unboundedRange)), "{\"r\":\"(-∞..+∞)\"}");

        // Test open range serialization with Duration
        Range<Duration> openDurationRange = RangeFactory.open(Duration.ofMinutes(30), Duration.ofMinutes(60));
        assertEquals(MAPPER.writeValueAsString(new Stringified<>(openDurationRange)), "{\"r\":\"(PT30M..PT1H)\"}");

        // Test closed range serialization with LocalDate
        Range<LocalDate> closedDateRange = RangeFactory.closed(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-31"));
        assertEquals(MAPPER.writeValueAsString(new Stringified<>(closedDateRange)), "{\"r\":\"[2023-01-01..2023-01-31]\"}");

        // Test open-closed range serialization with String
        Range<String> openClosedStringRange = RangeFactory.openClosed("abc", "def");
        assertEquals(MAPPER.writeValueAsString(new Stringified<>(openClosedStringRange)), "{\"r\":\"(abc..def]\"}");
    }
}