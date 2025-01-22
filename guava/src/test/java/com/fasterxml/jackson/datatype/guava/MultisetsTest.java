package com.fasterxml.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.google.common.collect.*;

import static org.junit.jupiter.api.Assertions.*;

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
    @Test
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
    @Test
    public void testWithoutDeserializers() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            /*Multiset<String> set =*/ mapper.readValue("[\"abc\",\"abc\",\"foo\"]",
                    new TypeReference<Multiset<String>>() { });
            fail("Should have failed");
        } catch (InvalidDefinitionException e) {
            // Exception changed a bit in 2.18.2, need to match
            //verifyException(e, "cannot find a deserializer");
            verifyException(e, "Cannot construct instance of ");
            verifyException(e, "No creators");
            verifyException(e, Multiset.class.getName());
        }
    }

    /*
    /**********************************************************************
    /* Unit tests for actual registered module
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testDefaultMultiset() throws Exception
    {
        _testMultiset(new TypeReference<Multiset<String>>() { });
    }

    @Test
    public void testDefaultSortedMultiset() throws Exception {
        _testMultiset(new TypeReference<SortedMultiset<String>>() { });
    }

    @Test
    public void testLinkedHashMultiset() throws Exception {
        _testMultiset(new TypeReference<LinkedHashMultiset<String>>() { });
    }

    @Test
    public void testHashMultiset() throws Exception {
        _testMultiset(new TypeReference<HashMultiset<String>>() { });
    }

    @Test
    public void testTreeMultiset() throws Exception {
        _testMultiset(new TypeReference<TreeMultiset<String>>() { });
    }

    @Test
    public void testImmutableMultiset() throws Exception {
        _testMultiset(new TypeReference<ImmutableMultiset<String>>() { });
    }

    @Test
    public void testImmutableSortedMultiset() throws Exception {
        _testMultiset(new TypeReference<ImmutableSortedMultiset<String>>() { });
    }

    private <T extends Multiset<String>> void _testMultiset(TypeReference<T> typeRef)
        throws Exception
    {
        // First, regular Set with no nulls
        T set = MAPPER.readValue("[\"abc\",\"abc\",\"foo\"]", typeRef);
        assertEquals(3, set.size());
        assertEquals(1, set.count("foo"));
        assertEquals(2, set.count("abc"));
        assertEquals(0, set.count("bar"));

        // This may or may not work, so...
        try {
            set = MAPPER.readValue("[\"abc\", null]", typeRef);
            assertEquals(1, set.count("abc"));
            assertEquals(1, set.count(null));
        } catch (MismatchedInputException e) {
            verifyException(e, "Guava `Collection` of type ");
            verifyException(e, "does not accept `null` values");
        }
    }

    @Test
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
