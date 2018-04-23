package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.eclipse.collections.api.PrimitiveIterable;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.factory.primitive.BooleanLists;
import org.eclipse.collections.impl.factory.primitive.ByteLists;
import org.eclipse.collections.impl.factory.primitive.CharLists;
import org.eclipse.collections.impl.factory.primitive.DoubleLists;
import org.eclipse.collections.impl.factory.primitive.FloatLists;
import org.eclipse.collections.impl.factory.primitive.IntLists;
import org.eclipse.collections.impl.factory.primitive.LongLists;
import org.eclipse.collections.impl.factory.primitive.ShortLists;
import org.junit.Assert;
import org.junit.Test;

public final class SerializerTest extends ModuleTestBase {
    @Test
    public void ref() throws JsonProcessingException {
        Assert.assertEquals(
                "[\"a\",\"b\",\"c\"]",
                mapperWithModule().writeValueAsString(Sets.immutable.of("a", "b", "c"))
        );
    }

    @Test
    public void primitive() throws JsonProcessingException {
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
                mapperWithModule().configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true)
                        .writeValueAsString(new char[]{ '1', '2', '3' }),
                mapperWithModule().configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true)
                        .writeValueAsString(CharLists.immutable.of('1', '2', '3')));
    }

    private void primitiveTypeSer(String data, PrimitiveIterable iterable) throws JsonProcessingException {
        primitiveTypeSer(data, iterable, mapperWithModule());
    }

    private void primitiveTypeSer(
            String data,
            PrimitiveIterable iterable,
            ObjectMapper objectMapper
    ) throws JsonProcessingException {
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
    public void primitiveTypeSer() throws JsonProcessingException {
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
                mapperWithModule().configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true)
                        .writeValueAsString(new char[]{ '1', '2', '3' }),
                CharLists.immutable.of('1', '2', '3'),
                mapperWithModule().configure(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true));
    }
}
