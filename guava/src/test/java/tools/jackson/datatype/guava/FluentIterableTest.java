package tools.jackson.datatype.guava;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.google.common.collect.FluentIterable;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests to verify serialization of {@link FluentIterable}s.
 */
public class FluentIterableTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    public static class FluentHolder {
        public final Iterable<Integer> value = createFluentIterable();
    }

    static FluentIterable<Integer> createFluentIterable() {
        // use List/array-based to ensure order
        return FluentIterable.from(Arrays.asList(1, 2, 3));
    }

    /**
     * This test is present so that we know if either Jackson's handling of FluentIterable
     * or Guava's implementation of FluentIterable changes.
     * @throws Exception
     */
    @Test
    public void testSerializationWithoutModule() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FluentHolder holder = new FluentHolder();
        String json = mapper.writeValueAsString(holder);
        assertEquals("{\"value\":{\"empty\":false}}", json);
    }

    @Test
    public void testSerialization() throws Exception {
        String json = MAPPER.writeValueAsString(createFluentIterable());
        assertEquals("[1,2,3]", json);
    }

    @Test
    public void testWrappedSerialization() throws Exception {
        FluentHolder holder = new FluentHolder();
        String json = MAPPER.writeValueAsString(holder);
        assertEquals("{\"value\":[1,2,3]}", json);
    }
}
