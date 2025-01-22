package tools.jackson.datatype.eclipsecollections;

import org.junit.jupiter.api.Test;

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

import static org.junit.jupiter.api.Assertions.*;

public final class SerializerTest extends ModuleTestBase {
    static class Wrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        Object object;
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void ref() throws Exception {
        assertEquals(
                "[\"a\",\"b\",\"c\"]",
                MAPPER.writeValueAsString(Sets.immutable.of("a", "b", "c"))
        );
    }

    @Test
    public void primitive() throws Exception {
        assertEquals("[true,false,true]", MAPPER.writeValueAsString(
                BooleanLists.immutable.of(true, false, true)));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(
                ShortLists.immutable.of((short) 1, (short) 2, (short) 3)));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(
                IntLists.immutable.of(1, 2, 3)));
        assertEquals("[1.1,2.3,3.5]", MAPPER.writeValueAsString(
                FloatLists.immutable.of(1.1F, 2.3F, 3.5F)));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(
                LongLists.immutable.of(1, 2, 3)));
        assertEquals("[1.1,2.3,3.5]", MAPPER.writeValueAsString(
                DoubleLists.immutable.of(1.1, 2.3, 3.5)));

        assertEquals(
                MAPPER.writeValueAsString(new byte[]{ 1, 2, 3 }),
                MAPPER.writeValueAsString(ByteLists.immutable.of((byte) 1, (byte) 2, (byte) 3)));
        assertEquals(
                MAPPER.writeValueAsString(new char[]{ '1', '2', '3' }),
                MAPPER.writeValueAsString(CharLists.immutable.of('1', '2', '3')));
        assertEquals(
                MAPPER.writer().with(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                        .writeValueAsString(new char[]{ '1', '2', '3' }),
                    MAPPER.writer().with(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                        .writeValueAsString(CharLists.immutable.of('1', '2', '3')));
    }

    @Test
    public void primitiveTypeSer() throws Exception
    {
        primitiveTypeSer("[true,false,true]", BooleanLists.immutable.of(true, false, true));
        primitiveTypeSer("[1,2,3]", ShortLists.immutable.of((short) 1, (short) 2, (short) 3));
        primitiveTypeSer("[1,2,3]", IntLists.immutable.of(1, 2, 3));
        primitiveTypeSer("[1.1,2.3,3.5]", FloatLists.immutable.of(1.1F, 2.3F, 3.5F));
        primitiveTypeSer("[1,2,3]", LongLists.immutable.of(1, 2, 3));
        primitiveTypeSer("[1.1,2.3,3.5]", DoubleLists.immutable.of(1.1, 2.3, 3.5));

        primitiveTypeSer(
                MAPPER.writeValueAsString(new byte[]{ 1, 2, 3 }),
                ByteLists.immutable.of((byte) 1, (byte) 2, (byte) 3));
        primitiveTypeSer(
                MAPPER.writeValueAsString(new char[]{ '1', '2', '3' }),
                CharLists.immutable.of('1', '2', '3'));
        primitiveTypeSer(
                MAPPER.writer().with(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                        .writeValueAsString(new char[]{ '1', '2', '3' }),
                CharLists.immutable.of('1', '2', '3'),
                mapperBuilder().configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true)
                    .build()
        );
    }

    private void primitiveTypeSer(String data, PrimitiveIterable iterable)
            throws Exception
    {
        primitiveTypeSer(data, iterable, MAPPER);
    }

    private void primitiveTypeSer(String data, PrimitiveIterable iterable,
            ObjectMapper objectMapper)
        throws Exception
    {
        Wrapper wrapper = new Wrapper();
        wrapper.object = iterable;

        assertEquals("{\"object\":[\"" + iterable.getClass().getName() + "\"," + data + "]}",
                            objectMapper.writeValueAsString(wrapper));
    }

    @Test
    public void primitiveMaps() throws Exception
    {
        DeserializerTest.primitiveMaps0(MAPPER, true);
    }

    @Test
    public void objectObjectMaps() throws Exception {
        assertEquals(
                "{\"abc\":\"def\"}",
                MAPPER.writerFor(MutableMap.class).writeValueAsString(Maps.mutable.of("abc", "def"))
        );
        assertEquals(
                "{\"abc\":\"def\"}",
                MAPPER.writerFor(ImmutableMap.class).writeValueAsString(Maps.immutable.of("abc", "def"))
        );
        assertEquals(
                "{\"abc\":\"def\"}",
                MAPPER.writerFor(MapIterable.class).writeValueAsString(Maps.immutable.of("abc", "def"))
        );
        assertEquals(
                "{\"abc\":\"def\"}",
                MAPPER.writerFor(MutableMapIterable.class).writeValueAsString(Maps.mutable.of("abc", "def"))
        );
        assertEquals(
                "{\"abc\":\"def\"}",
                MAPPER.writeValueAsString(Maps.immutable.of("abc", "def"))
        );
        assertEquals(
                "{\"abc\":\"def\"}",
                MAPPER.writerFor(new TypeReference<MapIterable<String, String>>() {})
                        .writeValueAsString(Maps.immutable.of("abc", "def"))
        );
    }

    @Test
    public void typeInfoObjectMap() throws Exception {
        assertEquals(
                "{\"map\":{\"0\":{\"@c\":\".SerializerTest$B\"}}}",
                MAPPER.writeValueAsString(new Container())
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
