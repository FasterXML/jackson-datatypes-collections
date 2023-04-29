package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.ArrayList;
import java.util.List;

// [databind#90] Guava Cache serialization
public class CacheSerialization90Test extends ModuleTestBase {

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

    private static List<String> _makeStringList(String str) {
        List<String> list = new ArrayList<String>();
        list.add(str);
        return list;
    }
    
    /*
    /**********************************************************
    /* Test Case
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

        assertEquals(a2q("{'cache':{'a':{'name':'a','age':1},'x':{'name':'b','age':2}}}"),
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
}
