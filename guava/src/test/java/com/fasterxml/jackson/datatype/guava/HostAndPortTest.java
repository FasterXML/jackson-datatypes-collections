package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HostAndPort;

public class HostAndPortTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    public void testSerialization() throws Exception
    {
        HostAndPort input = HostAndPort.fromParts("localhost", 80);
        String json = MAPPER.writeValueAsString(input);
        assertEquals("\"localhost:80\"", json);
    }

    public void testDeserialization() throws Exception
    {
        // Actually, let's support both old style and new style

        // old:
        HostAndPort result = MAPPER.readValue(aposToQuotes("{'hostText':'localhost','port':9090}"),
                HostAndPort.class);
        assertEquals("localhost", result.getHostText());
        assertEquals(9090, result.getPort());

        // and new:
        result = MAPPER.readValue(quote("localhost:7070"), HostAndPort.class);
        assertEquals("localhost", result.getHostText());
        assertEquals(7070, result.getPort());

        // and ... error (note: numbers, booleans may all be fine)
        try {
            result = MAPPER.readValue("[ ]", HostAndPort.class);
            fail("Should not deserialize from boolean: got "+result);
        } catch (JsonProcessingException e) {
            verifyException(e, "Cannot deserialize");
        }
    }
}
