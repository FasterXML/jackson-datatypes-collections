package tools.jackson.datatype.guava;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import com.google.common.hash.HashCode;

public class HashCodeTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    public void testSerialization() throws Exception
    {
        HashCode input = HashCode.fromString("cafebabe12345678");
        String json = MAPPER.writeValueAsString(input);
        assertEquals("\"cafebabe12345678\"", json);
    }

    public void testDeserialization() throws Exception
    {
        // success:
        HashCode result = MAPPER.readValue(q("0123456789cAfEbAbE"), HashCode.class);
        assertEquals("0123456789cafebabe", result.toString());

        // and ... error:
        try {
            result = MAPPER.readValue(q("ghijklmn0123456789"), HashCode.class);
            fail("Should not deserialize from non-hex string: got "+result);
        } catch (MismatchedInputException e) {
            verifyException(e, "Illegal hexadecimal character");
        }
    }
}
