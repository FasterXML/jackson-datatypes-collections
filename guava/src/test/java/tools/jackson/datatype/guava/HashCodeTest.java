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

    // [datatypes-collections#243]: implementation classes are package-private;
    // request one directly as the target type. Only value equality is expected:
    // deserializer always builds a plain (bytes-backed) HashCode.
    @Test
    public void testDeserializeAsImplementationSubtype() throws Exception
    {
        for (HashCode value : new HashCode[] {
                HashCode.fromInt(42), HashCode.fromLong(123456789L),
                HashCode.fromString("cafebabe12345678") }) {
            String json = MAPPER.writeValueAsString(value);
            Object result = MAPPER.readValue(json, value.getClass());
            assertEquals(value, result);
        }
    }
}
