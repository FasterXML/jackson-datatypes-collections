package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.primitives.ImmutableDoubleArray;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ImmutableDoubleArrayTest extends ModuleTestBase {

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testSerialization() throws IOException {
        assertEquals("[]", MAPPER.writeValueAsString(ImmutableDoubleArray.of()));
        assertEquals("[42.0]", MAPPER.writeValueAsString(ImmutableDoubleArray.of(42.0)));
        assertEquals("[-1.0,0.0,1.0,2.0,3.1]", MAPPER.writeValueAsString(ImmutableDoubleArray.of(-1, 0, 1, 2, 3.1)));
    }

    @Test
    public void testSerializationWriteSingleElemArraysUnwrapped() throws IOException {
        ObjectMapper mapper = builderWithModule().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .build();
        assertEquals("[]", MAPPER.writeValueAsString(ImmutableDoubleArray.of()));
        assertEquals("42.0", mapper.writeValueAsString(ImmutableDoubleArray.of(42.0)));
        assertEquals("[-1.0,0.0,1.0,2.0,3.1]", MAPPER.writeValueAsString(ImmutableDoubleArray.of(-1, 0, 1, 2, 3.1)));
    }

    @Test
    public void testDeserialization() throws IOException {
        assertNull(MAPPER.readValue("null", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(), MAPPER.readValue("[]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(1, 2, 3), MAPPER.readValue("[1, 2, 3]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(1.1, 2.1, 3.1), MAPPER.readValue("[1.1, 2.1, 3.1]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(0.0, 1234.1, -500), MAPPER.readValue("[null, \"1234.1\", -500]", ImmutableDoubleArray.class));
    }

    @Test
    public void testDeserializationWriteSingleElemArraysUnwrapped() throws IOException {
        ObjectMapper mapper = builderWithModule().enable(
                DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .build();
        assertNull(mapper.readValue("null", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(), mapper.readValue("[]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(42.0), mapper.readValue("42.0", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(41.0, 42.0), mapper.readValue("[41.0, [42.0]]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(1, 2, 3), mapper.readValue("[1, 2, 3]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(1.1, 2.1, 3.1), mapper.readValue("[1.1, 2.1, 3.1]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(0.0, 1234.1, -500), mapper.readValue("[null, \"1234.1\", -500]", ImmutableDoubleArray.class));
    }
}