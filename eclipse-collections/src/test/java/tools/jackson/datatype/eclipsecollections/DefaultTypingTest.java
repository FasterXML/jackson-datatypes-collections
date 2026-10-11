package tools.jackson.datatype.eclipsecollections;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.bimap.MutableBiMap;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Eclipse Collections types with polymorphic default typing enabled.
 *<p>
 * NOTE: only mutable object collections and maps are covered here: immutable
 * collections, primitive collections and primitive maps do not currently
 * round-trip with default typing, since the type id written is that of the
 * concrete implementation class (like {@code ImmutableTripletonList} or
 * {@code IntArrayList}), for which no deserializer is found.
 */
public class DefaultTypingTest extends ModuleTestBase
{
    static class NoCheckSubTypeValidator
        extends PolymorphicTypeValidator.Base
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    private static final DefaultTyping[] TYPINGS = {
            DefaultTyping.NON_FINAL,
            DefaultTyping.OBJECT_AND_NON_CONCRETE
    };

    // Non-final value type, used as a polymorphic element
    static class Point {
        public int x, y;

        protected Point() { }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Point other)) { return false; }
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Point(" + x + "," + y + ")";
        }
    }

    static class CollectionsBean {
        public MutableList<String> list;
        public MutableSet<String> set;
        public MutableBag<String> bag;
        public MutableSortedSet<String> sortedSet;
        public MutableSortedBag<String> sortedBag;
        public MutableList<Object> objectList;
        public MutableSet<Object> objectSet;
        public MutableList<Point> pointList;
        public MutableList<MutableList<String>> nestedList;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof CollectionsBean other)) { return false; }
            return Objects.equals(list, other.list)
                    && Objects.equals(set, other.set)
                    && Objects.equals(bag, other.bag)
                    && Objects.equals(sortedSet, other.sortedSet)
                    && Objects.equals(sortedBag, other.sortedBag)
                    && Objects.equals(objectList, other.objectList)
                    && Objects.equals(objectSet, other.objectSet)
                    && Objects.equals(pointList, other.pointList)
                    && Objects.equals(nestedList, other.nestedList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(list, set, bag);
        }
    }

    static class MapsBean {
        public MutableMap<String, Integer> map;
        public MutableSortedMap<String, Integer> sortedMap;
        public MutableBiMap<String, Integer> biMap;
        public MutableMap<String, Object> objectValueMap;
        public MutableMap<String, MutableList<Object>> nestedMap;

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MapsBean other)) { return false; }
            return Objects.equals(map, other.map)
                    && Objects.equals(sortedMap, other.sortedMap)
                    && Objects.equals(biMap, other.biMap)
                    && Objects.equals(objectValueMap, other.objectValueMap)
                    && Objects.equals(nestedMap, other.nestedMap);
        }

        @Override
        public int hashCode() {
            return Objects.hash(map, sortedMap, biMap);
        }
    }

    static class ObjectHolder {
        public Object value;

        protected ObjectHolder() { }

        public ObjectHolder(Object value) {
            this.value = value;
        }
    }

    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    private ObjectMapper mapperWithTyping(DefaultTyping typing) {
        return mapperBuilder()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing)
                .build();
    }

    private <T> void assertRootRoundTrip(T value, TypeReference<T> type) {
        for (DefaultTyping typing : TYPINGS) {
            ObjectMapper mapper = mapperWithTyping(typing);
            Class<?> rawType = mapper.getTypeFactory().constructType(type).getRawClass();
            // Use writerFor() so that the declared (interface) root type determines
            // whether type info is included, matching what readValue() expects
            String json = mapper.writerFor(type).writeValueAsString(value);
            T result = mapper.readValue(json, type);
            assertEquals(value, result, "Failed with " + typing + ", JSON: " + json);
            assertInstanceOf(rawType, result);
        }
    }

    private <T> void assertPojoRoundTrip(T value, Class<T> type) {
        for (DefaultTyping typing : TYPINGS) {
            ObjectMapper mapper = mapperWithTyping(typing);
            String json = mapper.writeValueAsString(value);
            T result = mapper.readValue(json, type);
            assertEquals(value, result, "Failed with " + typing + ", JSON: " + json);
        }
    }

    /*
    /**********************************************************************
    /* Root-level values
    /**********************************************************************
     */

    @Test
    public void testRootCollections() throws Exception {
        assertRootRoundTrip(Lists.mutable.of("a", "b", "c"), new TypeReference<MutableList<String>>() {});
        assertRootRoundTrip(Lists.mutable.empty(), new TypeReference<MutableList<String>>() {});
        assertRootRoundTrip(Sets.mutable.of("a", "b"), new TypeReference<MutableSet<String>>() {});
        assertRootRoundTrip(Bags.mutable.of("a", "a", "b"), new TypeReference<MutableBag<String>>() {});
        assertRootRoundTrip(SortedSets.mutable.of("b", "a"),
                new TypeReference<MutableSortedSet<String>>() {});
        assertRootRoundTrip(SortedBags.mutable.of("b", "a", "a"),
                new TypeReference<MutableSortedBag<String>>() {});
    }

    @Test
    public void testRootCollectionsWithPolymorphicElements() throws Exception {
        assertRootRoundTrip(Lists.mutable.of("a", 1, 2L, 3.5, true, new Point(1, 2)),
                new TypeReference<MutableList<Object>>() {});
        assertRootRoundTrip(Sets.mutable.<Object>of("s", 1L, new Point(1, 1)),
                new TypeReference<MutableSet<Object>>() {});
        assertRootRoundTrip(Bags.mutable.<Object>of(1L, 1L, new Point(1, 1)),
                new TypeReference<MutableBag<Object>>() {});
        assertRootRoundTrip(Lists.mutable.of(new Point(1, 2), new Point(3, 4)),
                new TypeReference<MutableList<Point>>() {});
        assertRootRoundTrip(Lists.mutable.of(Lists.mutable.of("a"), Lists.mutable.of("b", "c")),
                new TypeReference<MutableList<MutableList<String>>>() {});
        assertRootRoundTrip(Lists.mutable.<Object>of(Lists.mutable.of("a"), Sets.mutable.of(1L)),
                new TypeReference<MutableList<Object>>() {});
    }

    @Test
    public void testRootMaps() throws Exception {
        assertRootRoundTrip(Maps.mutable.of("a", 1, "b", 2),
                new TypeReference<MutableMap<String, Integer>>() {});
        assertRootRoundTrip(SortedMaps.mutable.of("b", 1, "a", 2),
                new TypeReference<MutableSortedMap<String, Integer>>() {});
        assertRootRoundTrip(BiMaps.mutable.of("a", 1, "b", 2),
                new TypeReference<MutableBiMap<String, Integer>>() {});
        assertRootRoundTrip(Maps.mutable.<String, Object>of("a", 1L, "b", new Point(1, 2), "c", "str"),
                new TypeReference<MutableMap<String, Object>>() {});
        assertRootRoundTrip(Maps.mutable.of("k", Lists.mutable.<Object>of(1L, new Point(1, 1))),
                new TypeReference<MutableMap<String, MutableList<Object>>>() {});
    }

    /*
    /**********************************************************************
    /* POJO fields declared with interface types
    /**********************************************************************
     */

    @Test
    public void testCollectionsAsFields() throws Exception {
        CollectionsBean bean = new CollectionsBean();
        bean.list = Lists.mutable.of("a", "b");
        bean.set = Sets.mutable.of("x", "y");
        bean.bag = Bags.mutable.of("a", "a", "b");
        bean.sortedSet = SortedSets.mutable.of("c", "a", "b");
        bean.sortedBag = SortedBags.mutable.of("c", "a", "a");
        bean.objectList = Lists.mutable.of("a", 1, 2L, 3.5, new Point(3, 4));
        bean.objectSet = Sets.mutable.of("a", 2L, new Point(5, 6));
        bean.pointList = Lists.mutable.of(new Point(7, 8));
        bean.nestedList = Lists.mutable.of(Lists.mutable.of("n1"), Lists.mutable.empty());
        assertPojoRoundTrip(bean, CollectionsBean.class);
    }

    @Test
    public void testMapsAsFields() throws Exception {
        MapsBean bean = new MapsBean();
        bean.map = Maps.mutable.of("a", 1);
        bean.sortedMap = SortedMaps.mutable.of("b", 2, "a", 1);
        bean.biMap = BiMaps.mutable.of("c", 3);
        bean.objectValueMap = Maps.mutable.of("p", new Point(1, 1), "l", 3L, "s", "str");
        bean.nestedMap = Maps.mutable.of("k", Lists.mutable.of(4L, new Point(2, 2)));
        assertPojoRoundTrip(bean, MapsBean.class);
    }

    @Test
    public void testCollectionsAsObjectField() throws Exception {
        Object[] values = {
                Lists.mutable.of("a", "b"),
                Lists.mutable.of(1L, new Point(1, 2)),
                Sets.mutable.of("x"),
                Bags.mutable.of("a", "a"),
                SortedSets.mutable.of("b", "a"),
                Maps.mutable.of("a", 1),
                Maps.mutable.of("a", new Point(3, 4)),
                SortedMaps.mutable.of("b", 2, "a", 1),
                BiMaps.mutable.of("c", 3),
        };
        for (DefaultTyping typing : TYPINGS) {
            ObjectMapper mapper = mapperWithTyping(typing);
            for (Object value : values) {
                String json = mapper.writeValueAsString(new ObjectHolder(value));
                ObjectHolder result = mapper.readValue(json, ObjectHolder.class);
                assertEquals(value, result.value, "Failed with " + typing + ", JSON: " + json);
                assertSame(value.getClass(), result.value.getClass());
            }
        }
    }
}
