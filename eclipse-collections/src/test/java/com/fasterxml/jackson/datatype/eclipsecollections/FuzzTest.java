package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.core.type.TypeReference;

import org.eclipse.collections.api.map.primitive.MutableCharCharMap;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for verifying the fixes for OSS-Fuzz issues
 * work as expected.
 */
public class FuzzTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testOSSFuzzIssue64629() throws Exception
    {
        // Invalid token {"x?":[x?]: where ? is not ascii characters
        final char[] invalid = {123, 34, 824, 34, 58, 91, 120, 7, 93};

        Assert.assertThrows(
            MismatchedInputException.class,
            () -> MAPPER.readValue(new String(invalid), MutableCharCharMap.class));
    }
}
