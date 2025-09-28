package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import org.eclipse.collections.api.BooleanIterable;
import org.eclipse.collections.api.ByteIterable;
import org.eclipse.collections.api.CharIterable;
import org.eclipse.collections.api.DoubleIterable;
import org.eclipse.collections.api.FloatIterable;
import org.eclipse.collections.api.IntIterable;
import org.eclipse.collections.api.InternalIterable;
import org.eclipse.collections.api.LongIterable;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.ShortIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.bag.primitive.BooleanBag;
import org.eclipse.collections.api.bag.primitive.ByteBag;
import org.eclipse.collections.api.bag.primitive.CharBag;
import org.eclipse.collections.api.bag.primitive.DoubleBag;
import org.eclipse.collections.api.bag.primitive.FloatBag;
import org.eclipse.collections.api.bag.primitive.ImmutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.ImmutableByteBag;
import org.eclipse.collections.api.bag.primitive.ImmutableCharBag;
import org.eclipse.collections.api.bag.primitive.ImmutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.ImmutableFloatBag;
import org.eclipse.collections.api.bag.primitive.ImmutableIntBag;
import org.eclipse.collections.api.bag.primitive.ImmutableLongBag;
import org.eclipse.collections.api.bag.primitive.ImmutableShortBag;
import org.eclipse.collections.api.bag.primitive.IntBag;
import org.eclipse.collections.api.bag.primitive.LongBag;
import org.eclipse.collections.api.bag.primitive.MutableBooleanBag;
import org.eclipse.collections.api.bag.primitive.MutableByteBag;
import org.eclipse.collections.api.bag.primitive.MutableCharBag;
import org.eclipse.collections.api.bag.primitive.MutableDoubleBag;
import org.eclipse.collections.api.bag.primitive.MutableFloatBag;
import org.eclipse.collections.api.bag.primitive.MutableIntBag;
import org.eclipse.collections.api.bag.primitive.MutableLongBag;
import org.eclipse.collections.api.bag.primitive.MutableShortBag;
import org.eclipse.collections.api.bag.primitive.ShortBag;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.api.collection.FixedSizeCollection;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableByteCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableCharCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableIntCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableLongCollection;
import org.eclipse.collections.api.collection.primitive.ImmutableShortCollection;
import org.eclipse.collections.api.collection.primitive.MutableBooleanCollection;
import org.eclipse.collections.api.collection.primitive.MutableByteCollection;
import org.eclipse.collections.api.collection.primitive.MutableCharCollection;
import org.eclipse.collections.api.collection.primitive.MutableDoubleCollection;
import org.eclipse.collections.api.collection.primitive.MutableFloatCollection;
import org.eclipse.collections.api.collection.primitive.MutableIntCollection;
import org.eclipse.collections.api.collection.primitive.MutableLongCollection;
import org.eclipse.collections.api.collection.primitive.MutableShortCollection;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.list.primitive.BooleanList;
import org.eclipse.collections.api.list.primitive.ByteList;
import org.eclipse.collections.api.list.primitive.CharList;
import org.eclipse.collections.api.list.primitive.DoubleList;
import org.eclipse.collections.api.list.primitive.FloatList;
import org.eclipse.collections.api.list.primitive.ImmutableBooleanList;
import org.eclipse.collections.api.list.primitive.ImmutableByteList;
import org.eclipse.collections.api.list.primitive.ImmutableCharList;
import org.eclipse.collections.api.list.primitive.ImmutableDoubleList;
import org.eclipse.collections.api.list.primitive.ImmutableFloatList;
import org.eclipse.collections.api.list.primitive.ImmutableIntList;
import org.eclipse.collections.api.list.primitive.ImmutableLongList;
import org.eclipse.collections.api.list.primitive.ImmutableShortList;
import org.eclipse.collections.api.list.primitive.IntList;
import org.eclipse.collections.api.list.primitive.LongList;
import org.eclipse.collections.api.list.primitive.MutableBooleanList;
import org.eclipse.collections.api.list.primitive.MutableByteList;
import org.eclipse.collections.api.list.primitive.MutableCharList;
import org.eclipse.collections.api.list.primitive.MutableDoubleList;
import org.eclipse.collections.api.list.primitive.MutableFloatList;
import org.eclipse.collections.api.list.primitive.MutableIntList;
import org.eclipse.collections.api.list.primitive.MutableLongList;
import org.eclipse.collections.api.list.primitive.MutableShortList;
import org.eclipse.collections.api.list.primitive.ShortList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.UnsortedMapIterable;
import org.eclipse.collections.api.map.sorted.ImmutableSortedMap;
import org.eclipse.collections.api.map.sorted.MutableSortedMap;
import org.eclipse.collections.api.map.sorted.SortedMapIterable;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.api.set.primitive.BooleanSet;
import org.eclipse.collections.api.set.primitive.ByteSet;
import org.eclipse.collections.api.set.primitive.CharSet;
import org.eclipse.collections.api.set.primitive.DoubleSet;
import org.eclipse.collections.api.set.primitive.FloatSet;
import org.eclipse.collections.api.set.primitive.ImmutableBooleanSet;
import org.eclipse.collections.api.set.primitive.ImmutableByteSet;
import org.eclipse.collections.api.set.primitive.ImmutableCharSet;
import org.eclipse.collections.api.set.primitive.ImmutableDoubleSet;
import org.eclipse.collections.api.set.primitive.ImmutableFloatSet;
import org.eclipse.collections.api.set.primitive.ImmutableIntSet;
import org.eclipse.collections.api.set.primitive.ImmutableLongSet;
import org.eclipse.collections.api.set.primitive.ImmutableShortSet;
import org.eclipse.collections.api.set.primitive.IntSet;
import org.eclipse.collections.api.set.primitive.LongSet;
import org.eclipse.collections.api.set.primitive.MutableBooleanSet;
import org.eclipse.collections.api.set.primitive.MutableByteSet;
import org.eclipse.collections.api.set.primitive.MutableCharSet;
import org.eclipse.collections.api.set.primitive.MutableDoubleSet;
import org.eclipse.collections.api.set.primitive.MutableFloatSet;
import org.eclipse.collections.api.set.primitive.MutableIntSet;
import org.eclipse.collections.api.set.primitive.MutableLongSet;
import org.eclipse.collections.api.set.primitive.MutableShortSet;
import org.eclipse.collections.api.set.primitive.ShortSet;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Triple;
import org.eclipse.collections.api.tuple.Triplet;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.SortedBags;
import org.eclipse.collections.impl.factory.SortedMaps;
import org.eclipse.collections.impl.factory.primitive.BooleanBags;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.BooleanSets;
import org.eclipse.collections.impl.factory.primitive.ByteBags;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.ByteSets;
import org.eclipse.collections.impl.factory.primitive.CharBags;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.CharSets;
import org.eclipse.collections.impl.factory.primitive.DoubleBags;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.DoubleSets;
import org.eclipse.collections.impl.factory.primitive.FloatBags;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.FloatSets;
import org.eclipse.collections.impl.factory.primitive.IntBags;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.factory.primitive.LongBags;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.LongSets;
import org.eclipse.collections.impl.factory.primitive.ShortBags;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import org.eclipse.collections.impl.factory.primitive.ShortSets;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class DeserializerTest extends ModuleTestBase {
    private <T> void testCollection(T expected, String json, TypeReference<T> type) throws Exception {
        ObjectMapper objectMapper = mapperWithModule();
        T value = objectMapper.readValue(json, type);
        assertEquals(expected, value);
        assertTrue(objectMapper.getTypeFactory().constructType(type).getRawClass().isInstance(value));
    }

    private <T> void testCollection(T expected, String json, Class<T> type) throws Exception {
        T value = mapperWithModule().readValue(json, type);
        assertEquals(expected, value);
        assertTrue(type.isInstance(value));
    }

    @Test
    public void immutableBag() throws Exception {
        testCollection(Bags.immutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<ImmutableBag<String>>() {});
        testCollection(BooleanBags.immutable.of(true, false, true), "[true, false, true]", ImmutableBooleanBag.class);
        testCollection(ByteBags.immutable.of((byte) 1, (byte) 2, (byte) 3), "[3, 2, 1]", ImmutableByteBag.class);
        testCollection(ShortBags.immutable.of((short) 1, (short) 2, (short) 3), "[3, 2, 1]", ImmutableShortBag.class);
        testCollection(CharBags.immutable.of('a', 'b', 'c'), "\"cba\"", ImmutableCharBag.class);
        testCollection(IntBags.immutable.of(1, 2, 3), "[3, 2, 1]", ImmutableIntBag.class);
        testCollection(FloatBags.immutable.of(1.1F, 2.3F, 3.5F), "[3.5, 2.3, 1.1]", ImmutableFloatBag.class);
        testCollection(LongBags.immutable.of(1, 2, 3), "[3, 2, 1]", ImmutableLongBag.class);
        testCollection(DoubleBags.immutable.of(1.1, 2.3, 3.5), "[3.5, 2.3, 1.1]", ImmutableDoubleBag.class);
    }

    @Test
    public void immutableSortedBag() throws Exception {
        testCollection(SortedBags.immutable.of("3", "2", "1"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<ImmutableSortedBag<String>>() {});
    }

    @Test
    public void mutableBag() throws Exception {
        testCollection(Bags.mutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<MutableBag<String>>() {});
        testCollection(BooleanBags.mutable.of(true, false, true), "[true, false, true]", MutableBooleanBag.class);
        testCollection(ByteBags.mutable.of((byte) 1, (byte) 2, (byte) 3), "[3, 2, 1]", MutableByteBag.class);
        testCollection(ShortBags.mutable.of((short) 1, (short) 2, (short) 3), "[3, 2, 1]", MutableShortBag.class);
        testCollection(CharBags.mutable.of('a', 'b', 'c'), "\"cba\"", MutableCharBag.class);
        testCollection(IntBags.mutable.of(1, 2, 3), "[3, 2, 1]", MutableIntBag.class);
        testCollection(FloatBags.mutable.of(1.1F, 2.3F, 3.5F), "[3.5, 2.3, 1.1]", MutableFloatBag.class);
        testCollection(LongBags.mutable.of(1, 2, 3), "[3, 2, 1]", MutableLongBag.class);
        testCollection(DoubleBags.mutable.of(1.1, 2.3, 3.5), "[3.5, 2.3, 1.1]", MutableDoubleBag.class);
    }

    @Test
    public void mutableSortedBag() throws Exception {
        testCollection(SortedBags.mutable.of("3", "2", "1"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<MutableSortedBag<String>>() {});
    }

    @Test
    public void bag() throws Exception {
        testCollection(Bags.mutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<Bag<String>>() {});
        testCollection(BooleanBags.mutable.of(true, false, true), "[true, false, true]", BooleanBag.class);
        testCollection(ByteBags.mutable.of((byte) 1, (byte) 2, (byte) 3), "[3, 2, 1]", ByteBag.class);
        testCollection(ShortBags.mutable.of((short) 1, (short) 2, (short) 3), "[3, 2, 1]", ShortBag.class);
        testCollection(CharBags.mutable.of('a', 'b', 'c'), "\"cba\"", CharBag.class);
        testCollection(IntBags.mutable.of(1, 2, 3), "[3, 2, 1]", IntBag.class);
        testCollection(FloatBags.mutable.of(1.1F, 2.3F, 3.5F), "[3.5, 2.3, 1.1]", FloatBag.class);
        testCollection(LongBags.mutable.of(1, 2, 3), "[3, 2, 1]", LongBag.class);
        testCollection(DoubleBags.mutable.of(1.1, 2.3, 3.5), "[3.5, 2.3, 1.1]", DoubleBag.class);
    }

    @Test
    public void immutableList() throws Exception {
        testCollection(Lists.immutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<ImmutableList<String>>() {});
        testCollection(BooleanLists.immutable.of(true, false, true), "[true, false, true]", ImmutableBooleanList.class);
        testCollection(ByteLists.immutable.of((byte) 1, (byte) 2, (byte) 3), "[1, 2, 3]", ImmutableByteList.class);
        testCollection(ShortLists.immutable.of((short) 1, (short) 2, (short) 3), "[1, 2, 3]", ImmutableShortList.class);
        testCollection(CharLists.immutable.of('a', 'b', 'c'), "\"abc\"", ImmutableCharList.class);
        testCollection(IntLists.immutable.of(1, 2, 3), "[1, 2, 3]", ImmutableIntList.class);
        testCollection(FloatLists.immutable.of(1.1F, 2.3F, 3.5F), "[1.1, 2.3, 3.5]", ImmutableFloatList.class);
        testCollection(LongLists.immutable.of(1, 2, 3), "[1, 2, 3]", ImmutableLongList.class);
        testCollection(DoubleLists.immutable.of(1.1, 2.3, 3.5), "[1.1, 2.3, 3.5]", ImmutableDoubleList.class);
    }

    @Test
    public void mutableList() throws Exception {
        testCollection(Lists.mutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<MutableList<String>>() {});
        testCollection(BooleanLists.mutable.of(true, false, true), "[true, false, true]", MutableBooleanList.class);
        testCollection(ByteLists.mutable.of((byte) 1, (byte) 2, (byte) 3), "[1, 2, 3]", MutableByteList.class);
        testCollection(ShortLists.mutable.of((short) 1, (short) 2, (short) 3), "[1, 2, 3]", MutableShortList.class);
        testCollection(CharLists.mutable.of('a', 'b', 'c'), "\"abc\"", MutableCharList.class);
        testCollection(IntLists.mutable.of(1, 2, 3), "[1, 2, 3]", MutableIntList.class);
        testCollection(FloatLists.mutable.of(1.1F, 2.3F, 3.5F), "[1.1, 2.3, 3.5]", MutableFloatList.class);
        testCollection(LongLists.mutable.of(1, 2, 3), "[1, 2, 3]", MutableLongList.class);
        testCollection(DoubleLists.mutable.of(1.1, 2.3, 3.5), "[1.1, 2.3, 3.5]", MutableDoubleList.class);
    }

    @Test
    public void fixedSizeList() throws Exception {
        testCollection(Lists.fixedSize.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<FixedSizeList<String>>() {});
        testCollection(Lists.fixedSize.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<FixedSizeCollection<String>>() {});
    }

    @Test
    public void list() throws Exception {
        testCollection(BooleanLists.mutable.of(true, false, true), "[true, false, true]", BooleanList.class);
        testCollection(ByteLists.mutable.of((byte) 1, (byte) 2, (byte) 3), "[1, 2, 3]", ByteList.class);
        testCollection(ShortLists.mutable.of((short) 1, (short) 2, (short) 3), "[1, 2, 3]", ShortList.class);
        testCollection(CharLists.mutable.of('a', 'b', 'c'), "\"abc\"", CharList.class);
        testCollection(IntLists.mutable.of(1, 2, 3), "[1, 2, 3]", IntList.class);
        testCollection(FloatLists.mutable.of(1.1F, 2.3F, 3.5F), "[1.1, 2.3, 3.5]", FloatList.class);
        testCollection(LongLists.mutable.of(1, 2, 3), "[1, 2, 3]", LongList.class);
        testCollection(DoubleLists.mutable.of(1.1, 2.3, 3.5), "[1.1, 2.3, 3.5]", DoubleList.class);
    }

    @Test
    public void iterable() throws Exception {
        testCollection(Lists.mutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<RichIterable<String>>() {});
        testCollection(Lists.mutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<InternalIterable<String>>() {});
        testCollection(BooleanLists.mutable.of(true, false, true), "[true, false, true]", BooleanIterable.class);
        testCollection(ByteLists.mutable.of((byte) 1, (byte) 2, (byte) 3), "[1, 2, 3]", ByteIterable.class);
        testCollection(ShortLists.mutable.of((short) 1, (short) 2, (short) 3), "[1, 2, 3]", ShortIterable.class);
        testCollection(CharLists.mutable.of('a', 'b', 'c'), "\"abc\"", CharIterable.class);
        testCollection(IntLists.mutable.of(1, 2, 3), "[1, 2, 3]", IntIterable.class);
        testCollection(FloatLists.mutable.of(1.1F, 2.3F, 3.5F), "[1.1, 2.3, 3.5]", FloatIterable.class);
        testCollection(LongLists.mutable.of(1, 2, 3), "[1, 2, 3]", LongIterable.class);
        testCollection(DoubleLists.mutable.of(1.1, 2.3, 3.5), "[1.1, 2.3, 3.5]", DoubleIterable.class);
    }

    @Test
    public void mutableCollection() throws Exception {
        testCollection(Lists.mutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<MutableCollection<String>>() {});
        //noinspection rawtypes
        testCollection(Lists.mutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<MutableCollection<?>>() {});
        testCollection(BooleanLists.mutable.of(true, false, true),
                       "[true, false, true]",
                       MutableBooleanCollection.class);
        testCollection(ByteLists.mutable.of((byte) 1, (byte) 2, (byte) 3), "[1, 2, 3]", MutableByteCollection.class);
        testCollection(ShortLists.mutable.of((short) 1, (short) 2, (short) 3),
                       "[1, 2, 3]",
                       MutableShortCollection.class);
        testCollection(CharLists.mutable.of('a', 'b', 'c'), "\"abc\"", MutableCharCollection.class);
        testCollection(IntLists.mutable.of(1, 2, 3), "[1, 2, 3]", MutableIntCollection.class);
        testCollection(FloatLists.mutable.of(1.1F, 2.3F, 3.5F), "[1.1, 2.3, 3.5]", MutableFloatCollection.class);
        testCollection(LongLists.mutable.of(1, 2, 3), "[1, 2, 3]", MutableLongCollection.class);
        testCollection(DoubleLists.mutable.of(1.1, 2.3, 3.5), "[1.1, 2.3, 3.5]", MutableDoubleCollection.class);
    }

    @Test
    public void immutableCollection() throws Exception {
        testCollection(Lists.immutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<ImmutableCollection<String>>() {});
        //noinspection rawtypes
        testCollection(Lists.immutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<ImmutableCollection<?>>() {});
        testCollection(BooleanLists.immutable.of(true, false, true),
                       "[true, false, true]",
                       ImmutableBooleanCollection.class);
        testCollection(ByteLists.immutable.of((byte) 1, (byte) 2, (byte) 3),
                       "[1, 2, 3]",
                       ImmutableByteCollection.class);
        testCollection(ShortLists.immutable.of((short) 1, (short) 2, (short) 3),
                       "[1, 2, 3]",
                       ImmutableShortCollection.class);
        testCollection(CharLists.immutable.of('a', 'b', 'c'), "\"abc\"", ImmutableCharCollection.class);
        testCollection(IntLists.immutable.of(1, 2, 3), "[1, 2, 3]", ImmutableIntCollection.class);
        testCollection(FloatLists.immutable.of(1.1F, 2.3F, 3.5F), "[1.1, 2.3, 3.5]", ImmutableFloatCollection.class);
        testCollection(LongLists.immutable.of(1, 2, 3), "[1, 2, 3]", ImmutableLongCollection.class);
        testCollection(DoubleLists.immutable.of(1.1, 2.3, 3.5), "[1.1, 2.3, 3.5]", ImmutableDoubleCollection.class);
    }

    @Test
    public void immutableSet() throws Exception {
        testCollection(Sets.immutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<ImmutableSet<String>>() {});
        testCollection(BooleanSets.immutable.of(true, false, true), "[true, false, true]", ImmutableBooleanSet.class);
        testCollection(ByteSets.immutable.of((byte) 1, (byte) 2, (byte) 3), "[1, 2, 3]", ImmutableByteSet.class);
        testCollection(ShortSets.immutable.of((short) 1, (short) 2, (short) 3), "[1, 2, 3]", ImmutableShortSet.class);
        testCollection(CharSets.immutable.of('a', 'b', 'c'), "\"abc\"", ImmutableCharSet.class);
        testCollection(IntSets.immutable.of(1, 2, 3), "[1, 2, 3]", ImmutableIntSet.class);
        testCollection(FloatSets.immutable.of(1.1F, 2.3F, 3.5F), "[1.1, 2.3, 3.5]", ImmutableFloatSet.class);
        testCollection(LongSets.immutable.of(1, 2, 3), "[1, 2, 3]", ImmutableLongSet.class);
        testCollection(DoubleSets.immutable.of(1.1, 2.3, 3.5), "[1.1, 2.3, 3.5]", ImmutableDoubleSet.class);
    }

    @Test
    public void mutableSet() throws Exception {
        testCollection(Sets.mutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<MutableSet<String>>() {});
        testCollection(BooleanSets.mutable.of(true, false, true), "[true, false, true]", MutableBooleanSet.class);
        testCollection(ByteSets.mutable.of((byte) 1, (byte) 2, (byte) 3), "[1, 2, 3]", MutableByteSet.class);
        testCollection(ShortSets.mutable.of((short) 1, (short) 2, (short) 3), "[1, 2, 3]", MutableShortSet.class);
        testCollection(CharSets.mutable.of('a', 'b', 'c'), "\"abc\"", MutableCharSet.class);
        testCollection(IntSets.mutable.of(1, 2, 3), "[1, 2, 3]", MutableIntSet.class);
        testCollection(FloatSets.mutable.of(1.1F, 2.3F, 3.5F), "[1.1, 2.3, 3.5]", MutableFloatSet.class);
        testCollection(LongSets.mutable.of(1, 2, 3), "[1, 2, 3]", MutableLongSet.class);
        testCollection(DoubleSets.mutable.of(1.1, 2.3, 3.5), "[1.1, 2.3, 3.5]", MutableDoubleSet.class);
    }

    @Test
    public void set() throws Exception {
        testCollection(BooleanSets.mutable.of(true, false, true), "[true, false, true]", BooleanSet.class);
        testCollection(ByteSets.mutable.of((byte) 1, (byte) 2, (byte) 3), "[1, 2, 3]", ByteSet.class);
        testCollection(ShortSets.mutable.of((short) 1, (short) 2, (short) 3), "[1, 2, 3]", ShortSet.class);
        testCollection(CharSets.mutable.of('a', 'b', 'c'), "\"abc\"", CharSet.class);
        testCollection(IntSets.mutable.of(1, 2, 3), "[1, 2, 3]", IntSet.class);
        testCollection(FloatSets.mutable.of(1.1F, 2.3F, 3.5F), "[1.1, 2.3, 3.5]", FloatSet.class);
        testCollection(LongSets.mutable.of(1, 2, 3), "[1, 2, 3]", LongSet.class);
        testCollection(DoubleSets.mutable.of(1.1, 2.3, 3.5), "[1.1, 2.3, 3.5]", DoubleSet.class);
    }

    @Test
    public void charsAsArray() throws Exception {
        testCollection(CharSets.mutable.of('a', 'b', 'c'), "[\"a\", \"b\", \"c\"]", CharSet.class);
    }

    @Test
    public void primitiveMaps() throws Exception {
        primitiveMaps0(mapperWithModule(), false);
    }

    @SuppressWarnings({ "StringConcatenationInLoop", "NonConstantStringShouldBeStringBuffer" })
    static void primitiveMaps0(ObjectMapper mapper, boolean serialize) throws Exception {
        List<Class<?>> keyPrimitives = Arrays.asList(
                Object.class, byte.class, short.class, char.class, int.class, float.class, long.class, double.class);
        List<Class<?>> valuePrimitives = new ArrayList<>(keyPrimitives);
        valuePrimitives.add(boolean.class);

        for (Class<?> key : keyPrimitives) {
            for (Class<?> value : valuePrimitives) {
                if (key == Object.class && value == Object.class) { continue; }

                String keyUpper = capitalize(key.getSimpleName());
                String valueUpper = capitalize(value.getSimpleName());
                Class<?> mutableMapType = Class.forName(
                        "org.eclipse.collections.api.map.primitive.Mutable" + keyUpper + valueUpper + "Map");
                Class<?> immutableMapType = Class.forName(
                        "org.eclipse.collections.api.map.primitive.Immutable" + keyUpper + valueUpper + "Map");
                Class<?> baseMapType = Class.forName(
                        "org.eclipse.collections.api.map.primitive." + keyUpper + valueUpper + "Map");
                Class<?> factoryType = Class.forName(
                        "org.eclipse.collections.impl.factory.primitive." + keyUpper + valueUpper + "Maps");

                Object mutableFactory = factoryType.getField("mutable").get(null);
                Object immutableFactory = factoryType.getField("immutable").get(null);

                Object mutableSample = mutableFactory.getClass().getMethod("empty").invoke(mutableFactory);

                Set<Object> seenKeys = new HashSet<>();
                String json = "{";
                for (int i = 0; i < 3; i++) {
                    Object keySample;
                    do {
                        keySample = randomSample(key);
                    } while (!seenKeys.add(keySample));
                    Object valueSample = randomSample(value);
                    mutableSample.getClass().getMethod("put", key, value)
                            .invoke(mutableSample, keySample, valueSample);
                    if (i > 0) { json += ","; }
                    json = json + '"' + keySample + "\":";
                    if (value == char.class || value == Object.class) {
                        json += '"' + valueSample.toString() + '"';
                    } else {
                        json += valueSample;
                    }
                }
                json += "}";
                //System.out.println(baseMapType + ": " + json);

                Object immutableSample = immutableFactory.getClass()
                        .getMethod("ofAll", baseMapType)
                        .invoke(immutableFactory, mutableSample);

                assertEquals(mutableSample, immutableSample);

                Function<Class<?>, JavaType> generify;
                if (key == Object.class || value == Object.class) {
                    generify = c -> mapper.getTypeFactory().constructParametricType(c, String.class);
                } else {
                    generify = c -> mapper.getTypeFactory().constructType(c);
                }

                if (serialize) {
                    String mutablePrinted = mapper.writerFor(generify.apply(mutableMapType)).writeValueAsString(
                            mutableSample);
                    String immutablePrinted = mapper.writerFor(generify.apply(immutableMapType))
                            .writeValueAsString(immutableSample);
                    String basePrinted =
                            mapper.writerFor(generify.apply(baseMapType)).writeValueAsString(mutableSample);
                    String polyPrinted = mapper.writeValueAsString(mutableSample);
                    // compare trees so property order doesn't matter
                    assertEquals(mapper.readTree(json), mapper.readTree(mutablePrinted));
                    assertEquals(mapper.readTree(json), mapper.readTree(immutablePrinted));
                    assertEquals(mapper.readTree(json), mapper.readTree(basePrinted));
                    assertEquals(mapper.readTree(json), mapper.readTree(polyPrinted));
                } else {
                    Object mutableParsed = mapper.readValue(json, generify.apply(mutableMapType));
                    Object immutableParsed = mapper.readValue(json, generify.apply(immutableMapType));
                    Object baseParsed = mapper.readValue(json, generify.apply(baseMapType));
                    assertEquals(mutableSample, mutableParsed);
                    assertEquals(immutableSample, immutableParsed);
                    assertEquals(mutableSample, baseParsed);

                    assertTrue(mutableMapType.isInstance(mutableParsed));
                    assertTrue(immutableMapType.isInstance(immutableParsed));
                    assertTrue(baseMapType.isInstance(baseParsed));
                }
            }
        }
    }

    private static String capitalize(String simpleName) {
        return simpleName.substring(0, 1).toUpperCase() + simpleName.substring(1);
    }

    @Test
    public void objectObjectMaps() throws Exception {
        assertEquals(
                mapperWithModule().readValue("{\"abc\":\"def\"}", new TypeReference<MutableMap<String, String>>() {}),
                Maps.mutable.of("abc", "def")
        );
        assertEquals(
                mapperWithModule().readValue("{\"abc\":\"def\"}", new TypeReference<ImmutableMap<String, String>>() {}),
                Maps.immutable.of("abc", "def")
        );
        assertEquals(
                mapperWithModule().readValue("{\"abc\":\"def\"}", new TypeReference<MapIterable<String, String>>() {}),
                Maps.mutable.of("abc", "def")
        );
        assertEquals(
                mapperWithModule().readValue("{\"abc\":\"def\"}",
                                             new TypeReference<UnsortedMapIterable<String, String>>() {}),
                Maps.mutable.of("abc", "def")
        );
    }

    @SuppressWarnings("ObjectEquality")
    private static Object randomSample(Class<?> type) {
        if (type == boolean.class) { return ThreadLocalRandom.current().nextBoolean(); }
        if (type == byte.class) { return ((byte) ThreadLocalRandom.current().nextInt()); }
        if (type == short.class) { return ((short) ThreadLocalRandom.current().nextInt()); }
        if (type == char.class) { return ((char) (ThreadLocalRandom.current().nextInt(20) + 'a')); }
        if (type == int.class) { return ThreadLocalRandom.current().nextInt(); }
        if (type == float.class) { return ThreadLocalRandom.current().nextFloat(); }
        if (type == long.class) { return ThreadLocalRandom.current().nextLong(); }
        if (type == double.class) { return ThreadLocalRandom.current().nextDouble(); }
        if (type == Object.class) { return randomSample(char.class).toString(); }
        throw new AssertionError();
    }

    @Test
    public void typeInfoObjectMap() throws Exception {
        assertEquals(
                mapperWithModule()
                        .readValue("{\"map\":{\"0\":{\"@c\":\".DeserializerTest$B\"}}}", Container.class).map,
                IntObjectMaps.immutable.of(0, new B())
        );
    }

    static class Container {
        public IntObjectMap<A> map;
    }

    @JsonSubTypes(@JsonSubTypes.Type(B.class))
    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    static abstract class A {

    }

    static class B extends A {
        public B() {
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof B;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    @Test
    public void typeInfoNestedMapList() throws Exception {
        // test case for jackson-datatypes-collections#71
        ImmutableMap<String, ImmutableList<A>> property =
                Maps.immutable.of("foo", Lists.immutable.of(new B()));
        assertEquals(
                mapperWithModule().readValue(
                        "{\"foo\": [{\"@c\": \".DeserializerTest$B\"}]}",
                        new TypeReference<ImmutableMap<String, ImmutableList<A>>>() {}),
                property
        );
    }

    @Test
    public void typeInfoNestedMapMap() throws Exception {
        // auxiliary test case for jackson-datatypes-collections#71 - also worked before fix
        ImmutableMap<String, ImmutableMap<String, A>> property =
                Maps.immutable.of("foo", Maps.immutable.of("bar", new B()));
        assertEquals(
                mapperWithModule().readValue(
                        "{\"foo\": {\"bar\": {\"@c\": \".DeserializerTest$B\"}}}",
                        new TypeReference<ImmutableMap<String, ImmutableMap<String, A>>>() {}),
                property
        );
    }

    @Test
    public void primitivePairs() throws Exception {
        List<Class<?>> types = Arrays.asList(
                Object.class,
                boolean.class,
                byte.class,
                short.class,
                char.class,
                int.class,
                float.class,
                long.class,
                double.class);

        for (Class<?> oneType : types) {
            for (Class<?> twoType : types) {
                Class<?> pairClass;
                Method factory;
                if (oneType == Object.class && twoType == Object.class) {
                    pairClass = Pair.class;
                    factory = Tuples.class.getMethod("pair", Object.class, Object.class);
                } else {
                    pairClass = Class.forName("org.eclipse.collections.api.tuple.primitive." +
                                              capitalize(oneType.getSimpleName()) +
                                              capitalize(twoType.getSimpleName()) +
                                              "Pair");
                    factory = PrimitiveTuples.class.getMethod("pair", oneType, twoType);
                }

                Object sampleOne = randomSample(oneType);
                Object sampleTwo = randomSample(twoType);

                JavaType pairType;
                // possibly generify with the sample type
                if (oneType == Object.class) {
                    if (twoType == Object.class) {
                        pairType = mapperWithModule().getTypeFactory().constructParametricType(
                                pairClass, sampleOne.getClass(), sampleTwo.getClass());
                    } else {
                        pairType = mapperWithModule().getTypeFactory().constructParametricType(
                                pairClass, sampleOne.getClass());
                    }
                } else {
                    if (twoType == Object.class) {
                        pairType = mapperWithModule().getTypeFactory().constructParametricType(
                                pairClass, sampleTwo.getClass());
                    } else {
                        pairType = mapperWithModule().constructType(pairClass);
                    }
                }

                String expectedJson = "{\"one\":" + mapperWithModule().writeValueAsString(sampleOne)
                                      + ",\"two\":" + mapperWithModule().writeValueAsString(sampleTwo) + "}";
                Object samplePair = factory.invoke(null, sampleOne, sampleTwo);

                assertEquals(expectedJson, mapperWithModule().writeValueAsString(samplePair));
                assertEquals(samplePair, mapperWithModule().readValue(expectedJson, pairType));
            }
        }
    }

    @Test
    public void twin() throws Exception {
        final ObjectMapper mapper = mapperWithModule();
        Object sampleOne = randomSample(Object.class);
        Object sampleTwo = randomSample(Object.class);
        String expectedJson = "{\"one\":" + mapper.writeValueAsString(sampleOne)
                              + ",\"two\":" + mapper.writeValueAsString(sampleTwo) + "}";
        Twin<String> twin = Tuples.twin((String) sampleOne, (String) sampleTwo);
        assertEquals(expectedJson, mapper.writeValueAsString(twin));
        assertEquals(twin, mapper.readValue(expectedJson, new TypeReference<Twin<String>>() {}));
    }

    @Test
    public void pairTyped() throws Exception {
        final ObjectMapper mapper = mapperWithModule();
        ObjectIntPair<A> pair = PrimitiveTuples.pair(new B(), 5);
        final String actJson = mapper.writerFor(new TypeReference<ObjectIntPair<A>>() {})
                .writeValueAsString(pair);
        String expJson = "{\"one\":{\"@c\":\".DeserializerTest$B\"},\"two\":5}";
        assertEquals(mapper.readTree(expJson), mapper.readTree(actJson));
        assertEquals(pair,
                mapper.readValue(actJson, new TypeReference<ObjectIntPair<A>>() {})
        );
    }

    @Test
    public void nestedMap() throws Exception {
        MutableMap<String, MutableMap<String, String>> pair = Maps.mutable.of("a", Maps.mutable.of("b", "c"));
        String json = "{\"a\":{\"b\":\"c\"}}";
        TypeReference<MutableMap<String, MutableMap<String, String>>> type =
                new TypeReference<MutableMap<String, MutableMap<String, String>>>() {};
        assertEquals(json, mapperWithModule().writerFor(type).writeValueAsString(pair));
        assertEquals(pair, mapperWithModule().readValue(json, type));
    }

    @Test
    public void triple() throws Exception {
        final ObjectMapper mapper = mapperWithModule();
        Triple<String, Integer, Boolean> triple = Tuples.triple("a", 2, false);
        String actJson = mapper.writerFor(new TypeReference<Triple<String, Integer, Boolean>>() {})
                .writeValueAsString(triple);
        String expJson = "{\"one\":\"a\",\"two\":2,\"three\":false}";
        assertEquals(mapper.readTree(expJson), mapper.readTree(actJson));
        assertEquals(
                triple,
                mapper.readValue(actJson, new TypeReference<Triple<String, Integer, Boolean>>() {})
        );
    }

    @Test
    public void triplet() throws Exception {
        final ObjectMapper mapper = mapperWithModule();
        Triplet<String> triple = Tuples.triplet("a", "b", "c");
        String actJson = mapper.writerFor(new TypeReference<Triplet<String>>() {})
                .writeValueAsString(triple);
        String expJson = "{\"one\":\"a\",\"two\":\"b\",\"three\":\"c\"}";
        assertEquals(mapper.readTree(expJson), mapper.readTree(actJson));
        assertEquals(
                triple,
                mapper.readValue(actJson, new TypeReference<Triplet<String>>() {})
        );
    }

    @Test
    public void mutableSortedMap() throws Exception {
        final ObjectMapper mapper = mapperWithModule();
        final MutableSortedMap<String, Object> sortedMap = SortedMaps.mutable.of("c", 3, "a", 1, "b", 2);
        final String json = mapper.writeValueAsString(sortedMap);

        final MutableSortedMap<String, Object> result = mapper.readValue(
                json,
                new TypeReference<MutableSortedMap<String, Object>>() {}
        );

        assertEquals(sortedMap, result);
        assertTrue(result instanceof MutableSortedMap);
    }

    @Test
    public void sortedMapIterable() throws Exception {
        final ObjectMapper mapper = mapperWithModule();
        final MutableSortedMap<String, Object> sortedMap = SortedMaps.mutable.of("c", 3, "a", 1, "b", 2);
        final String json = mapper.writeValueAsString(sortedMap);

        final SortedMapIterable<String, Object> result = mapper.readValue(
                json,
                new TypeReference<SortedMapIterable<String, Object>>() {}
        );

        assertEquals(sortedMap, result);
        assertTrue(result instanceof SortedMapIterable);
    }

    @Test
    public void immutableSortedMap() throws Exception {
        final ObjectMapper mapper = mapperWithModule();
        final ImmutableSortedMap<String, Object> sortedMap = SortedMaps.immutable.of("c", 3, "a", 1, "b", 2);
        final String json = mapper.writeValueAsString(sortedMap);

        final ImmutableSortedMap<String, Object> result = mapper.readValue(
                json,
                new TypeReference<ImmutableSortedMap<String, Object>>() {}
        );

        assertEquals(sortedMap, result);
        assertTrue(result instanceof ImmutableSortedMap);
    }

    @Test
    public void sortedMap_nonComparableKey() throws Exception {
        final ObjectMapper mapper = mapperWithModule();

        // Currency doesn't implement Comparable
        assertFalse(Comparable.class.isAssignableFrom(Currency.class));

        final String json = "{\"USD\":1}";

        final JsonMappingException e = assertThrows(
                JsonMappingException.class,
                () -> mapper.readValue(
                        json,
                        new TypeReference<MutableSortedMap<Currency, Object>>() {}
                )
        );
        verifyException(e, "Cannot deserialize `org.eclipse.collections.api.map.sorted.MutableSortedMap<java.util.Currency,java.lang.Object>`");
        verifyException(e, "key type `java.util.Currency` does not implement `java.lang.Comparable`");
    }
}
