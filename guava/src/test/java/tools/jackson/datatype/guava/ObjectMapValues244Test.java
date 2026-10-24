package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.*;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectMapValues244Test extends ModuleTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
    @JsonSubTypes({ @JsonSubTypes.Type(value = Dog.class, name = "Dog") })
    static abstract class Animal {
        public String name = "n";
    }

    static class Dog extends Animal {
        public int barks = 3;
    }

    private final static String DOG_JSON = "{'@type':'Dog','barks':3,'name':'n'}";

    @Test
    public void multimapWithExplicitRootType() {
        ObjectMapper mapper = mapperWithModule();
        Multimap<String, Object> input = ArrayListMultimap.create();
        input.put("a", 1);
        input.put("a", "x");
        input.put("b", 2);
        TypeReference<Multimap<String, Object>> type = new TypeReference<>() { };
        assertEquals(a2q("{'a':[1,'x'],'b':[2]}"),
                mapper.writerFor(type).writeValueAsString(input));
    }

    @Test
    public void rangeMapWithExplicitRootType() {
        ObjectMapper mapper = mapperWithModule();
        RangeMap<Integer, Object> input = TreeRangeMap.create();
        input.put(Range.closed(1, 5), "a");
        input.put(Range.open(10, 20), 42);
        TypeReference<RangeMap<Integer, Object>> type = new TypeReference<>() { };
        assertEquals(a2q("{'[1..5]':'a','(10..20)':42}"),
                mapper.writerFor(type).writeValueAsString(input));
    }

    @Test
    public void cacheWithExplicitRootType() {
        ObjectMapper mapper = mapperWithModule();
        Cache<String, Object> input = CacheBuilder.newBuilder().build();
        input.put("a", 1);
        input.put("b", "x");
        TypeReference<Cache<String, Object>> type = new TypeReference<>() { };
        assertEquals(a2q("{'a':1,'b':'x'}"),
                mapper.writerFor(type).writeValueAsString(input));
    }

    @Test
    public void multimapWithDefaultTyping() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL)
                .build();
        Multimap<String, Object> input = ArrayListMultimap.create();
        input.put("a", 1);
        input.put("a", "x");
        TypeReference<Multimap<String, Object>> type = new TypeReference<>() { };
        String json = mapper.writerFor(type).writeValueAsString(input);
        assertEquals(a2q("['com.google.common.collect.ArrayListMultimap',{'a':[1,'x']}]"), json);
        assertEquals(input, mapper.readValue(json, type));
    }

    @Test
    public void rangeMapWithDefaultTyping() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL)
                .build();
        RangeMap<Integer, Object> input = TreeRangeMap.create();
        input.put(Range.closed(1, 5), "a");
        input.put(Range.open(10, 20), 42);
        TypeReference<RangeMap<Integer, Object>> type = new TypeReference<>() { };
        String json = mapper.writerFor(type).writeValueAsString(input);
        assertEquals(a2q("['com.google.common.collect.TreeRangeMap',{'[1..5]':'a','(10..20)':42}]"), json);
        RangeMap<Integer, Object> result = mapper.readValue(json, type);
        assertEquals(input.asMapOfRanges(), result.asMapOfRanges());
    }

    @Test
    public void tableWithExplicitRootType() {
        ObjectMapper mapper = mapperWithModule();
        Table<String, String, Object> input = HashBasedTable.create();
        input.put("r", "c", 1);
        TypeReference<Table<String, String, Object>> type = new TypeReference<>() { };
        assertEquals(a2q("{'r':{'c':1}}"),
                mapper.writerFor(type).writeValueAsString(input));
    }

    // Polymorphic values must not be serialized using static base type serializer

    @Test
    public void multimapWithPolymorphicValues() {
        ObjectMapper mapper = mapperWithModule();
        Multimap<String, Animal> input = ArrayListMultimap.create();
        input.put("a", new Dog());
        TypeReference<Multimap<String, Animal>> type = new TypeReference<>() { };
        assertEquals(a2q("{'a':["+DOG_JSON+"]}"),
                mapper.writerFor(type).writeValueAsString(input));
    }

    @Test
    public void cacheWithPolymorphicValues() {
        ObjectMapper mapper = mapperWithModule();
        Cache<String, Animal> input = CacheBuilder.newBuilder().build();
        input.put("a", new Dog());
        TypeReference<Cache<String, Animal>> type = new TypeReference<>() { };
        assertEquals(a2q("{'a':"+DOG_JSON+"}"),
                mapper.writerFor(type).writeValueAsString(input));
    }

    @Test
    public void rangeMapWithPolymorphicValues() {
        ObjectMapper mapper = mapperWithModule();
        RangeMap<Integer, Animal> input = TreeRangeMap.create();
        input.put(Range.closed(1, 5), new Dog());
        TypeReference<RangeMap<Integer, Animal>> type = new TypeReference<>() { };
        assertEquals(a2q("{'[1..5]':"+DOG_JSON+"}"),
                mapper.writerFor(type).writeValueAsString(input));
    }
}
