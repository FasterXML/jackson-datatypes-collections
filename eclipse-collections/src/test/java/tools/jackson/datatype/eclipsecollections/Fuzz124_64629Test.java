package tools.jackson.datatype.eclipsecollections;

import org.eclipse.collections.api.map.primitive.MutableCharCharMap;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for verifying the fixes for OSS-Fuzz issues
 * work as expected
 * (see [datatypes-collections#124]).
 */
public class Fuzz124_64629Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testOSSFuzzIssue64629() throws Exception
    {
        // Invalid token {"x?":[x?]: where ? is not ascii characters
        final char[] invalid = {123, 34, 824, 34, 58, 91, 120, 7, 93};

        MismatchedInputException e = assertThrows(
            MismatchedInputException.class,
            () -> MAPPER.readValue(new String(invalid), MutableCharCharMap.class));
        assertTrue(e.getMessage().contains("Cannot convert a JSON Null into a char element of map"));
    }
}
