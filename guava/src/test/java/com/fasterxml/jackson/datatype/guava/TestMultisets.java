package com.fasterxml.jackson.datatype.guava;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.*;

import com.google.common.collect.*;

/**
 * Unit tests to verify handling of various {@link Multiset}s.
 * 
 * @author tsaloranta
 */
public class TestMultisets extends ModuleTestBase
{
    /*
    /**********************************************************************
    /* Unit tests for verifying handling in absence of module registration
    /**********************************************************************
     */
    
    /**
     * Multi-sets can actually be serialized as regular collections, without
     * problems.
     */
    public void testWithoutSerializers() throws Exception
    {
        
        ObjectMapper mapper = new ObjectMapper();
        Multiset<String> set = LinkedHashMultiset.create();
        // hash-based multi-sets actually keeps 'same' instances together, otherwise insertion-ordered:
        set.add("abc");
        set.add("foo");
        set.add("abc");
        String json = mapper.writeValueAsString(set);
        assertEquals("[\"abc\",\"abc\",\"foo\"]", json);
    }

    public void testWithoutDeserializers() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            /*Multiset<String> set =*/ mapper.readValue("[\"abc\",\"abc\",\"foo\"]",
                    new TypeReference<Multiset<String>>() { });
            fail("Should have failed");
        } catch (JsonMappingException e) {
            verifyException(e, "can not find a deserializer");
        }
    }
    
    /*
    /**********************************************************************
    /* Unit tests for actual registered module
    /**********************************************************************
     */

    final ObjectMapper MAPPER = mapperWithModule();
    
    public void testDefaultMultiset() throws Exception
    {
        Multiset<String> set = MAPPER.readValue("[\"abc\",\"abc\",\"foo\"]", new TypeReference<Multiset<String>>() { });
        assertEquals(3, set.size());
        assertEquals(1, set.count("foo"));
        assertEquals(2, set.count("abc"));
        assertEquals(0, set.count("bar"));
    }
    
    public void testDefaultSortedMultiset() throws Exception {
        SortedMultiset<String> set = MAPPER.readValue("[\"abc\",\"abc\",\"foo\"]", new TypeReference<SortedMultiset<String>>() { });
        assertEquals(3, set.size());
        assertEquals(1, set.count("foo"));
        assertEquals(2, set.count("abc"));
        assertEquals(0, set.count("bar"));
    }

    public void testLinkedHashMultiset() throws Exception {
        LinkedHashMultiset<String> set = MAPPER.readValue("[\"abc\",\"abc\",\"foo\"]", new TypeReference<LinkedHashMultiset<String>>() { });
        assertEquals(3, set.size());
        assertEquals(1, set.count("foo"));
        assertEquals(2, set.count("abc"));
        assertEquals(0, set.count("bar"));
    }
    
    public void testHashMultiset() throws Exception {
        HashMultiset<String> set = MAPPER.readValue("[\"abc\",\"abc\",\"foo\"]", new TypeReference<HashMultiset<String>>() { });
        assertEquals(3, set.size());
        assertEquals(1, set.count("foo"));
        assertEquals(2, set.count("abc"));
        assertEquals(0, set.count("bar"));
    }
    
    public void testTreeMultiset() throws Exception {
        TreeMultiset<String> set = MAPPER.readValue("[\"abc\",\"abc\",\"foo\"]", new TypeReference<TreeMultiset<String>>() { });
        assertEquals(3, set.size());
        assertEquals(1, set.count("foo"));
        assertEquals(2, set.count("abc"));
        assertEquals(0, set.count("bar"));
    }
    
    public void testImmutableMultiset() throws Exception {
        ImmutableMultiset<String> set = MAPPER.readValue("[\"abc\",\"abc\",\"foo\"]", new TypeReference<ImmutableMultiset<String>>() { });
        assertEquals(3, set.size());
        assertEquals(1, set.count("foo"));
        assertEquals(2, set.count("abc"));
        assertEquals(0, set.count("bar"));
    }

    public void testImmutableSortedMultiset() throws Exception {
        ImmutableSortedMultiset<String> set = MAPPER.readValue("[\"abc\",\"abc\",\"foo\"]", new TypeReference<ImmutableSortedMultiset<String>>() { });
        assertEquals(3, set.size());
        assertEquals(1, set.count("foo"));
        assertEquals(2, set.count("abc"));
        assertEquals(0, set.count("bar"));
    }

    public void testFromSingle() throws Exception
    {
        ObjectMapper mapper = mapperWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        Multiset<String> set = mapper.readValue("\"abc\"",
                new TypeReference<Multiset<String>>() { });
        assertEquals(1, set.size());
        assertTrue(set.contains("abc"));
    }
    
    public void testFromSingleValue() throws Exception
    {
        ObjectMapper mapper = mapperWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        SampleMultiMapTest sampleTest = mapper.readValue("{\"map\":{\"test\":\"value\"}}",
               new TypeReference<SampleMultiMapTest>() { });
       	
       	assertEquals(1, sampleTest.map.get("test").size());
    }
    
    public void testFromMultiValueWithSingleValueOptionEnabled() throws Exception
    {
        ObjectMapper mapper = mapperWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
       	
        SampleMultiMapTest sampleTest = mapper.readValue("{\"map\":{\"test\":\"val\",\"test1\":[\"val1\",\"val2\"]}}",
                new TypeReference<SampleMultiMapTest>() { });
       	
       	assertEquals(1, sampleTest.map.get("test").size());
       	assertEquals(2, sampleTest.map.get("test1").size());
       	
    }
    
    public void testFromMultiValueWithNoSingleValueOptionEnabled() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
       	
        SampleMultiMapTest sampleTest = mapper.readValue("{\"map\":{\"test\":[\"val\"],\"test1\":[\"val1\",\"val2\"]}}",
                new TypeReference<SampleMultiMapTest>() { });
       	
       	assertEquals(1, sampleTest.map.get("test").size());
       	assertEquals(2, sampleTest.map.get("test1").size());
       	
    }
    
    //Sample class for testing multimaps single value option
    static class SampleMultiMapTest {
        public HashMultimap<String, String> map;
    }
}
