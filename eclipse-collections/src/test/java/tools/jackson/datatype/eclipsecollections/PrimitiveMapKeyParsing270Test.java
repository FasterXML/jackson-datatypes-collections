package tools.jackson.datatype.eclipsecollections;

import org.eclipse.collections.api.map.primitive.MutableByteIntMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleIntMap;
import org.eclipse.collections.api.map.primitive.MutableFloatIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableLongIntMap;
import org.eclipse.collections.api.map.primitive.MutableShortIntMap;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for parsing primitive map keys with {@code NumberInput}, and reporting
 * problems via {@code DeserializationContext.handleWeirdKey()}, same as
 * {@code jackson-databind} does for {@code java.util.Map} keys
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

    // Like databind, accept "unsigned" byte values 128 - 255
    @Test
    public void testUnsignedByteKeys() throws Exception
    {
        MutableByteIntMap map = MAPPER.readValue("{\"200\":1,\"255\":2}", MutableByteIntMap.class);
        assertEquals(1, map.get((byte) 200));
        assertEquals(2, map.get((byte) 255));
        assertInvalidKey("{\"256\":1}", MutableByteIntMap.class, "8-bit value");
        assertInvalidKey("{\"-129\":1}", MutableByteIntMap.class, "8-bit value");
        assertInvalidKey("{\"32768\":1}", MutableShortIntMap.class, "16-bit value");
    }

    @Test
    public void testInvalidKeys() throws Exception
    {
        assertInvalidKey("{\"x\":1}", MutableByteIntMap.class, "Map key of type `java.lang.Byte` from String \"x\"");
        assertInvalidKey("{\"x\":1}", MutableShortIntMap.class, "Map key of type `java.lang.Short` from String \"x\"");
        assertInvalidKey("{\"x\":1}", MutableIntIntMap.class, "Map key of type `java.lang.Integer` from String \"x\"");
        assertInvalidKey("{\"2147483648\":1}", MutableIntIntMap.class, "from String \"2147483648\"");
        assertInvalidKey("{\"x\":1}", MutableLongIntMap.class, "Map key of type `java.lang.Long` from String \"x\"");
        assertInvalidKey("{\"x\":1}", MutableFloatIntMap.class, "Map key of type `java.lang.Float` from String \"x\"");
        assertInvalidKey("{\"x\":1}", MutableDoubleIntMap.class, "Map key of type `java.lang.Double` from String \"x\"");
    }

    @Test
    public void testEmptyKeys() throws Exception
    {
        assertInvalidKey("{\"\":1}", MutableByteIntMap.class, "empty String");
        assertInvalidKey("{\"\":1}", MutableShortIntMap.class, "empty String");
        assertInvalidKey("{\"\":1}", MutableIntIntMap.class, "empty String");
        assertInvalidKey("{\"\":1}", MutableLongIntMap.class, "empty String");
        assertInvalidKey("{\"\":1}", MutableFloatIntMap.class, "from String \"\"");
        assertInvalidKey("{\"\":1}", MutableDoubleIntMap.class, "from String \"\"");
    }

    // Invalid keys can be replaced by a `DeserializationProblemHandler`
    @Test
    public void testProblemHandlerReplacesKey() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new EclipseCollectionsModule())
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleWeirdKey(DeserializationContext ctxt,
                            Class<?> rawKeyType, String keyValue, String failureMsg) {
                        if (rawKeyType == Byte.class) {
                            return (byte) 1;
                        }
                        if (rawKeyType == Short.class) {
                            return (short) 2;
                        }
                        if (rawKeyType == Integer.class) {
                            return 3;
                        }
                        if (rawKeyType == Long.class) {
                            return 4L;
                        }
                        if (rawKeyType == Float.class) {
                            return 5f;
                        }
                        if (rawKeyType == Double.class) {
                            return 6d;
                        }
                        return NOT_HANDLED;
                    }
                })
                .build();
        final String JSON = "{\"x\":10}";
        assertEquals(10, mapper.readValue(JSON, MutableByteIntMap.class).get((byte) 1));
        assertEquals(10, mapper.readValue("{\"999\":10}", MutableByteIntMap.class).get((byte) 1));
        assertEquals(10, mapper.readValue(JSON, MutableShortIntMap.class).get((short) 2));
        assertEquals(10, mapper.readValue(JSON, MutableIntIntMap.class).get(3));
        assertEquals(10, mapper.readValue("{\"\":10}", MutableIntIntMap.class).get(3));
        assertEquals(10, mapper.readValue(JSON, MutableLongIntMap.class).get(4L));
        assertEquals(10, mapper.readValue(JSON, MutableFloatIntMap.class).get(5f));
        assertEquals(10, mapper.readValue(JSON, MutableDoubleIntMap.class).get(6d));
    }

    // But `null` is not a valid replacement for a primitive key
    @Test
    public void testProblemHandlerNullKey() throws Exception
    {
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new EclipseCollectionsModule())
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleWeirdKey(DeserializationContext ctxt,
                            Class<?> rawKeyType, String keyValue, String failureMsg) {
                        return null;
                    }
                })
                .build();
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue("{\"x\":1}", MutableIntIntMap.class));
        verifyException(e, "Cannot use `null` as `int` map key");
    }

    private void assertInvalidKey(String json, Class<?> type, String expectedMessage)
    {
        InvalidFormatException e = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(json, type));
        verifyException(e, expectedMessage);
    }
}
