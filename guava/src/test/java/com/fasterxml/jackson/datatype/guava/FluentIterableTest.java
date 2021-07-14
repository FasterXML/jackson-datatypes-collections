package com.fasterxml.jackson.datatype.guava;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

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
        return FluentIterable.from(Sets.newHashSet(1, 2, 3));
    }

    /**
     * This test is present so that we know if either Jackson's handling of FluentIterable
     * or Guava's implementation of FluentIterable changes.
     * @throws Exception
     */
    private void assertJsonEqualsNonStrict(String json0, String json1) {
        try {
            JSONAssert.assertEquals(json0, json1, false);
        } catch (JSONException jse) {
            throw new IllegalArgumentException(jse.getMessage());
        }
    }
    public void testSerializationWithoutModule() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        FluentHolder holder = new FluentHolder();
        String json = mapper.writeValueAsString(holder);
        assertEquals("{\"value\":{\"empty\":false}}", json);
    }

    public void testSerialization() throws Exception {
        String json = MAPPER.writeValueAsString(createFluentIterable());
        assertJsonEqualsNonStrict("[1,2,3]", json);
    }

    public void testWrappedSerialization() throws Exception {
        FluentHolder holder = new FluentHolder();
        String json = MAPPER.writeValueAsString(holder);
        assertJsonEqualsNonStrict("{\"value\":[1,2,3]}", json);
    }

}
