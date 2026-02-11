package tools.jackson.datatype.eclipsecollections;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonMappingException;
import tools.jackson.databind.ObjectMapper;
import org.eclipse.collections.api.map.primitive.ByteIntMap;
import org.eclipse.collections.api.map.primitive.ShortIntMap;
import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.api.map.primitive.LongIntMap;
import org.eclipse.collections.api.map.primitive.FloatIntMap;
import org.eclipse.collections.api.map.primitive.DoubleIntMap;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests to verify that number parsing errors are properly handled
 * and converted to Jackson exceptions with proper context.
 */
public class NumberParsingErrorHandlingTest extends ModuleTestBase {

    @Test
    public void testInvalidByteKeyFormat() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        String json = "{\"not_a_byte\": 1}";
        
        try {
            mapper.readValue(json, new TypeReference<ByteIntMap>() {});
            fail("Should have thrown JsonMappingException");
        } catch (JsonMappingException e) {
            // Verify it's a JsonMappingException (not NumberFormatException)
            assertTrue("Exception should be JsonMappingException", e instanceof JsonMappingException);
            // Verify the error message contains the problematic value
            String message = e.getMessage();
            assertTrue("Message should contain the invalid value: " + message, 
                       message.contains("not_a_byte"));
            assertTrue("Message should mention parse error: " + message,
                       message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"));
        }
    }

    @Test
    public void testInvalidShortKeyFormat() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        String json = "{\"invalid_short\": 1}";
        
        try {
            mapper.readValue(json, new TypeReference<ShortIntMap>() {});
            fail("Should have thrown JsonMappingException");
        } catch (JsonMappingException e) {
            assertTrue("Exception should be JsonMappingException", e instanceof JsonMappingException);
            String message = e.getMessage();
            assertTrue("Message should contain the invalid value: " + message, 
                       message.contains("invalid_short"));
            assertTrue("Message should mention parse error: " + message,
                       message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"));
        }
    }

    @Test
    public void testInvalidIntKeyFormat() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        String json = "{\"not_an_int\": 1}";
        
        try {
            mapper.readValue(json, new TypeReference<IntIntMap>() {});
            fail("Should have thrown JsonMappingException");
        } catch (JsonMappingException e) {
            assertTrue("Exception should be JsonMappingException", e instanceof JsonMappingException);
            String message = e.getMessage();
            assertTrue("Message should contain the invalid value: " + message, 
                       message.contains("not_an_int"));
            assertTrue("Message should mention parse error: " + message,
                       message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"));
        }
    }

    @Test
    public void testInvalidLongKeyFormat() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        String json = "{\"not_a_long\": 1}";
        
        try {
            mapper.readValue(json, new TypeReference<LongIntMap>() {});
            fail("Should have thrown JsonMappingException");
        } catch (JsonMappingException e) {
            assertTrue("Exception should be JsonMappingException", e instanceof JsonMappingException);
            String message = e.getMessage();
            assertTrue("Message should contain the invalid value: " + message, 
                       message.contains("not_a_long"));
            assertTrue("Message should mention parse error: " + message,
                       message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"));
        }
    }

    @Test
    public void testInvalidFloatKeyFormat() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        String json = "{\"not_a_float\": 1}";
        
        try {
            mapper.readValue(json, new TypeReference<FloatIntMap>() {});
            fail("Should have thrown JsonMappingException");
        } catch (JsonMappingException e) {
            assertTrue("Exception should be JsonMappingException", e instanceof JsonMappingException);
            String message = e.getMessage();
            assertTrue("Message should contain the invalid value: " + message, 
                       message.contains("not_a_float"));
            assertTrue("Message should mention parse error: " + message,
                       message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"));
        }
    }

    @Test
    public void testInvalidDoubleKeyFormat() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        String json = "{\"not_a_double\": 1}";
        
        try {
            mapper.readValue(json, new TypeReference<DoubleIntMap>() {});
            fail("Should have thrown JsonMappingException");
        } catch (JsonMappingException e) {
            assertTrue("Exception should be JsonMappingException", e instanceof JsonMappingException);
            String message = e.getMessage();
            assertTrue("Message should contain the invalid value: " + message, 
                       message.contains("not_a_double"));
            assertTrue("Message should mention parse error: " + message,
                       message.toLowerCase().contains("parse") || message.toLowerCase().contains("cannot"));
        }
    }

    @Test
    public void testByteKeyOverflow() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        String json = "{\"999\": 1}"; // 999 is too large for a byte
        
        try {
            mapper.readValue(json, new TypeReference<ByteIntMap>() {});
            fail("Should have thrown JsonMappingException");
        } catch (JsonMappingException e) {
            assertTrue("Exception should be JsonMappingException", e instanceof JsonMappingException);
            String message = e.getMessage();
            assertTrue("Message should contain the invalid value: " + message, 
                       message.contains("999"));
        }
    }

    @Test
    public void testShortKeyOverflow() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        String json = "{\"99999\": 1}"; // 99999 is too large for a short
        
        try {
            mapper.readValue(json, new TypeReference<ShortIntMap>() {});
            fail("Should have thrown JsonMappingException");
        } catch (JsonMappingException e) {
            assertTrue("Exception should be JsonMappingException", e instanceof JsonMappingException);
            String message = e.getMessage();
            assertTrue("Message should contain the invalid value: " + message, 
                       message.contains("99999"));
        }
    }

    @Test
    public void testValidNumberParsing() throws Exception {
        ObjectMapper mapper = mapperWithModule();
        
        // Test that valid values still work correctly
        ByteIntMap byteMap = mapper.readValue("{\"127\": 1, \"-128\": 2}", 
                                               new TypeReference<ByteIntMap>() {});
        assertTrue("Map should contain key 127", byteMap.containsKey((byte)127));
        assertTrue("Map should contain key -128", byteMap.containsKey((byte)-128));
        
        ShortIntMap shortMap = mapper.readValue("{\"32767\": 1, \"-32768\": 2}", 
                                                 new TypeReference<ShortIntMap>() {});
        assertTrue("Map should contain key 32767", shortMap.containsKey((short)32767));
        assertTrue("Map should contain key -32768", shortMap.containsKey((short)-32768));
        
        IntIntMap intMap = mapper.readValue("{\"2147483647\": 1, \"-2147483648\": 2}", 
                                             new TypeReference<IntIntMap>() {});
        assertTrue("Map should contain key 2147483647", intMap.containsKey(2147483647));
        assertTrue("Map should contain key -2147483648", intMap.containsKey(-2147483648));
        
        LongIntMap longMap = mapper.readValue("{\"9223372036854775807\": 1}", 
                                               new TypeReference<LongIntMap>() {});
        assertTrue("Map should contain key 9223372036854775807", 
                   longMap.containsKey(9223372036854775807L));
        
        FloatIntMap floatMap = mapper.readValue("{\"3.14\": 1, \"-2.5\": 2}", 
                                                 new TypeReference<FloatIntMap>() {});
        assertTrue("Map should contain key 3.14", floatMap.containsKey(3.14f));
        assertTrue("Map should contain key -2.5", floatMap.containsKey(-2.5f));
        
        DoubleIntMap doubleMap = mapper.readValue("{\"3.141592653589793\": 1}", 
                                                   new TypeReference<DoubleIntMap>() {});
        assertTrue("Map should contain key 3.141592653589793", 
                   doubleMap.containsKey(3.141592653589793));
    }
}
