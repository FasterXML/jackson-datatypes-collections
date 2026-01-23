package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.google.common.collect.SetMultimap;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

// [datatypes-collections#211]: `GuavaMultimapDeserializer` does not respect
// `JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY`
public class MultimapAcceptSingle211Test
    extends ModuleTestBase
{
    static class MultimapContainer211 {
        public String name;

        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public SetMultimap<String, String> options;
    }

    // Container without the annotation - should fail with single value
    static class MultimapContainerNoAnnotation {
        public String name;

        public SetMultimap<String, String> options;
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testMultimapWithSingleValueAnnotation()
            throws Exception
    {
        // Single value for "options.key1" should work with annotation
        String json = "{\"name\":\"test\",\"options\":{\"key1\":\"value1\"}}";

        MultimapContainer211 result = MAPPER.readValue(json, MultimapContainer211.class);
        assertEquals("test", result.name);
        assertEquals(1, result.options.size());
        assertTrue(result.options.containsEntry("key1", "value1"));
    }

    @Test
    public void testMultimapWithArrayValueAndAnnotation()
            throws Exception
    {
        // Array values should also work
        String json = "{\"name\":\"test\",\"options\":{\"key1\":[\"value1\",\"value2\"]}}";

        MultimapContainer211 result = MAPPER.readValue(json, MultimapContainer211.class);
        assertEquals("test", result.name);
        assertEquals(2, result.options.size());
        assertTrue(result.options.containsEntry("key1", "value1"));
        assertTrue(result.options.containsEntry("key1", "value2"));
    }

    @Test
    public void testMultimapWithMixedValuesAndAnnotation()
            throws Exception
    {
        // Mix of single values and arrays should work
        String json = "{\"name\":\"test\",\"options\":{\"key1\":\"single\",\"key2\":[\"arr1\",\"arr2\"]}}";

        MultimapContainer211 result = MAPPER.readValue(json, MultimapContainer211.class);
        assertEquals("test", result.name);
        assertEquals(3, result.options.size());
        assertTrue(result.options.containsEntry("key1", "single"));
        assertTrue(result.options.containsEntry("key2", "arr1"));
        assertTrue(result.options.containsEntry("key2", "arr2"));
    }

    @Test
    public void testMultimapWithoutAnnotationRequiresArray()
            throws Exception
    {
        // Without annotation, array syntax should work
        String json = "{\"name\":\"test\",\"options\":{\"key1\":[\"value1\"]}}";

        MultimapContainerNoAnnotation result = MAPPER.readValue(json, MultimapContainerNoAnnotation.class);
        assertEquals("test", result.name);
        assertEquals(1, result.options.size());
        assertTrue(result.options.containsEntry("key1", "value1"));
    }

    @Test
    public void testMultimapWithoutAnnotationFailsOnSingleValue()
    {
        // Without annotation, single value should fail
        String json = "{\"name\":\"test\",\"options\":{\"key1\":\"value1\"}}";

        try {
            MAPPER.readValue(json, MultimapContainerNoAnnotation.class);
            fail("Should have thrown exception for single value without annotation");
        } catch (Exception e) {
            // Expected: should fail with message about expecting START_ARRAY
            assertTrue(e.getMessage().contains("START_ARRAY"),
                    "Expected error about START_ARRAY, got: " + e.getMessage());
        }
    }
}
