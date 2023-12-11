package tools.jackson.datatype.guava.failing;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.guava.ModuleTestBase;
import tools.jackson.datatype.guava.deser.util.RangeFactory;
import com.google.common.collect.Range;

import java.time.Duration;
import java.time.LocalDate;

// Test for [dataformats-collections#118]
public class RangeStringFormatTest extends ModuleTestBase
{
    // [dataformats-collections#118]
    static class Stringified<T extends Comparable<?>>
    {
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Range<T> r;

        public Stringified(Range<T> r) {
            this.r = r;
        }

        public Range<T> toRange() {
            return r;
        }
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    // [dataformats-collections#118]
    public void testRangeSerializationToString() throws Exception
    {
        // Test open range serialization
        Range<Integer> openRange = RangeFactory.open(1, 10);
        assertEquals("{\"r\":\"(1..10)\"}",
                MAPPER.writeValueAsString(new Stringified<>(openRange)));

        // Test closed range serialization
        Range<Integer> closedRange = RangeFactory.closed(5, 15);
        assertEquals("{\"r\":\"[5..15]\"}",
                MAPPER.writeValueAsString(new Stringified<>(closedRange)));

        // Test open-closed range serialization
        Range<Integer> openClosedRange = RangeFactory.openClosed(10, 20);
        assertEquals("{\"r\":\"(10..20]\"}",
                MAPPER.writeValueAsString(new Stringified<>(openClosedRange)));

        // Test closed-open range serialization
        Range<Integer> closedOpenRange = RangeFactory.closedOpen(25, 30);
        assertEquals("{\"r\":\"[25..30)\"}",
                MAPPER.writeValueAsString(new Stringified<>(closedOpenRange)));

        // Test single point range serialization
        Range<Integer> singletonRange = RangeFactory.singleton(42);
        assertEquals("{\"r\":\"[42..42]\"}",
                MAPPER.writeValueAsString(new Stringified<>(singletonRange)));

        // Test unbounded range serialization
        Range<Integer> unboundedRange = RangeFactory.all();
        assertEquals("{\"r\":\"(-∞..+∞)\"}",
                MAPPER.writeValueAsString(new Stringified<>(unboundedRange)));

        // Test open range serialization with Duration
        Range<Duration> openDurationRange = RangeFactory.open(Duration.ofMinutes(30), Duration.ofMinutes(60));
        assertEquals("{\"r\":\"(PT30M..PT1H)\"}",
                MAPPER.writeValueAsString(new Stringified<>(openDurationRange)));

        // Test closed range serialization with LocalDate
        Range<LocalDate> closedDateRange = RangeFactory.closed(LocalDate.parse("2023-01-01"), LocalDate.parse("2023-01-31"));
        assertEquals("{\"r\":\"[2023-01-01..2023-01-31]\"}",
                MAPPER.writeValueAsString(new Stringified<>(closedDateRange)));

        // Test open-closed range serialization with String
        Range<String> openClosedStringRange = RangeFactory.openClosed("abc", "def");
        assertEquals("{\"r\":\"(abc..def]\"}",
                MAPPER.writeValueAsString(new Stringified<>(openClosedStringRange)));
    }
}
