package com.fasterxml.jackson.datatype.guava;

import com.google.common.cache.Cache;

import java.util.List;

class CacheTypeDeserTest {
    
    /*
    // Cache Deserializer not implemented yet
    public void testCacheDeserializationWithTypeReference() throws Exception {
        final TypeReference<Cache<MyEnum, Integer>> type = new TypeReference<Cache<MyEnum, Integer>>() {};
        final Cache<MyEnum, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put(MyEnum.YAY, 5);
        cache.put(MyEnum.BOO, 2);

        final String serializedForm = MAPPER.writerFor(type).writeValueAsString(cache);
        
        assertEquals(cache, MAPPER.readValue(serializedForm, type));
    }
     */


    static class SampleCacheTest {
        public Cache<String, List<String>> cache;
    }
    
    /*
    // DESER

    public void testFromSingleValue() throws Exception {
        ObjectMapper mapper = mapperWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        SampleCacheTest sampleTest = mapper.readValue("{\"cache\":{\"test\":\"value\"}}",
            new TypeReference<SampleCacheTest>() {});

        List<String> output = sampleTest.cache.getIfPresent("test");
        assertNotNull(output);
        assertEquals(1, output.size());
        assertEquals("value", output.get(0));
    }

    * public void testFromMultiValueWithSingleValueOptionEnabled() throws Exception {
        ObjectMapper mapper = mapperWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        TestMultimaps.SampleMultiMapTest sampleTest = mapper.readValue("{\"map\":{\"test\":\"val\",\"test1\":[\"val1\",\"val2\"]}}",
            new TypeReference<TestMultimaps.SampleMultiMapTest>() {});

        assertEquals(1, sampleTest.map.get("test").size());
        assertEquals(2, sampleTest.map.get("test1").size());
        assertEquals(sampleTest.map.entries().iterator().next().getValue(), "val");
    }

    public void testFromMultiValueWithSingleValueOptionDisabled() throws Exception {
        ObjectMapper mapper = mapperWithModule()
            .disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        try {
            mapper.readValue("{\"map\":{\"test\":\"val\",\"test1\":[\"val1\",\"val2\"]}}",
                new TypeReference<TestMultimaps.SampleMultiMapTest>() {});
        } catch (JsonMappingException e) {
            verifyException(e, "Expecting START_ARRAY to start ", "found VALUE_STRING");
        }
    }

    public void testFromMultiValueWithNoSingleValueOptionEnabled() throws Exception {
        TestMultimaps.SampleMultiMapTest sampleTest = MAPPER.readValue("{\"map\":{\"test\":[\"val\"],\"test1\":[\"val1\",\"val2\"]}}",
            new TypeReference<TestMultimaps.SampleMultiMapTest>() {});

        assertEquals(1, sampleTest.map.get("test").size());
        assertEquals(2, sampleTest.map.get("test1").size());
    } 
    * */


    /*}

    static class Pojo96Properties {
        @JsonProperty
        ArrayListMultimap<Long, Integer> multimap;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public Pojo96Properties(@JsonProperty("multimap") ArrayListMultimap<Long, Integer> multimap) {
            this.multimap = multimap;
        }
    }

    static class Pojo96Delegating {
        ArrayListMultimap<Long, Integer> multimap;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public Pojo96Delegating(ArrayListMultimap<Long, Integer> multimap) {
            this.multimap = multimap;
        }

        @JsonValue
        ArrayListMultimap<Long, Integer> mapValue() {
            return multimap;
        }
    }

    // [datatype-collections#96]
    public void testMultimapIssue96() throws Exception {
        // First the original, properties case:
        final ArrayListMultimap<Long, Integer> multimap = ArrayListMultimap.create();
        multimap.put(1L, 1);
        multimap.put(1L, 2);

        String json = MAPPER.writeValueAsString(new Pojo96Properties(multimap));
        Pojo96Properties result1 = MAPPER.readValue(json, Pojo96Properties.class);
        assertEquals(2, result1.multimap.size());
        assertEquals(Collections.singleton(1L), result1.multimap.keySet());

        // Then delegating
        json = MAPPER.writeValueAsString(new Pojo96Delegating(multimap));
//        System.out.println(json);
        Pojo96Delegating result2 = MAPPER.readValue(json, Pojo96Delegating.class);
        assertEquals(2, result2.multimap.size());
        assertEquals(Collections.singleton(1L), result2.multimap.keySet());
    }
    

    static class Pojo96Properties {
        @JsonProperty
        ArrayListMultimap<Long, Integer> multimap;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public Pojo96Properties(@JsonProperty("multimap") ArrayListMultimap<Long, Integer> multimap) {
            this.multimap = multimap;
        }
    }

    static class Pojo96Delegating {
        ArrayListMultimap<Long, Integer> multimap;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public Pojo96Delegating(ArrayListMultimap<Long, Integer> multimap) {
            this.multimap = multimap;
        }

        @JsonValue
        ArrayListMultimap<Long, Integer> mapValue() {
            return multimap;
        }
    }

    // [datatype-collections#96]
    public void testMultimapIssue96() throws Exception {
        // First the original, properties case:
        final ArrayListMultimap<Long, Integer> multimap = ArrayListMultimap.create();
        multimap.put(1L, 1);
        multimap.put(1L, 2);

        String json = MAPPER.writeValueAsString(new Pojo96Properties(multimap));
        Pojo96Properties result1 = MAPPER.readValue(json, Pojo96Properties.class);
        assertEquals(2, result1.multimap.size());
        assertEquals(Collections.singleton(1L), result1.multimap.keySet());

        // Then delegating
        json = MAPPER.writeValueAsString(new Pojo96Delegating(multimap));
//        System.out.println(json);
        Pojo96Delegating result2 = MAPPER.readValue(json, Pojo96Delegating.class);
        assertEquals(2, result2.multimap.size());
        assertEquals(Collections.singleton(1L), result2.multimap.keySet());
    }
     */
}
