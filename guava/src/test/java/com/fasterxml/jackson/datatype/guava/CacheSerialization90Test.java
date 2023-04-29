package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

// [databind#90] Guava Cache serialization
public class CacheSerialization90Test extends ModuleTestBase {

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testGuavaCacheApi() throws Exception {
        Cache<String, String> cache = CacheBuilder.newBuilder().build();
        // Cache does not allow null key
        try {
            cache.put(null, "value");
            fail("should not pass");
        } catch (NullPointerException e) {}

        // Cache does not allow null value
        try {
            cache.put("key", null);
            fail("should not pass");
        } catch (NullPointerException e) {
            verifyException(e);
        }
    }

    public void testCacheSerialization() throws Exception {
        // Create a Guava Cache
        Cache<String, String> cache = CacheBuilder.newBuilder().build();
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");

        assertEquals(
            a2q("{'key1':'value1','key2':'value2','key3':'value3'}"),
            MAPPER.writeValueAsString(cache));
    }
}
