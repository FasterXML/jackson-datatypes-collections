package tools.jackson.datatype.fastutil;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;

import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.chars.*;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for deserialization features, coercions and error handling.
 */
public class DeserializationFeaturesTest extends ModuleTestBase
{
    static class Bean {
        public IntList ints;
        public Int2LongMap map;
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    void testNulls() throws Exception
    {
        // root-level null
        assertNull(MAPPER.readValue("null", IntList.class));
        assertNull(MAPPER.readValue("null", Int2IntMap.class));

        // null property values
        Bean bean = MAPPER.readValue(a2q("{'ints':null,'map':null}"), Bean.class);
        assertNull(bean.ints);
        assertNull(bean.map);

        // null elements are rejected by default (as with primitive arrays)...
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("[1,null,3]", IntList.class));
        verifyException(e, "FAIL_ON_NULL_FOR_PRIMITIVES");

        // ... but become default values if allowed
        ObjectMapper lenient = mapperBuilder()
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
        assertEquals(IntList.of(1, 0, 3), lenient.readValue("[1,null,3]", IntList.class));
        assertEquals(BooleanList.of(true, false), lenient.readValue("[true,null]", BooleanList.class));
        assertEquals(DoubleList.of(0.0), lenient.readValue("[null]", DoubleList.class));
    }

    @Test
    void testCoercions() throws Exception
    {
        // Numbers in Strings, and floating-point values for integral types
        assertEquals(IntList.of(1, 2), MAPPER.readValue("[\"1\",2]", IntList.class));
        assertEquals(LongList.of(5L), MAPPER.readValue("[5.0]", LongList.class));
        assertEquals(DoubleList.of(1.0, 2.5), MAPPER.readValue("[1,\"2.5\"]", DoubleList.class));
        assertEquals(BooleanList.of(true), MAPPER.readValue("[\"true\"]", BooleanList.class));
    }

    @Test
    void testOverflow() throws Exception
    {
        JacksonException e = assertThrows(JacksonException.class,
                () -> MAPPER.readValue("[100000]", ShortList.class));
        verifyException(e, "out of range");
        e = assertThrows(JacksonException.class,
                () -> MAPPER.readValue("[" + Long.MAX_VALUE + "]", IntList.class));
        verifyException(e, "out of range");
    }

    @Test
    void testSingleValueAsArray() throws Exception
    {
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("7", IntList.class));
        ObjectMapper mapper = mapperBuilder()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        assertEquals(IntList.of(7), mapper.readValue("7", IntList.class));
        assertEquals(LongSet.of(7L), mapper.readValue("7", LongSet.class));
    }

    @Test
    void testUnwrapSingleElementOnSerialization() throws Exception
    {
        ObjectMapper mapper = mapperBuilder()
                .enable(tools.jackson.databind.SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .build();
        assertEquals("7", mapper.writeValueAsString(IntList.of(7)));
        assertEquals("[7,8]", mapper.writeValueAsString(IntList.of(7, 8)));
    }

    @Test
    void testInvalidCollectionInput() throws Exception
    {
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("{}", IntList.class));
        verifyException(e, "IntList");
        e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("[{}]", IntList.class));
        verifyException(e, "int");
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("[\"abc\"]", IntList.class));
    }

    @Test
    void testChars() throws Exception
    {
        assertEquals(CharList.of('a', 'b'), MAPPER.readValue("\"ab\"", CharList.class));
        assertEquals(CharList.of('a', 'b'), MAPPER.readValue("[\"a\",\"b\"]", CharList.class));
        assertEquals(new CharRBTreeSet(new char[] { 'a', 'b' }), MAPPER.readValue("\"bab\"", CharSortedSet.class));
        assertEquals(CharList.of(), MAPPER.readValue("\"\"", CharList.class));

        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("[\"ab\"]", CharList.class));
        verifyException(e, "length 2");
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("12", CharList.class));
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("{}", CharList.class));
    }

    @Test
    void testInvalidMapInput() throws Exception
    {
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("[1]", Int2IntMap.class));
        verifyException(e, "Int2IntMap");
        e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("{\"x\":1}", Int2IntMap.class));
        verifyException(e, "Cannot parse 'x'");
        e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue("{\"ab\":1}", Char2IntMap.class));
        verifyException(e, "length 2");
    }

    @Test
    void testMapKeyTypes() throws Exception
    {
        // Custom key types for reference-keyed maps
        Object2IntMap<java.util.UUID> map = MAPPER.readValue(
                "{\"00000000-0000-0000-0000-000000000001\":3}",
                new TypeReference<Object2IntMap<java.util.UUID>>() { });
        assertInstanceOf(Object2IntOpenHashMap.class, map);
        assertEquals(3, map.getInt(new java.util.UUID(0L, 1L)));
    }

    @Test
    void testGenericValueTypes() throws Exception
    {
        Int2ObjectMap<LongList> map = MAPPER.readValue("{\"1\":[2,3]}",
                new TypeReference<Int2ObjectMap<LongList>>() { });
        assertInstanceOf(LongArrayList.class, map.get(1));
        assertEquals(LongList.of(2L, 3L), map.get(1));
    }
}
