package tools.jackson.datatype.pcollections;

import java.util.Arrays;
import java.util.Iterator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;

import org.pcollections.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for verifying that various PCollection types
 * work as expected.
 */
public class TestPCollections extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    static class Holder {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object value;

        public Holder() { }
        public Holder(Object v) {
            value = v;
        }
    }

    /*
    /**********************************************************************
    /* Unit tests for verifying handling in absence of module registration
    /**********************************************************************
     */
    
    /**
     * PCollections types can actually be serialized as regular collections, without
     * problems.
     */
    @Test
    public void withoutSerializers() throws Exception
    {
        PVector<Integer> list = TreePVector.from(Arrays.asList(1, 2, 3));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(list));

        PStack<String> stack = ConsPStack.singleton("def").plus("abc");
        assertEquals("[\"abc\",\"def\"]", MAPPER.writeValueAsString(stack));
    }

    // 11-Jul-2017, tatu: Seems pointless to verify this... ?
    /**
     * Deserialization will fail, however.
     */
    @Test
    public void withoutDeserializers() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue("[1,2,3]",
                    new TypeReference<PSequence<Integer>>() { });
            fail("Expected failure for missing deserializer");
        } catch (InvalidDefinitionException e) {
            _verifyImmutableException(e, PSequence.class);
        }

        try {
            mapper.readValue("[1,2,3]", new TypeReference<PSet<Integer>>() { });
            fail("Expected failure for missing deserializer");
        } catch (InvalidDefinitionException e) {
            _verifyImmutableException(e, PSet.class);
        }

        try {
            mapper.readValue("{\"a\":true,\"b\":false}", new TypeReference<PMap<Integer,Boolean>>() { });
            fail("Expected failure for missing deserializer");
        } catch (InvalidDefinitionException e) {
            _verifyImmutableException(e, PMap.class);
        }
    }

    private void _verifyImmutableException(InvalidDefinitionException e, Class<?> type) {
        // Exception changed a bit in 2.18.2, need to match
        //verifyException(e, "cannot find a deserializer");
        verifyException(e, "Cannot construct instance of ");
        verifyException(e, "No creators");
        verifyException(e, type.getName());
    }

    /*
    /**********************************************************************
    /* Unit tests for actual registered module
    /**********************************************************************
     */
    @Test
    public void pCollection() throws Exception
    {
        PCollection<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<PCollection<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(1), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(3), elements.next());
    }

    @Test
    public void pSequence() throws Exception
    {
        PSequence<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<PSequence<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(1), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(3), elements.next());
    }

    @Test
    public void pVector() throws Exception
    {
        PVector<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<PVector<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(1), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(3), elements.next());
    }

    @Test
    public void treePVector() throws Exception
    {
        TreePVector<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<TreePVector<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(1), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(3), elements.next());
    }

    @Test
    public void orderedPSet() throws Exception
    {
        OrderedPSet<Integer> set = MAPPER.readValue("[1,2,3]", new TypeReference<OrderedPSet<Integer>>() { });
        assertEquals(3, set.size());
        Iterator<Integer> elements = set.iterator();
        assertEquals(Integer.valueOf(1), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(3), elements.next());
    }

    @Test
    public void pStack() throws Exception
    {
        PStack<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<PStack<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(3), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(1), elements.next());
    }

    @Test
    public void consPStack() throws Exception
    {
        ConsPStack<Integer> list = MAPPER.readValue("[1,2,3]", new TypeReference<ConsPStack<Integer>>() { });
        assertEquals(3, list.size());
        Iterator<Integer> elements = list.iterator();
        assertEquals(Integer.valueOf(3), elements.next());
        assertEquals(Integer.valueOf(2), elements.next());
        assertEquals(Integer.valueOf(1), elements.next());
    }

    @Test
    public void pSet() throws Exception
    {
        PSet<Integer> set = MAPPER.readValue("[1,2,3]", new TypeReference<PSet<Integer>>() { });
        assertEquals(3, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    public void hashTreePSet() throws Exception
    {
        MapPSet<Integer> set = MAPPER.readValue("[1,2,3]", new TypeReference<MapPSet<Integer>>() { });
        assertEquals(3, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    public void pBag() throws Exception
    {
        PBag<Integer> set = MAPPER.readValue("[1,2,3,3]", new TypeReference<PBag<Integer>>() { });
        assertEquals(4, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    public void mapPBag() throws Exception
    {
        MapPBag<Integer> set = MAPPER.readValue("[1,2,3,3]", new TypeReference<MapPBag<Integer>>() { });
        assertEquals(4, set.size());
        assertTrue(set.contains(1));
        assertTrue(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    public void pMap() throws Exception
    {
        PMap<String, Integer> map = MAPPER.readValue("{\"a\":1,\"b\":2}", new TypeReference<PMap<String, Integer>>() { });
        assertEquals(2, map.size());
        assertEquals(Integer.valueOf(1), map.get("a"));
        assertEquals(Integer.valueOf(2), map.get("b"));
    }

    @Test
    public void hashPMap() throws Exception
    {
        HashPMap<String, Integer> map = MAPPER.readValue("{\"a\":1,\"b\":2}", new TypeReference<HashPMap<String, Integer>>() { });
        assertEquals(2, map.size());
        assertEquals(Integer.valueOf(1), map.get("a"));
        assertEquals(Integer.valueOf(2), map.get("b"));
    }

    @Test
    public void pSortedSet() throws Exception
    {
        PSortedSet<Integer> set = MAPPER.readValue("[3,1,2,1]", new TypeReference<PSortedSet<Integer>>() { });
        assertEquals(TreePSet.class, set.getClass());
        assertEquals(Arrays.asList(1, 2, 3), Arrays.asList(set.toArray()));
    }

    @Test
    public void treePSet() throws Exception
    {
        TreePSet<String> set = MAPPER.readValue("[\"c\",\"a\",\"b\"]", new TypeReference<TreePSet<String>>() { });
        assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(set.toArray()));
        assertEquals("a", set.first());
        assertEquals("c", set.last());
    }

    @Test
    public void pQueue() throws Exception
    {
        PQueue<Integer> queue = MAPPER.readValue("[1,2,3]", new TypeReference<PQueue<Integer>>() { });
        assertEquals(AmortizedPQueue.class, queue.getClass());
        assertEquals(Arrays.asList(1, 2, 3), Arrays.asList(queue.toArray()));
        assertEquals(Integer.valueOf(1), queue.peek());
    }

    @Test
    public void amortizedPQueue() throws Exception
    {
        AmortizedPQueue<Integer> queue = MAPPER.readValue("[1,2,3]", new TypeReference<AmortizedPQueue<Integer>>() { });
        assertEquals(Arrays.asList(1, 2, 3), Arrays.asList(queue.toArray()));
        assertEquals(Arrays.asList(2, 3), Arrays.asList(queue.minus().toArray()));
    }

    @Test
    public void pSortedMap() throws Exception
    {
        PSortedMap<String, Integer> map = MAPPER.readValue("{\"b\":2,\"c\":3,\"a\":1}", new TypeReference<PSortedMap<String, Integer>>() { });
        assertEquals(TreePMap.class, map.getClass());
        assertEquals(Arrays.asList("a", "b", "c"), Arrays.asList(map.keySet().toArray()));
        assertEquals(Arrays.asList(1, 2, 3), Arrays.asList(map.values().toArray()));
    }

    @Test
    public void treePMap() throws Exception
    {
        TreePMap<Integer, Boolean> map = MAPPER.readValue("{\"10\":true,\"2\":false}", new TypeReference<TreePMap<Integer, Boolean>>() { });
        assertEquals(Arrays.asList(2, 10), Arrays.asList(map.keySet().toArray()));
        assertEquals(Boolean.FALSE, map.get(2));
        assertEquals(Boolean.TRUE, map.get(10));
    }

    @Test
    public void orderedPMap() throws Exception
    {
        OrderedPMap<String, Integer> map = MAPPER.readValue("{\"c\":3,\"a\":1,\"b\":2}", new TypeReference<OrderedPMap<String, Integer>>() { });
        assertEquals(Arrays.asList("c", "a", "b"), Arrays.asList(map.keySet().toArray()));
        assertEquals(Arrays.asList(3, 1, 2), Arrays.asList(map.values().toArray()));
    }

    @Test
    public void newTypesRoundTrip() throws Exception
    {
        _verifyRoundTrip(TreePSet.from(Arrays.asList(3, 1, 2)),
                new TypeReference<TreePSet<Integer>>() { });
        _verifyRoundTrip(AmortizedPQueue.<Integer>empty().plus(1).plus(2),
                new TypeReference<AmortizedPQueue<Integer>>() { });
        _verifyRoundTrip(TreePMap.singleton("b", 2).plus("a", 1),
                new TypeReference<TreePMap<String, Integer>>() { });
        _verifyRoundTrip(OrderedPMap.<String, Integer>empty().plus("b", 2).plus("a", 1),
                new TypeReference<OrderedPMap<String, Integer>>() { });
    }

    private void _verifyRoundTrip(Object value, TypeReference<?> type) throws Exception
    {
        String json = MAPPER.writeValueAsString(value);
        Object result = MAPPER.readValue(json, type);
        assertEquals(value.getClass(), result.getClass());
        assertEquals(json, MAPPER.writeValueAsString(result));
    }

}
