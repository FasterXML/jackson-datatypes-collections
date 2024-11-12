package com.fasterxml.jackson.datatype.pcollections;

import java.util.Arrays;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;

import org.junit.Test;
import org.pcollections.*;

import static org.junit.Assert.*;

/**
 * Unit tests for verifying that various PCollection types
 * work as expected.
 */
public class TestPCollections extends ModuleTestBase
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
     * PCollections types can actually be serialized as regular collections, without
     * problems.
     */
    @Test
    public void withoutSerializers() throws Exception
    {
        PVector<Integer> list = TreePVector.from(Arrays.asList(1, 2, 3));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(list));

        PStack<String> stack = ConsPStack.singleton("def").plus("abc");
        assertEquals("[\"abc\",\"def\"]", MAPPER.writeValueAsString(stack));
    }

    // 11-Jul-2017, tatu: Seems pointless to verify this... ?
    /**
     * Deserialization will fail, however.
     */
    @Test
    public void withoutDeserializers() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue("[1,2,3]",
                    new TypeReference<PSequence<Integer>>() { });
            fail("Expected failure for missing deserializer");
        } catch (InvalidDefinitionException e) {
            _verifyImmutableException(e, PSequence.class);
        }

        try {
            mapper.readValue("[1,2,3]", new TypeReference<PSet<Integer>>() { });
            fail("Expected failure for missing deserializer");
        } catch (InvalidDefinitionException e) {
            _verifyImmutableException(e, PSet.class);
        }

        try {
            mapper.readValue("{\"a\":true,\"b\":false}", new TypeReference<PMap<Integer,Boolean>>() { });
            fail("Expected failure for missing deserializer");
        } catch (InvalidDefinitionException e) {
            _verifyImmutableException(e, PMap.class);
        }
    }

    private void _verifyImmutableException(InvalidDefinitionException e, Class<?> type) {
        // Exception changed a bit in 2.18.2, need to match
        //verifyException(e, "cannot find a deserializer");
        verifyException(e, "Cannot construct instance of ");
        verifyException(e, "No creators");
        verifyException(e, type.getName());
    }

    /*
    /**********************************************************************
    /* Unit tests for actual registered module
    /**********************************************************************
     */
    @Test
    public void pCollection() throws Exception
    {
        PCollection<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<PCollection<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(1), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(3), elements.next());
    }

    @Test
    public void pSequence() throws Exception
    {
        PSequence<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<PSequence<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(1), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(3), elements.next());
    }

    @Test
    public void pVector() throws Exception
    {
        PVector<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<PVector<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(1), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(3), elements.next());
    }

    @Test
    public void treePVector() throws Exception
    {
        TreePVector<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<TreePVector<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(1), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(3), elements.next());
    }

    @Test
    public void orderedPSet() throws Exception
    {
        OrderedPSet<Integer> set = MAPPER.readValue("[1,2,3]", new TypeReference<OrderedPSet<Integer>>() { });
        assertEquals(3, set.size());
        Iterator<Integer> elements = set.iterator();
        assertEquals(Integer.valueOf(1), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(3), elements.next());
    }

    @Test
    public void pStack() throws Exception
    {
        PStack<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<PStack<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(3), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(1), elements.next());
    }

    @Test
    public void consPStack() throws Exception
    {
        ConsPStack<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<ConsPStack<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(3), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(1), elements.next());
    }

    @Test
    public void pSet() throws Exception
    {
        PSet<Integer> set = MAPPER.readValue("[1,2,3]", new TypeReference<PSet<Integer>>() { });
        assertEquals(3, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    public void hashTreePSet() throws Exception
    {
        MapPSet<Integer> set = MAPPER.readValue("[1,2,3]", new TypeReference<MapPSet<Integer>>() { });
        assertEquals(3, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    public void pBag() throws Exception
    {
        PBag<Integer> set = MAPPER.readValue("[1,2,3,3]", new TypeReference<PBag<Integer>>() { });
        assertEquals(4, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    public void mapPBag() throws Exception
    {
        MapPBag<Integer> set = MAPPER.readValue("[1,2,3,3]", new TypeReference<MapPBag<Integer>>() { });
        assertEquals(4, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    public void pMap() throws Exception
    {
        PMap<String, Integer> map = MAPPER.readValue("{\"a\":1,\"b\":2}", new TypeReference<PMap<String, Integer>>() { });
        assertEquals(2, map.size());
        assertEquals(Integer.valueOf(1), map.get("a"));
        assertEquals(Integer.valueOf(2), map.get("b"));
    }

    @Test
    public void hashPMap() throws Exception
    {
        HashPMap<String, Integer> map = MAPPER.readValue("{\"a\":1,\"b\":2}", new TypeReference<HashPMap<String, Integer>>() { });
        assertEquals(2, map.size());
        assertEquals(Integer.valueOf(1), map.get("a"));
        assertEquals(Integer.valueOf(2), map.get("b"));
    }

}
