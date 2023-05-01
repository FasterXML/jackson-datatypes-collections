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

    static class Outside104 {
        @JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.NAME)
        public Object aProperty;
    }

    static class PolymorphicWrapperBean {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type", visible = true)
        public Object aProperty;

        @Override
        public String toString() {
            return aProperty.toString();
        }
    }

    /*
    /**********************************************************
    /* Serialization Tests
    /**********************************************************
     */

    private final ObjectMapper ORDERED_MAPPER = mapperWithModule().enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS);

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
            ORDERED_MAPPER.writeValueAsString(cache));
    }

    public void testCacheSerializationIgnoreProperties() throws Exception {
        CacheContainerWithIgnores container = new CacheContainerWithIgnores();

        assertEquals(
            a2q("{'cache':{'a':'foo'}}"),
            ORDERED_MAPPER.writeValueAsString(container));
    }

    public void testCacheSerializationWithBean() throws Exception {
        CacheContainerWithBean container = new CacheContainerWithBean();

        assertEquals(
            a2q("{'cache':{'a':{'name':'a','age':1},'x':{'name':'b','age':2}}}"),
            ORDERED_MAPPER.writeValueAsString(container));
    }

    public void testCacheSerializationWithList() throws Exception {
        CacheContainerWithList container = new CacheContainerWithList();

        assertEquals(
            a2q("{'cache':{'a':['a'],'b':['b']}}"),
            ORDERED_MAPPER.writeValueAsString(container));
    }

    public void testCacheSerializationWithEmptyCache() throws Exception {
        Cache<String, String> cache = CacheBuilder.newBuilder().build();

        assertEquals(
            a2q("{}"),
            ORDERED_MAPPER.writeValueAsString(cache));
    }

    public void testCacheSerializationBeanKey() throws Exception {
        Cache<BeanKey, String> cache = CacheBuilder.newBuilder().build();
        cache.put(new BeanKey(1), "value1");

        assertEquals(
            a2q("{'key_1':'value1'}"),
            ORDERED_MAPPER.writeValueAsString(cache));
    }

    public void testCacheSerializationBeanKeyEquals() throws Exception {
        Cache<BeanKeyEquals, String> cache = CacheBuilder.newBuilder().build();
        cache.put(new BeanKeyEquals(1), "value1");
        cache.put(new BeanKeyEquals(1), "value1");
        cache.put(new BeanKeyEquals(2), "value2");

        assertEquals(
            a2q("{'key_2':'value2','key_1':'value1'}"),
            ORDERED_MAPPER.writeValueAsString(cache));
    }


    public void testEmptyCacheExclusion() throws Exception {
        String json = ORDERED_MAPPER.writeValueAsString(new CacheWrapper());

        assertEquals("{}", json);
    }

    public void testCacheSerializationWithTypeReference() throws Exception {
        final Cache<MyEnum, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put(MyEnum.YAY, 5);
        cache.put(MyEnum.BOO, 2);

        final String writerSer = ORDERED_MAPPER.writerFor(new TypeReference<Cache<MyEnum, Integer>>() {}).writeValueAsString(cache);
        final String mapperSer = ORDERED_MAPPER.writeValueAsString(cache);

        String expected = a2q("{'YAY':5,'BOO':2}");
        assertEquals(expected, writerSer);
        assertEquals(expected, mapperSer);
    }

    public void testOrderByKeyViaProperty() throws Exception {
        CacheOrderingBean input = new CacheOrderingBean("c", "b", "a");

        String json = ORDERED_MAPPER.writeValueAsString(input);

        assertEquals(a2q("{'cache':{'a':3,'b':2,'c':1}}"), json);
    }

    public void testPolymorphicCacheSerialization() throws Exception {
        Cache<String, Animal> cache = CacheBuilder.newBuilder().build();
        cache.put("c", new Cat());
        cache.put("d", new Dog());
        AnimalCacheContainer animalCacheContainer = new AnimalCacheContainer(cache);

        String json = ORDERED_MAPPER.writeValueAsString(animalCacheContainer);

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

        String json = ORDERED_MAPPER.writeValueAsString(nestedCacheContainer);

        assertEquals(a2q("{'cache':" +
            "{'a':{'a_x':'value','a_y':'value'}," +
            "'b':{'b_x':'value','b_y':'value'}}}"), json);
    }

    private Cache<String, String> _buildCacheWithKeys(String... keys) {
        Cache<String, String> cache = CacheBuilder.newBuilder().build();
        for (int i = 0; i < keys.length; i++) {
            cache.put(keys[i], "value");
        }
        return cache;
    }

    // [datatypes-collections#104]
    public void testPolymorphicCacheEmpty() throws Exception {
        final Cache<String, Object> cache = CacheBuilder.newBuilder().build();
        cache.put("aKey", 1);
        _testPolymorphicCache(cache,
            a2q("{'aProperty':{'@type':'LocalCache$LocalManualCache','aKey':1}}"));
    }

    public void testPolymorphicCacheNonEmpty() throws Exception {
        _testPolymorphicCache(CacheBuilder.newBuilder().build(),
            a2q("{'aProperty':{'@type':'LocalCache$LocalManualCache'}}"));
    }

    private void _testPolymorphicCache(Cache<String, Object> cache, String expected) throws Exception {
        final Outside104 outside = new Outside104();
        outside.aProperty = cache;

        final String json = ORDERED_MAPPER.writeValueAsString(outside);

        assertEquals(expected, json);
    }

    public void testCacheSerializeOrderedByKey() throws Exception {
        final Cache<String, String> cache = _buildCacheWithKeys("c_key", "d_key", "a_key", "e_key", "b_key");

        String expected =
            a2q("{'a_key':'value','b_key':'value','c_key':'value','d_key':'value','e_key':'value'}");

        assertEquals(expected, ORDERED_MAPPER.writeValueAsString(cache));
        assertEquals(expected, ORDERED_MAPPER.writerFor(
            new TypeReference<Cache<String, String>>() {}).writeValueAsString(cache));
    }

    public void testPolymorphicCacheWrapperSerialization() throws Exception {
        final Cache<String, String> cache = _buildCacheWithKeys("c_key", "a_key", "e_key", "b_key", "d_key");
        PolymorphicWrapperBean outside = new PolymorphicWrapperBean();
        outside.aProperty = cache;

        String expected = a2q("{'aProperty':{'type':'" + cache.getClass().getTypeName()
            + "','a_key':'value','b_key':'value','c_key':'value','d_key':'value','e_key':'value'}}");

        assertEquals(expected, ORDERED_MAPPER.writeValueAsString(outside));
        assertEquals(expected, ORDERED_MAPPER.writerFor(
            PolymorphicWrapperBean.class).writeValueAsString(outside));
    }
}
