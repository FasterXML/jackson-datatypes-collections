package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.ValueInstantiationException;

import com.google.common.collect.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify that RuntimeExceptions thrown during builder.build() calls
 * are properly caught and reported via DeserializationContext.reportInputMismatch()
 */
public class BuilderExceptionHandlingTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    /**
     * ImmutableMap.Builder.build() throws IllegalArgumentException when there are duplicate keys
     */
    @Test
    public void testImmutableMapDuplicateKeysHandling() throws Exception
    {
        // ImmutableMap does not allow duplicate keys, so this should trigger an error during build()
        String json = "{\"a\":1,\"b\":2,\"a\":3}";
        try {
            MAPPER.readValue(json, ImmutableMap.class);
            fail("Should have thrown an exception for duplicate keys");
        } catch (ValueInstantiationException e) {
            // Expected - should contain meaningful error message
            String msg = e.getMessage();
            assertTrue(msg.contains("Failed to build ImmutableMap") || msg.contains("duplicate"),
                    "Error message should mention ImmutableMap build failure or duplicate keys, got: " + msg);
        }
    }

    /**
     * ImmutableBiMap.Builder.build() throws IllegalArgumentException for duplicate keys or values
     */
    @Test
    public void testImmutableBiMapDuplicateKeysHandling() throws Exception
    {
        String json = "{\"a\":1,\"b\":2,\"a\":3}";
        try {
            MAPPER.readValue(json, ImmutableBiMap.class);
            fail("Should have thrown an exception for duplicate keys");
        } catch (ValueInstantiationException e) {
            String msg = e.getMessage();
            assertTrue(msg.contains("Failed to build ImmutableMap") || msg.contains("duplicate"),
                    "Error message should mention build failure or duplicate, got: " + msg);
        }
    }

    /**
     * ImmutableSortedMap.Builder.build() can throw IllegalArgumentException 
     * if the elements are not mutually comparable
     */
    @Test
    public void testImmutableSortedMapDuplicateKeysHandling() throws Exception
    {
        String json = "{\"a\":1,\"b\":2,\"a\":3}";
        try {
            MAPPER.readValue(json, ImmutableSortedMap.class);
            fail("Should have thrown an exception for duplicate keys");
        } catch (ValueInstantiationException e) {
            String msg = e.getMessage();
            assertTrue(msg.contains("Failed to build ImmutableMap") || msg.contains("duplicate"),
                    "Error message should mention build failure or duplicate, got: " + msg);
        }
    }

    /**
     * Test that valid ImmutableMap deserialization still works correctly
     */
    @Test
    public void testImmutableMapValidData() throws Exception
    {
        String json = "{\"a\":1,\"b\":2,\"c\":3}";
        ImmutableMap<?,?> result = MAPPER.readValue(json, ImmutableMap.class);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1, result.get("a"));
        assertEquals(2, result.get("b"));
        assertEquals(3, result.get("c"));
    }

    /**
     * ImmutableTable.Builder.build() can throw IllegalArgumentException for duplicate row+column keys.
     * Note: This test uses duplicate JSON keys, which may be handled by the JSON parser itself
     * before reaching the builder. The test validates that if a RuntimeException does occur
     * during build(), it will be properly caught and reported.
     */
    @Test
    public void testImmutableTableDuplicateCellHandling() throws Exception
    {
        // Table with duplicate row/column combination
        String json = "{\"row1\":{\"col1\":\"val1\",\"col1\":\"val2\"}}";
        try {
            MAPPER.readValue(json, ImmutableTable.class);
            fail("Should have thrown an exception for duplicate cells");
        } catch (Exception e) {
            // Expected - either during JSON parsing (duplicate key in JSON)
            // or during build() if it gets that far
            String msg = e.getMessage();
            assertTrue(msg.contains("Failed to build ImmutableTable") 
                    || msg.contains("duplicate") 
                    || msg.contains("Duplicate"),
                    "Error message should indicate build failure or duplicate, got: " + msg);
        }
    }

    /**
     * Test that valid ImmutableTable deserialization still works correctly
     */
    @Test
    public void testImmutableTableValidData() throws Exception
    {
        String json = "{\"row1\":{\"col1\":\"val1\",\"col2\":\"val2\"},\"row2\":{\"col1\":\"val3\"}}";
        ImmutableTable<?,?,?> result = MAPPER.readValue(json, ImmutableTable.class);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("val1", result.get("row1", "col1"));
        assertEquals("val2", result.get("row1", "col2"));
        assertEquals("val3", result.get("row2", "col1"));
    }

    /**
     * Test that valid ImmutableList deserialization still works correctly
     */
    @Test
    public void testImmutableListValidData() throws Exception
    {
        String json = "[1,2,3,4,5]";
        ImmutableList<?> result = MAPPER.readValue(json, ImmutableList.class);
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(1, result.get(0));
        assertEquals(5, result.get(4));
    }

    /**
     * Test that valid ImmutableSet deserialization still works correctly
     */
    @Test
    public void testImmutableSetValidData() throws Exception
    {
        String json = "[1,2,3,4,5]";
        ImmutableSet<?> result = MAPPER.readValue(json, ImmutableSet.class);
        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(5));
    }
}
