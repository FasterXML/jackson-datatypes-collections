package tools.jackson.datatype.guava;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

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
        HostAndPort result = MAPPER.readValue(a2q("{'hostText':'localhost','port':9090}"),
                HostAndPort.class);
        assertEquals("localhost", result.getHost());
        assertEquals(9090, result.getPort());

        // and Alt Old too:
        result = MAPPER.readValue(a2q("{'port':8080, 'host':'foobar.com'}"),
                HostAndPort.class);
        assertEquals("foobar.com", result.getHost());
        assertEquals(8080, result.getPort());

        // and new:
        result = MAPPER.readValue(q("localhost:7070"), HostAndPort.class);
        assertEquals("localhost", result.getHost());
        assertEquals(7070, result.getPort());

        // and ... error (note: numbers, booleans may all be fine)
        try {
            result = MAPPER.readValue("[ ]", HostAndPort.class);
            fail("Should not deserialize from boolean: got "+result);
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize");
        }
    }
}
