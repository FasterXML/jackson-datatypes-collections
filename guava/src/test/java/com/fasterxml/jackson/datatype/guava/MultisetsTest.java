package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.google.common.collect.*;

/**
 * Unit tests to verify handling of various {@link Multiset}s.
 * 
 * @author tsaloranta
 */
public class MultisetsTest extends ModuleTestBase
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

    // 11-Jul-2017, tatu: Not sure if this test makes sense actually...
    public void testWithoutDeserializers() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            /*Multiset<String> set =*/ mapper.readValue("[\"abc\",\"abc\",\"foo\"]",
                    new TypeReference<Multiset<String>>() { });
            fail("Should have failed");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "cannot find a deserializer");
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
}
