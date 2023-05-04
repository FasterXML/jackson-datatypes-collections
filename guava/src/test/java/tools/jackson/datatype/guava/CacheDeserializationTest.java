package tools.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Map;
import java.util.Objects;

/**
 * Unit tests for verifying deserialization of Guava's {@link Cache} type.
 *
 * @since 2.16
 */
public class CacheDeserializationTest extends ModuleTestBase
{
    // [datatype-collections#96]
    static class CacheProperties {
        @JsonProperty
        Cache<Long, Integer> cache;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public CacheProperties(@JsonProperty("cache") Cache<Long, Integer> cache) {
            this.cache = cache;
        }
    }

    // [datatype-collections#96]
    static class CacheDelegating {
        Cache<Long, Integer> cache;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public CacheDelegating(Cache<Long, Integer> cache) {
            this.cache = cache;
        }

        @JsonValue
        Cache<Long, Integer> mapValue() {
            return cache;
        }
    }

    private enum MyEnum {
        YAY,
        BOO
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class CacheWrapper {
        @JsonProperty
        private Cache<String, String> cache = CacheBuilder.newBuilder().build();
    }

    /*
    /**********************************************************
    /* Tests
    /**********************************************************
     */

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
        } catch (NullPointerException e) {}
    }

    public void testCacheDeserializationSimple() throws Exception {
        // Create a delegate cache using CacheBuilder
        Cache<String, Integer> delegateCache = CacheBuilder.newBuilder().build();
        delegateCache.put("key1", 1);

        Cache<String, String> s = MAPPER.readValue(a2q("{'a':'foo'}"),
            new TypeReference<Cache<String, String>>() {});

        assertEquals(1, s.size());
        assertEquals("foo", s.getIfPresent("a"));
    }

    public void testCacheDeserRoundTrip() throws Exception {
        Cache<String, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put("key1", 1);
        cache.put("key2", 2);
        String json = MAPPER.writeValueAsString(cache);

        Cache<String, Integer> deserializedCache = MAPPER.readValue(json,
            new TypeReference<Cache<String, Integer>>() {});

        int value1 = Objects.requireNonNull(deserializedCache.getIfPresent("key1"));
        int value2 = Objects.requireNonNull(deserializedCache.getIfPresent("key2"));
        assertEquals(1, value1);
        assertEquals(2, value2);
    }

    // [datatype-collections#96]
    public void testCacheSerialization() throws Exception {
        Cache<Long, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put(1L, 1);
        cache.put(2L, 2);

        // properties
        String json = MAPPER.writeValueAsString(new CacheProperties(cache));
        CacheProperties propertiesCache = MAPPER.readValue(json, CacheProperties.class);
        _verifySizeTwoAndContains(propertiesCache.cache.asMap());

        // Delegating
        String delegatingJson = MAPPER.writeValueAsString(new CacheDelegating(cache));
        CacheDelegating delegtingCache = MAPPER.readValue(delegatingJson, CacheDelegating.class);
        _verifySizeTwoAndContains(delegtingCache.cache.asMap());
    }

    // [datatype-collections#96]
    private void _verifySizeTwoAndContains(Map<Long, Integer> map) {
        assertEquals(2, map.size());
        assertEquals(1, map.get(1L).intValue());
        assertEquals(2, map.get(2L).intValue());
    }

    public void testEnumKey() throws Exception {
        final TypeReference<Cache<MyEnum, Integer>> type = new TypeReference<Cache<MyEnum, Integer>>() {};
        final Cache<MyEnum, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put(MyEnum.YAY, 5);
        cache.put(MyEnum.BOO, 2);

        // test serialization
        final String serializedForm = MAPPER.writerFor(type).writeValueAsString(cache);
        assertEquals(serializedForm, MAPPER.writeValueAsString(cache));
        
        // test deserialization
        final Cache<MyEnum, Integer> deserializedCache = MAPPER.readValue(serializedForm, type);
        assertEquals(
            cache.asMap().entrySet(),
            deserializedCache.asMap().entrySet());
    }

    public void testEmptyCacheExclusion() throws Exception {
        String json = MAPPER.writeValueAsString(new CacheWrapper());
        assertEquals("{}", json);
    }

    public void testWithGuavaOptional() throws Exception {
        // set up
        Cache<String, Optional<Double>> cache = CacheBuilder.newBuilder().build();
        cache.put("a", Optional.of(6.0));
        cache.put("b", Optional.absent());

        // test ser
        String jsonStr = MAPPER.writeValueAsString(cache);
        assertEquals(a2q("{'a':6.0,'b':null}"), jsonStr);

        // test deser
        Cache<String, Optional<Double>> deserializedCache = MAPPER.readValue(jsonStr,
            new TypeReference<Cache<String, Optional<Double>>>() {});

        // test before and after
        assertEquals(cache.asMap().entrySet(), deserializedCache.asMap().entrySet());
    }
}
