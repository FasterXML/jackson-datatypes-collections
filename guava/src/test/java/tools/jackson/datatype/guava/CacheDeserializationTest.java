package tools.jackson.datatype.guava;

import java.util.Map;
import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
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

    @Test
    public void testCacheDeserializationSimple() throws Exception {
        // Create a delegate cache using CacheBuilder
        Cache<String, Integer> delegateCache = CacheBuilder.newBuilder().build();
        delegateCache.put("key1", 1);

        Cache<String, String> s = MAPPER.readValue(a2q("{'a':'foo'}"),
            new TypeReference<Cache<String, String>>() {});

        assertEquals(1, s.size());
        assertEquals("foo", s.getIfPresent("a"));
    }

    @Test
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
    @Test
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

    @Test
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

    @Test
    public void testEmptyCacheExclusion() throws Exception {
        String json = MAPPER.writeValueAsString(new CacheWrapper());
        assertEquals("{}", json);
    }

    @Test
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

    // [datatypes-collections#140]: handle null values
    @Test
    public void testCacheWithNulls() throws Exception {
        Cache<String, Integer> cache;
        try {
            cache = MAPPER.readValue(
                "{ \"key\": null }",
                new TypeReference<Cache<String, Integer>>() {});
            fail("Expected fail, deserialized as: "+cache);
        } catch (MismatchedInputException e) {
            verifyException(e, "Guava `Cache` of type");
            verifyException(e, "does not accept `null` values");
        }
    }

    // Deserialized from JSON Object, but always as `null`
    @JsonDeserialize(using = AlwaysNullDeserializer.class)
    static class AlwaysNull { }

    static class AlwaysNullDeserializer extends ValueDeserializer<AlwaysNull> {
        @Override
        public AlwaysNull deserialize(JsonParser p, DeserializationContext ctxt) {
            p.skipChildren();
            return null;
        }
    }

    // Records token and target type it is called with, returns fixed replacement
    static class ReplacingHandler extends DeserializationProblemHandler {
        final Object replacement;
        JsonToken token;
        JavaType targetType;

        ReplacingHandler(Object replacement) {
            this.replacement = replacement;
        }

        @Override
        public Object handleUnexpectedToken(DeserializationContext ctxt,
                JavaType targetType, JsonToken t, JsonParser p, String failureMsg) {
            this.targetType = targetType;
            token = t;
            return replacement;
        }
    }

    @Test
    public void testCacheNullsWithProblemHandler() throws Exception {
        ReplacingHandler h = new ReplacingHandler(42);
        ObjectMapper mapper = builderWithModule().addHandler(h).build();
        Cache<String, Integer> cache = mapper.readValue("{ \"a\": 1, \"key\": null }",
                new TypeReference<Cache<String, Integer>>() {});
        assertEquals(Map.of("a", 1, "key", 42), cache.asMap());
        assertEquals(JsonToken.VALUE_NULL, h.token);
        assertEquals(Integer.class, h.targetType.getRawClass());

        // and `null` from handler drops the entry
        mapper = builderWithModule().addHandler(new ReplacingHandler(null)).build();
        cache = mapper.readValue("{ \"a\": 1, \"key\": null }",
                new TypeReference<Cache<String, Integer>>() {});
        assertEquals(Map.of("a", 1), cache.asMap());
    }

    @Test
    public void testCacheNullFromObjectValue() throws Exception {
        // `null` from value deserializer: fails without handler...
        try {
            Cache<String, AlwaysNull> cache = MAPPER.readValue("{ \"key\": { } }",
                    new TypeReference<Cache<String, AlwaysNull>>() {});
            fail("Expected fail, deserialized as: "+cache);
        } catch (MismatchedInputException e) {
            verifyException(e, "does not accept `null` values");
        }
        // ... and handler is given the token parser is positioned at (end
        // of the value, as value deserializer has consumed it)
        ReplacingHandler h = new ReplacingHandler(null);
        ObjectMapper mapper = builderWithModule().addHandler(h).build();
        Cache<String, AlwaysNull> cache = mapper.readValue("{ \"key\": { \"x\": 1 } }",
                new TypeReference<Cache<String, AlwaysNull>>() {});
        assertEquals(0, cache.size());
        assertEquals(JsonToken.END_OBJECT, h.token);
    }
}
