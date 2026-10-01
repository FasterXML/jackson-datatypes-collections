package tools.jackson.datatype.fastutil;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.doubles.Double2DoubleMap;
import it.unimi.dsi.fastutil.doubles.Double2DoubleOpenHashMap;
import it.unimi.dsi.fastutil.doubles.DoubleSortedSet;
import it.unimi.dsi.fastutil.doubles.DoubleRBTreeSet;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.*;
import it.unimi.dsi.fastutil.shorts.ShortImmutableList;
import it.unimi.dsi.fastutil.shorts.ShortList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that only use types included in {@code fastutil-core}. Besides the normal test
 * run, this test also runs with only {@code fastutil-core} on the class path (see
 * {@code pom.xml}): the module must work for the types that are available, and skip
 * the ones that are not.
 */
public class FastutilCoreTest extends ModuleTestBase
{
    static class Holder {
        public Object value;

        protected Holder() { }
        Holder(Object value) { this.value = value; }
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    void testExpectedFastutilVariant() {
        // Guards against the `fastutil-core` run silently using the full jar
        boolean expectCore = Boolean.getBoolean("expectFastutilCore");
        assertEquals(!expectCore, _isPresent("it.unimi.dsi.fastutil.shorts.ShortSet"));
        assertEquals(!expectCore, _isPresent("it.unimi.dsi.fastutil.booleans.BooleanList"));
        assertEquals(!expectCore, _isPresent("it.unimi.dsi.fastutil.objects.ReferenceList"));
    }

    @Test
    void testHandlersRegistered()
    {
        for (Class<?> type : List.of(IntCollection.class, IntList.class, IntSet.class,
                IntSortedSet.class, IntBigList.class, LongSet.class, DoubleSortedSet.class,
                ShortList.class, CharList.class, FloatList.class,
                Int2IntMap.class, Int2LongSortedMap.class, Long2DoubleMap.class,
                Double2DoubleMap.class, Int2ObjectMap.class, Object2LongMap.class)) {
            assertThat(findModuleSerializer(MAPPER, type)).as(type.getName()).isNotNull();
            assertThat(findModuleDeserializer(MAPPER, type)).as(type.getName()).isNotNull();
        }
    }

    @Test
    void testCollectionsRoundTrip() throws Exception
    {
        _verifyRoundTrip(IntList.of(1, 2), "[1,2]", IntList.class, IntArrayList.class);
        _verifyRoundTrip(new IntLinkedOpenHashSet(new int[] { 3, 1 }), "[3,1]",
                IntLinkedOpenHashSet.class, IntLinkedOpenHashSet.class);
        _verifyRoundTrip(LongSet.of(5L), "[5]", LongSet.class, LongOpenHashSet.class);
        _verifyRoundTrip(new DoubleRBTreeSet(new double[] { 2.5, 0.5 }), "[0.5,2.5]",
                DoubleSortedSet.class, DoubleRBTreeSet.class);
        _verifyRoundTrip(new LongBigArrayBigList(new long[][] { { 7L } }), "[7]",
                LongBigList.class, LongBigArrayBigList.class);
        // Only lists of these types are included in `fastutil-core`
        _verifyRoundTrip(ShortList.of((short) 1), "[1]", ShortList.class, null);
        _verifyRoundTrip(FloatList.of(0.5f), "[0.5]", FloatList.class, null);
        _verifyRoundTrip(CharList.of('a', 'b'), "\"ab\"", CharList.class, CharArrayList.class);
        _verifyRoundTrip(new ShortImmutableList(new short[] { 4 }), "[4]",
                ShortImmutableList.class, ShortImmutableList.class);
        _verifyRoundTrip(new ObjectArrayList<>(List.of("a")), "[\"a\"]",
                ObjectList.class, ObjectArrayList.class);
    }

    @Test
    void testMapsRoundTrip() throws Exception
    {
        Int2LongMap intToLong = new Int2LongLinkedOpenHashMap();
        intToLong.put(1, 2L);
        _verifyRoundTrip(intToLong, a2q("{'1':2}"), Int2LongMap.class, Int2LongOpenHashMap.class);

        Double2DoubleMap doubles = new Double2DoubleOpenHashMap();
        doubles.put(0.5, 1.5);
        _verifyRoundTrip(doubles, a2q("{'0.5':1.5}"), Double2DoubleMap.class, Double2DoubleOpenHashMap.class);

        Object2IntSortedMap<String> stringToInt = new Object2IntRBTreeMap<>();
        stringToInt.put("b", 2);
        stringToInt.put("a", 1);
        _verifyRoundTrip(stringToInt, a2q("{'a':1,'b':2}"), Object2IntSortedMap.class, Object2IntRBTreeMap.class);

        Long2ObjectMap<String> longToString = new Long2ObjectArrayMap<>();
        longToString.put(3L, "c");
        _verifyRoundTrip(longToString, a2q("{'3':'c'}"), Long2ObjectMap.class, Long2ObjectOpenHashMap.class);

        Object2ObjectMap<String, String> refs = new Object2ObjectOpenHashMap<>();
        refs.put("k", "v");
        _verifyRoundTrip(refs, a2q("{'k':'v'}"), Object2ObjectMap.class, Object2ObjectOpenHashMap.class);
    }

    @Test
    void testDefaultTyping() throws Exception
    {
        ObjectMapper mapper = defaultTypingMapper(DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.As.PROPERTY);
        Int2DoubleMap map = new Int2DoubleRBTreeMap();
        map.put(1, 0.5);
        Object[] values = {
                new IntArrayList(new int[] { 1, 2 }),
                IntLists.unmodifiable(new IntArrayList(new int[] { 3 })),
                new LongRBTreeSet(new long[] { 4L }),
                CharArrayList.wrap(new char[] { 'x' }),
                map,
                Int2DoubleMaps.unmodifiable(map),
        };
        for (Object value : values) {
            String json = mapper.writeValueAsString(new Holder(value));
            Object result = mapper.readValue(json, Holder.class).value;
            assertInstanceOf(value.getClass(), result);
            assertEquals(value, result);
        }
    }

    private <T> void _verifyRoundTrip(T value, String expectedJson,
            Class<?> readAs, Class<?> expectedType) throws Exception
    {
        String json = MAPPER.writeValueAsString(value);
        assertEquals(expectedJson, json);
        Object result = MAPPER.readValue(json, readAs);
        assertThat(result).isEqualTo(value);
        assertInstanceOf(readAs, result);
        if (expectedType != null) {
            assertInstanceOf(expectedType, result);
        }
    }

    private static boolean _isPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
