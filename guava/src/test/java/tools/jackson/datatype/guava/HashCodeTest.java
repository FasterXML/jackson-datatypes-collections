package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.google.common.hash.HashCode;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.*;

public class HashCodeTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testSerialization() throws Exception
    {
        HashCode input = HashCode.fromString("cafebabe12345678");
        String json = MAPPER.writeValueAsString(input);
        assertEquals("\"cafebabe12345678\"", json);
    }

    @Test
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
