package com.fasterxml.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;

import com.google.common.collect.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// [datatype-guava#92] : JsonDeserialize contentConverter does not work for non-builtin collections
public class JsonDeserContentConverter92Test extends ModuleTestBase {

    /*
    /**********************************************************
    /* Set up
    /**********************************************************
     */

    static class DoublingConverter extends StdConverter<Integer, Integer> {
        @Override
        public Integer convert(Integer value) {
            return value != null ? value * 2 : null;
        }
    }

    static class StandardWrapper {
        @JsonSerialize(contentConverter = DoublingConverter.class)
        public List<Integer> list;

        public StandardWrapper(List<Integer> asList) {
            this.list = asList;
        }
    }

    static class GuavaListWrapper {
        @JsonSerialize(contentConverter = DoublingConverter.class)
        public ImmutableList<Integer> list;

        public GuavaListWrapper(ImmutableList<Integer> asList) {
            this.list = asList;
        }
    }

    static class StandardHolder {
        @JsonDeserialize(contentConverter = DoublingConverter.class)
        public List<Integer> ints;
    }

    static class GuavaImmutableListHolder {
        @JsonDeserialize(contentConverter = DoublingConverter.class)
        public ImmutableList<Integer> ints;
    }

    static class GuavaImmutableSetHolder {
        @JsonDeserialize(contentConverter = DoublingConverter.class)
        public ImmutableSet<Integer> ints;
    }

    static class GuavaImmutableSortedSetHolder {
        @JsonDeserialize(contentConverter = DoublingConverter.class)
        public ImmutableSortedSet<Integer> ints;
    }

    static class GuavaImmutableMapHolder {
        @JsonDeserialize(contentConverter = DoublingConverter.class)
        public ImmutableMap<String, Integer> ints;
    }

    static class GuavaImmutableMultisetHolder {
        @JsonDeserialize(contentConverter = DoublingConverter.class)
        public ImmutableMultiset<Integer> ints;
    }

    static class GuavaImmutableBiMapHolder {
        @JsonDeserialize(contentConverter = DoublingConverter.class)
        public ImmutableBiMap<String, Integer> ints;
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testJsonSerialize() throws Exception {
        String jsonStr = a2q("{'list':[2,4]}");

        assertEquals(jsonStr, _write(new StandardWrapper(Arrays.asList(1, 2))));
        assertEquals(jsonStr, _write(new GuavaListWrapper(ImmutableList.of(1, 2))));
    }

    @Test
    public void testJsonDeserialize() throws Exception {
        String withIntsArr = a2q("{'ints': [1,2,3] }");
        String withIntsMap = a2q("{'ints': {'one':1, 'two':2, 'three':3}}");

        List<List<Integer>> inputs = new ArrayList<>();
        inputs.add(_read(withIntsArr, StandardHolder.class).ints);
        inputs.add(_read(withIntsArr, GuavaImmutableListHolder.class).ints);
        inputs.add(_read(withIntsArr, GuavaImmutableSetHolder.class).ints.asList());
        inputs.add(_read(withIntsArr, GuavaImmutableSortedSetHolder.class).ints.asList());
        inputs.add(_read(withIntsArr, GuavaImmutableMultisetHolder.class).ints.asList());
        inputs.add(_read(withIntsMap, GuavaImmutableMapHolder.class).ints.values().asList());
        inputs.add(_read(withIntsMap, GuavaImmutableBiMapHolder.class).ints.values().asList());

        for (List<Integer> ints : inputs) {
            containsExactly(ints, 2, 4, 6);
        }
    }

    private <T> String _write(T obj) throws Exception {
        return MAPPER.writeValueAsString(obj);
    }

    private <T> T _read(String withIntsArr, Class<T> clazz) throws Exception {
        return MAPPER.readValue(withIntsArr, clazz);
    }

    private void containsExactly(List<Integer> ints, Integer... expected) {
        assertEquals(expected.length, ints.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], ints.get(i));
        }
    }
}
