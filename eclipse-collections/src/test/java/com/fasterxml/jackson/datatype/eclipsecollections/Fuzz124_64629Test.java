package com.fasterxml.jackson.datatype.eclipsecollections;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import org.eclipse.collections.api.map.primitive.MutableCharCharMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

/**
 * Unit tests for verifying the fixes for OSS-Fuzz issues
 * work as expected.
 */
public class Fuzz124_64629Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testOSSFuzzIssue64629() throws Exception
    {
        // Invalid token {"x?":[x?]: where ? is not ascii characters
        final char[] invalid = {123, 34, 824, 34, 58, 91, 120, 7, 93};

        MismatchedInputException e = Assert.assertThrows(
            MismatchedInputException.class,
            () -> MAPPER.readValue(new String(invalid), MutableCharCharMap.class));
        assertTrue(e.getMessage().contains("Cannot convert a JSON Null into a char element of map"));
    }
}
