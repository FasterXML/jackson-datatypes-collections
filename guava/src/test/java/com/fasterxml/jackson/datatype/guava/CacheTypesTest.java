package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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

    static class CacheOrderingBean {
        @JsonPropertyOrder(alphabetic = true)
        public Cache<String, Integer> cache;

        public CacheOrderingBean(String... keys) {
            cache = CacheBuilder.newBuilder().build();
            int ix = 1;
            for (String key : keys) {
                cache.put(key, ix++);
            }
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "_type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Cat.class, name = "t_cat"),
        @JsonSubTypes.Type(value = Dog.class, name = "t_dog")})
    public static interface Animal {}

    public static class Cat implements Animal {
        public String name = "Whiskers";
    }

    public static class Dog implements Animal {
        public String name = "Woof";
    }

    static class AnimalCacheContainer {
        public Cache<String, Animal> cache;

        public AnimalCacheContainer(Cache<String, Animal> cache) {
            this.cache = cache;
        }
    }

    static class NestedCacheContainer {
        public Cache<String, Cache<String, String>> cache;

        public NestedCacheContainer(Cache<String, Cache<String, String>> cache) {
            this.cache = cache;
        }
    }

    /*
    /**********************************************************
    /* Serialization Tests
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule().enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

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
        final Cache<MyEnum, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put(MyEnum.YAY, 5);
        cache.put(MyEnum.BOO, 2);

        final String writerSer = MAPPER.writerFor(new TypeReference<Cache<MyEnum, Integer>>() {}).writeValueAsString(cache);
        final String mapperSer = MAPPER.writeValueAsString(cache);

        String expected = a2q("{'YAY':5,'BOO':2}");
        assertEquals(expected, writerSer);
        assertEquals(expected, mapperSer);
    }

    public void testOrderByKeyViaProperty() throws Exception {
        CacheOrderingBean input = new CacheOrderingBean("c", "b", "a");

        String json = MAPPER.writeValueAsString(input);

        assertEquals(a2q("{'cache':{'a':3,'b':2,'c':1}}"), json);
    }

    public void testPolymorphicCacheSerialization() throws Exception {
        Cache<String, Animal> cache = CacheBuilder.newBuilder().build();
        cache.put("c", new Cat());
        cache.put("d", new Dog());
        AnimalCacheContainer animalCacheContainer = new AnimalCacheContainer(cache);

        String json = MAPPER.writeValueAsString(animalCacheContainer);

        assertEquals(
            a2q("{'cache':" +
                "{'c':{'_type':'t_cat','name':'Whiskers'}," +
                "'d':{'_type':'t_dog','name':'Woof'}}}"), json);
    }

    public void testNestedCacheSerialization() throws Exception {
        Cache<String, Cache<String, String>> nestedCache = CacheBuilder.newBuilder().build();
        nestedCache.put("a", _buildCacheWithKeys("a_x", "a_y"));
        nestedCache.put("b", _buildCacheWithKeys("b_x", "b_y"));
        NestedCacheContainer nestedCacheContainer = new NestedCacheContainer(nestedCache);

        String json = MAPPER.writeValueAsString(nestedCacheContainer);

        assertEquals(a2q("{'cache':" +
            "{'a':{'a_x':'1','a_y':'2'}," +
            "'b':{'b_x':'1','b_y':'2'}}}"), json);
    }

    private Cache<String, String> _buildCacheWithKeys(String... keys) {
        Cache<String, String> cache = CacheBuilder.newBuilder().build();
        for (int i = 0; i < keys.length; i++) {
            cache.put(keys[i], String.valueOf(i + 1));
        }
        return cache;
    }
}
