package com.fasterxml.jackson.datatype.guava.failing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

public class JsonDeserContentConverter92Test extends ModuleTestBase {

    /*
    /**********************************************************
    /* Set up
    /**********************************************************
     */

    static class StandardHolder {
        @JsonDeserialize(contentConverter = DoublingConverter.class)
        public List<Integer> ints;
    }

    static class GuavaHolder {
        @JsonDeserialize(contentConverter = DoublingConverter.class)
        public ImmutableList<Integer> ints;
    }

    static class DoublingConverter extends StdConverter<Integer, Integer> {
        @Override
        public Integer convert(Integer value) {
            return value != null ? value * 2 : null;
        }
    }

    static class StandardWrapper {
        @JsonSerialize(contentConverter = DoublingConverterSer.class)
        public List<Integer> list;

        public StandardWrapper(List<Integer> asList) {
            this.list = asList;
        }
    }

    static class GuavaListWrapper {
        @JsonSerialize(contentConverter = DoublingConverterSer.class)
        public ImmutableList<Integer> list;

        public GuavaListWrapper(ImmutableList<Integer> asList) {
            this.list = asList;
        }
    }

    static class DoublingConverterSer extends StdConverter<Integer, String> {

        @Override
        public String convert(Integer n) {
            return "doubled_" + n * 2;
        }
    }

    /*
    /**********************************************************
    /* Test
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testJsonSerialize() throws Exception {
        String jsonStr = a2q("{'list':['doubled_2','doubled_4']}");

        // serialization passes
        assertEquals(jsonStr, MAPPER.writeValueAsString(new StandardWrapper(Arrays.asList(1, 2))));
        assertEquals(jsonStr, MAPPER.writeValueAsString(new GuavaListWrapper(ImmutableList.of(1, 2))));
    }

    public void testFailingGuavaDoublingConverter() throws Exception {
        String json = "{ \"ints\": [1,2,3] }";

        // passes
        StandardHolder stdHolder = MAPPER.readValue(json, StandardHolder.class);
        containsExactly(stdHolder.ints, 2, 4, 6);

        // fails
        GuavaHolder guavaHolder = MAPPER.readValue(json, GuavaHolder.class);
        containsExactly(guavaHolder.ints, 2, 4, 6);
    }

    private void containsExactly(List<Integer> ints, int a, int b, int c) {
        assertEquals(3, ints.size());
        assertEquals(a, ints.get(0).intValue());
        assertEquals(b, ints.get(1).intValue());
        assertEquals(c, ints.get(2).intValue());
    }
}
