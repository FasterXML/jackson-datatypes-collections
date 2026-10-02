package tools.jackson.datatype.eclipsecollections;

import org.eclipse.collections.api.map.primitive.MutableDoubleIntMap;
import org.eclipse.collections.api.map.primitive.MutableFloatIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableLongIntMap;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for parsing primitive map keys with {@code NumberInput}
 * (see [datatypes-collections#270]).
 */
public class PrimitiveMapKeyParsing270Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testValidKeys() throws Exception
    {
        assertEquals(-12, MAPPER.readValue("{\"7\":-12}", MutableIntIntMap.class).get(7));
        assertEquals(3, MAPPER.readValue("{\"-9876543210\":3}", MutableLongIntMap.class).get(-9876543210L));
        assertEquals(4, MAPPER.readValue("{\"1.5\":4}", MutableFloatIntMap.class).get(1.5f));
        assertEquals(5, MAPPER.readValue("{\"2.25\":5}", MutableDoubleIntMap.class).get(2.25));
    }

    @Test
    public void testInvalidKeys() throws Exception
    {
        assertInvalidKey("{\"x\":1}", MutableIntIntMap.class, "Cannot parse 'x' as int value");
        assertInvalidKey("{\"2147483648\":1}", MutableIntIntMap.class, "Cannot parse '2147483648' as int value");
        assertInvalidKey("{\"x\":1}", MutableLongIntMap.class, "Cannot parse 'x' as long value");
        assertInvalidKey("{\"x\":1}", MutableFloatIntMap.class, "Cannot parse 'x' as float value");
        assertInvalidKey("{\"x\":1}", MutableDoubleIntMap.class, "Cannot parse 'x' as double value");
    }

    // `NumberInput.parseInt("")`/`parseLong("")` throw `StringIndexOutOfBoundsException`,
    // which must still be reported as an input mismatch
    @Test
    public void testEmptyKeys() throws Exception
    {
        assertInvalidKey("{\"\":1}", MutableIntIntMap.class, "Cannot parse '' as int value");
        assertInvalidKey("{\"\":1}", MutableLongIntMap.class, "Cannot parse '' as long value");
        assertInvalidKey("{\"\":1}", MutableFloatIntMap.class, "Cannot parse '' as float value");
        assertInvalidKey("{\"\":1}", MutableDoubleIntMap.class, "Cannot parse '' as double value");
    }

    private void assertInvalidKey(String json, Class<?> type, String expectedMessage)
    {
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(json, type));
        verifyException(e, expectedMessage);
    }
}
