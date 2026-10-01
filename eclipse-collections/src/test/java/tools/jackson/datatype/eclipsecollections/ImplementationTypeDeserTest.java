package tools.jackson.datatype.eclipsecollections;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.primitive.IntBag;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.primitive.ImmutableIntIntMap;
import org.eclipse.collections.api.map.primitive.IntIntMap;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.LongDoubleMap;
import org.eclipse.collections.api.map.primitive.ObjectIntMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.primitive.ImmutableLongSet;
import org.eclipse.collections.api.set.primitive.ShortSet;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.SortedBags;
import org.eclipse.collections.impl.factory.SortedSets;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.IntBags;
import org.eclipse.collections.impl.factory.primitive.IntIntMaps;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.LongDoubleMaps;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.LongSets;
import org.eclipse.collections.impl.factory.primitive.ObjectIntMaps;
import org.eclipse.collections.impl.factory.primitive.ShortSets;
import org.eclipse.collections.impl.list.mutable.primitive.IntArrayList;
import org.eclipse.collections.impl.map.mutable.primitive.IntIntHashMap;
import org.eclipse.collections.impl.map.mutable.primitive.ObjectIntHashMap;
import org.eclipse.collections.impl.stack.mutable.primitive.IntArrayStack;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for deserializing into concrete Eclipse Collections implementation types
 * (like {@code IntIntHashMap} or {@code ImmutableTripletonList}): these are what
 * type ids name when default typing is enabled.
 */
public class ImplementationTypeDeserTest extends ModuleTestBase
{
    static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    static class Holder {
        public IntIntMap intIntMap;
        public ObjectIntMap<String> objectIntMap;
        public IntObjectMap<Object> intObjectMap;
        public IntList intList;
        public ImmutableList<Object> immutableList;
        public ImmutableMap<String, Object> immutableMap;
        public Object anything;
    }

    private static final List<DefaultTyping> TYPINGS = Arrays.asList(
            DefaultTyping.NON_FINAL, DefaultTyping.OBJECT_AND_NON_CONCRETE);

    private ObjectMapper typingMapper(DefaultTyping typing) {
        return mapperBuilder()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), typing)
                .build();
    }

    private <T> void roundTrip(T value, TypeReference<T> type) throws Exception {
        for (DefaultTyping typing : TYPINGS) {
            ObjectMapper mapper = typingMapper(typing);
            String json = mapper.writerFor(type).writeValueAsString(value);
            Object result = mapper.readValue(json, type);
            assertEquals(value, result, typing + ": " + json);
            assertEquals(value.getClass(), result.getClass(), typing + ": " + json);
        }
    }

    @Test
    public void testPrimitiveMapsWithDefaultTyping() throws Exception
    {
        // used to silently come back empty
        roundTrip(IntIntMaps.mutable.of(1, 2, 3, 4), new TypeReference<IntIntMap>() { });
        roundTrip(ObjectIntMaps.mutable.of("a", 1), new TypeReference<ObjectIntMap<String>>() { });
        roundTrip(IntObjectMaps.mutable.of(1, "a"), new TypeReference<IntObjectMap<String>>() { });
        roundTrip(LongDoubleMaps.mutable.of(1L, 2.5), new TypeReference<LongDoubleMap>() { });
        roundTrip(IntIntMaps.immutable.of(1, 2), new TypeReference<ImmutableIntIntMap>() { });
        roundTrip(IntIntMaps.immutable.empty(), new TypeReference<ImmutableIntIntMap>() { });
    }

    @Test
    public void testPrimitiveCollectionsWithDefaultTyping() throws Exception
    {
        roundTrip(IntLists.mutable.of(1, 2, 3), new TypeReference<IntList>() { });
        roundTrip(IntLists.mutable.of(1, 2, 3), new TypeReference<MutableIntList>() { });
        roundTrip(IntLists.immutable.empty(), new TypeReference<ImmutableIntList>() { });
        roundTrip(IntLists.immutable.of(1), new TypeReference<ImmutableIntList>() { });
        roundTrip(IntLists.immutable.of(1, 2, 3), new TypeReference<ImmutableIntList>() { });
        roundTrip(LongLists.mutable.of(1L, 2L), new TypeReference<LongList>() { });
        roundTrip(BooleanLists.mutable.of(true, false), new TypeReference<BooleanList>() { });
        roundTrip(ByteLists.mutable.of((byte) 1, (byte) 2), new TypeReference<ByteList>() { });
        roundTrip(CharLists.mutable.of('a', 'b'), new TypeReference<CharList>() { });
        roundTrip(ShortSets.mutable.of((short) 1, (short) 2), new TypeReference<ShortSet>() { });
        roundTrip(LongSets.immutable.of(1L, 2L, 3L), new TypeReference<ImmutableLongSet>() { });
        roundTrip(IntBags.mutable.of(1, 1, 2), new TypeReference<IntBag>() { });
    }

    @Test
    public void testImmutableCollectionsWithDefaultTyping() throws Exception
    {
        // different sizes use different implementation classes
        roundTrip(Lists.immutable.empty(), new TypeReference<ImmutableList<Integer>>() { });
        roundTrip(Lists.immutable.of(1), new TypeReference<ImmutableList<Integer>>() { });
        roundTrip(Lists.immutable.of(1, 2), new TypeReference<ImmutableList<Integer>>() { });
        roundTrip(Lists.immutable.of(1, 2, 3), new TypeReference<ImmutableList<Integer>>() { });
        roundTrip(Lists.immutable.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12),
                new TypeReference<ImmutableList<Integer>>() { });
        roundTrip(Sets.immutable.of("a", "b"), new TypeReference<ImmutableSet<String>>() { });
        roundTrip(Bags.immutable.of("a", "a", "b"), new TypeReference<ImmutableBag<String>>() { });
        roundTrip(SortedSets.immutable.of("b", "a"), new TypeReference<ImmutableSortedSet<String>>() { });
        roundTrip(SortedBags.immutable.of("b", "a", "a"), new TypeReference<ImmutableSortedBag<String>>() { });
        roundTrip(Maps.immutable.of("a", "x"), new TypeReference<ImmutableMap<String, String>>() { });
        roundTrip(Maps.immutable.of("a", "x", "b", "y"), new TypeReference<ImmutableMap<String, String>>() { });
    }

    @Test
    public void testFixedSizeListWithDefaultTyping() throws Exception
    {
        roundTrip(Lists.fixedSize.of("a", "b"), new TypeReference<FixedSizeList<String>>() { });
    }

    @Test
    public void testPojoWithDefaultTyping() throws Exception
    {
        Holder input = new Holder();
        input.intIntMap = IntIntMaps.mutable.of(1, 2);
        input.objectIntMap = ObjectIntMaps.mutable.of("a", 1);
        input.intObjectMap = IntObjectMaps.mutable.of(1, Lists.immutable.of("x", 2L));
        input.intList = IntLists.immutable.of(1, 2, 3);
        input.immutableList = Lists.immutable.of("a", 1.5, IntLists.mutable.of(7));
        input.immutableMap = Maps.immutable.of("k", IntIntMaps.mutable.of(3, 4));
        input.anything = IntIntMaps.immutable.of(5, 6);

        for (DefaultTyping typing : TYPINGS) {
            ObjectMapper mapper = typingMapper(typing);
            String json = mapper.writeValueAsString(input);
            Holder result = mapper.readValue(json, Holder.class);
            assertEquals(input.intIntMap, result.intIntMap, json);
            assertEquals(input.objectIntMap, result.objectIntMap, json);
            assertEquals(input.intObjectMap, result.intObjectMap, json);
            assertEquals(input.intList, result.intList, json);
            assertEquals(input.immutableList, result.immutableList, json);
            assertEquals(input.immutableMap, result.immutableMap, json);
            assertEquals(input.anything, result.anything, json);
        }
    }

    // Concrete types requested directly, without default typing
    @Test
    public void testConcreteTypesWithoutDefaultTyping() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        // used to silently come back empty
        assertEquals(IntIntMaps.mutable.of(1, 2, 3, 4),
                mapper.readValue("{\"1\":2,\"3\":4}", IntIntHashMap.class));
        assertEquals(ObjectIntMaps.mutable.of("a", 1),
                mapper.readValue("{\"a\":1}", new TypeReference<ObjectIntHashMap<String>>() { }));
        assertEquals(IntLists.mutable.of(1, 2, 3),
                mapper.readValue("[1,2,3]", IntArrayList.class));
    }

    // Concrete types that cannot be produced by the matching deserializer are reported
    @Test
    public void testUnsupportedConcreteType() throws Exception
    {
        try {
            // `IntArrayStack` is an `IntIterable`, but deserializer for that builds an `IntArrayList`
            mapperWithModule().readValue("[1,2]", IntArrayStack.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "IntArrayStack");
        }
    }
}
