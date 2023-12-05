package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableSortedMultiset;

/**
 * Unit tests for verifying the fixes for OSS-Fuzz issues
 * work as expected.
 */
public class FuzzTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    public void testOSSFuzzIssue64610() throws Exception
    {
        try {
            final TypeReference ref = new TypeReference<ImmutableSortedMultiset<String>>() {};
            MAPPER.readValue("[null", ref);
            fail("Should not reach here");
        } catch (Exception e) {
            if (!(e instanceof MismatchedInputException)) {
                throw e;
            }
        }
    }
}
