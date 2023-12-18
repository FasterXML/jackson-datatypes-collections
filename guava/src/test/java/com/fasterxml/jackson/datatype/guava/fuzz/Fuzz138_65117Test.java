package com.fasterxml.jackson.datatype.guava.fuzz;

import org.junit.Assert;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;

import com.google.common.collect.ImmutableList;

/**
 * Unit tests for verifying the fixes for OSS-Fuzz issues
 * work as expected
 * (see [datatypes-collections#138]).
 */
public class Fuzz138_65117Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    public void testOSSFuzzIssue65117() throws Exception
    {
        final TypeReference<?> ref = new TypeReference<ImmutableList<Integer>>() {};
        MismatchedInputException e = Assert.assertThrows(
                MismatchedInputException.class,
            () ->  MAPPER.readValue("[\"\"s(", ref));
        e.printStackTrace();
        assertTrue(e.getMessage().contains("Guava `Collection` of type "));
        assertTrue(e.getMessage().contains("does not accept `null` values"));
    }
}
