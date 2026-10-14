package tools.jackson.datatype.eclipsecollections;

import java.util.Map;

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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests to verify that number parsing errors are properly handled
 * and converted to Jackson exceptions with proper context.
 *<p>
 * Also verifies that primitive map keys are parsed, and problems reported
 * (via {@code DeserializationContext.handleWeirdKey()}), the same way
 * {@code jackson-databind} does for {@code java.util.Map} keys
 * (see [datatypes-collections#270]); except that {@code float} keys are
 * parsed directly, not as {@code double} narrowed to {@code float}.
 */
public class NumberParsingErrorHandlingTest extends ModuleTestBase {

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testValidKeys() {
        assertEquals(1, readKey(MAPPER, ByteIntMap.class, "127").get((byte) 127));
        assertEquals(1, readKey(MAPPER, ByteIntMap.class, "-128").get((byte) -128));
        assertEquals(1, readKey(MAPPER, ShortIntMap.class, "32767").get(Short.MAX_VALUE));
        assertEquals(1, readKey(MAPPER, ShortIntMap.class, "-32768").get(Short.MIN_VALUE));
        assertEquals(1, readKey(MAPPER, LongIntMap.class, "9223372036854775807").get(Long.MAX_VALUE));
        assertEquals(1, readKey(MAPPER, LongIntMap.class, "-9223372036854775808").get(Long.MIN_VALUE));
        assertEquals(1, readKey(MAPPER, FloatIntMap.class, "3.14").get(3.14f));
        assertEquals(1, readKey(MAPPER, FloatIntMap.class, "-2.5").get(-2.5f));
        assertEquals(1, readKey(MAPPER, DoubleIntMap.class, "3.141592653589793").get(3.141592653589793));
    }

    // Values that are close to boundaries of `NumberInput` "fast path" parsing
    // (up to 9 characters for `int`/`long`)
    @Test
    public void testNumberInputFastPathBoundaryKeys() {
        for (String key : new String[] { "999999999", "-999999999", "1000000000", "-1000000000",
                "2147483647", "-2147483648", "+5", "-0", "007" }) {
            assertEquals(1, readKey(MAPPER, IntIntMap.class, key).get(Integer.parseInt(key)), key);
            assertEquals(1, readKey(MAPPER, LongIntMap.class, key).get(Long.parseLong(key)), key);
        }
        for (String key : new String[] { "-", "+", "1-2", "12a" }) {
            assertInvalidKey(IntIntMap.class, key);
            assertInvalidKey(LongIntMap.class, key);
        }
    }

    @Test
    public void testInvalidKeys() {
        assertInvalidKey(ByteIntMap.class, "x");
        assertInvalidKey(ShortIntMap.class, "x");
        assertInvalidKey(IntIntMap.class, "x");
        assertInvalidKey(LongIntMap.class, "x");
        assertInvalidKey(FloatIntMap.class, "x");
        assertInvalidKey(DoubleIntMap.class, "x");
        assertInvalidKey(CharIntMap.class, "xy");
    }

    @Test
    public void testInvalidKeyMessages() {
        assertInvalidKey(IntIntMap.class, "x",
                "Cannot deserialize Map key of type `java.lang.Integer` from String \"x\": "
                +"not a valid representation, problem: (java.lang.NumberFormatException)");
        assertInvalidKey(CharIntMap.class, "ab",
                "Cannot deserialize Map key of type `java.lang.Character` from String \"ab\": "
                +"can only convert 1-character Strings");
    }

    @Test
    public void testKeyOverflow() {
        // Like databind, accept "unsigned" byte values 128 - 255
        assertEquals(1, readKey(MAPPER, ByteIntMap.class, "200").get((byte) 200));
        assertEquals(1, readKey(MAPPER, ByteIntMap.class, "255").get((byte) 255));
        assertInvalidKey(ByteIntMap.class, "256", "8-bit value");
        assertInvalidKey(ByteIntMap.class, "-129", "8-bit value");
        assertInvalidKey(ShortIntMap.class, "32768", "16-bit value");
        assertInvalidKey(ShortIntMap.class, "-32769", "16-bit value");
        assertInvalidKey(IntIntMap.class, "2147483648");
        assertInvalidKey(IntIntMap.class, "-2147483649");
        assertInvalidKey(LongIntMap.class, "9223372036854775808");
    }

    // Float keys must be parsed directly, not as `double` narrowed to `float`
    // (which rounds twice): value just below midpoint of `1 + 2^-23` and `1 + 2^-22`
    @Test
    public void testFloatKeyNotDoubleRounded() {
        final String key = "1.00000017881393432617187499";
        assertEquals(1f + 0x1p-23f, Float.parseFloat(key));
        assertEquals(1, readKey(MAPPER, FloatIntMap.class, key).get(1f + 0x1p-23f));
    }

    @Test
    public void testEmptyKeys() {
        assertInvalidKey(ByteIntMap.class, "", "empty String");
        assertInvalidKey(ShortIntMap.class, "", "empty String");
        assertInvalidKey(IntIntMap.class, "", "empty String");
        assertInvalidKey(LongIntMap.class, "", "empty String");
        assertInvalidKey(FloatIntMap.class, "");
        assertInvalidKey(DoubleIntMap.class, "");
        assertInvalidKey(CharIntMap.class, "", "1-character Strings");
    }

    // Invalid keys can be replaced by a `DeserializationProblemHandler`
    @Test
    public void testProblemHandlerReplacesKey() {
        final Map<Class<?>, Object> replacements = Map.of(Byte.class, (byte) 1, Short.class, (short) 2,
                Character.class, 'c', Integer.class, 3, Long.class, 4L, Float.class, 5f, Double.class, 6d);
        ObjectMapper mapper = mapperWithHandler(new DeserializationProblemHandler() {
            @Override
            public Object handleWeirdKey(DeserializationContext ctxt,
                    Class<?> rawKeyType, String keyValue, String failureMsg) {
                return replacements.getOrDefault(rawKeyType, NOT_HANDLED);
            }
        });
        assertEquals(1, readKey(mapper, ByteIntMap.class, "xy").get((byte) 1));
        assertEquals(1, readKey(mapper, ByteIntMap.class, "999").get((byte) 1));
        assertEquals(1, readKey(mapper, ShortIntMap.class, "xy").get((short) 2));
        assertEquals(1, readKey(mapper, CharIntMap.class, "xy").get('c'));
        assertEquals(1, readKey(mapper, IntIntMap.class, "xy").get(3));
        assertEquals(1, readKey(mapper, IntIntMap.class, "").get(3));
        assertEquals(1, readKey(mapper, LongIntMap.class, "xy").get(4L));
        assertEquals(1, readKey(mapper, FloatIntMap.class, "xy").get(5f));
        assertEquals(1, readKey(mapper, DoubleIntMap.class, "xy").get(6d));
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
                () -> readKey(mapper, IntIntMap.class, "x"));
        verifyException(e, "Cannot use `null` as `int` map key");
        e = assertThrows(MismatchedInputException.class,
                () -> readKey(mapper, CharIntMap.class, "xy"));
        verifyException(e, "Cannot use `null` as `char` map key");
    }

    private static ObjectMapper mapperWithHandler(DeserializationProblemHandler h) {
        return JsonMapper.builder()
                .addModule(new EclipseCollectionsModule())
                .addHandler(h)
                .build();
    }

    // Reads a map with a single entry, `key` mapped to `1`
    private static <T> T readKey(ObjectMapper mapper, Class<T> type, String key) {
        return mapper.readValue("{\"" + key + "\":1}", type);
    }

    private void assertInvalidKey(Class<?> type, String key, String... expectedMessages) {
        InvalidFormatException e = assertThrows(InvalidFormatException.class,
                () -> readKey(MAPPER, type, key));
        verifyException(e, "Cannot deserialize Map key");
        verifyException(e, "from String \"" + key + "\"");
        for (String msg : expectedMessages) {
            verifyException(e, msg);
        }
    }
}
