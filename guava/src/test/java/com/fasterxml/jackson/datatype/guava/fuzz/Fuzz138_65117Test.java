package com.fasterxml.jackson.datatype.guava.fuzz;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;

import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for verifying the fixes for OSS-Fuzz issues
 * work as expected
 * (see [datatypes-collections#138]).
 */
public class Fuzz138_65117Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testOSSFuzzIssue65117() throws Exception
    {
        final TypeReference<?> ref = new TypeReference<ImmutableList<Integer>>() {};
        MismatchedInputException e = assertThrows(
                MismatchedInputException.class,
            () ->  MAPPER.readValue("[\"\"s(", ref));
        verifyException(e, "Guava `Collection` of type ");
        verifyException(e, "does not accept `null` values");
    }
}
