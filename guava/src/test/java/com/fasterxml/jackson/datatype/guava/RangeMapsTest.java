package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    }

    @Test
    public void testRangeMapWithEnumValue() throws Exception {
        final TypeReference<TreeRangeMap<Integer, MyEnum>> type = new TypeReference<TreeRangeMap<Integer, MyEnum>>() {};
        final RangeMap<Integer, MyEnum> map = TreeRangeMap.create();

        map.put(Range.range(0, BoundType.OPEN, 10, BoundType.CLOSED), MyEnum.BOO);
        map.put(Range.range(10, BoundType.OPEN, 20, BoundType.CLOSED), MyEnum.YAY);

        final String serializedForm = MAPPER.writerFor(type).writeValueAsString(map);

        assertEquals(serializedForm, MAPPER.writeValueAsString(map));
    }

    @Test
    public void testEmptyMapExclusion() throws Exception {
        String json = MAPPER.writeValueAsString(new RangeMapWrapper());
        assertEquals("{}", json);
    }

    @Test
    public void testRangeMapWithIgnores() throws IOException {
        assertEquals("{\"map\":{\"(0..10]\":\"A\",\"(10..20]\":\"B\",\"(40..50]\":\"E\"}}",
                MAPPER.writeValueAsString(new RangeMapWithIgnores()));
    }

    @Test
    public void testRangeMapWithFilters() throws IOException {
        FilterProvider filters = new SimpleFilterProvider() .addFilter(
                "myFilter", SimpleBeanPropertyFilter.filterOutAllExcept("(10..20]", "(40..50]"));

        assertEquals("{\"map\":{\"(10..20]\":\"B\",\"(40..50]\":\"E\"}}",
                MAPPER.writer(filters).writeValueAsString(new RangeMapWithFilter()));
    }

}
