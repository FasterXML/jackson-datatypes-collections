package com.fasterxml.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.pojo.AddOp;
import com.fasterxml.jackson.datatype.guava.pojo.MathOp;
import com.fasterxml.jackson.datatype.guava.pojo.MulOp;
import com.google.common.base.Optional;
import com.google.common.collect.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.collect.TreeMultimap.create;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to verify handling of various {@link Multimap}s.
 *
 * @author steven@nesscomputing.com
 */
public class MultimapsTest extends ModuleTestBase
{
    // Test for issue #13 on github, provided by stevenschlansker
    public static enum MyEnum {
        YAY,
        BOO
    }

    // [Issue#41]
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    static class MultiMapWrapper {
        @JsonProperty
        Multimap<String, String> map = ArrayListMultimap.create();
    }

    static class MultiMapWithIgnores {
        @JsonIgnoreProperties({ "x", "y" })
        public Multimap<String, String> map = ArrayListMultimap.create();
        
        public MultiMapWithIgnores()
        {
            map.put("a", "foo");
            map.put("x", "bar");
        }
    }

    public static class ImmutableMultimapWrapper {

        private ImmutableMultimap<String, MathOp> multimap;

        public ImmutableMultimapWrapper() {
        }

        public ImmutableMultimapWrapper(ImmutableMultimap<String, MathOp> f) {
            this.multimap = f;
        }

        public ImmutableMultimap<String, MathOp> getMultimap() {
            return multimap;
        }

        public void setMultimap(ImmutableMultimap<String, MathOp> multimap) {
            this.multimap = multimap;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.multimap != null ? this.multimap.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ImmutableMultimapWrapper other = (ImmutableMultimapWrapper) obj;
            return !(this.multimap != other.multimap && (this.multimap == null || !this.multimap.equals(other.multimap)));
        }
    }

    //Sample class for testing multimaps single value option
    static class SampleMultiMapTest {
        public HashMultimap<String, String> map;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testMultimap() throws Exception
    {
        _testMultimap(TreeMultimap.create(), true,
                "{\"false\":[false],\"maybe\":[false,true],\"true\":[true]}");
        _testMultimap(LinkedListMultimap.create(), false,
                "{\"true\":[true],\"false\":[false],\"maybe\":[true,false]}");
        _testMultimap(LinkedHashMultimap.create(), false, null);
    }

    private void _testMultimap(Multimap<?,?> map0, boolean fullyOrdered, String EXPECTED) throws Exception
    {
        @SuppressWarnings("unchecked")
        Multimap<String,Boolean> map = (Multimap<String,Boolean>) map0;
        map.put("true", Boolean.TRUE);
        map.put("false", Boolean.FALSE);
        map.put("maybe", Boolean.TRUE);
        map.put("maybe", Boolean.FALSE);

        // Test that typed writes work
        if (EXPECTED != null) {
            String json =  MAPPER.writerFor(new TypeReference<Multimap<String, Boolean>>() {}).writeValueAsString(map);
            assertEquals(EXPECTED, json);
        }

        // And untyped too
        String serializedForm = MAPPER.writeValueAsString(map);

        if (EXPECTED != null) {
            assertEquals(EXPECTED, serializedForm);
        }

        // these seem to be order-sensitive as well, so only use for ordered-maps
        if (fullyOrdered) {
            assertEquals(map, MAPPER.<TreeMultimap<String, Boolean>>readValue(serializedForm, new TypeReference<TreeMultimap<String, Boolean>>() {}));
            assertEquals(map, create(MAPPER.<Multimap<String, Boolean>>readValue(serializedForm, new TypeReference<Multimap<String, Boolean>>() {})));
            assertEquals(map, create(MAPPER.<HashMultimap<String, Boolean>>readValue(serializedForm, new TypeReference<HashMultimap<String, Boolean>>() {})));
            assertEquals(map, create(MAPPER.<ImmutableMultimap<String, Boolean>>readValue(serializedForm, new TypeReference<ImmutableMultimap<String, Boolean>>() {})));
        }
    }

    @Test
    public void testMultimapIssue3() throws Exception
    {
        Multimap<String, String> m1 = TreeMultimap.create();
        m1.put("foo", "bar");
        m1.put("foo", "baz");
        m1.put("qux", "quux");
        ObjectMapper o = MAPPER;
        
        String t1 = o.writerFor(new TypeReference<TreeMultimap<String, String>>(){}).writeValueAsString(m1);
        Map<?,?> javaMap = o.readValue(t1, Map.class);
        assertEquals(2, javaMap.size());
        
        String t2 = o.writerFor(new TypeReference<Multimap<String, String>>(){}).writeValueAsString(m1);
        javaMap = o.readValue(t2, Map.class);
        assertEquals(2, javaMap.size());
        
        TreeMultimap<String, String> m2 = TreeMultimap.create();
        m2.put("foo", "bar");
        m2.put("foo", "baz");
        m2.put("qux", "quux");
        
        String t3 = o.writerFor(new TypeReference<TreeMultimap<String, String>>(){}).writeValueAsString(m2);
        javaMap = o.readValue(t3, Map.class);
        assertEquals(2, javaMap.size());
   
        String t4 = o.writerFor(new TypeReference<Multimap<String, String>>(){}).writeValueAsString(m2);
        javaMap = o.readValue(t4, Map.class);
        assertEquals(2, javaMap.size());
    }

    @Test
    public void testEnumKey() throws Exception
    {
        final TypeReference<TreeMultimap<MyEnum, Integer>> type = new TypeReference<TreeMultimap<MyEnum, Integer>>() {};
        final Multimap<MyEnum, Integer> map = TreeMultimap.create();

        map.put(MyEnum.YAY, 5);
        map.put(MyEnum.BOO, 2);

        final String serializedForm = MAPPER.writerFor(type).writeValueAsString(map);

        assertEquals(serializedForm, MAPPER.writeValueAsString(map));
        assertEquals(map, MAPPER.readValue(serializedForm, type));
    }

    // [Issue#41]
    @Test
    public void testEmptyMapExclusion() throws Exception
    {
        String json = MAPPER.writeValueAsString(new MultiMapWrapper());
        assertEquals("{}", json);
    }

    @Test
    public void testNullHandling() throws Exception
    {
        Multimap<String,Integer> input = ArrayListMultimap.create();
        input.put("empty", null);
        String json = MAPPER.writeValueAsString(input);
        assertEquals(aposToQuotes("{'empty':[null]}"), json);
    }

    // [datatypes-collections#27]:
    @Test
    public void testWithReferenceType() throws Exception
    {
        String json = "{\"a\" : [5.0, null, 6.0]}";
        ListMultimap<String, Optional<Double>> result = MAPPER.readValue(
            json, 
            new TypeReference<ListMultimap<String, Optional<Double>>>() {});

        assertEquals(3, result.size());
        assertEquals(ImmutableListMultimap.of(
                "a", Optional.of(5.0), 
                "a", Optional.absent(),
                "a", Optional.of(6.0)),
                result);
    }

    /*
    /**********************************************************************
    /* Unit tests for set-based multimaps
    /**********************************************************************
     */

    /*
    public void testTreeMultimap() throws IOException {
    }

    public void testForwardingSortedSetMultimap() throws IOException {

    }
    */

    @Test
    public void testImmutableSetMultimap() throws IOException {
        SetMultimap<String, String> map =
                _verifyMultiMapRead(new TypeReference<ImmutableSetMultimap<String, String>>() {
                });
        assertTrue(map instanceof ImmutableSetMultimap);
    }

    @Test
    public void testHashMultimap() throws IOException {
        SetMultimap<String, String> map =
                _verifyMultiMapRead(new TypeReference<HashMultimap<String, String>>() {
                });
        assertTrue(map instanceof HashMultimap);
    }

    @Test
    public void testLinkedHashMultimap() throws IOException {
        SetMultimap<String, String> map =
                _verifyMultiMapRead(new TypeReference<LinkedHashMultimap<String, String>>() {
                });
        assertTrue(map instanceof LinkedHashMultimap);
    }

    /*
    public void testForwardingSetMultimap() {
    }
    */

    @SuppressWarnings("unchecked")
    private SetMultimap<String, String> _verifyMultiMapRead(TypeReference<?> type)
        throws IOException
    {
        SetMultimap<String, String> map = (SetMultimap<String, String>) MAPPER
                .readValue( "{\"first\":[\"abc\",\"abc\",\"foo\"]," + "\"second\":[\"bar\"]}", type);
        assertEquals(3, map.size());
        assertTrue(map.containsEntry("first", "abc"));
        assertTrue(map.containsEntry("first", "foo"));
        assertTrue(map.containsEntry("second", "bar"));
        return map;
    }

    /*
    /**********************************************************************
    /* Unit tests for list-based multimaps
    /**********************************************************************
     */

    @Test
    public void testArrayListMultimap() throws IOException {
        ListMultimap<String, String> map =
                listBasedHelper(new TypeReference<ArrayListMultimap<String, String>>() {
                });
        assertTrue(map instanceof ArrayListMultimap);
    }

    @Test
    public void testLinkedListMultimap() throws IOException {
        ListMultimap<String, String> map =
                listBasedHelper(new TypeReference<LinkedListMultimap<String, String>>() {
                });
        assertTrue(map instanceof LinkedListMultimap);
    }

    @Test
    public void testMultimapWithIgnores() throws IOException {
        assertEquals("{\"map\":{\"a\":[\"foo\"]}}",
                MAPPER.writeValueAsString(new MultiMapWithIgnores()));
    }

    @SuppressWarnings("unchecked")
    private ListMultimap<String, String> listBasedHelper(TypeReference<?> type) throws IOException {
        ListMultimap<String, String> map = (ListMultimap<String, String>) MAPPER
                .readValue( "{\"first\":[\"abc\",\"abc\",\"foo\"]," + "\"second\":[\"bar\"]}", type);
        assertEquals(4, map.size());
        assertTrue(map.remove("first", "abc"));
        assertTrue(map.containsEntry("first", "abc"));
        assertTrue(map.containsEntry("first", "foo"));
        assertTrue(map.containsEntry("second", "bar"));
        return map;
    }

    @Test
    public void testIssue67() throws IOException
    {
        ImmutableSetMultimap<String, Integer> map = MAPPER.readValue(
            "{\"d\":[1,2],\"c\":[3,4],\"b\":[5,6],\"a\":[7,8]}",
            new TypeReference<ImmutableSetMultimap<String, Integer>>() {});
        assertTrue(map instanceof ImmutableSetMultimap);
        assertEquals(8, map.size());
        Iterator<Map.Entry<String, Integer>> iterator = map.entries().iterator();
        assertEquals(Maps.immutableEntry("d", 1), iterator.next());
        assertEquals(Maps.immutableEntry("d", 2), iterator.next());
        assertEquals(Maps.immutableEntry("c", 3), iterator.next());
        assertEquals(Maps.immutableEntry("c", 4), iterator.next());
        assertEquals(Maps.immutableEntry("b", 5), iterator.next());
        assertEquals(Maps.immutableEntry("b", 6), iterator.next());
        assertEquals(Maps.immutableEntry("a", 7), iterator.next());
        assertEquals(Maps.immutableEntry("a", 8), iterator.next());
    }

    // [Issue#25]
    @Test
    public void testDefaultSetMultiMap() throws IOException {
        @SuppressWarnings("unchecked")
        SetMultimap<String, String> map = (SetMultimap<String, String>) MAPPER
            .readValue( "{\"first\":[\"abc\",\"abc\",\"foo\"]," + "\"second\":[\"bar\"]}",
                SetMultimap.class);
        assertTrue(map instanceof LinkedHashMultimap);
    }

    @Test
    public void testPolymorphicValue() throws IOException {
        ImmutableMultimapWrapper input = new ImmutableMultimapWrapper(ImmutableMultimap.of("add", new AddOp(3, 2), "mul", new MulOp(4, 6)));

        String json = MAPPER.writeValueAsString(input);

        ImmutableMultimapWrapper output = MAPPER.readValue(json, ImmutableMultimapWrapper.class);
        assertEquals(input, output);        
    }

    @Test
    public void testFromSingleValue() throws Exception
    {
        ObjectMapper mapper = mapperWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        SampleMultiMapTest sampleTest = mapper.readValue("{\"map\":{\"test\":\"value\"}}",
               new TypeReference<SampleMultiMapTest>() { });
        
        assertEquals(1, sampleTest.map.get("test").size());
    }

    @Test
    public void testFromMultiValueWithSingleValueOptionEnabled() throws Exception
    {
        ObjectMapper mapper = mapperWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        
        SampleMultiMapTest sampleTest = mapper.readValue("{\"map\":{\"test\":\"val\",\"test1\":[\"val1\",\"val2\"]}}",
                new TypeReference<SampleMultiMapTest>() { });
        
        assertEquals(1, sampleTest.map.get("test").size());
        assertEquals(2, sampleTest.map.get("test1").size());

        // Make sure that our Value is still a String not [String]
        assertEquals(sampleTest.map.entries().iterator().next().getValue(), "val");
    }

    @Test
    public void testFromMultiValueWithNoSingleValueOptionEnabled() throws Exception
    {
        SampleMultiMapTest sampleTest = MAPPER.readValue("{\"map\":{\"test\":[\"val\"],\"test1\":[\"val1\",\"val2\"]}}",
                new TypeReference<SampleMultiMapTest>() { });
        
        assertEquals(1, sampleTest.map.get("test").size());
        assertEquals(2, sampleTest.map.get("test1").size());
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
    @Test
    public void testMultimapIssue96() throws Exception
    {
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
}
