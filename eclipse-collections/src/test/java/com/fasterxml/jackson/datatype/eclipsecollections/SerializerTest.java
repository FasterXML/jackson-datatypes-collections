package com.fasterxml.jackson.datatype.eclipsecollections;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;
import org.eclipse.collections.api.map.MutableMapIterable;
import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import org.junit.Assert;
import org.junit.Test;

public final class SerializerTest extends ModuleTestBase {
    @Test
    public void ref() throws IOException {
        Assert.assertEquals(
                "[\"a\",\"b\",\"c\"]",
                mapperWithModule().writeValueAsString(Sets.immutable.of("a", "b", "c"))
        );
    }

    @Test
    public void primitive() throws IOException {
        Assert.assertEquals("[true,false,true]", mapperWithModule().writeValueAsString(
                BooleanLists.immutable.of(true, false, true)));
        Assert.assertEquals("[1,2,3]", mapperWithModule().writeValueAsString(
                ShortLists.immutable.of((short) 1, (short) 2, (short) 3)));
        Assert.assertEquals("[1,2,3]", mapperWithModule().writeValueAsString(
                IntLists.immutable.of(1, 2, 3)));
        Assert.assertEquals("[1.1,2.3,3.5]", mapperWithModule().writeValueAsString(
                FloatLists.immutable.of(1.1F, 2.3F, 3.5F)));
        Assert.assertEquals("[1,2,3]", mapperWithModule().writeValueAsString(
                LongLists.immutable.of(1, 2, 3)));
        Assert.assertEquals("[1.1,2.3,3.5]", mapperWithModule().writeValueAsString(
                DoubleLists.immutable.of(1.1, 2.3, 3.5)));

        Assert.assertEquals(
                mapperWithModule().writeValueAsString(new byte[]{ 1, 2, 3 }),
                mapperWithModule().writeValueAsString(ByteLists.immutable.of((byte) 1, (byte) 2, (byte) 3)));
        Assert.assertEquals(
                mapperWithModule().writeValueAsString(new char[]{ '1', '2', '3' }),
                mapperWithModule().writeValueAsString(CharLists.immutable.of('1', '2', '3')));
        Assert.assertEquals(
                mapperWithModule().writer().with(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                        .writeValueAsString(new char[]{ '1', '2', '3' }),
                mapperWithModule().writer().with(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                        .writeValueAsString(CharLists.immutable.of('1', '2', '3')));
    }

    private void primitiveTypeSer(String data, PrimitiveIterable iterable)
        throws IOException
    {
        primitiveTypeSer(data, iterable, mapperWithModule());
    }

    private void primitiveTypeSer(
            String data,
            PrimitiveIterable iterable,
            ObjectMapper objectMapper
    ) throws IOException {
        class Wrapper {
            @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
            Object object;
        }
        Wrapper wrapper = new Wrapper();
        wrapper.object = iterable;

        Assert.assertEquals("{\"object\":[\"" + iterable.getClass().getName() + "\"," + data + "]}",
                            objectMapper.writeValueAsString(wrapper));
    }

    @Test
    public void primitiveTypeSer() throws IOException
    {
        primitiveTypeSer("[true,false,true]", BooleanLists.immutable.of(true, false, true));
        primitiveTypeSer("[1,2,3]", ShortLists.immutable.of((short) 1, (short) 2, (short) 3));
        primitiveTypeSer("[1,2,3]", IntLists.immutable.of(1, 2, 3));
        primitiveTypeSer("[1.1,2.3,3.5]", FloatLists.immutable.of(1.1F, 2.3F, 3.5F));
        primitiveTypeSer("[1,2,3]", LongLists.immutable.of(1, 2, 3));
        primitiveTypeSer("[1.1,2.3,3.5]", DoubleLists.immutable.of(1.1, 2.3, 3.5));

        primitiveTypeSer(
                mapperWithModule().writeValueAsString(new byte[]{ 1, 2, 3 }),
                ByteLists.immutable.of((byte) 1, (byte) 2, (byte) 3));
        primitiveTypeSer(
                mapperWithModule().writeValueAsString(new char[]{ '1', '2', '3' }),
                CharLists.immutable.of('1', '2', '3'));
        primitiveTypeSer(
                mapperWithModule().writer().with(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                        .writeValueAsString(new char[]{ '1', '2', '3' }),
                CharLists.immutable.of('1', '2', '3'),
                mapperBuilder().configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true)
                    .build()
        );
    }

    @Test
    public void primitiveMaps() throws Exception
    {
        DeserializerTest.primitiveMaps0(mapperWithModule(), true);
    }

    @Test
    public void objectObjectMaps() throws IOException {
        Assert.assertEquals(
                "{\"abc\":\"def\"}",
                mapperWithModule().writerFor(MutableMap.class).writeValueAsString(Maps.mutable.of("abc", "def"))
        );
        Assert.assertEquals(
                "{\"abc\":\"def\"}",
                mapperWithModule().writerFor(ImmutableMap.class).writeValueAsString(Maps.immutable.of("abc", "def"))
        );
        Assert.assertEquals(
                "{\"abc\":\"def\"}",
                mapperWithModule().writerFor(MapIterable.class).writeValueAsString(Maps.immutable.of("abc", "def"))
        );
        Assert.assertEquals(
                "{\"abc\":\"def\"}",
                mapperWithModule().writerFor(MutableMapIterable.class).writeValueAsString(Maps.mutable.of("abc", "def"))
        );
        Assert.assertEquals(
                "{\"abc\":\"def\"}",
                mapperWithModule().writeValueAsString(Maps.immutable.of("abc", "def"))
        );
        Assert.assertEquals(
                "{\"abc\":\"def\"}",
                mapperWithModule().writerFor(new TypeReference<MapIterable<String, String>>() {})
                        .writeValueAsString(Maps.immutable.of("abc", "def"))
        );
    }

    @Test
    public void typeInfoObjectMap() throws IOException {
        Assert.assertEquals(
                "{\"map\":{\"0\":{\"@c\":\".SerializerTest$B\"}}}",
                mapperWithModule().writeValueAsString(new Container())
        );
    }

    static class Container {
        public final IntObjectMap<A> map = IntObjectMaps.immutable.of(0, new B());
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS)
    static abstract class A {

    }

    static class B extends A {

    }
}
