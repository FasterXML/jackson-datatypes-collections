package com.fasterxml.jackson.datatype.guava;

import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import com.google.common.collect.*;

/**
 * Unit tests for verifying that various immutable types
 * (like {@link ImmutableList}, {@link ImmutableMap} and {@link ImmutableSet})
 * work as expected.
 */
public class TestImmutables extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    static class Holder {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object value;

        public Holder() { }
        public Holder(Object v) {
            value = v;
        }
    }

    /*
    /**********************************************************************
    /* Unit tests for verifying handling in absence of module registration
    /**********************************************************************
     */
    
    /**
     * Immutable types can actually be serialized as regular collections, without
     * problems.
     */
    public void testWithoutSerializers() throws Exception
    {
        ImmutableList<Integer> list = ImmutableList.<Integer>builder()
            .add(1).add(2).add(3).build();
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(list));

        ImmutableSet<String> set = ImmutableSet.<String>builder()
            .add("abc").add("def").build();
        assertEquals("[\"abc\",\"def\"]", MAPPER.writeValueAsString(set));

        ImmutableMap<String,Integer> map = ImmutableMap.<String,Integer>builder()
            .put("a", 1).put("b", 2).build();
        assertEquals("{\"a\":1,\"b\":2}", MAPPER.writeValueAsString(map));
    }

    /**
     * Deserialization will fail, however.
     */
    public void testWithoutDeserializers() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue("[1,2,3]",
                    new TypeReference<ImmutableList<Integer>>() { });
            fail("Expected failure for missing deserializer");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "cannot find a deserializer");
        }

        try {
            mapper.readValue("[1,2,3]", new TypeReference<ImmutableSet<Integer>>() { });
            fail("Expected failure for missing deserializer");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "cannot find a deserializer");
        }

        try {
            mapper.readValue("[1,2,3]", new TypeReference<ImmutableSortedSet<Integer>>() { });
            fail("Expected failure for missing deserializer");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "cannot find a deserializer");
        }
        
        try {
            mapper.readValue("{\"a\":true,\"b\":false}", new TypeReference<ImmutableMap<Integer,Boolean>>() { });
            fail("Expected failure for missing deserializer");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "cannot find a deserializer");
        }
    }

    /*
    /**********************************************************************
    /* Unit tests for actual registered module
    /**********************************************************************
     */

    public void testImmutableList() throws Exception
    {
        ImmutableList<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<ImmutableList<Integer>>() { });
        assertEquals(3, list.size());
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
        assertEquals(Integer.valueOf(3), list.get(2));
    }

    public void testImmutableSet() throws Exception
    {
        ImmutableSet<Integer> set = MAPPER.readValue("[3,7,8]",
                new TypeReference<ImmutableSet<Integer>>() { });
        assertEquals(3, set.size());
        Iterator<Integer> it = set.iterator();
        assertEquals(Integer.valueOf(3), it.next());
        assertEquals(Integer.valueOf(7), it.next());
        assertEquals(Integer.valueOf(8), it.next());

        set = MAPPER.readValue("[  ]",
                new TypeReference<ImmutableSet<Integer>>() { });
        assertEquals(0, set.size());
    }

    public void testImmutableSetFromSingle() throws Exception
    {
        ObjectMapper mapper = mapperWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        ImmutableSet<String> set = mapper.readValue("\"abc\"",
                new TypeReference<ImmutableSet<String>>() { });
        assertEquals(1, set.size());
        assertTrue(set.contains("abc"));
    }
    
    public void testTypedImmutableset() throws Exception
    {
        ImmutableSet<Integer> set;
        Holder h;
        String json;
        Holder result;

        // First, with one entry
        set = new ImmutableSet.Builder<Integer>()
                .add(1).build();
        h = new Holder(set);
        json = MAPPER.writeValueAsString(h);

        // so far so good. and back?
        result = MAPPER.readValue(json, Holder.class);
        assertNotNull(result.value);
        if (!(result.value instanceof ImmutableSet<?>)) {
            fail("Expected ImmutableSet, got "+result.value.getClass());
        }
        assertEquals(1, ((ImmutableSet<?>) result.value).size());
        // and then an empty version:
        set = new ImmutableSet.Builder<Integer>().build();
        h = new Holder(set);
        json = MAPPER.writeValueAsString(h);
        result = MAPPER.readValue(json, Holder.class);
        assertNotNull(result.value);
        if (!(result.value instanceof ImmutableSet<?>)) {
            fail("Expected ImmutableSet, got "+result.value.getClass());
        }
        assertEquals(0, ((ImmutableSet<?>) result.value).size());
    }

    public void testImmutableSortedSet() throws Exception
    {
        ImmutableSortedSet<Integer> set = MAPPER.readValue("[5,1,2]", new TypeReference<ImmutableSortedSet<Integer>>() { });
        assertEquals(3, set.size());
        Iterator<Integer> it = set.iterator();
        assertEquals(Integer.valueOf(1), it.next());
        assertEquals(Integer.valueOf(2), it.next());
        assertEquals(Integer.valueOf(5), it.next());
    }
    
    public void testImmutableMap() throws Exception
    {
        final JavaType type = MAPPER.getTypeFactory().constructType(new TypeReference<ImmutableMap<Integer,Boolean>>() { });
        ImmutableMap<Integer,Boolean> map = MAPPER.readValue("{\"12\":true,\"4\":false}", type);
        assertEquals(2, map.size());
        assertEquals(Boolean.TRUE, map.get(Integer.valueOf(12)));
        assertEquals(Boolean.FALSE, map.get(Integer.valueOf(4)));

        map = MAPPER.readValue("{}", type);
        assertNotNull(map);
        assertEquals(0, map.size());

        // and for [datatype-guava#52], verify allowance of JSON nulls
        map = MAPPER.readValue("{\"12\":true,\"4\":null}", type);
        assertEquals(1, map.size());
    }

    public void testTypedImmutableMap() throws Exception
    {
        ImmutableMap<String,Integer> map;
        Holder h;
        String json;
        Holder result;

        // First, with one entry
        map = new ImmutableMap.Builder<String,Integer>()
                .put("a", 1).build();
        h = new Holder(map);
        json = MAPPER.writeValueAsString(h);

        // so far so good. and back?
        result = MAPPER.readValue(json, Holder.class);
        assertNotNull(result.value);
        if (!(result.value instanceof ImmutableMap<?,?>)) {
            fail("Expected ImmutableMap, got "+result.value.getClass());
        }
        assertEquals(1, ((ImmutableMap<?,?>) result.value).size());
        // and then an empty version:
        map = new ImmutableMap.Builder<String,Integer>().build();
        h = new Holder(map);
        json = MAPPER.writeValueAsString(h);
        result = MAPPER.readValue(json, Holder.class);
        assertNotNull(result.value);
        if (!(result.value instanceof ImmutableMap<?,?>)) {
            fail("Expected ImmutableMap, got "+result.value.getClass());
        }
        assertEquals(0, ((ImmutableMap<?,?>) result.value).size());
    }
    
    public void testImmutableSortedMap() throws Exception
    {
        ImmutableSortedMap<Integer,Boolean> map = MAPPER.readValue("{\"12\":true,\"4\":false}", new TypeReference<ImmutableSortedMap<Integer,Boolean>>() { });
        assertEquals(2, map.size());
        assertEquals(Boolean.TRUE, map.get(Integer.valueOf(12)));
        assertEquals(Boolean.FALSE, map.get(Integer.valueOf(4)));
    }
    
    public void testImmutableBiMap() throws Exception
    {
        ImmutableBiMap<Integer,Boolean> map = MAPPER.readValue("{\"12\":true,\"4\":false}", new TypeReference<ImmutableBiMap<Integer,Boolean>>() { });
        assertEquals(2, map.size());
        assertEquals(Boolean.TRUE, map.get(12));
        assertEquals(Boolean.FALSE, map.get(4));
        assertEquals(map.get(12), Boolean.TRUE);
        assertEquals(map.get(4), Boolean.FALSE);
    }
    
}
