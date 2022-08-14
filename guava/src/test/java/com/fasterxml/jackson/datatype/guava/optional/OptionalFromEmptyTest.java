package com.fasterxml.jackson.datatype.guava.optional;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;
import com.google.common.base.Optional;

public class OptionalFromEmptyTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    // [datatype-guava#48]
    public void testDeserNull() throws Exception {
        Optional<?> value = MAPPER.readValue("\"\"", new TypeReference<Optional<Integer>>() {});
        assertEquals(false, value.isPresent());
    }
}
