package tools.jackson.datatype.eclipsecollections;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.ObjectMapper;
import org.eclipse.collections.api.map.primitive.ByteIntMap;
import org.eclipse.collections.api.map.primitive.ShortIntMap;
import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.api.map.primitive.LongIntMap;
import org.eclipse.collections.api.map.primitive.FloatIntMap;
import org.eclipse.collections.api.map.primitive.DoubleIntMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests to verify that number parsing errors are properly handled
 * and converted to Jackson exceptions with proper context.
 */
public class NumberParsingErrorHandlingTest extends ModuleTestBase {

    @Test
    public void testInvalidByteKeyFormat() {
        ObjectMapper mapper = mapperWithModule();
        String json = a2q("{'not_a_byte': 1}");
        
        try {
            mapper.readValue(json, new TypeReference<ByteIntMap>() {});
            fail("Should have thrown MismatchedInputException");
        } catch (MismatchedInputException e) {
            // Verify the error message contains the problematic value
            String message = e.getMessage();
            assertTrue(message.contains("not_a_byte"),
                "Message should contain the invalid value: " + message);
            assertTrue(message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"),
                "Message should mention parse error: " + message);
        }
    }

    @Test
    public void testInvalidShortKeyFormat() {
        ObjectMapper mapper = mapperWithModule();
        String json = a2q("{'invalid_short': 1}");
        
        try {
            mapper.readValue(json, new TypeReference<ShortIntMap>() {});
            fail("Should have thrown MismatchedInputException");
        } catch (MismatchedInputException e) {
            String message = e.getMessage();
            assertTrue(message.contains("invalid_short"),
                "Message should contain the invalid value: " + message);
            assertTrue(message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"),
                "Message should mention parse error: " + message);
        }
    }

    @Test
    public void testInvalidIntKeyFormat() {
        ObjectMapper mapper = mapperWithModule();
        String json = a2q("{'not_an_int': 1}");
        
        try {
            mapper.readValue(json, new TypeReference<IntIntMap>() {});
            fail("Should have thrown MismatchedInputException");
        } catch (MismatchedInputException e) {
            String message = e.getMessage();
            assertTrue(message.contains("not_an_int"),
                "Message should contain the invalid value: " + message);
            assertTrue(message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"),
                "Message should mention parse error: " + message);
        }
    }

    @Test
    public void testInvalidLongKeyFormat() {
        ObjectMapper mapper = mapperWithModule();
        String json = a2q("{'not_a_long': 1}");
        
        try {
            mapper.readValue(json, new TypeReference<LongIntMap>() {});
            fail("Should have thrown MismatchedInputException");
        } catch (MismatchedInputException e) {
            String message = e.getMessage();
            assertTrue(message.contains("not_a_long"),
                "Message should contain the invalid value: " + message);
            assertTrue(message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"),
                "Message should mention parse error: " + message);
        }
    }

    @Test
    public void testInvalidFloatKeyFormat() {
        ObjectMapper mapper = mapperWithModule();
        String json = a2q("{'not_a_float': 1}");
        
        try {
            mapper.readValue(json, new TypeReference<FloatIntMap>() {});
            fail("Should have thrown MismatchedInputException");
        } catch (MismatchedInputException e) {
            String message = e.getMessage();
            assertTrue(message.contains("not_a_float"),
                "Message should contain the invalid value: " + message);
            assertTrue(message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"),
                "Message should mention parse error: " + message);
        }
    }

    @Test
    public void testInvalidDoubleKeyFormat() {
        ObjectMapper mapper = mapperWithModule();
        String json = a2q("{'not_a_double': 1}");
        
        try {
            mapper.readValue(json, new TypeReference<DoubleIntMap>() {});
            fail("Should have thrown MismatchedInputException");
        } catch (MismatchedInputException e) {
            String message = e.getMessage();
            assertTrue(message.contains("not_a_double"),
                "Message should contain the invalid value: " + message);
            assertTrue(message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"),
                "Message should mention parse error: " + message);
        }
    }

    @Test
    public void testByteKeyOverflow() {
        ObjectMapper mapper = mapperWithModule();
        String json = a2q("{'999': 1}"); // 999 is too large for a byte
        
        try {
            mapper.readValue(json, new TypeReference<ByteIntMap>() {});
            fail("Should have thrown JsonMappingException");
        } catch (MismatchedInputException e) {
            String message = e.getMessage();
            assertTrue(message.contains("999"),
                "Message should contain the invalid value: " + message);
        }
    }

    @Test
    public void testShortKeyOverflow() {
        ObjectMapper mapper = mapperWithModule();
        String json = a2q("{'99999': 1}"); // 99999 is too large for a short
        
        try {
            mapper.readValue(json, new TypeReference<ShortIntMap>() {});
            fail("Should have thrown MismatchedInputException");
        } catch (MismatchedInputException e) {
            String message = e.getMessage();
            assertTrue(message.contains("99999"), "Message should contain the invalid value: " + message );
        }
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

}
