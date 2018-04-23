package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
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
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.SortedBags;
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
import org.eclipse.collections.impl.factory.primitive.IntSets;
import org.eclipse.collections.impl.factory.primitive.LongBags;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.LongSets;
import org.eclipse.collections.impl.factory.primitive.ShortBags;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import org.eclipse.collections.impl.factory.primitive.ShortSets;
import org.junit.Assert;
import org.junit.Test;

public final class DeserializerTest extends ModuleTestBase {
    private <T> void testCollection(T expected, String json, TypeReference<T> type) throws IOException {
        ObjectMapper objectMapper = mapperWithModule();
        T value = objectMapper.readValue(json, type);
        Assert.assertEquals(expected, value);
        Assert.assertTrue(objectMapper.getTypeFactory().constructType(type).getRawClass().isInstance(value));
    }

    private <T> void testCollection(T expected, String json, Class<T> type) throws IOException {
        T value = mapperWithModule().readValue(json, type);
        Assert.assertEquals(expected, value);
        Assert.assertTrue(type.isInstance(value));
    }

    @Test
    public void immutableBag() throws IOException {
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
    public void immutableSortedBag() throws IOException {
        testCollection(SortedBags.immutable.of("3", "2", "1"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<ImmutableSortedBag<String>>() {});
    }

    @Test
    public void mutableBag() throws IOException {
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
    public void mutableSortedBag() throws IOException {
        testCollection(SortedBags.mutable.of("3", "2", "1"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<MutableSortedBag<String>>() {});
    }

    @Test
    public void bag() throws IOException {
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
    public void immutableList() throws IOException {
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
    public void mutableList() throws IOException {
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
    public void fixedSizeList() throws IOException {
        testCollection(Lists.fixedSize.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<FixedSizeList<String>>() {});
        testCollection(Lists.fixedSize.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<FixedSizeCollection<String>>() {});
    }

    @Test
    public void list() throws IOException {
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
    public void iterable() throws IOException {
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
    public void mutableCollection() throws IOException {
        testCollection(Lists.mutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<MutableCollection<String>>() {});
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
    public void immutableCollection() throws IOException {
        testCollection(Lists.immutable.of("1", "2", "3"),
                       "[\"1\", \"2\", \"3\"]",
                       new TypeReference<ImmutableCollection<String>>() {});
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
    public void immutableSet() throws IOException {
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
    public void mutableSet() throws IOException {
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
    public void set() throws IOException {
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
    public void charsAsArray() throws IOException {
        testCollection(CharSets.mutable.of('a', 'b', 'c'), "[\"a\", \"b\", \"c\"]", CharSet.class);
    }
}
