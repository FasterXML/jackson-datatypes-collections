package tools.jackson.datatype.fastutil;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.exc.InvalidNullException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;

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

    static class ContentNullsBean {
        @JsonSetter(contentNulls = Nulls.SKIP)
        public IntList skipList;

        @JsonSetter(contentNulls = Nulls.SKIP)
        public CharList skipChars;

        @JsonSetter(contentNulls = Nulls.SKIP)
        public Int2IntMap skipMap;

        @JsonSetter(contentNulls = Nulls.SKIP)
        public Int2ObjectMap<String> skipRefMap;

        @JsonSetter(contentNulls = Nulls.FAIL)
        public Int2ObjectMap<String> failRefMap;

        @JsonSetter(contentNulls = Nulls.FAIL)
        public it.unimi.dsi.fastutil.longs.LongList failList;

        @JsonSetter(contentNulls = Nulls.AS_EMPTY)
        public Int2ObjectMap<java.util.List<String>> emptyRefMap;
    }

    static class SingleValueBean {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public IntList ints;
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

        // errors refer to the requested type (not to the type used internally)
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("[1]", refType));
        verifyException(e, "Reference2IntMap");
        assertFalse(e.getMessage().contains("Object2Int"), e.getMessage());

        // merging (copies entries into existing map)
        Reference2IntMap<String> existing = new Reference2IntOpenHashMap<>();
        existing.put("a", 1);
        assertSame(existing, MAPPER.readerForUpdating(existing).readValue(a2q("{'b':2}")));
        assertEquals(2, existing.size());
        assertEquals(2, existing.getInt(_key(existing, "b")));
    }

    @Test
    void testMergeVariants() throws Exception
    {
        // entries added directly into existing map, replacing values
        Int2ObjectMap<String> objMap = new Int2ObjectOpenHashMap<>();
        objMap.put(1, "a");
        objMap.put(2, "b");
        assertSame(objMap, MAPPER.readerFor(new TypeReference<Int2ObjectMap<String>>() { })
                .withValueToUpdate(objMap).readValue(a2q("{'2':'x','3':'c'}")));
        assertEquals(3, objMap.size());
        assertEquals("x", objMap.get(2));

        // single values (not JSON Arrays) merged too
        IntList list = new IntArrayList(new int[] { 1 });
        MAPPER.readerForUpdating(list)
                .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .readValue("2");
        assertEquals(IntList.of(1, 2), list);

        // as well as `char` arrays
        CharList chars = new CharArrayList(new char[] { 'a' });
        MAPPER.readerForUpdating(chars).readValue("[\"b\",\"c\"]");
        assertEquals(CharList.of('a', 'b', 'c'), chars);
    }

    // Identity-based maps need the same key instance
    private static <K> K _key(java.util.Map<K, ?> map, String key) {
        return map.keySet().stream().filter(key::equals).findFirst().orElseThrow();
    }

    @Test
    void testContentNulls() throws Exception
    {
        ContentNullsBean bean = MAPPER.readValue("""
                {"skipList":[1,null,2], "skipChars":["a",null,"b"],
                 "skipMap":{"1":null,"2":20}, "skipRefMap":{"1":null,"2":"x"}}
                """, ContentNullsBean.class);
        assertEquals(IntList.of(1, 2), bean.skipList);
        assertEquals(CharList.of('a', 'b'), bean.skipChars);
        assertEquals(new Int2IntOpenHashMap(new int[] { 2 }, new int[] { 20 }), bean.skipMap);
        assertEquals(1, bean.skipRefMap.size());
        assertEquals("x", bean.skipRefMap.get(2));

        // without nulls, regular handling (String form for chars too)
        bean = MAPPER.readValue("{\"skipChars\":\"ab\"}", ContentNullsBean.class);
        assertEquals(CharList.of('a', 'b'), bean.skipChars);

        assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue("{\"failRefMap\":{\"1\":null}}", ContentNullsBean.class));
        assertThrows(InvalidNullException.class,
                () -> LENIENT_MAPPER.readValue("{\"failList\":[1,null]}", ContentNullsBean.class));
        // but without `contentNulls`, null values of reference maps are fine
        Int2ObjectMap<String> refMap = MAPPER.readValue("{\"1\":null}",
                new TypeReference<Int2ObjectMap<String>>() { });
        assertTrue(refMap.containsKey(1));

        // AS_EMPTY uses "empty" value of the value deserializer
        bean = MAPPER.readValue("{\"emptyRefMap\":{\"1\":null}}", ContentNullsBean.class);
        assertEquals(java.util.List.of(), bean.emptyRefMap.get(1));
    }

    @Test
    void testCharNullsWithoutFailOnNullForPrimitives() throws Exception
    {
        // like `char[]` (and other primitive collections), nulls become default value
        assertEquals(CharList.of('a', '\0'), LENIENT_MAPPER.readValue("[\"a\",null]", CharList.class));
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("[\"a\",null]", CharList.class));
    }

    @Test
    void testEmptyStringCoercion() throws Exception
    {
        // Not allowed by default
        assertThrows(MismatchedInputException.class, () -> MAPPER.readValue("\"\"", IntList.class));
        assertThrows(MismatchedInputException.class, () -> MAPPER.readValue("\"\"", Int2IntMap.class));

        ObjectMapper mapper = mapperBuilder()
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .build();
        assertNull(mapper.readValue("\"\"", IntList.class));
        assertNull(mapper.readValue("\"\"", Int2IntMap.class));
        assertNull(mapper.readValue("\"\"", Int2ObjectMap.class));
        // merging nothing leaves value as is
        IntList list = new IntArrayList(new int[] { 1 });
        assertSame(list, mapper.readerForUpdating(list).readValue("\"\""));
        assertEquals(IntList.of(1), list);

        // Coercion configuration by logical type
        mapper = mapperBuilder()
                .withCoercionConfig(LogicalType.Collection, cfg ->
                        cfg.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
                .withCoercionConfig(LogicalType.Map, cfg ->
                        cfg.setAcceptBlankAsEmpty(Boolean.TRUE)
                            .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
                .build();
        assertEquals(new IntRBTreeSet(), assertInstanceOf(IntRBTreeSet.class,
                mapper.readValue("\"\"", IntSortedSet.class)));
        assertEquals(new Int2IntOpenHashMap(), mapper.readValue("\" \"", Int2IntMap.class));

        // but for char collections, Strings are content, so empty one is an empty collection
        assertEquals(CharList.of(), MAPPER.readValue("\"\"", CharList.class));
    }

    @Test
    void testSingleValueAsArrayPerProperty() throws Exception
    {
        SingleValueBean bean = MAPPER.readValue("{\"ints\":3}", SingleValueBean.class);
        assertEquals(IntList.of(3), bean.ints);
        bean = MAPPER.readValue("{\"ints\":[3,4]}", SingleValueBean.class);
        assertEquals(IntList.of(3, 4), bean.ints);

        // not enabled for root values (or other properties) by default
        assertThrows(MismatchedInputException.class, () -> MAPPER.readValue("3", IntList.class));
    }

    @Test
    void testMergeIntoUnmodifiable() throws Exception
    {
        IntList list = IntLists.unmodifiable(IntArrayList.of(1));
        DatabindException e = assertThrows(DatabindException.class,
                () -> MAPPER.readerForUpdating(list).readValue("[2]"));
        assertTrue(e.getMessage().contains("Cannot merge into value"), e.getMessage());

        Int2IntMap map = Int2IntMaps.unmodifiable(new Int2IntOpenHashMap());
        assertThrows(DatabindException.class,
                () -> MAPPER.readerForUpdating(map).readValue("{\"1\":2}"));

        CharList chars = it.unimi.dsi.fastutil.chars.CharLists.unmodifiable(CharList.of('a'));
        assertThrows(DatabindException.class,
                () -> MAPPER.readerForUpdating(chars).readValue("\"b\""));
    }
}
