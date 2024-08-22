package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.ImmutableDoubleArray;

import java.io.IOException;

public class ImmutableDoubleArrayTest extends ModuleTestBase {

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testSerialization() throws IOException {
        assertEquals("[]", MAPPER.writeValueAsString(ImmutableDoubleArray.of()));
        assertEquals("[-1.0,0.0,1.0,2.0,3.1]", MAPPER.writeValueAsString(ImmutableDoubleArray.of(-1, 0, 1, 2, 3.1)));
    }

    public void testDeserialization() throws IOException {
        assertEquals(ImmutableDoubleArray.of(), MAPPER.readValue("[]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(1, 2, 3), MAPPER.readValue("[1, 2, 3]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(1.1, 2.1, 3.1), MAPPER.readValue("[1.1, 2.1, 3.1]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(0.0, 1234.1, -500), MAPPER.readValue("[null, \"1234.1\", -500]", ImmutableDoubleArray.class));
    }
}