package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

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

    public void testDeserializationOk() throws Exception
    {
        // Actually, let's support both old style and new style

        // old:
        HostAndPort result = MAPPER.readValue(aposToQuotes("{'hostText':'localhost','port':9090}"),
                HostAndPort.class);
        assertEquals("localhost", result.getHost());
        assertEquals(9090, result.getPort());

        // and Alt Old too:
        result = MAPPER.readValue(aposToQuotes("{'port':8080, 'host':'foobar.com'}"),
                HostAndPort.class);
        assertEquals("foobar.com", result.getHost());
        assertEquals(8080, result.getPort());

        // and new:
        result = MAPPER.readValue(quote("localhost:7070"), HostAndPort.class);
        assertEquals("localhost", result.getHost());
        assertEquals(7070, result.getPort());

        assertNull(MAPPER.readValue("null", HostAndPort.class));

        // Null values lead to "empty" Value
        result = MAPPER.readValue("{\"host\": null}", HostAndPort.class);
        assertEquals(HostAndPort.fromHost(""), result);
    }

    public void testDeserializationFail() throws Exception
    {
        HostAndPort result = null;

        // and ... error if given, say, JSON Array
        try {
            result = MAPPER.readValue("[ ]", HostAndPort.class);
            fail("Should not deserialize from empty JSON Array: got "+MAPPER.writeValueAsString(result));
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize");
            verifyException(e, "from Array value");
        }
    }
}
