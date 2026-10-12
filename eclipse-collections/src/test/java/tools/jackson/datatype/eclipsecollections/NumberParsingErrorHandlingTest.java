package tools.jackson.datatype.eclipsecollections;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ObjectMapper;
import org.eclipse.collections.api.map.primitive.ByteIntMap;
import org.eclipse.collections.api.map.primitive.CharIntMap;
import org.eclipse.collections.api.map.primitive.ShortIntMap;
import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.api.map.primitive.LongIntMap;
import org.eclipse.collections.api.map.primitive.FloatIntMap;
import org.eclipse.collections.api.map.primitive.DoubleIntMap;
import org.eclipse.collections.api.map.primitive.MutableByteIntMap;
import org.eclipse.collections.api.map.primitive.MutableCharIntMap;
import org.eclipse.collections.api.map.primitive.MutableDoubleIntMap;
import org.eclipse.collections.api.map.primitive.MutableFloatIntMap;
import org.eclipse.collections.api.map.primitive.MutableIntIntMap;
import org.eclipse.collections.api.map.primitive.MutableLongIntMap;
import org.eclipse.collections.api.map.primitive.MutableShortIntMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests to verify that number parsing errors are properly handled
 * and converted to Jackson exceptions with proper context.
 *<p>
 * Also verifies that primitive map keys are parsed, and problems reported
 * (via {@code DeserializationContext.handleWeirdKey()}), the same way
 * {@code jackson-databind} does for {@code java.util.Map} keys
 * (see [datatypes-collections#270]).
 */
public class NumberParsingErrorHandlingTest extends ModuleTestBase {

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testInvalidByteKeyFormat() {
        assertInvalidKey(a2q("{'not_a_byte': 1}"), new TypeReference<ByteIntMap>() {},
                "from String \"not_a_byte\"");
    }

    @Test
    public void testInvalidShortKeyFormat() {
        assertInvalidKey(a2q("{'invalid_short': 1}"), new TypeReference<ShortIntMap>() {},
                "from String \"invalid_short\"");
    }

    @Test
    public void testInvalidIntKeyFormat() {
        assertInvalidKey(a2q("{'not_an_int': 1}"), new TypeReference<IntIntMap>() {},
                "from String \"not_an_int\"");
    }

    @Test
    public void testInvalidLongKeyFormat() {
        assertInvalidKey(a2q("{'not_a_long': 1}"), new TypeReference<LongIntMap>() {},
                "from String \"not_a_long\"");
    }

    @Test
    public void testInvalidFloatKeyFormat() {
        assertInvalidKey(a2q("{'not_a_float': 1}"), new TypeReference<FloatIntMap>() {},
                "from String \"not_a_float\"");
    }

    @Test
    public void testInvalidDoubleKeyFormat() {
        assertInvalidKey(a2q("{'not_a_double': 1}"), new TypeReference<DoubleIntMap>() {},
                "from String \"not_a_double\"");
    }

    @Test
    public void testByteKeyOverflow() {
        // 999 is too large for a byte
        assertInvalidKey(a2q("{'999': 1}"), new TypeReference<ByteIntMap>() {},
                "from String \"999\"");
    }

    @Test
    public void testShortKeyOverflow() {
        // 99999 is too large for a short
        assertInvalidKey(a2q("{'99999': 1}"), new TypeReference<ShortIntMap>() {},
                "from String \"99999\"");
    }

    @Test
    public void testValidNumberParsing() {
        ObjectMapper mapper = mapperWithModule();
        
        // Test that valid values still work correctly
        ByteIntMap byteMap = mapper.readValue(a2q("{'127': 1, '-128': 2}"),
                                               new TypeReference<ByteIntMap>() {});
        assertTrue(byteMap.containsKey((byte)127), "Map should contain key 127");
        assertTrue(byteMap.containsKey((byte)-128), "Map should contain key -128");
        
        ShortIntMap shortMap = mapper.readValue(a2q("{'32767': 1, '-32768': 2}"),
                                                 new TypeReference<ShortIntMap>() {});
        assertTrue(shortMap.containsKey((short)32767), "Map should contain key 32767");
        assertTrue(shortMap.containsKey((short)-32768), "Map should contain key -32768");
        
        IntIntMap intMap = mapper.readValue(a2q("{'2147483647': 1, '-2147483648': 2}"),
                                             new TypeReference<IntIntMap>() {});
        assertTrue(intMap.containsKey(2147483647), "Map should contain key 2147483647");
        assertTrue(intMap.containsKey(-2147483648), "Map should contain key -2147483648");
        
        LongIntMap longMap = mapper.readValue(a2q("{'9223372036854775807': 1}"),
                                               new TypeReference<LongIntMap>() {});
        assertTrue(longMap.containsKey(9223372036854775807L),
            "Map should contain key 9223372036854775807");
        
        FloatIntMap floatMap = mapper.readValue(a2q("{'3.14': 1, '-2.5': 2}"),
                                                 new TypeReference<FloatIntMap>() {});
        assertTrue(floatMap.containsKey(3.14f), "Map should contain key 3.14");
        assertTrue(floatMap.containsKey(-2.5f), "Map should contain key -2.5");
        
        DoubleIntMap doubleMap = mapper.readValue(a2q("{'3.141592653589793': 1}"),
                                                   new TypeReference<DoubleIntMap>() {});
        assertTrue(doubleMap.containsKey(3.141592653589793),
            "Map should contain key 3.141592653589793");
    }

    // Values that are close to boundaries of `NumberInput` "fast path" parsing
    // (up to 9 characters for `int`/`long`); see [datatypes-collections#270]
    @Test
    public void testNumberInputFastPathBoundaryKeys() {
        for (String key : new String[] { "999999999", "-999999999", "1000000000", "-1000000000",
                "2147483647", "-2147483648", "+5", "-0", "007" }) {
            String json = "{\"" + key + "\":1}";
            assertEquals(1, MAPPER.readValue(json, MutableIntIntMap.class).get(Integer.parseInt(key)),
                    "int key '" + key + "'");
            assertEquals(1, MAPPER.readValue(json, MutableLongIntMap.class).get(Long.parseLong(key)),
                    "long key '" + key + "'");
        }
        assertEquals(1, MAPPER.readValue("{\"-9223372036854775808\":1}", MutableLongIntMap.class)
                .get(Long.MIN_VALUE));
        for (String key : new String[] { "-", "+", "-2147483649", "1-2", "12a" }) {
            assertInvalidKey("{\"" + key + "\":1}", MutableIntIntMap.class, "from String \"" + key + "\"");
        }
        for (String key : new String[] { "-", "+", "9223372036854775808", "1-2", "12a" }) {
            assertInvalidKey("{\"" + key + "\":1}", MutableLongIntMap.class, "from String \"" + key + "\"");
        }
    }

    // Float keys must be parsed directly, not as `double` narrowed to `float`
    // (which rounds twice): value just below midpoint of `1 + 2^-23` and `1 + 2^-22`
    @Test
    public void testFloatKeyNotDoubleRounded() {
        final String key = "1.00000017881393432617187499";
        assertEquals(Float.parseFloat(key), 1f + 0x1p-23f);
        assertEquals(1, MAPPER.readValue("{\"" + key + "\":1}", MutableFloatIntMap.class)
                .get(1f + 0x1p-23f));
    }

    // Like databind, accept "unsigned" byte values 128 - 255
    @Test
    public void testUnsignedByteKeys() {
        MutableByteIntMap map = MAPPER.readValue("{\"200\":1,\"255\":2}", MutableByteIntMap.class);
        assertEquals(1, map.get((byte) 200));
        assertEquals(2, map.get((byte) 255));
        assertInvalidKey("{\"256\":1}", MutableByteIntMap.class, "8-bit value");
        assertInvalidKey("{\"-129\":1}", MutableByteIntMap.class, "8-bit value");
        assertInvalidKey("{\"32768\":1}", MutableShortIntMap.class, "16-bit value");
    }

    @Test
    public void testInvalidKeyMessages() {
        assertInvalidKey("{\"x\":1}", MutableIntIntMap.class,
                "Cannot deserialize Map key of type `java.lang.Integer` from String \"x\": "
                +"not a valid representation, problem: (java.lang.NumberFormatException)");
        assertInvalidKey("{\"ab\":1}", MutableCharIntMap.class,
                "Cannot deserialize Map key of type `java.lang.Character` from String \"ab\": "
                +"can only convert 1-character Strings");
    }

    @Test
    public void testEmptyKeys() {
        assertInvalidKey("{\"\":1}", MutableByteIntMap.class, "empty String");
        assertInvalidKey("{\"\":1}", MutableShortIntMap.class, "empty String");
        assertInvalidKey("{\"\":1}", MutableIntIntMap.class, "empty String");
        assertInvalidKey("{\"\":1}", MutableLongIntMap.class, "empty String");
        assertInvalidKey("{\"\":1}", MutableFloatIntMap.class, "from String \"\"");
        assertInvalidKey("{\"\":1}", MutableDoubleIntMap.class, "from String \"\"");
        assertInvalidKey("{\"\":1}", MutableCharIntMap.class, "1-character Strings");
    }

    // Invalid keys can be replaced by a `DeserializationProblemHandler`
    @Test
    public void testProblemHandlerReplacesKey() {
        ObjectMapper mapper = mapperWithHandler(new DeserializationProblemHandler() {
            @Override
            public Object handleWeirdKey(DeserializationContext ctxt,
                    Class<?> rawKeyType, String keyValue, String failureMsg) {
                if (rawKeyType == Byte.class) {
                    return (byte) 1;
                }
                if (rawKeyType == Short.class) {
                    return (short) 2;
                }
                if (rawKeyType == Character.class) {
                    return 'c';
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
        });
        final String JSON = "{\"xy\":10}";
        assertEquals(10, mapper.readValue(JSON, MutableByteIntMap.class).get((byte) 1));
        assertEquals(10, mapper.readValue("{\"999\":10}", MutableByteIntMap.class).get((byte) 1));
        assertEquals(10, mapper.readValue(JSON, MutableShortIntMap.class).get((short) 2));
        assertEquals(10, mapper.readValue(JSON, MutableCharIntMap.class).get('c'));
        assertEquals(10, mapper.readValue(JSON, MutableIntIntMap.class).get(3));
        assertEquals(10, mapper.readValue("{\"\":10}", MutableIntIntMap.class).get(3));
        assertEquals(10, mapper.readValue(JSON, MutableLongIntMap.class).get(4L));
        assertEquals(10, mapper.readValue(JSON, MutableFloatIntMap.class).get(5f));
        assertEquals(10, mapper.readValue(JSON, MutableDoubleIntMap.class).get(6d));
    }

    // But `null` is not a valid replacement for a primitive key
    @Test
    public void testProblemHandlerNullKey() {
        ObjectMapper mapper = mapperWithHandler(new DeserializationProblemHandler() {
            @Override
            public Object handleWeirdKey(DeserializationContext ctxt,
                    Class<?> rawKeyType, String keyValue, String failureMsg) {
                return null;
            }
        });
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue("{\"x\":1}", MutableIntIntMap.class));
        verifyException(e, "Cannot use `null` as `int` map key");
        e = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue("{\"xy\":1}", MutableCharIntMap.class));
        verifyException(e, "Cannot use `null` as `char` map key");
    }

    private ObjectMapper mapperWithHandler(DeserializationProblemHandler h) {
        return JsonMapper.builder()
                .addModule(new EclipseCollectionsModule())
                .addHandler(h)
                .build();
    }

    private void assertInvalidKey(String json, Class<?> type, String expectedMessage) {
        InvalidFormatException e = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(json, type));
        verifyException(e, expectedMessage);
    }

    private void assertInvalidKey(String json, TypeReference<?> type, String expectedMessage) {
        InvalidFormatException e = assertThrows(InvalidFormatException.class,
                () -> MAPPER.readValue(json, type));
        verifyException(e, "Cannot deserialize Map key");
        verifyException(e, expectedMessage);
    }
}
