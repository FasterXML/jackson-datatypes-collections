package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to verify handling of various {@link RangeMap}s.
 *
 * @author mcvayc
 */
public class RangeMapsTest extends ModuleTestBase {
    public enum MyEnum {
        YAY,
        BOO
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class RangeMapWrapper {
        @JsonProperty
        RangeMap<String, String> map = TreeRangeMap.create();
    }

    static class RangeMapWithFilter {
        @JsonProperty
        @JsonFilter("myFilter")
        RangeMap<Integer, String> map = TreeRangeMap.create();

        public RangeMapWithFilter() {
            map.put(Range.range(0, BoundType.OPEN, 10, BoundType.CLOSED), "A");
            map.put(Range.range(10, BoundType.OPEN, 20, BoundType.CLOSED), "B");
            map.put(Range.range(20, BoundType.OPEN, 30, BoundType.CLOSED), "C");
            map.put(Range.range(30, BoundType.OPEN, 40, BoundType.CLOSED), "D");
            map.put(Range.range(40, BoundType.OPEN, 50, BoundType.CLOSED), "E");
        }
    }

    static class RangeMapWithIgnores {
        @JsonIgnoreProperties({"(20..30]", "(30..40]"})
        public RangeMap<Integer, String> map = TreeRangeMap.create();

        public RangeMapWithIgnores() {
            map.put(Range.range(0, BoundType.OPEN, 10, BoundType.CLOSED), "A");
            map.put(Range.range(10, BoundType.OPEN, 20, BoundType.CLOSED), "B");
            map.put(Range.range(20, BoundType.OPEN, 30, BoundType.CLOSED), "C");
            map.put(Range.range(30, BoundType.OPEN, 40, BoundType.CLOSED), "D");
            map.put(Range.range(40, BoundType.OPEN, 50, BoundType.CLOSED), "E");
        }
    }

    public static class ImmutableRangeMapWrapper {

        private ImmutableRangeMap<Integer, String> rangeMap;

        public ImmutableRangeMapWrapper() {
        }

        public ImmutableRangeMapWrapper(ImmutableRangeMap<Integer, String> f) {
            this.rangeMap = f;
        }

        public ImmutableRangeMap<Integer, String> getRangeMap() {
            return rangeMap;
        }

        public void setRangeMap(ImmutableRangeMap<Integer, String> rangeMap) {
            this.rangeMap = rangeMap;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.rangeMap != null ? this.rangeMap.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ImmutableRangeMapWrapper other = (ImmutableRangeMapWrapper) obj;
            return !(this.rangeMap != other.rangeMap && (this.rangeMap == null || !this.rangeMap.equals(other.rangeMap)));
        }
    }

    //Sample class for testing rangemaps single value option
    static class SampleRangeMapTest {
        public TreeRangeMap<Integer, String> map;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testRangeMap() throws Exception {
        _testRangeMap(TreeRangeMap.create(), true,
                "{\"(0..10]\":\"A\",\"(10..20]\":\"B\",\"(20..30]\":\"C\",\"(30..40]\":\"D\",\"(40..50]\":\"E\"}");
        _testRangeMap(TreeRangeMap.create(), false,
                "{\"(0..10]\":\"A\",\"(10..20]\":\"B\",\"(20..30]\":\"C\",\"(30..40]\":\"D\",\"(40..50]\":\"E\"}");
        _testRangeMap(TreeRangeMap.create(), false, null);
    }

    private void _testRangeMap(RangeMap<?, ?> map0, boolean fullyOrdered, String EXPECTED) throws Exception {
        @SuppressWarnings("unchecked")
        RangeMap<Integer, String> map = (RangeMap<Integer, String>) map0;
        map.put(Range.range(0, BoundType.OPEN, 10, BoundType.CLOSED), "A");
        map.put(Range.range(10, BoundType.OPEN, 20, BoundType.CLOSED), "B");
        map.put(Range.range(20, BoundType.OPEN, 30, BoundType.CLOSED), "C");
        map.put(Range.range(30, BoundType.OPEN, 40, BoundType.CLOSED), "D");
        map.put(Range.range(40, BoundType.OPEN, 50, BoundType.CLOSED), "E");

        // Test that typed writes work
        if (EXPECTED != null) {
            String json = MAPPER.writerFor(new TypeReference<RangeMap<Integer, String>>() {}).writeValueAsString(map);
            assertEquals(EXPECTED, json);
        }

        // And untyped too
        String serializedForm = MAPPER.writeValueAsString(map);

        if (EXPECTED != null) {
            assertEquals(EXPECTED, serializedForm);
        }

        // these seem to be order-sensitive as well, so only use for ordered-maps
        if (fullyOrdered) {
            assertEquals(map, MAPPER.readValue(serializedForm, new TypeReference<RangeMap<Integer, String>>() {
            }));
            assertEquals(map, MAPPER.readValue(serializedForm, new TypeReference<TreeRangeMap<Integer, String>>() {
            }));
            assertEquals(map, MAPPER.readValue(serializedForm, new TypeReference<ImmutableRangeMap<Integer, String>>() {
            }));
        }
    }

    @Test
    public void testRangeMapCompatibilityWithMap() throws Exception {
        RangeMap<Integer, String> m1 = TreeRangeMap.create();
        m1.put(Range.range(0, BoundType.OPEN, 10, BoundType.CLOSED), "A");
        m1.put(Range.range(10, BoundType.OPEN, 20, BoundType.CLOSED), "B");
        m1.put(Range.range(20, BoundType.OPEN, 30, BoundType.CLOSED), "C");

        ObjectMapper o = MAPPER;

        String t1 = o.writerFor(new TypeReference<TreeRangeMap<String, String>>() {
        }).writeValueAsString(m1);
        Map<?, ?> javaMap = o.readValue(t1, Map.class);
        assertEquals(3, javaMap.size());

        String t2 = o.writerFor(new TypeReference<RangeMap<String, String>>() {
        }).writeValueAsString(m1);
        javaMap = o.readValue(t2, Map.class);
        assertEquals(3, javaMap.size());

        TreeRangeMap<Integer, String> m2 = TreeRangeMap.create();
        m2.put(Range.range(0, BoundType.OPEN, 10, BoundType.CLOSED), "A");
        m2.put(Range.range(10, BoundType.OPEN, 20, BoundType.CLOSED), "B");
        m2.put(Range.range(20, BoundType.OPEN, 30, BoundType.CLOSED), "C");

        String t3 = o.writerFor(new TypeReference<TreeRangeMap<String, String>>() {
        }).writeValueAsString(m2);
        javaMap = o.readValue(t3, Map.class);
        assertEquals(3, javaMap.size());

        String t4 = o.writerFor(new TypeReference<RangeMap<String, String>>() {
        }).writeValueAsString(m2);
        javaMap = o.readValue(t4, Map.class);
        assertEquals(3, javaMap.size());
    }

    @Test
    public void testRangeMapWithEnumValue() throws Exception {
        final TypeReference<TreeRangeMap<Integer, MyEnum>> type = new TypeReference<TreeRangeMap<Integer, MyEnum>>() {};
        final RangeMap<Integer, MyEnum> map = TreeRangeMap.create();

        map.put(Range.range(0, BoundType.OPEN, 10, BoundType.CLOSED), MyEnum.BOO);
        map.put(Range.range(10, BoundType.OPEN, 20, BoundType.CLOSED), MyEnum.YAY);

        final String serializedForm = MAPPER.writerFor(type).writeValueAsString(map);

        assertEquals(serializedForm, MAPPER.writeValueAsString(map));
        assertEquals(map, MAPPER.readValue(serializedForm, type));
    }

    @Test
    public void testEmptyMapExclusion() throws Exception {
        String json = MAPPER.writeValueAsString(new RangeMapWrapper());
        assertEquals("{}", json);
    }

    @Test
    public void testWithReferenceType() throws Exception {
        String json = "{\"(0..10]\":5.0,\"(10..20]\":15.0,\"(20..30]\":25.0,\"(30..40]\":35.0,\"(40..50]\":45.0}";
        TreeRangeMap<Integer, Optional<Double>> result = MAPPER.readValue(
                json,
                new TypeReference<TreeRangeMap<Integer, Optional<Double>>>() {
                });

        assertEquals(5, result.asMapOfRanges().size());
        assertEquals(
                ImmutableRangeMap.builder()
                        .put(Range.range(0, BoundType.OPEN, 10, BoundType.CLOSED), Optional.of(5.0))
                        .put(Range.range(10, BoundType.OPEN, 20, BoundType.CLOSED), Optional.of(15.0))
                        .put(Range.range(20, BoundType.OPEN, 30, BoundType.CLOSED), Optional.of(25.0))
                        .put(Range.range(30, BoundType.OPEN, 40, BoundType.CLOSED), Optional.of(35.0))
                        .put(Range.range(40, BoundType.OPEN, 50, BoundType.CLOSED), Optional.of(45.0))
                        .build(),
                result);
    }

    @Test
    public void testImmutableRangeMap() throws Exception {
        RangeMap<String, String> map =
                _verifyRangeMapRead(new TypeReference<ImmutableRangeMap<String, String>>() {
                });
        assertTrue(map instanceof ImmutableRangeMap);
    }

    @Test
    public void testTreeRangeMap() throws Exception {
        RangeMap<String, String> map =
                _verifyRangeMapRead(new TypeReference<TreeRangeMap<String, String>>() {
                });
        assertTrue(map instanceof TreeRangeMap);
    }

    private RangeMap<String, String> _verifyRangeMapRead(TypeReference<?> type)
            throws Exception {
        RangeMap<String, String> map = (RangeMap<String, String>) MAPPER
                .readValue("{\"(a..c]\":\"b\",\"(d..f]\":\"e\",\"(g..i]\":\"h\"}", type);
        assertEquals(3, map.asMapOfRanges().size());
        assertTrue(map.asMapOfRanges().containsKey(Range.openClosed("a", "c")));
        assertTrue(map.asMapOfRanges().containsKey(Range.openClosed("d", "f")));
        assertTrue(map.asMapOfRanges().containsKey(Range.openClosed("g", "i")));
        return map;
    }

    @Test
    public void testRangeMapWithIgnores() throws Exception {
        assertEquals("{\"map\":{\"(0..10]\":\"A\",\"(10..20]\":\"B\",\"(40..50]\":\"E\"}}",
                MAPPER.writeValueAsString(new RangeMapWithIgnores()));
    }

    @Test
    public void testRangeMapWithFilters() throws Exception {
        FilterProvider filters = new SimpleFilterProvider() .addFilter(
                "myFilter", SimpleBeanPropertyFilter.filterOutAllExcept("(10..20]", "(40..50]"));

        assertEquals("{\"map\":{\"(10..20]\":\"B\",\"(40..50]\":\"E\"}}",
                MAPPER.writer(filters).writeValueAsString(new RangeMapWithFilter()));
    }

    @Test
    public void testRangeMapDeserializationWithEmptyStringKey() throws Exception {
        ValueInstantiationException exception = assertThrows(ValueInstantiationException.class,() ->
            MAPPER.readValue("{\"\":\"B\",\"(40..50]\":\"E\"}", new TypeReference<ImmutableRangeMap<String, String>>() {})
        );

        assertTrue(exception.getMessage().contains("RangeMap keys can't be null or empty."));
    }

    @Test
    public void testPolymorphicValue() throws Exception {
        ImmutableRangeMapWrapper input = new ImmutableRangeMapWrapper(ImmutableRangeMap.of(Range.range(0, BoundType.OPEN, 10, BoundType.CLOSED), "A"));

        String json = MAPPER.writeValueAsString(input);

        ImmutableRangeMapWrapper output = MAPPER.readValue(json, ImmutableRangeMapWrapper.class);
        assertEquals(input, output);
    }
}
