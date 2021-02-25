package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.core.type.TypeReference;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.bag.Bag;
import org.eclipse.collections.api.bag.ImmutableBag;
import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.collection.ImmutableCollection;
import org.eclipse.collections.api.collection.MutableCollection;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.primitive.*;
import org.eclipse.collections.api.set.*;
import org.eclipse.collections.impl.factory.Bags;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.primitive.*;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

public class RawDeserializationTest extends ModuleTestBase {
    @Test
    public void objectObjectMap() throws IOException {
        testCollection(Maps.mutable.of("a", 1, "b", Collections.emptyMap()), "{\"a\":1, \"b\":{}}",
                new TypeReference<MutableMap<?,?>>() {},
                new TypeReference<ImmutableMap<?,?>>() {});
        testCollection(Maps.mutable.of("a", 1, "b", Collections.emptyMap()), "{\"a\":1, \"b\":{}}",
                MutableMap.class,
                ImmutableMap.class);
    }

    @Test
    public void objectList() throws IOException {
        testCollection(Lists.mutable.of("a", Collections.emptyMap(), 1), "[\"a\", {}, 1]",
                new TypeReference<MutableList<?>>() {},
                new TypeReference<ImmutableList<?>>() {},
                new TypeReference<MutableCollection<?>>() {},
                new TypeReference<ImmutableCollection<?>>() {},
                new TypeReference<RichIterable<?>>() {});
        testCollection(Lists.mutable.of("a", Collections.emptyMap(), 1), "[\"a\", {}, 1]",
                MutableList.class,
                ImmutableList.class,
                MutableCollection.class,
                ImmutableCollection.class,
                RichIterable.class);
    }

    @Test
    public void objectSet() throws IOException {
        testCollection(Sets.mutable.of("a", Collections.emptyMap(), 1), "[\"a\", {}, 1]",
                new TypeReference<MutableSet<?>>() {},
                new TypeReference<ImmutableSet<?>>() {});
        testCollection(Sets.mutable.of("a", Collections.emptyMap(), 1), "[\"a\", {}, 1]",
                MutableSet.class,
                ImmutableSet.class);
    }

    @Test
    public void objectBag() throws IOException {
        testCollection(Bags.mutable.of("a", Collections.emptyMap(), 1), "[\"a\", {}, 1]",
                new TypeReference<Bag<?>>() {},
                new TypeReference<MutableBag<?>>() {},
                new TypeReference<ImmutableBag<?>>() {});
        testCollection(Bags.mutable.of("a", Collections.emptyMap(), 1), "[\"a\", {}, 1]",
                Bag.class,
                MutableBag.class,
                ImmutableBag.class);
    }

    /* with byte|short|int|float|long|double primType */
    @Test
    public void rawObjectByteMap() throws IOException {
        MutableObjectByteMap<Object> expectedObjectByteMap = ObjectByteMaps.mutable.of();
        expectedObjectByteMap.put("a", (byte) 1);
        expectedObjectByteMap.put("b", (byte) 2);
        testCollection(expectedObjectByteMap, "{\"a\":1, \"b\":2}", ObjectByteMap.class);
    }

    @Test
    public void rawByteObjectMap() throws IOException {
        MutableByteObjectMap<Object> expectedByteObjectMap = ByteObjectMaps.mutable.of();
        expectedByteObjectMap.put((byte) 1, "a");
        expectedByteObjectMap.put((byte) 2, Collections.emptyList());
        testCollection(expectedByteObjectMap, "{\"1\":\"a\", \"2\":[]}", ByteObjectMap.class);
    }
    /* endwith */
}
