package tools.jackson.datatype.guava;

import java.util.Arrays;
import java.util.Objects;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.*;
import com.google.common.net.HostAndPort;
import com.google.common.net.InternetDomainName;
import com.google.common.primitives.ImmutableDoubleArray;
import com.google.common.primitives.ImmutableIntArray;
import com.google.common.primitives.ImmutableLongArray;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for round-tripping Guava types with polymorphic default typing enabled,
 * both as root values and as POJO properties declared using interface types.
 */
public class DefaultTypingTest extends ModuleTestBase
{
    /*
    /**********************************************************************
    /* POJOs
    /**********************************************************************
     */

    static class ImmutableCollections {
        public ImmutableList<String> list;
        public ImmutableSet<Integer> set;
        public ImmutableSortedSet<String> sortedSet;
        public ImmutableMultiset<String> multiset;
        public ImmutableSortedMultiset<String> sortedMultiset;
        public ImmutableMap<String, Integer> map;
        public ImmutableBiMap<String, Integer> biMap;
        public ImmutableSortedMap<String, Integer> sortedMap;
        public ImmutableList<Object> objectList;
        public ImmutableMap<String, Object> objectMap;
        public Object untypedList;
        public Object untypedMap;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ImmutableCollections other)) return false;
            return Objects.equals(list, other.list)
                    && Objects.equals(set, other.set)
                    && Objects.equals(sortedSet, other.sortedSet)
                    && Objects.equals(multiset, other.multiset)
                    && Objects.equals(sortedMultiset, other.sortedMultiset)
                    && Objects.equals(map, other.map)
                    && Objects.equals(biMap, other.biMap)
                    && Objects.equals(sortedMap, other.sortedMap)
                    && Objects.equals(objectList, other.objectList)
                    && Objects.equals(objectMap, other.objectMap)
                    && Objects.equals(untypedList, other.untypedList)
                    && Objects.equals(untypedMap, other.untypedMap);
        }

        @Override
        public int hashCode() { return Objects.hash(list, set, map); }

        @Override
        public String toString() {
            return "ImmutableCollections{list=" + list + ", set=" + set + ", sortedSet=" + sortedSet
                    + ", multiset=" + multiset + ", sortedMultiset=" + sortedMultiset
                    + ", map=" + map + ", biMap=" + biMap + ", sortedMap=" + sortedMap
                    + ", objectList=" + objectList + ", objectMap=" + objectMap
                    + ", untypedList=" + untypedList + ", untypedMap=" + untypedMap + "}";
        }
    }

    static class Multisets {
        public Multiset<String> multiset;
        public Multiset<String> treeMultiset;
        public SortedMultiset<String> sortedMultiset;
        public HashMultiset<String> hashMultiset;
        public Multiset<Object> objectMultiset;
        public Object untyped;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Multisets other)) return false;
            return Objects.equals(multiset, other.multiset)
                    && Objects.equals(treeMultiset, other.treeMultiset)
                    && Objects.equals(sortedMultiset, other.sortedMultiset)
                    && Objects.equals(hashMultiset, other.hashMultiset)
                    && Objects.equals(objectMultiset, other.objectMultiset)
                    && Objects.equals(untyped, other.untyped);
        }

        @Override
        public int hashCode() { return Objects.hash(multiset, treeMultiset); }

        @Override
        public String toString() {
            return "Multisets{multiset=" + multiset + ", treeMultiset=" + treeMultiset
                    + ", sortedMultiset=" + sortedMultiset + ", hashMultiset=" + hashMultiset
                    + ", objectMultiset=" + objectMultiset + ", untyped=" + untyped + "}";
        }
    }

    static class Multimaps {
        public Multimap<String, Integer> arrayList;
        public Multimap<String, Integer> hash;
        public ListMultimap<String, Integer> linkedList;
        public ListMultimap<String, Integer> immutableList;
        public SetMultimap<String, Integer> immutableSet;
        public SortedSetMultimap<String, Integer> tree;
        public Multimap<String, Object> objectValues;
        public Object untyped;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Multimaps other)) return false;
            return Objects.equals(arrayList, other.arrayList)
                    && Objects.equals(hash, other.hash)
                    && Objects.equals(linkedList, other.linkedList)
                    && Objects.equals(immutableList, other.immutableList)
                    && Objects.equals(immutableSet, other.immutableSet)
                    && Objects.equals(tree, other.tree)
                    && Objects.equals(objectValues, other.objectValues)
                    && Objects.equals(untyped, other.untyped);
        }

        @Override
        public int hashCode() { return Objects.hash(arrayList, hash); }

        @Override
        public String toString() {
            return "Multimaps{arrayList=" + arrayList + ", hash=" + hash + ", linkedList=" + linkedList
                    + ", immutableList=" + immutableList + ", immutableSet=" + immutableSet
                    + ", tree=" + tree + ", objectValues=" + objectValues + ", untyped=" + untyped + "}";
        }
    }

    static class Tables {
        public Table<String, Integer, String> hashBased;
        public Table<String, Integer, String> treeBased;
        public Table<String, Integer, String> immutable;
        public Table<String, String, Object> objectValues;
        public Object untyped;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Tables other)) return false;
            return Objects.equals(hashBased, other.hashBased)
                    && Objects.equals(treeBased, other.treeBased)
                    && Objects.equals(immutable, other.immutable)
                    && Objects.equals(objectValues, other.objectValues)
                    && Objects.equals(untyped, other.untyped);
        }

        @Override
        public int hashCode() { return Objects.hash(hashBased, treeBased); }

        @Override
        public String toString() {
            return "Tables{hashBased=" + hashBased + ", treeBased=" + treeBased
                    + ", immutable=" + immutable + ", objectValues=" + objectValues
                    + ", untyped=" + untyped + "}";
        }
    }

    static class Ranges {
        public RangeMap<Integer, String> rangeMap;
        public RangeMap<Integer, String> immutableRangeMap;
        public RangeMap<Integer, Object> objectRangeMap;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Ranges other)) return false;
            return Objects.equals(rangeMap, other.rangeMap)
                    && Objects.equals(immutableRangeMap, other.immutableRangeMap)
                    && Objects.equals(objectRangeMap, other.objectRangeMap);
        }

        @Override
        public int hashCode() { return Objects.hash(rangeMap, objectRangeMap); }

        @Override
        public String toString() {
            return "Ranges{rangeMap=" + rangeMap + ", immutableRangeMap=" + immutableRangeMap
                    + ", objectRangeMap=" + objectRangeMap + "}";
        }
    }

    // NOTE: only declared with concrete (final) types: as `Object` these fail
    //   since serializers do not implement `serializeWithType()`
    static class PrimitiveArrays {
        public ImmutableIntArray ints;
        public ImmutableLongArray longs;
        public ImmutableDoubleArray doubles;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof PrimitiveArrays other)) return false;
            return Objects.equals(ints, other.ints)
                    && Objects.equals(longs, other.longs)
                    && Objects.equals(doubles, other.doubles);
        }

        @Override
        public int hashCode() { return Objects.hash(ints, longs, doubles); }

        @Override
        public String toString() {
            return "PrimitiveArrays{ints=" + ints + ", longs=" + longs + ", doubles=" + doubles + "}";
        }
    }

    static class Scalars {
        public HostAndPort hostAndPort;
        public InternetDomainName domainName;
        public Object untypedHostAndPort;
        public Object untypedDomainName;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Scalars other)) return false;
            return Objects.equals(hostAndPort, other.hostAndPort)
                    && Objects.equals(domainName, other.domainName)
                    && Objects.equals(untypedHostAndPort, other.untypedHostAndPort)
                    && Objects.equals(untypedDomainName, other.untypedDomainName);
        }

        @Override
        public int hashCode() { return Objects.hash(hostAndPort, domainName); }

        @Override
        public String toString() {
            return "Scalars{hostAndPort=" + hostAndPort + ", domainName=" + domainName
                    + ", untypedHostAndPort=" + untypedHostAndPort
                    + ", untypedDomainName=" + untypedDomainName + "}";
        }
    }

    static class CacheHolder {
        public Cache<String, Integer> cache;
        public Cache<String, Object> objectCache;
    }

    /*
    /**********************************************************************
    /* Helpers
    /**********************************************************************
     */

    private ObjectMapper mapper(DefaultTyping typing) {
        return builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing)
                .build();
    }

    private <T> T roundTrip(ObjectMapper mapper, T value, TypeReference<T> type) {
        String json = mapper.writerFor(type).writeValueAsString(value);
        T result = mapper.readValue(json, type);
        assertEquals(value, result, "Round-trip failed for JSON: " + json);
        return result;
    }

    private <T> T roundTrip(ObjectMapper mapper, T value, Class<T> type) {
        String json = mapper.writerFor(type).writeValueAsString(value);
        T result = mapper.readValue(json, type);
        assertEquals(value, result, "Round-trip failed for JSON: " + json);
        return result;
    }

    // Root value declared as `Object`: type id must always be written and used
    private Object roundTripAsObject(ObjectMapper mapper, Object value) {
        String json = mapper.writerFor(Object.class).writeValueAsString(value);
        Object result = mapper.readValue(json, Object.class);
        assertEquals(value, result, "Round-trip (as Object) failed for JSON: " + json);
        return result;
    }

    /*
    /**********************************************************************
    /* Tests: immutable collections
    /**********************************************************************
     */

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testImmutableCollectionsRoot(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);

        roundTrip(mapper, ImmutableList.of("a", "b", "c"),
                new TypeReference<ImmutableList<String>>() { });
        roundTrip(mapper, ImmutableSet.of(1, 2, 3),
                new TypeReference<ImmutableSet<Integer>>() { });
        roundTrip(mapper, ImmutableSortedSet.of("c", "a", "b"),
                new TypeReference<ImmutableSortedSet<String>>() { });
        roundTrip(mapper, ImmutableMultiset.of("a", "a", "b"),
                new TypeReference<ImmutableMultiset<String>>() { });
        roundTrip(mapper, ImmutableSortedMultiset.of("b", "a", "a"),
                new TypeReference<ImmutableSortedMultiset<String>>() { });
        roundTrip(mapper, ImmutableMap.of("a", 1, "b", 2),
                new TypeReference<ImmutableMap<String, Integer>>() { });
        roundTrip(mapper, ImmutableBiMap.of("a", 1, "b", 2),
                new TypeReference<ImmutableBiMap<String, Integer>>() { });
        roundTrip(mapper, ImmutableSortedMap.of("b", 2, "a", 1),
                new TypeReference<ImmutableSortedMap<String, Integer>>() { });
        roundTrip(mapper, ImmutableList.<Object>of("a", 1, 2.5, true, ImmutableList.of("x")),
                new TypeReference<ImmutableList<Object>>() { });
        roundTrip(mapper, ImmutableMap.<String, Object>of("a", 1, "b", "x", "c", ImmutableSet.of(3L)),
                new TypeReference<ImmutableMap<String, Object>>() { });

        roundTripAsObject(mapper, ImmutableList.of("a", "b"));
        roundTripAsObject(mapper, ImmutableSet.of("a", "b"));
        // NOTE: ImmutableSortedSet / ImmutableSortedMultiset cannot be read back
        // as `Object` (element type is not known to be Comparable)
        roundTripAsObject(mapper, ImmutableMultiset.of("a", "a", "b"));
        roundTripAsObject(mapper, ImmutableMap.of("a", "b"));
        roundTripAsObject(mapper, ImmutableBiMap.of("a", "b"));
        roundTripAsObject(mapper, ImmutableSortedMap.of("a", "b"));
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testImmutableCollectionsInPojo(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);
        ImmutableCollections input = new ImmutableCollections();
        input.list = ImmutableList.of("a", "b", "c");
        input.set = ImmutableSet.of(1, 2, 3);
        input.sortedSet = ImmutableSortedSet.of("c", "a", "b");
        input.multiset = ImmutableMultiset.of("a", "a", "b");
        input.sortedMultiset = ImmutableSortedMultiset.of("b", "a", "a");
        input.map = ImmutableMap.of("a", 1, "b", 2);
        input.biMap = ImmutableBiMap.of("a", 1, "b", 2);
        input.sortedMap = ImmutableSortedMap.of("b", 2, "a", 1);
        input.objectList = ImmutableList.of("a", 1, 2.5, true, ImmutableList.of("x"));
        input.objectMap = ImmutableMap.of("a", 1, "b", "x", "c", ImmutableSet.of(3L));
        input.untypedList = ImmutableList.of("x", "y");
        input.untypedMap = ImmutableSortedMap.of("k", 42L);

        ImmutableCollections result = roundTrip(mapper, input, ImmutableCollections.class);
        assertInstanceOf(ImmutableList.class, result.untypedList);
        assertInstanceOf(ImmutableSortedMap.class, result.untypedMap);
    }

    /*
    /**********************************************************************
    /* Tests: multisets
    /**********************************************************************
     */

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testMultisetsRoot(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);

        roundTrip(mapper, HashMultiset.create(Arrays.asList("a", "a", "b")),
                new TypeReference<HashMultiset<String>>() { });
        roundTrip(mapper, TreeMultiset.create(Arrays.asList("b", "a", "a")),
                new TypeReference<TreeMultiset<String>>() { });
        roundTrip(mapper, LinkedHashMultiset.create(Arrays.asList("b", "a", "a")),
                new TypeReference<LinkedHashMultiset<String>>() { });
        roundTrip(mapper, HashMultiset.create(Arrays.asList("a", "a", "b")),
                new TypeReference<Multiset<String>>() { });
        roundTrip(mapper, TreeMultiset.create(Arrays.asList("b", "a", "a")),
                new TypeReference<SortedMultiset<String>>() { });

        Object result = roundTripAsObject(mapper, HashMultiset.create(Arrays.asList("a", "a", "b")));
        assertInstanceOf(HashMultiset.class, result);
        result = roundTripAsObject(mapper, TreeMultiset.create(Arrays.asList("b", "a", "a")));
        assertInstanceOf(TreeMultiset.class, result);
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testMultisetsInPojo(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);
        Multisets input = new Multisets();
        input.multiset = HashMultiset.create(Arrays.asList("a", "a", "b"));
        input.treeMultiset = TreeMultiset.create(Arrays.asList("b", "a", "a"));
        input.sortedMultiset = TreeMultiset.create(Arrays.asList("z", "y", "y"));
        input.hashMultiset = HashMultiset.create(Arrays.asList("q", "q"));
        input.objectMultiset = HashMultiset.create(Arrays.<Object>asList("a", 1, 1, 2.5));
        input.untyped = LinkedHashMultiset.create(Arrays.asList("b", "a", "b"));

        Multisets result = roundTrip(mapper, input, Multisets.class);
        // type id must preserve concrete implementation
        assertInstanceOf(TreeMultiset.class, result.treeMultiset);
        assertInstanceOf(LinkedHashMultiset.class, result.untyped);
    }

    /*
    /**********************************************************************
    /* Tests: multimaps
    /**********************************************************************
     */

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testMultimapsRoot(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);

        ArrayListMultimap<String, Integer> arrayList = ArrayListMultimap.create();
        arrayList.putAll("a", Arrays.asList(1, 2, 1));
        arrayList.put("b", 3);
        roundTrip(mapper, arrayList, new TypeReference<ArrayListMultimap<String, Integer>>() { });
        roundTrip(mapper, arrayList, new TypeReference<Multimap<String, Integer>>() { });

        HashMultimap<String, Integer> hash = HashMultimap.create();
        hash.putAll("a", Arrays.asList(1, 2));
        hash.put("b", 3);
        roundTrip(mapper, hash, new TypeReference<HashMultimap<String, Integer>>() { });
        roundTrip(mapper, hash, new TypeReference<SetMultimap<String, Integer>>() { });

        LinkedListMultimap<String, Integer> linkedList = LinkedListMultimap.create();
        linkedList.putAll("b", Arrays.asList(2, 1));
        linkedList.put("a", 3);
        roundTrip(mapper, linkedList, new TypeReference<LinkedListMultimap<String, Integer>>() { });

        ImmutableListMultimap<String, Integer> immutableList = ImmutableListMultimap.of("a", 1, "a", 2, "b", 3);
        roundTrip(mapper, immutableList, new TypeReference<ImmutableListMultimap<String, Integer>>() { });

        ImmutableSetMultimap<String, Integer> immutableSet = ImmutableSetMultimap.of("a", 1, "a", 2, "b", 3);
        roundTrip(mapper, immutableSet, new TypeReference<ImmutableSetMultimap<String, Integer>>() { });

        TreeMultimap<String, Integer> tree = TreeMultimap.create();
        tree.putAll("b", Arrays.asList(2, 1));
        tree.put("a", 3);
        roundTrip(mapper, tree, new TypeReference<TreeMultimap<String, Integer>>() { });

        // Multimap with `Object` values only as POJO property, see `testMultimapsInPojo`

        assertInstanceOf(ArrayListMultimap.class, roundTripAsObject(mapper, arrayList));
        assertInstanceOf(HashMultimap.class, roundTripAsObject(mapper, hash));
        assertInstanceOf(LinkedListMultimap.class, roundTripAsObject(mapper, linkedList));
        assertInstanceOf(ImmutableListMultimap.class, roundTripAsObject(mapper, immutableList));
        assertInstanceOf(ImmutableSetMultimap.class, roundTripAsObject(mapper, immutableSet));
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testMultimapsInPojo(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);
        Multimaps input = new Multimaps();

        ArrayListMultimap<String, Integer> arrayList = ArrayListMultimap.create();
        arrayList.putAll("a", Arrays.asList(1, 2, 1));
        input.arrayList = arrayList;

        HashMultimap<String, Integer> hash = HashMultimap.create();
        hash.putAll("a", Arrays.asList(1, 2));
        input.hash = hash;

        LinkedListMultimap<String, Integer> linkedList = LinkedListMultimap.create();
        linkedList.putAll("b", Arrays.asList(2, 1));
        linkedList.put("a", 3);
        input.linkedList = linkedList;

        input.immutableList = ImmutableListMultimap.of("a", 1, "a", 2, "b", 3);
        input.immutableSet = ImmutableSetMultimap.of("a", 1, "a", 2, "b", 3);

        TreeMultimap<String, Integer> tree = TreeMultimap.create();
        tree.putAll("b", Arrays.asList(2, 1));
        input.tree = tree;

        ArrayListMultimap<String, Object> objectValues = ArrayListMultimap.create();
        objectValues.putAll("a", Arrays.<Object>asList(1, "x", 2.5, true));
        input.objectValues = objectValues;

        input.untyped = ImmutableListMultimap.of("x", "y", "x", "z");

        Multimaps result = roundTrip(mapper, input, Multimaps.class);
        assertInstanceOf(ArrayListMultimap.class, result.arrayList);
        assertInstanceOf(HashMultimap.class, result.hash);
        assertInstanceOf(LinkedListMultimap.class, result.linkedList);
        assertInstanceOf(TreeMultimap.class, result.tree);
        assertInstanceOf(ImmutableListMultimap.class, result.untyped);
    }

    /*
    /**********************************************************************
    /* Tests: tables
    /**********************************************************************
     */

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testTablesRoot(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);

        HashBasedTable<String, Integer, String> hash = HashBasedTable.create();
        hash.put("r1", 1, "a");
        hash.put("r1", 2, "b");
        hash.put("r2", 1, "c");
        roundTrip(mapper, hash, new TypeReference<HashBasedTable<String, Integer, String>>() { });

        TreeBasedTable<String, Integer, String> tree = TreeBasedTable.create();
        tree.putAll(hash);
        roundTrip(mapper, tree, new TypeReference<TreeBasedTable<String, Integer, String>>() { });

        ImmutableTable<String, Integer, String> immutable = ImmutableTable.copyOf(hash);
        roundTrip(mapper, immutable, new TypeReference<ImmutableTable<String, Integer, String>>() { });

        // When read as `Object`, row/column keys come back as Strings, so use String keys
        HashBasedTable<String, String, String> hashS = HashBasedTable.create();
        hashS.put("r1", "c1", "a");
        hashS.put("r2", "c2", "b");
        TreeBasedTable<String, String, String> treeS = TreeBasedTable.create();
        treeS.putAll(hashS);
        assertInstanceOf(HashBasedTable.class, roundTripAsObject(mapper, hashS));
        assertInstanceOf(TreeBasedTable.class, roundTripAsObject(mapper, treeS));
        assertInstanceOf(ImmutableTable.class, roundTripAsObject(mapper, ImmutableTable.copyOf(hashS)));
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testTablesInPojo(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);
        Tables input = new Tables();

        HashBasedTable<String, Integer, String> hash = HashBasedTable.create();
        hash.put("r1", 1, "a");
        hash.put("r2", 2, "b");
        input.hashBased = hash;

        TreeBasedTable<String, Integer, String> tree = TreeBasedTable.create();
        tree.putAll(hash);
        input.treeBased = tree;

        input.immutable = ImmutableTable.of("r", 3, "c");

        HashBasedTable<String, String, Object> objectValues = HashBasedTable.create();
        objectValues.put("r", "c1", 1);
        objectValues.put("r", "c2", "x");
        objectValues.put("s", "c1", 2.5);
        input.objectValues = objectValues;

        input.untyped = ImmutableTable.of("a", "b", "c");

        Tables result = roundTrip(mapper, input, Tables.class);
        assertInstanceOf(HashBasedTable.class, result.hashBased);
        assertInstanceOf(TreeBasedTable.class, result.treeBased);
        assertInstanceOf(ImmutableTable.class, result.immutable);
        assertInstanceOf(ImmutableTable.class, result.untyped);
    }

    /*
    /**********************************************************************
    /* Tests: RangeMap
    /*
    /* NOTE: RangeSet / ImmutableRangeSet not covered: `RangeSetSerializer`
    /*   does not implement `serializeWithType()`. RangeMap is not covered
    /*   as `Object`-typed value (no key deserializer for `Comparable`).
    /**********************************************************************
     */

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testRangeMapsRoot(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);

        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closed(1, 5), "a");
        rangeMap.put(Range.open(10, 20), "b");
        roundTrip(mapper, rangeMap, new TypeReference<RangeMap<Integer, String>>() { });
        roundTrip(mapper, ImmutableRangeMap.copyOf(rangeMap),
                new TypeReference<ImmutableRangeMap<Integer, String>>() { });
        // RangeMap with `Object` values only as POJO property, see `testRangeMapsInPojo`
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testRangeMapsInPojo(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);
        Ranges input = new Ranges();

        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closed(1, 5), "a");
        rangeMap.put(Range.open(10, 20), "b");
        input.rangeMap = rangeMap;
        input.immutableRangeMap = ImmutableRangeMap.of(Range.closedOpen(3, 7), "c");

        RangeMap<Integer, Object> objectRangeMap = TreeRangeMap.create();
        objectRangeMap.put(Range.closed(1, 5), "a");
        objectRangeMap.put(Range.open(10, 20), 42);
        input.objectRangeMap = objectRangeMap;

        Ranges result = roundTrip(mapper, input, Ranges.class);
        assertInstanceOf(TreeRangeMap.class, result.rangeMap);
        assertInstanceOf(ImmutableRangeMap.class, result.immutableRangeMap);
    }

    /*
    /**********************************************************************
    /* Tests: primitive arrays
    /*
    /* NOTE: only round-tripped with concrete declared type: as `Object`,
    /*   serializers lack `serializeWithType()`
    /**********************************************************************
     */

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testPrimitiveArraysRoot(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);

        roundTrip(mapper, ImmutableIntArray.of(1, 2, 3), ImmutableIntArray.class);
        roundTrip(mapper, ImmutableLongArray.of(1L, Long.MAX_VALUE), ImmutableLongArray.class);
        roundTrip(mapper, ImmutableDoubleArray.of(0.5, -1.25), ImmutableDoubleArray.class);
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testPrimitiveArraysInPojo(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);
        PrimitiveArrays input = new PrimitiveArrays();
        input.ints = ImmutableIntArray.of(1, 2, 3);
        input.longs = ImmutableLongArray.of(1L, Long.MAX_VALUE);
        input.doubles = ImmutableDoubleArray.of(0.5, -1.25);

        roundTrip(mapper, input, PrimitiveArrays.class);
    }

    /*
    /**********************************************************************
    /* Tests: HostAndPort, InternetDomainName
    /*
    /* NOTE: HashCode not covered: type id is that of a non-public subtype
    /*   (like `HashCode$BytesHashCode`) for which no deserializer is found
    /**********************************************************************
     */

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testScalarsRoot(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);

        roundTrip(mapper, HostAndPort.fromParts("example.com", 8080), HostAndPort.class);
        roundTrip(mapper, InternetDomainName.from("www.example.com"), InternetDomainName.class);

        roundTripAsObject(mapper, HostAndPort.fromParts("example.com", 8080));
        roundTripAsObject(mapper, InternetDomainName.from("www.example.com"));
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testScalarsInPojo(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);
        Scalars input = new Scalars();
        input.hostAndPort = HostAndPort.fromParts("example.com", 8080);
        input.domainName = InternetDomainName.from("www.example.com");
        input.untypedHostAndPort = HostAndPort.fromString("localhost:1234");
        input.untypedDomainName = InternetDomainName.from("example.org");

        roundTrip(mapper, input, Scalars.class);
    }

    /*
    /**********************************************************************
    /* Tests: Cache
    /**********************************************************************
     */

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testCacheRoot(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);
        TypeReference<Cache<String, Integer>> type = new TypeReference<Cache<String, Integer>>() { };

        Cache<String, Integer> cache = CacheBuilder.newBuilder().build();
        cache.put("a", 1);
        cache.put("b", 2);
        String json = mapper.writerFor(type).writeValueAsString(cache);
        Cache<String, Integer> result = mapper.readValue(json, type);
        assertEquals(cache.asMap(), result.asMap(), "JSON: " + json);
    }

    @ParameterizedTest
    @EnumSource(value = DefaultTyping.class, names = { "NON_FINAL", "OBJECT_AND_NON_CONCRETE" })
    public void testCacheInPojo(DefaultTyping typing) throws Exception
    {
        ObjectMapper mapper = mapper(typing);
        CacheHolder input = new CacheHolder();
        input.cache = CacheBuilder.newBuilder().build();
        input.cache.put("a", 1);
        input.objectCache = CacheBuilder.newBuilder().build();
        input.objectCache.put("x", "y");
        input.objectCache.put("z", 2.5);

        String json = mapper.writeValueAsString(input);
        CacheHolder result = mapper.readValue(json, CacheHolder.class);
        assertEquals(input.cache.asMap(), result.cache.asMap(), "JSON: " + json);
        assertEquals(input.objectCache.asMap(), result.objectCache.asMap(), "JSON: " + json);
    }
}
