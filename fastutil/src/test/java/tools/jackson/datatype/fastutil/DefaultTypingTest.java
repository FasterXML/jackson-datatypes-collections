package tools.jackson.datatype.fastutil;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.exc.InvalidTypeIdException;

import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.bytes.ByteOpenHashSet;
import it.unimi.dsi.fastutil.bytes.ByteSet;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.doubles.Double2ObjectMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.floats.FloatBigArrayBigList;
import it.unimi.dsi.fastutil.floats.FloatBigList;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.Long2CharLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2CharMap;
import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import it.unimi.dsi.fastutil.objects.*;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortImmutableList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for fastutil types with polymorphic type handling, both via "default typing"
 * and via {@code @JsonTypeInfo}.
 */
public class DefaultTypingTest extends ModuleTestBase
{
    // Non-final, so that NON_FINAL typing applies to values too
    static class Point {
        public int x, y;

        protected Point() { }
        Point(int x, int y) { this.x = x; this.y = y; }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Point) && ((Point) o).x == x && ((Point) o).y == y;
        }

        @Override
        public int hashCode() { return Objects.hash(x, y); }

        @Override
        public String toString() { return "Point(" + x + "," + y + ")"; }
    }

    static class Bean {
        public IntList ints;
        public BooleanList booleans;
        public ByteSet bytes;
        public ShortCollection shorts;
        public CharList chars;
        public LongSortedSet longs;
        public FloatBigList floats;
        public Int2IntMap intToInt;
        public Long2CharMap longToChar;
        public Int2ObjectMap<Point> intToPoint;
        public Double2ObjectMap<Object> doubleToAny;
        public Object2IntMap<String> stringToInt;
        public Object2DoubleSortedMap<String> stringToDouble;
        public ObjectList<Point> points;
        public Object2ObjectMap<String, Point> stringToPoint;
        public Object untyped;
        public Object untypedMap;

        static Bean create() {
            Bean bean = new Bean();
            bean.ints = new IntArrayList(new int[] { 1, 2, 3 });
            bean.booleans = new BooleanArrayList(new boolean[] { true, false });
            bean.bytes = new ByteOpenHashSet(new byte[] { 1, 2 });
            bean.shorts = new ShortImmutableList(new short[] { 7, 8 });
            bean.chars = new CharArrayList(new char[] { 'a', 'b' });
            bean.longs = new LongRBTreeSet(new long[] { 5L, Long.MAX_VALUE });
            bean.floats = new FloatBigArrayBigList();
            bean.floats.add(0.5f);
            bean.intToInt = new Int2IntLinkedOpenHashMap();
            bean.intToInt.put(1, 10);
            bean.intToInt.put(2, 20);
            bean.longToChar = new Long2CharLinkedOpenHashMap();
            bean.longToChar.put(3L, 'z');
            bean.intToPoint = new Int2ObjectOpenHashMap<>();
            bean.intToPoint.put(1, new Point(1, 2));
            bean.intToPoint.put(2, null);
            bean.doubleToAny = new Double2ObjectRBTreeMap<>();
            bean.doubleToAny.put(0.5, new Point(3, 4));
            bean.doubleToAny.put(1.5, "text");
            bean.doubleToAny.put(2.5, new IntArrayList(new int[] { 4, 5 }));
            bean.stringToInt = new Object2IntOpenHashMap<>();
            bean.stringToInt.put("a", 1);
            bean.stringToDouble = new Object2DoubleRBTreeMap<>();
            bean.stringToDouble.put("b", 2.5);
            bean.points = new ObjectArrayList<>();
            bean.points.add(new Point(5, 6));
            bean.stringToPoint = new Object2ObjectLinkedOpenHashMap<>();
            bean.stringToPoint.put("p", new Point(7, 8));
            bean.untyped = IntSets.unmodifiable(new IntLinkedOpenHashSet(new int[] { 9, 8 }));
            Int2LongMap map = new Int2LongAVLTreeMap();
            map.put(1, 2L);
            bean.untypedMap = map;
            return bean;
        }

        /**
         * @param typesRetained Whether type ids are written for all fields (if not,
         *    only for `Object` valued ones), so that concrete types are retained
         */
        void assertEqualTo(Bean other, boolean typesRetained) {
            final BiConsumer<Object, Object> check = typesRetained
                    ? DefaultTypingTest::assertSameTypeAndContents
                    : Assertions::assertEquals;
            check.accept(ints, other.ints);
            check.accept(booleans, other.booleans);
            check.accept(bytes, other.bytes);
            check.accept(shorts, other.shorts);
            check.accept(chars, other.chars);
            check.accept(longs, other.longs);
            check.accept(floats, other.floats);
            check.accept(intToInt, other.intToInt);
            check.accept(longToChar, other.longToChar);
            check.accept(intToPoint, other.intToPoint);
            check.accept(doubleToAny, other.doubleToAny);
            check.accept(stringToInt, other.stringToInt);
            check.accept(stringToDouble, other.stringToDouble);
            check.accept(points, other.points);
            check.accept(stringToPoint, other.stringToPoint);
            // Values of `Object` valued properties always have type ids
            assertSameTypeAndContents(untyped, other.untyped);
            assertSameTypeAndContents(untypedMap, other.untypedMap);
            for (double key : doubleToAny.keySet()) {
                assertSameTypeAndContents(doubleToAny.get(key), other.doubleToAny.get(key));
            }
        }
    }

    static void assertSameTypeAndContents(Object expected, Object actual) {
        assertNotNull(actual);
        assertInstanceOf(expected.getClass(), actual);
        assertEquals(expected, actual);
    }

    static class Holder {
        public Object value;

        protected Holder() { }
        Holder(Object value) { this.value = value; }
    }

    static class AnnotatedHolder {
        // On a non-container property, applies to the value itself
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "type")
        public Object map;

        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public Object chars;

        // On a container property, applies to the values
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
        public Int2ObjectMap<Point> points;

        // ... which for primitive values is not applicable
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
        public IntList ints;
    }

    static Stream<Arguments> typingModes() {
        return Stream.of(
                Arguments.of(DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY),
                Arguments.of(DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY),
                Arguments.of(DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT),
                Arguments.of(DefaultTyping.NON_FINAL_AND_ENUMS, JsonTypeInfo.As.PROPERTY),
                Arguments.of(DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.PROPERTY),
                Arguments.of(DefaultTyping.OBJECT_AND_NON_CONCRETE, JsonTypeInfo.As.WRAPPER_ARRAY),
                Arguments.of(DefaultTyping.NON_CONCRETE_AND_ARRAYS, JsonTypeInfo.As.WRAPPER_OBJECT),
                Arguments.of(DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.As.PROPERTY),
                Arguments.of(DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.As.WRAPPER_ARRAY),
                Arguments.of(DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.As.WRAPPER_OBJECT));
    }

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("typingModes")
    void testBeanRoundTrip(DefaultTyping typing, JsonTypeInfo.As inclusion) throws Exception
    {
        ObjectMapper mapper = defaultTypingMapper(typing, inclusion);
        Bean input = Bean.create();
        String json = mapper.writeValueAsString(input);
        Bean result = mapper.readValue(json, Bean.class);
        input.assertEqualTo(result, typing != DefaultTyping.JAVA_LANG_OBJECT);
    }

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("typingModes")
    void testBeanRoundTripWithCharArrays(DefaultTyping typing, JsonTypeInfo.As inclusion) throws Exception
    {
        ObjectMapper mapper = mapperBuilder()
                .activateDefaultTyping(PTV, typing, inclusion)
                .enable(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                .build();
        Bean input = Bean.create();
        String json = mapper.writeValueAsString(input);
        assertThat(json).contains("[\"a\",\"b\"]");
        input.assertEqualTo(mapper.readValue(json, Bean.class),
                typing != DefaultTyping.JAVA_LANG_OBJECT);
    }

    @ParameterizedTest(name = "{0}, {1}")
    @MethodSource("typingModes")
    void testNestedContainersAsObject(DefaultTyping typing, JsonTypeInfo.As inclusion) throws Exception
    {
        ObjectMapper mapper = defaultTypingMapper(typing, inclusion);
        Int2ObjectMap<Object> nested = new Int2ObjectLinkedOpenHashMap<>();
        nested.put(1, new LongRBTreeSet(new long[] { 3L, 1L }));
        nested.put(2, new CharArrayList(new char[] { 'q' }));
        Object2ObjectMap<String, Object> inner = new Object2ObjectOpenHashMap<>();
        inner.put("x", new IntOpenHashSet(new int[] { 4 }));
        nested.put(3, inner);

        String json = mapper.writeValueAsString(new Holder(nested));
        Holder result = mapper.readValue(json, Holder.class);
        assertSameTypeAndContents(nested, result.value);
        @SuppressWarnings("unchecked")
        Int2ObjectMap<Object> map = (Int2ObjectMap<Object>) result.value;
        assertInstanceOf(LongRBTreeSet.class, map.get(1));
        assertInstanceOf(CharArrayList.class, map.get(2));
        assertInstanceOf(Object2ObjectOpenHashMap.class, map.get(3));
        assertInstanceOf(IntOpenHashSet.class, ((Object2ObjectMap<?, ?>) map.get(3)).get("x"));
    }

    @Test
    void testTypeIdsWritten() throws Exception
    {
        ObjectMapper mapper = defaultTypingMapper(DefaultTyping.JAVA_LANG_OBJECT,
                JsonTypeInfo.As.WRAPPER_ARRAY);
        assertEquals(a2q("{'value':['it.unimi.dsi.fastutil.ints.IntArrayList',[1,2]]}"),
                mapper.writeValueAsString(new Holder(new IntArrayList(new int[] { 1, 2 }))));
        assertEquals(a2q("{'value':['it.unimi.dsi.fastutil.chars.CharArrayList','ab']}"),
                mapper.writeValueAsString(new Holder(new CharArrayList(new char[] { 'a', 'b' }))));
        Int2IntMap map = new Int2IntArrayMap();
        map.put(1, 2);
        assertEquals(a2q("{'value':['it.unimi.dsi.fastutil.ints.Int2IntArrayMap',{'1':2}]}"),
                mapper.writeValueAsString(new Holder(map)));

        mapper = defaultTypingMapper(DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.As.PROPERTY);
        assertEquals(a2q("{'value':{'@class':'it.unimi.dsi.fastutil.ints.Int2IntArrayMap','1':2}}"),
                mapper.writeValueAsString(new Holder(map)));
    }

    @Test
    void testRootValueRoundTrip() throws Exception
    {
        ObjectMapper mapper = defaultTypingMapper(DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        IntList list = new IntArrayList(new int[] { 1, 2, 3 });
        String json = mapper.writerFor(IntList.class).writeValueAsString(list);
        assertEquals(a2q("['it.unimi.dsi.fastutil.ints.IntArrayList',[1,2,3]]"), json);
        assertSameTypeAndContents(list, mapper.readValue(json, IntList.class));
        assertSameTypeAndContents(list, mapper.readValue(json, IntCollection.class));

        Object2IntMap<String> map = new Object2IntLinkedOpenHashMap<>();
        map.put("a", 1);
        json = mapper.writerFor(Object2IntMap.class).writeValueAsString(map);
        assertSameTypeAndContents(map, mapper.readValue(json, Object2IntMap.class));
    }

    @Test
    void testJsonTypeInfoAnnotations() throws Exception
    {
        ObjectMapper mapper = mapperBuilder().polymorphicTypeValidator(PTV).build();
        AnnotatedHolder input = new AnnotatedHolder();
        Int2DoubleMap map = new Int2DoubleOpenHashMap();
        map.put(1, 0.5);
        input.map = map;
        input.chars = CharArrayList.wrap(new char[] { 'x', 'y' });
        input.points = new Int2ObjectArrayMap<>();
        input.points.put(3, new Point(1, 2));
        input.ints = new IntArrayList(new int[] { 3, 1, 2 });

        String json = mapper.writeValueAsString(input);
        assertEquals(a2q("{'chars':['it.unimi.dsi.fastutil.chars.CharArrayList','xy'],"
                + "'ints':[3,1,2],"
                + "'map':{'type':'it.unimi.dsi.fastutil.ints.Int2DoubleOpenHashMap','1':0.5},"
                + "'points':{'3':{'tools.jackson.datatype.fastutil.DefaultTypingTest$Point':{'x':1,'y':2}}}}"),
                json);

        AnnotatedHolder result = mapper.readValue(json, AnnotatedHolder.class);
        assertSameTypeAndContents(input.map, result.map);
        assertSameTypeAndContents(input.chars, result.chars);
        // default implementation, as there is no type id for the map itself
        assertInstanceOf(Int2ObjectOpenHashMap.class, result.points);
        assertEquals(input.points, result.points);
        assertSameTypeAndContents(input.ints, result.ints);
    }

    @Test
    void testDisallowedSubtypeRejected() throws Exception
    {
        // Validator that does not allow fastutil types
        ObjectMapper mapper = mapperBuilder()
                .activateDefaultTyping(tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("java.").build(),
                        DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.As.WRAPPER_ARRAY)
                .build();
        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(a2q("{'value':['it.unimi.dsi.fastutil.ints.IntArrayList',[1]]}"),
                        Holder.class));
        verifyException(e, "denied resolution");
    }

    @Test
    void testIncompatibleTypeIdRejected() throws Exception
    {
        ObjectMapper mapper = defaultTypingMapper(DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        Exception e = assertThrows(Exception.class,
                () -> mapper.readValue(a2q("['it.unimi.dsi.fastutil.longs.LongArrayList',[1]]"),
                        IntList.class));
        verifyException(e, "not subtype of", "not a subtype");
    }
}
