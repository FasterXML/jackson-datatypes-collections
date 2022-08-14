package com.fasterxml.jackson.datatype.guava;

import tools.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class CacheTypesTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();
    
    // [datatypes-collections#90]: only ensure we can serialize caches as
    //   empty, to begin with
    public void testSerializabilityOfCacheAsEmpty() throws Exception
    {
        Cache<String, String> cache = CacheBuilder.newBuilder().build();
        cache.put("key", "value");

        assertEquals("{}", MAPPER.writeValueAsString(cache));
    }
}
