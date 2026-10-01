package tools.jackson.datatype.pcollections;

import java.util.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.pcollections.*;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests verifying that PCollections types round-trip when polymorphic
 * default typing is enabled.
 */
public class DefaultTypingTest extends ModuleTestBase
{
    // Non-final POJO, so it gets type information with default typing
    // when used as an {@code Object}-typed element value
    static class Point {
        public int x, y;

        protected Point() { }
        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point)) return false;
            Point other = (Point) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() { return 31 * x + y; }

        @Override
        public String toString() { return "Point(" + x + "," + y + ")"; }
    }

    // POJO with properties declared using PCollections interface types
    static class InterfaceTypedHolder {
        public PCollection<String> collection;
        public PSequence<String> sequence;
        public PVector<String> vector;
        public PVector<Object> objectVector;
        public PStack<Integer> stack;
        public PSet<String> set;
        public PSet<Object> objectSet;
        public PSet<String> orderedSet; // holds OrderedPSet
        public PBag<String> bag;
        public PSortedSet<String> sortedSet;
        public PQueue<String> queue;
        public PMap<String, Integer> map;
        public PMap<String, Object> objectMap;
        public PSortedMap<String, Integer> sortedMap;
    }

    // POJO with properties declared using concrete PCollections types
    static class ConcreteTypedHolder {
        public TreePVector<String> vector;
        public ConsPStack<Integer> stack;
        public MapPSet<String> set;
        public OrderedPSet<String> orderedSet;
        public MapPBag<String> bag;
        public TreePSet<String> sortedSet;
        public AmortizedPQueue<String> queue;
        public HashPMap<String, Integer> map;
        public TreePMap<String, Integer> sortedMap;
        public OrderedPMap<String, Object> orderedMap;
    }

    // POJO with {@code Object}-typed properties holding PCollections values
    static class ObjectTypedHolder {
        public Object vector;
        public Object stack;
        public Object set;
        public Object orderedSet;
        public Object bag;
        public Object sortedSet;
        public Object queue;
        public Object map;
        public Object sortedMap;
        public Object orderedMap;
    }

    /*
    /**********************************************************************
    /* Root-level values
    /**********************************************************************
     */

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class,
            names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE", "NON_CONCRETE_AND_ARRAYS" })
    public void rootLevelInterfaceTypes(DefaultTyping typing) throws Exception
    {
        final ObjectMapper mapper = _mapper(typing);

        _rootRoundTrip(mapper, TreePVector.from(Arrays.asList("a", "b", "c")),
                new TypeReference<PCollection<String>>() { }, TreePVector.class);
        _rootRoundTrip(mapper, TreePVector.from(Arrays.asList("a", "b", "c")),
                new TypeReference<PSequence<String>>() { }, TreePVector.class);
        _rootRoundTrip(mapper, TreePVector.from(Arrays.asList("a", "b", "c")),
                new TypeReference<PVector<String>>() { }, TreePVector.class);
        _rootRoundTrip(mapper, ConsPStack.from(Arrays.asList(1, 2, 3)),
                new TypeReference<PStack<Integer>>() { }, ConsPStack.class);
        _rootRoundTrip(mapper, HashTreePSet.from(Arrays.asList("a", "b", "c")),
                new TypeReference<PSet<String>>() { }, MapPSet.class);
        _rootRoundTrip(mapper, OrderedPSet.from(Arrays.asList("c", "a", "b")),
                new TypeReference<PSet<String>>() { }, OrderedPSet.class);
        _rootRoundTrip(mapper, HashTreePBag.from(Arrays.asList("a", "b", "a")),
                new TypeReference<PBag<String>>() { }, MapPBag.class);
        _rootRoundTrip(mapper, TreePSet.from(Arrays.asList("c", "a", "b")),
                new TypeReference<PSortedSet<String>>() { }, TreePSet.class);
        _rootRoundTrip(mapper, AmortizedPQueue.<String>empty().plus("a").plus("b").plus("c"),
                new TypeReference<PQueue<String>>() { }, AmortizedPQueue.class);
        _rootRoundTrip(mapper, HashTreePMap.<String, Integer>empty().plus("a", 1).plus("b", 2),
                new TypeReference<PMap<String, Integer>>() { }, HashPMap.class);
        _rootRoundTrip(mapper, TreePMap.<String, Integer>empty().plus("b", 2).plus("a", 1),
                new TypeReference<PSortedMap<String, Integer>>() { }, TreePMap.class);
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class,
            names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE", "NON_CONCRETE_AND_ARRAYS" })
    public void rootLevelConcreteTypes(DefaultTyping typing) throws Exception
    {
        final ObjectMapper mapper = _mapper(typing);

        _rootRoundTrip(mapper, TreePVector.from(Arrays.asList("a", "b", "c")),
                new TypeReference<TreePVector<String>>() { }, TreePVector.class);
        _rootRoundTrip(mapper, ConsPStack.from(Arrays.asList(1, 2, 3)),
                new TypeReference<ConsPStack<Integer>>() { }, ConsPStack.class);
        _rootRoundTrip(mapper, HashTreePSet.from(Arrays.asList("a", "b", "c")),
                new TypeReference<MapPSet<String>>() { }, MapPSet.class);
        _rootRoundTrip(mapper, OrderedPSet.from(Arrays.asList("c", "a", "b")),
                new TypeReference<OrderedPSet<String>>() { }, OrderedPSet.class);
        _rootRoundTrip(mapper, HashTreePBag.from(Arrays.asList("a", "b", "a")),
                new TypeReference<MapPBag<String>>() { }, MapPBag.class);
        _rootRoundTrip(mapper, TreePSet.from(Arrays.asList("c", "a", "b")),
                new TypeReference<TreePSet<String>>() { }, TreePSet.class);
        _rootRoundTrip(mapper, AmortizedPQueue.<String>empty().plus("a").plus("b").plus("c"),
                new TypeReference<AmortizedPQueue<String>>() { }, AmortizedPQueue.class);
        _rootRoundTrip(mapper, HashTreePMap.<String, Integer>empty().plus("a", 1).plus("b", 2),
                new TypeReference<HashPMap<String, Integer>>() { }, HashPMap.class);
        _rootRoundTrip(mapper, TreePMap.<String, Integer>empty().plus("b", 2).plus("a", 1),
                new TypeReference<TreePMap<String, Integer>>() { }, TreePMap.class);
        _rootRoundTrip(mapper, OrderedPMap.<String, Integer>empty().plus("b", 2).plus("a", 1),
                new TypeReference<OrderedPMap<String, Integer>>() { }, OrderedPMap.class);
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class,
            names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE", "NON_CONCRETE_AND_ARRAYS" })
    public void rootLevelObjectElements(DefaultTyping typing) throws Exception
    {
        final ObjectMapper mapper = _mapper(typing);

        _rootRoundTrip(mapper, TreePVector.<Object>from(Arrays.asList("a", 1, true, new Point(1, 2))),
                new TypeReference<PVector<Object>>() { }, TreePVector.class);
        _rootRoundTrip(mapper, ConsPStack.<Object>from(Arrays.asList("a", 1, new Point(1, 2))),
                new TypeReference<PStack<Object>>() { }, ConsPStack.class);
        _rootRoundTrip(mapper, HashTreePSet.<Object>from(Arrays.asList("a", 1, new Point(1, 2))),
                new TypeReference<PSet<Object>>() { }, MapPSet.class);
        _rootRoundTrip(mapper, OrderedPSet.<Object>from(Arrays.asList("a", 1, new Point(1, 2))),
                new TypeReference<PSet<Object>>() { }, OrderedPSet.class);
        _rootRoundTrip(mapper, HashTreePBag.<Object>from(Arrays.asList("a", 1, new Point(1, 2), "a")),
                new TypeReference<PBag<Object>>() { }, MapPBag.class);
        _rootRoundTrip(mapper, AmortizedPQueue.<Object>empty().plus("a").plus(1).plus(new Point(1, 2)),
                new TypeReference<PQueue<Object>>() { }, AmortizedPQueue.class);
        _rootRoundTrip(mapper, HashTreePMap.<String, Object>empty()
                        .plus("a", 1).plus("b", "x").plus("c", new Point(1, 2)),
                new TypeReference<PMap<String, Object>>() { }, HashPMap.class);
        _rootRoundTrip(mapper, TreePMap.<String, Object>empty()
                        .plus("a", 1).plus("b", "x").plus("c", new Point(1, 2)),
                new TypeReference<PSortedMap<String, Object>>() { }, TreePMap.class);
        _rootRoundTrip(mapper, OrderedPMap.<String, Object>empty()
                        .plus("c", new Point(1, 2)).plus("a", 1).plus("b", "x"),
                new TypeReference<OrderedPMap<String, Object>>() { }, OrderedPMap.class);
        // nested PCollection as Object-typed element value
        _rootRoundTrip(mapper, TreePVector.<Object>from(Arrays.asList("a",
                        TreePVector.from(Arrays.asList("b", "c")))),
                new TypeReference<PVector<Object>>() { }, TreePVector.class);
    }

    /*
    /**********************************************************************
    /* POJO properties
    /**********************************************************************
     */

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class,
            names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE", "NON_CONCRETE_AND_ARRAYS" })
    public void pojoWithInterfaceTypedProperties(DefaultTyping typing) throws Exception
    {
        final ObjectMapper mapper = _mapper(typing);

        InterfaceTypedHolder input = new InterfaceTypedHolder();
        input.collection = TreePVector.from(Arrays.asList("a", "b"));
        input.sequence = ConsPStack.from(Arrays.asList("c", "d"));
        input.vector = TreePVector.from(Arrays.asList("a", "b", "c"));
        input.objectVector = TreePVector.<Object>from(Arrays.asList("a", 1, true, new Point(3, 4)));
        input.stack = ConsPStack.from(Arrays.asList(1, 2, 3));
        input.set = HashTreePSet.from(Arrays.asList("a", "b"));
        input.objectSet = HashTreePSet.<Object>from(Arrays.asList("a", 2, new Point(5, 6)));
        input.orderedSet = OrderedPSet.from(Arrays.asList("z", "y", "x"));
        input.bag = HashTreePBag.from(Arrays.asList("a", "a", "b"));
        input.sortedSet = TreePSet.from(Arrays.asList("c", "a", "b"));
        input.queue = AmortizedPQueue.<String>empty().plus("q1").plus("q2");
        input.map = HashTreePMap.<String, Integer>empty().plus("a", 1).plus("b", 2);
        input.objectMap = HashTreePMap.<String, Object>empty()
                .plus("a", 1).plus("b", "x").plus("c", new Point(7, 8));
        input.sortedMap = TreePMap.<String, Integer>empty().plus("b", 2).plus("a", 1);

        String json = mapper.writeValueAsString(input);
        InterfaceTypedHolder result = mapper.readValue(json, InterfaceTypedHolder.class);

        // Note: values written with type ids, so concrete type is preserved even
        // where it differs from the "default" one (PSequence as ConsPStack)
        _assertSame(input.collection, result.collection, TreePVector.class, json);
        _assertSame(input.sequence, result.sequence, ConsPStack.class, json);
        _assertSame(input.vector, result.vector, TreePVector.class, json);
        _assertSame(input.objectVector, result.objectVector, TreePVector.class, json);
        _assertSame(input.stack, result.stack, ConsPStack.class, json);
        _assertSame(input.set, result.set, MapPSet.class, json);
        _assertSame(input.objectSet, result.objectSet, MapPSet.class, json);
        _assertSame(input.orderedSet, result.orderedSet, OrderedPSet.class, json);
        _assertSame(input.bag, result.bag, MapPBag.class, json);
        _assertSame(input.sortedSet, result.sortedSet, TreePSet.class, json);
        _assertSame(input.queue, result.queue, AmortizedPQueue.class, json);
        _assertSame(input.map, result.map, HashPMap.class, json);
        _assertSame(input.objectMap, result.objectMap, HashPMap.class, json);
        _assertSame(input.sortedMap, result.sortedMap, TreePMap.class, json);
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class,
            names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE", "NON_CONCRETE_AND_ARRAYS" })
    public void pojoWithConcreteTypedProperties(DefaultTyping typing) throws Exception
    {
        final ObjectMapper mapper = _mapper(typing);

        ConcreteTypedHolder input = new ConcreteTypedHolder();
        input.vector = TreePVector.from(Arrays.asList("a", "b", "c"));
        input.stack = ConsPStack.from(Arrays.asList(1, 2, 3));
        input.set = HashTreePSet.from(Arrays.asList("a", "b"));
        input.orderedSet = OrderedPSet.from(Arrays.asList("z", "y", "x"));
        input.bag = HashTreePBag.from(Arrays.asList("a", "a", "b"));
        input.sortedSet = TreePSet.from(Arrays.asList("c", "a", "b"));
        input.queue = AmortizedPQueue.<String>empty().plus("q1").plus("q2");
        input.map = HashTreePMap.<String, Integer>empty().plus("a", 1).plus("b", 2);
        input.sortedMap = TreePMap.<String, Integer>empty().plus("b", 2).plus("a", 1);
        input.orderedMap = OrderedPMap.<String, Object>empty()
                .plus("c", new Point(1, 2)).plus("a", 1).plus("b", "x");

        String json = mapper.writeValueAsString(input);
        ConcreteTypedHolder result = mapper.readValue(json, ConcreteTypedHolder.class);

        _assertSame(input.vector, result.vector, TreePVector.class, json);
        _assertSame(input.stack, result.stack, ConsPStack.class, json);
        _assertSame(input.set, result.set, MapPSet.class, json);
        _assertSame(input.orderedSet, result.orderedSet, OrderedPSet.class, json);
        _assertSame(input.bag, result.bag, MapPBag.class, json);
        _assertSame(input.sortedSet, result.sortedSet, TreePSet.class, json);
        _assertSame(input.queue, result.queue, AmortizedPQueue.class, json);
        _assertSame(input.map, result.map, HashPMap.class, json);
        _assertSame(input.sortedMap, result.sortedMap, TreePMap.class, json);
        _assertSame(input.orderedMap, result.orderedMap, OrderedPMap.class, json);
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class,
            names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE", "NON_CONCRETE_AND_ARRAYS",
                    "JAVA_LANG_OBJECT" })
    public void pojoWithObjectTypedProperties(DefaultTyping typing) throws Exception
    {
        final ObjectMapper mapper = _mapper(typing);

        ObjectTypedHolder input = new ObjectTypedHolder();
        input.vector = TreePVector.<Object>from(Arrays.asList("a", 1, new Point(1, 2)));
        input.stack = ConsPStack.from(Arrays.asList(1, 2, 3));
        input.set = HashTreePSet.from(Arrays.asList("a", "b"));
        input.orderedSet = OrderedPSet.from(Arrays.asList("z", "y", "x"));
        input.bag = HashTreePBag.from(Arrays.asList("a", "a", "b"));
        input.sortedSet = TreePSet.from(Arrays.asList("c", "a", "b"));
        input.queue = AmortizedPQueue.<String>empty().plus("q1").plus("q2");
        input.map = HashTreePMap.<String, Object>empty().plus("a", 1).plus("p", new Point(3, 4));
        input.sortedMap = TreePMap.<String, Integer>empty().plus("b", 2).plus("a", 1);
        input.orderedMap = OrderedPMap.<String, Integer>empty().plus("b", 2).plus("a", 1);

        String json = mapper.writeValueAsString(input);
        ObjectTypedHolder result = mapper.readValue(json, ObjectTypedHolder.class);

        _assertSame(input.vector, result.vector, TreePVector.class, json);
        _assertSame(input.stack, result.stack, ConsPStack.class, json);
        _assertSame(input.set, result.set, MapPSet.class, json);
        _assertSame(input.orderedSet, result.orderedSet, OrderedPSet.class, json);
        _assertSame(input.bag, result.bag, MapPBag.class, json);
        _assertSame(input.sortedSet, result.sortedSet, TreePSet.class, json);
        _assertSame(input.queue, result.queue, AmortizedPQueue.class, json);
        _assertSame(input.map, result.map, HashPMap.class, json);
        _assertSame(input.sortedMap, result.sortedMap, TreePMap.class, json);
        _assertSame(input.orderedMap, result.orderedMap, OrderedPMap.class, json);
    }

    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    private ObjectMapper _mapper(DefaultTyping typing) {
        return builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing)
                .build();
    }

    private <T> void _rootRoundTrip(ObjectMapper mapper, T value, TypeReference<T> type,
            Class<?> expectedClass) throws Exception
    {
        String json = mapper.writerFor(type).writeValueAsString(value);
        T result = mapper.readValue(json, type);
        _assertSame(value, result, expectedClass, json);
    }

    private void _assertSame(Object expected, Object actual, Class<?> expectedClass,
            String json)
    {
        assertNotNull(actual, "null result from JSON: " + json);
        assertEquals(expectedClass, actual.getClass(), "Wrong type from JSON: " + json);
        // ConsPStack is built by pushing elements in JSON order, so contents come
        // back reversed (same as without default typing; see TestPCollections.consPStack())
        if (expected instanceof PStack<?>) {
            List<?> reversed = new ArrayList<>((Collection<?>) expected);
            Collections.reverse(reversed);
            assertEquals(reversed, actual, "JSON: " + json);
            return;
        }
        // AmortizedPQueue does not implement equals(); compare contents in order
        if (expected instanceof Queue<?>) {
            assertEquals(new ArrayList<>((Collection<?>) expected),
                    new ArrayList<>((Collection<?>) actual), "JSON: " + json);
        } else {
            assertEquals(expected, actual, "JSON: " + json);
        }
        // and for ordered/sorted types, verify iteration order too
        if (expected instanceof List<?> || expected instanceof SortedSet<?>
                || expected instanceof OrderedPSet<?>) {
            assertEquals(new ArrayList<>((Collection<?>) expected),
                    new ArrayList<>((Collection<?>) actual), "JSON: " + json);
        }
        if (expected instanceof SortedMap<?, ?> || expected instanceof OrderedPMap<?, ?>) {
            assertEquals(new ArrayList<>(((Map<?, ?>) expected).entrySet()),
                    new ArrayList<>(((Map<?, ?>) actual).entrySet()), "JSON: " + json);
        }
    }
}
