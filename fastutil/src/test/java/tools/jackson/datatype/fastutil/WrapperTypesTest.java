package tools.jackson.datatype.fastutil;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharLists;
import it.unimi.dsi.fastutil.doubles.DoubleBigArrayBigList;
import it.unimi.dsi.fastutil.doubles.DoubleBigLists;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.Object2BooleanLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMaps;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the unmodifiable, synchronized, singleton and empty wrappers created by
 * fastutil helper classes (like {@code IntLists.unmodifiable(...)}); their class names
 * show up as type ids with polymorphic typing.
 */
public class WrapperTypesTest extends ModuleTestBase
{
    static class Holder {
        public Object value;

        protected Holder() { }
        Holder(Object value) { this.value = value; }
    }

    private final ObjectMapper MAPPER = defaultTypingMapper(DefaultTyping.JAVA_LANG_OBJECT,
            JsonTypeInfo.As.PROPERTY);

    static Stream<Arguments> wrappers() {
        IntArrayList ints = new IntArrayList(new int[] { 3, 1, 2 });
        IntLinkedOpenHashSet intSet = new IntLinkedOpenHashSet(new int[] { 3, 1, 2 });
        IntRBTreeSet sortedInts = new IntRBTreeSet(new int[] { 3, 1, 2 });
        Int2LongLinkedOpenHashMap map = new Int2LongLinkedOpenHashMap();
        map.put(3, 30L);
        map.put(1, 10L);
        Int2LongRBTreeMap sortedMap = new Int2LongRBTreeMap(map);
        Long2ObjectLinkedOpenHashMap<String> refMap = new Long2ObjectLinkedOpenHashMap<>();
        refMap.put(5L, "five");
        Object2BooleanLinkedOpenHashMap<String> boolMap = new Object2BooleanLinkedOpenHashMap<>();
        boolMap.put("yes", true);
        DoubleBigArrayBigList bigList = new DoubleBigArrayBigList();
        bigList.add(0.5);

        return Stream.of(
                // unmodifiable
                Arguments.of(IntLists.unmodifiable(ints), true),
                Arguments.of(IntSets.unmodifiable(intSet), true),
                Arguments.of(IntSortedSets.unmodifiable(sortedInts), true),
                Arguments.of(IntCollections.unmodifiable(ints), true),
                Arguments.of(ShortCollections.unmodifiable(new ShortArrayList(new short[] { 1 })), true),
                Arguments.of(CharLists.unmodifiable(new CharArrayList(new char[] { 'a', 'b' })), true),
                Arguments.of(DoubleBigLists.unmodifiable(bigList), true),
                Arguments.of(Int2LongMaps.unmodifiable(map), true),
                Arguments.of(Int2LongSortedMaps.unmodifiable(sortedMap), true),
                Arguments.of(Long2ObjectMaps.unmodifiable(refMap), true),
                Arguments.of(Object2BooleanMaps.unmodifiable(boolMap), true),
                // synchronized
                Arguments.of(IntLists.synchronize(ints), false),
                Arguments.of(IntSets.synchronize(intSet), false),
                Arguments.of(IntSortedSets.synchronize(sortedInts), false),
                Arguments.of(Int2LongMaps.synchronize(map), false),
                Arguments.of(Int2LongSortedMaps.synchronize(sortedMap), false),
                // singletons
                Arguments.of(IntLists.singleton(7), true),
                Arguments.of(IntSets.singleton(7), true),
                Arguments.of(IntSortedSets.singleton(7), true),
                Arguments.of(LongBigLists.singleton(7L), true),
                Arguments.of(Int2LongMaps.singleton(7, 70L), true),
                Arguments.of(Int2LongSortedMaps.singleton(7, 70L), true),
                // empty
                Arguments.of(IntLists.emptyList(), true),
                Arguments.of(IntSets.emptySet(), true),
                Arguments.of(IntSortedSets.EMPTY_SET, true),
                Arguments.of(LongBigLists.EMPTY_BIG_LIST, true),
                Arguments.of(Int2LongMaps.EMPTY_MAP, true),
                Arguments.of(Int2LongSortedMaps.EMPTY_MAP, true));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("wrappers")
    void testWrapperRoundTrip(Object input, boolean unmodifiable) throws Exception
    {
        String json = MAPPER.writeValueAsString(new Holder(input));
        assertTrue(json.contains(input.getClass().getName()), json);

        Object result = MAPPER.readValue(json, Holder.class).value;
        if (input instanceof Collection && !(input instanceof java.util.List)
                && !(input instanceof java.util.Set)) {
            // Like `java.util.Collections.unmodifiableCollection()`, plain collection
            // wrappers do not implement `equals()`: compare contents instead
            assertEquals(new java.util.ArrayList<>((Collection<?>) input),
                    new java.util.ArrayList<>((Collection<?>) result));
        } else {
            assertEquals(input, result);
        }
        // Singleton and empty instances are replaced by unmodifiable wrappers,
        // others retain their exact type
        String simpleName = input.getClass().getSimpleName();
        if (!simpleName.startsWith("Singleton") && !simpleName.startsWith("Empty")) {
            assertInstanceOf(input.getClass(), result);
        }
        if (unmodifiable) {
            assertThrows(UnsupportedOperationException.class, () -> _modify(result));
        } else {
            _modify(result);
        }
    }

    @Test
    void testUnmodifiableListAsDeclaredType() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        IntList list = mapper.readValue("[1,2]", IntLists.UnmodifiableList.class);
        // `IntArrayList` is used as the intermediate value, so wrapper is random-access too
        assertInstanceOf(IntLists.UnmodifiableRandomAccessList.class, list);
        assertEquals(IntList.of(1, 2), list);
    }

    @Test
    @SuppressWarnings("deprecation")
    void testImmutableList() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        IntImmutableList list = mapper.readValue("[1,2,3]", IntImmutableList.class);
        assertEquals(IntImmutableList.of(1, 2, 3), list);
        assertThrows(UnsupportedOperationException.class, () -> list.add(4));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void _modify(Object container) {
        if (container instanceof Map) {
            ((Map) container).clear();
        } else {
            // `clear()` may be a no-op on empty unmodifiable collections, but `add()` is not
            Collection coll = (Collection) container;
            if (container instanceof it.unimi.dsi.fastutil.longs.LongCollection) {
                coll.add(1L);
            } else if (container instanceof it.unimi.dsi.fastutil.ints.IntCollection) {
                coll.add(1);
            } else {
                coll.clear();
            }
        }
    }
}
