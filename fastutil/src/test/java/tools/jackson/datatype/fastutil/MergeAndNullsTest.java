package tools.jackson.datatype.fastutil;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for merging ({@code @JsonMerge}, {@code readerForUpdating()}), null handling
 * ({@code Nulls.AS_EMPTY}, {@code null} map values) and maps with identity-based keys.
 */
public class MergeAndNullsTest extends ModuleTestBase
{
    static class MergeBean {
        @JsonMerge
        public IntList list = IntArrayList.of(1);

        @JsonMerge
        public Int2IntMap map = new Int2IntOpenHashMap(new int[] { 1 }, new int[] { 10 });
    }

    static class AsEmptyBean {
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        public IntSet set;

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        public Int2LongSortedMap map;

        @JsonSetter(nulls = Nulls.AS_EMPTY)
        public CharList chars;
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    private final ObjectMapper LENIENT_MAPPER = mapperBuilder()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

    @Test
    void testReaderForUpdating() throws Exception
    {
        IntList list = new IntArrayList(new int[] { 1, 2 });
        assertSame(list, MAPPER.readerForUpdating(list).readValue("[3]"));
        assertEquals(IntList.of(1, 2, 3), list);

        CharList chars = new CharArrayList(new char[] { 'a' });
        MAPPER.readerForUpdating(chars).readValue("\"bc\"");
        assertEquals(CharList.of('a', 'b', 'c'), chars);

        Int2IntMap map = new Int2IntOpenHashMap();
        map.put(1, 10);
        map.put(2, 20);
        assertSame(map, MAPPER.readerForUpdating(map).readValue(a2q("{'2':21,'3':30}")));
        assertEquals(10, map.get(1));
        assertEquals(21, map.get(2));
        assertEquals(30, map.get(3));
    }

    @Test
    void testMergeProperty() throws Exception
    {
        MergeBean bean = MAPPER.readValue(a2q("{'list':[2],'map':{'2':20}}"), MergeBean.class);
        assertEquals(IntList.of(1, 2), bean.list);
        assertEquals(10, bean.map.get(1));
        assertEquals(20, bean.map.get(2));
    }

    @Test
    void testNullsAsEmpty() throws Exception
    {
        AsEmptyBean bean = MAPPER.readValue(a2q("{'set':null,'map':null,'chars':null}"),
                AsEmptyBean.class);
        assertInstanceOf(IntOpenHashSet.class, bean.set);
        assertTrue(bean.set.isEmpty());
        assertInstanceOf(Int2LongRBTreeMap.class, bean.map);
        assertTrue(bean.map.isEmpty());
        assertInstanceOf(CharArrayList.class, bean.chars);
        assertTrue(bean.chars.isEmpty());
    }

    @Test
    void testCharMapValues() throws Exception
    {
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(a2q("{'1':null}"), Int2CharMap.class));
        verifyException(e, "FAIL_ON_NULL_FOR_PRIMITIVES");
        assertEquals('\0', LENIENT_MAPPER.readValue(a2q("{'1':null}"), Int2CharMap.class).get(1));

        assertEquals('x', MAPPER.readValue(a2q("{'1':'x'}"), Int2CharMap.class).get(1));
        e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(a2q("{'1':'ab'}"), Int2CharMap.class));
        verifyException(e, "length 2");
        e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(a2q("{'1':{}}"), Int2CharMap.class));
        verifyException(e, "START_OBJECT");
    }

    @Test
    void testReferenceKeyedMaps() throws Exception
    {
        // `null` values handled the same way as for `Object2IntMap`
        TypeReference<Reference2IntMap<String>> refType = new TypeReference<>() { };
        TypeReference<Object2IntMap<String>> objType = new TypeReference<>() { };
        for (TypeReference<?> type : java.util.List.of(refType, objType)) {
            MismatchedInputException e = assertThrows(MismatchedInputException.class,
                    () -> MAPPER.readValue(a2q("{'a':null}"), type));
            verifyException(e, "FAIL_ON_NULL_FOR_PRIMITIVES");
        }
        Reference2IntMap<String> map = LENIENT_MAPPER.readValue(a2q("{'a':null,'b':2}"), refType);
        assertInstanceOf(Reference2IntOpenHashMap.class, map);
        assertEquals(0, map.getInt(_key(map, "a")));
        assertEquals(2, map.getInt(_key(map, "b")));

        // Sorted variants keep insertion order, as with `ReferenceSortedSet`
        Reference2LongSortedMap<String> sorted = MAPPER.readValue(a2q("{'b':1,'a':2}"),
                new TypeReference<Reference2LongSortedMap<String>>() { });
        assertInstanceOf(Reference2LongLinkedOpenHashMap.class, sorted);
        assertEquals(java.util.List.of("b", "a"), new java.util.ArrayList<>(sorted.keySet()));

        Reference2ObjectSortedMap<String, String> refObj = MAPPER.readValue(a2q("{'b':'x','a':'y'}"),
                new TypeReference<Reference2ObjectSortedMap<String, String>>() { });
        assertInstanceOf(Reference2ObjectLinkedOpenHashMap.class, refObj);
        assertEquals(java.util.List.of("b", "a"), new java.util.ArrayList<>(refObj.keySet()));

        // and round-trip with type information
        Reference2DoubleMap<String> doubles = new Reference2DoubleOpenHashMap<>();
        doubles.put("x", 0.5);
        String json = MAPPER.writeValueAsString(doubles);
        Reference2DoubleMap<String> result = MAPPER.readValue(json,
                new TypeReference<Reference2DoubleMap<String>>() { });
        assertEquals(0.5, result.getDouble(_key(result, "x")));
    }

    // Identity-based maps need the same key instance
    private static <K> K _key(java.util.Map<K, ?> map, String key) {
        return map.keySet().stream().filter(key::equals).findFirst().orElseThrow();
    }
}
