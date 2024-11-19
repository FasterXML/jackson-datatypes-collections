package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.ImmutableIntArray;

import java.io.IOException;

public class ImmutableIntArrayTest extends ModuleTestBase {

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testSerialization() throws IOException {
        assertEquals("[]", MAPPER.writeValueAsString(ImmutableIntArray.of()));
        assertEquals("[-1,0,1,2,3]", MAPPER.writeValueAsString(ImmutableIntArray.of(-1, 0, 1, 2, 3)));
    }

    public void testDeserialization() throws IOException {
        assertEquals(ImmutableIntArray.of(), MAPPER.readValue("[]", ImmutableIntArray.class));
        assertEquals(ImmutableIntArray.of(1, 2, 3), MAPPER.readValue("[1, 2, 3]", ImmutableIntArray.class));
        assertEquals(ImmutableIntArray.of(0, 1234, -5), MAPPER.readValue("[null, \"1234\", -5]", ImmutableIntArray.class));
    }
}
