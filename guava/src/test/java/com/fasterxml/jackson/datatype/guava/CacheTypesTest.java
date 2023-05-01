package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.ArrayList;
import java.util.List;

public class CacheTypesTest extends ModuleTestBase {

    /*
    /**********************************************************
    /* Set up
    /**********************************************************
     */

    static class CacheContainerWithIgnores {
        @JsonIgnoreProperties({"x", "y"})
        public Cache<String, String> cache = CacheBuilder.newBuilder().build();

        public CacheContainerWithIgnores() {
            cache.put("a", "foo");
            cache.put("x", "bar");
        }
    }

    static class CacheContainerWithBean {
        public Cache<String, Bean90> cache = CacheBuilder.newBuilder().build();

        public CacheContainerWithBean() {
            cache.put("a", new Bean90("a", 1));
            cache.put("x", new Bean90("b", 2));
        }
    }

    static class Bean90 {
        public String name;
        public Integer age;

        public Bean90(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }

    static class CacheContainerWithList {
        public Cache<String, List<String>> cache = CacheBuilder.newBuilder().build();

        public CacheContainerWithList() {
            cache.put("a", _makeStringList("a"));
            cache.put("b", _makeStringList("b"));
        }
    }

    private static List<String> _makeStringList(String... str) {
        List<String> list = new ArrayList<String>();
        for (String s : str) {
            list.add(s);
        }
        return list;
    }

    static class BeanKey {
        public int age;

        public BeanKey(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "key_" + age;
        }
    }

    static class BeanKeyEquals {
        public int age;

        public BeanKeyEquals(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "key_" + age;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BeanKeyEquals that = (BeanKeyEquals) o;
            return age == that.age;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(age);
        }
    }


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class CacheWrapper {
        @JsonProperty
        Cache<String, String> cache = CacheBuilder.newBuilder().build();
    }

    public static enum MyEnum {
        YAY,
        BOO
    }

    
    /*
    /**********************************************************
    /* Serialization Tests
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

    public void testCacheSerialization() throws Exception {
        // Create a Guava Cache
        Cache<String, String> cache = CacheBuilder.newBuilder().build();
        cache.put("key1", "value1");
        cache.put("key2", "value2");

        assertEquals(
            a2q("{'key1':'value1','key2':'value2'}"),
            MAPPER.writeValueAsString(cache));
    }

    public void testCacheSerializationIgnoreProperties() throws Exception {
        CacheContainerWithIgnores container = new CacheContainerWithIgnores();

        assertEquals(
            a2q("{'cache':{'a':'foo'}}"),
            MAPPER.writeValueAsString(container));
    }

    public void testCacheSerializationWithBean() throws Exception {
        CacheContainerWithBean container = new CacheContainerWithBean();

        assertEquals(
            a2q("{'cache':{'a':{'name':'a','age':1},'x':{'name':'b','age':2}}}"),
            MAPPER.writeValueAsString(container));
    }

    public void testCacheSerializationWithList() throws Exception {
        CacheContainerWithList container = new CacheContainerWithList();

        assertEquals(
            a2q("{'cache':{'a':['a'],'b':['b']}}"),
            MAPPER.writeValueAsString(container));
    }

    public void testCacheSerializationWithEmptyCache() throws Exception {
        Cache<String, String> cache = CacheBuilder.newBuilder().build();

        assertEquals(
            a2q("{}"),
            MAPPER.writeValueAsString(cache));
    }

    public void testCacheSerializationBeanKey() throws Exception {
        Cache<BeanKey, String> cache = CacheBuilder.newBuilder().build();
        cache.put(new BeanKey(1), "value1");

        assertEquals(
            a2q("{'key_1':'value1'}"),
            MAPPER.writeValueAsString(cache));
    }

    public void testCacheSerializationBeanKeyEquals() throws Exception {
        Cache<BeanKeyEquals, String> cache = CacheBuilder.newBuilder().build();
        cache.put(new BeanKeyEquals(1), "value1");
        cache.put(new BeanKeyEquals(1), "value1");
        cache.put(new BeanKeyEquals(2), "value2");

        assertEquals(
            a2q("{'key_2':'value2','key_1':'value1'}"),
            MAPPER.writeValueAsString(cache));
    }


    public void testEmptyCacheExclusion() throws Exception {
        String json = MAPPER.writeValueAsString(new CacheWrapper());
        assertEquals("{}", json);
    }

    public void testCacheSerializationWithTypeReference() throws Exception {
        final TypeReference<Cache<MyEnum, Integer>> type = new TypeReference<Cache<MyEnum, Integer>>() {};
        final Cache<MyEnum, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put(MyEnum.YAY, 5);
        cache.put(MyEnum.BOO, 2);

        final String serializedForm = MAPPER.writerFor(type).writeValueAsString(cache);
        assertEquals(serializedForm, MAPPER.writeValueAsString(cache));
    }
}
