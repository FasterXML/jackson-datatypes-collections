package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.guava.util.PrimitiveTypes;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.ImmutableDoubleArray;
import com.google.common.primitives.ImmutableIntArray;
import com.google.common.primitives.ImmutableLongArray;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedInts;
import com.google.common.primitives.UnsignedLong;
import com.google.common.primitives.UnsignedLongs;

import java.util.Collections;
import java.util.List;

/**
 * Unit tests for verifying that various primitive types
 * (like {@link Booleans}, {@link Bytes},{@link Chars},{@link Doubles},{@link Floats},{@link ImmutableDoubleArray},
 * {@link ImmutableIntArray},{@link ImmutableLongArray},{@link Ints},{@link Longs},{@link Shorts},
 * {@link SignedBytes},{@link UnsignedBytes},{@link UnsignedInteger},{@link UnsignedInts},{@link UnsignedLong},
 * and {@link UnsignedLongs})
 * work as expected.
 *
 * @author robert@albertlr.ro
 */
public class TestPrimitives extends ModuleTestBase {
    private final ObjectMapper MAPPER = mapperWithModule();

    // For polymorphic cases need to allow bit more access
    private final ObjectMapper POLY_MAPPER = builderWithModule()
            .polymorphicTypeValidator(BasicPolymorphicTypeValidator
                    .builder()
                    .allowIfBaseType(Object.class)
                    .build()
            ).build();

    private final ObjectMapper SINGLE_MAPPER = builderWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();

    static class PolymorphicHolder {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public Object value;

        public PolymorphicHolder() {
        }

        public PolymorphicHolder(Object v) {
            value = v;
        }
    }

    /**********************************************************************
     * Unit tests for verifying handling in absence of module registration
     /***********************************************************************/

    /**
     * Immutable types can actually be serialized as regular collections, without
     * problems.
     */
    public void testWithoutSerializers() throws Exception {
        assertEquals("[true,false,true]", MAPPER.writeValueAsString(Booleans.asList(true, false, true)));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(Bytes.asList((byte) 1, (byte) 2, (byte) 3)));
        assertEquals("[\"a\",\"b\",\"c\"]", MAPPER.writeValueAsString(Chars.asList('a', 'b', 'c')));
        assertEquals("[1.5,2.5,3.5]", MAPPER.writeValueAsString(Doubles.asList(1.5, 2.5, 3.5)));
        assertEquals("[1.5,2.5,3.5]", MAPPER.writeValueAsString(Floats.asList((float) 1.5, (float) 2.5, (float) 3.5)));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(Ints.asList(1, 2, 3)));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(Longs.asList(1L, 2L, 3L)));
        assertEquals("[1,2,3]", MAPPER.writeValueAsString(Shorts.asList((short) 1, (short) 2, (short) 3)));
    }

    /**
     * Deserialization will fail, however.
     */
    public void testWithoutDeserializers() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.readValue("[true,false,true]", PrimitiveTypes.BooleansTypeReference);
            fail("Expected failure for missing deserializer");
        } catch (JsonMappingException e) {
            verifyException(e, PrimitiveTypes.BooleansTypeName);
        }

        try {
            mapper.readValue("[1,2,3]", PrimitiveTypes.IntsTypeReference);
            fail("Expected failure for missing deserializer");
        } catch (JsonMappingException e) {
            verifyException(e, PrimitiveTypes.IntsTypeName);
        }

        try {
            mapper.readValue("[1,2,3]", PrimitiveTypes.LongsTypeReference);
            fail("Expected failure for missing deserializer");
        } catch (JsonMappingException e) {
            verifyException(e, PrimitiveTypes.LongsTypeName);
        }

    }

    /**********************************************************************
     * Basic tests for actual registered module
     /***********************************************************************/

    public void testBooleans() throws Exception {
        List<Boolean> list = MAPPER.readValue("[true,false,true]", PrimitiveTypes.BooleansTypeReference);
        assertEquals(3, list.size());
        assertEquals(Boolean.TRUE, list.get(0));
        assertEquals(Boolean.FALSE, list.get(1));
        assertEquals(Boolean.TRUE, list.get(2));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.BooleansTypeName));
    }

    public void testBooleansFromSingle() throws Exception {
        List<Boolean> list = SINGLE_MAPPER.readValue("true", PrimitiveTypes.BooleansTypeReference);
        assertEquals(1, list.size());
        assertEquals(Boolean.TRUE, list.get(0));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.BooleansTypeName));
    }

    public void testBytes() throws Exception {
        List<Byte> list = MAPPER.readValue("[1,2,3]", PrimitiveTypes.BytesTypeReference);
        assertEquals(3, list.size());
        assertEquals(Byte.valueOf((byte) 1), list.get(0));
        assertEquals(Byte.valueOf((byte) 2), list.get(1));
        assertEquals(Byte.valueOf((byte) 3), list.get(2));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.BytesTypeName));
    }

    public void testBytesFromSingle() throws Exception {
        List<Byte> list = SINGLE_MAPPER.readValue("1", PrimitiveTypes.BytesTypeReference);
        assertEquals(1, list.size());
        assertEquals(Byte.valueOf((byte) 1), list.get(0));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.BytesTypeName));
    }

    public void testChars() throws Exception {
        List<Character> list = MAPPER.readValue("[\"\\u0001\",\"\\u0002\",\"\\u0003\",\"a\",\"b\",\"c\",\"D\",\"E\"]", PrimitiveTypes.CharsTypeReference);
        assertEquals(8, list.size());
        assertEquals(Character.valueOf((char) 1), list.get(0));
        assertEquals(Character.valueOf((char) 2), list.get(1));
        assertEquals(Character.valueOf((char) 3), list.get(2));
        assertEquals(Character.valueOf('a'), list.get(3));
        assertEquals(Character.valueOf('b'), list.get(4));
        assertEquals(Character.valueOf('c'), list.get(5));
        assertEquals(Character.valueOf('D'), list.get(6));
        assertEquals(Character.valueOf('E'), list.get(7));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.CharsTypeName));
    }

    public void testCharsFromSingle() throws Exception {
        List<Character> list = SINGLE_MAPPER.readValue("\"a\"", PrimitiveTypes.CharsType);
        assertEquals(1, list.size());
        assertEquals(Character.valueOf('a'), list.get(0));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.CharsTypeName));
    }

    public void testFloats() throws Exception {
        List<Float> list = MAPPER.readValue("[1.5,2.5,3.5]", PrimitiveTypes.FloatsTypeReference);
        assertEquals(3, list.size());
        assertEquals(Float.valueOf((float) 1.5), list.get(0));
        assertEquals(Float.valueOf((float) 2.5), list.get(1));
        assertEquals(Float.valueOf((float) 3.5), list.get(2));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.FloatsTypeName));
    }

    public void testFloatsFromSingle() throws Exception {
        List<Float> list = SINGLE_MAPPER.readValue("1", PrimitiveTypes.FloatsType);
        assertEquals(1, list.size());
        assertEquals(Float.valueOf(1), list.get(0));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.FloatsTypeName));
    }

    public void testDoubles() throws Exception {
        List<Double> list = MAPPER.readValue("[1.5,2.5,3.5]", PrimitiveTypes.DoublesTypeReference);
        assertEquals(3, list.size());
        assertEquals(Double.valueOf(1.5), list.get(0));
        assertEquals(Double.valueOf(2.5), list.get(1));
        assertEquals(Double.valueOf(3.5), list.get(2));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.DoublesTypeName));
    }

    public void testDoublesFromSingle() throws Exception {
        List<Double> list = SINGLE_MAPPER.readValue("1", PrimitiveTypes.DoublesType);
        assertEquals(1, list.size());
        assertEquals(Double.valueOf(1d), list.get(0));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.DoublesTypeName));
    }

    public void testInts() throws Exception {
        List<Integer> list = MAPPER.readValue("[1,2,3]", PrimitiveTypes.IntsTypeReference);
        assertEquals(3, list.size());
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
        assertEquals(Integer.valueOf(3), list.get(2));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.IntsTypeName));
    }

    public void testIntsFromSingle() throws Exception {
        List<Integer> list = SINGLE_MAPPER.readValue("1", PrimitiveTypes.IntsTypeReference);
        assertEquals(1, list.size());
        assertEquals(Integer.valueOf(1), list.get(0));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.IntsTypeName));
    }

    public void testLongs() throws Exception {
        List<Long> list = MAPPER.readValue("[1,2,3]", PrimitiveTypes.LongsTypeReference);
        assertEquals(3, list.size());
        assertEquals(Long.valueOf(1), list.get(0));
        assertEquals(Long.valueOf(2), list.get(1));
        assertEquals(Long.valueOf(3), list.get(2));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.LongsTypeName));
    }

    public void testLongsFromSingle() throws Exception {
        List<Long> list = SINGLE_MAPPER.readValue("1", PrimitiveTypes.LongsTypeReference);
        assertEquals(1, list.size());
        assertEquals(Long.valueOf(1), list.get(0));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.LongsTypeName));
    }

    public void testShorts() throws Exception {
        List<Short> list = MAPPER.readValue("[1,2,3]", PrimitiveTypes.ShortsTypeReference);
        assertEquals(3, list.size());
        assertEquals(Short.valueOf((short) 1), list.get(0));
        assertEquals(Short.valueOf((short) 2), list.get(1));
        assertEquals(Short.valueOf((short) 3), list.get(2));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.ShortsTypeName));
    }

    public void testShortsFromSingle() throws Exception {
        List<Short> list = SINGLE_MAPPER.readValue("1", PrimitiveTypes.ShortsTypeReference);
        assertEquals(1, list.size());
        assertEquals(Short.valueOf((short) 1), list.get(0));
        assertTrue(list.getClass().getName().equals(PrimitiveTypes.ShortsTypeName));
    }

    /*
    /**********************************************************************
    /* Polymorphic handling
    /**********************************************************************
     */

    public void testTypedInts() throws Exception {
        PolymorphicHolder h;
        String json;
        PolymorphicHolder result;

        // First, with one entry
        List<Integer> ints = Ints.asList(1);
        h = new PolymorphicHolder(ints);
        json = POLY_MAPPER.writeValueAsString(h);

        // so far so good. and back?
        result = POLY_MAPPER.readValue(json, PolymorphicHolder.class);
        assertNotNull(result.value);
        if (!(PrimitiveTypes.IntsType.isInstance(result.value))) {
            fail("Expected " + PrimitiveTypes.IntsTypeName + ", got " + result.value.getClass());
        }
        assertEquals(1, ((List<?>) result.value).size());

        // and then an empty version:
        List<Integer> emptyList = Ints.asList(new int[0]);
        h = new PolymorphicHolder(emptyList);
        json = POLY_MAPPER.writeValueAsString(h);
        result = POLY_MAPPER.readValue(json, PolymorphicHolder.class);
        assertNotNull(result.value);
        if (!(Collections.emptyList().getClass().isInstance(result.value))) {
            fail("Expected " + Collections.emptyList().getClass().getName() + ", got " + result.value.getClass());
        }
        assertEquals(0, ((List<?>) result.value).size());
    }

    public void testTypedLongs() throws Exception {
        PolymorphicHolder h;
        String json;
        PolymorphicHolder result;

        // First, with one entry
        List<Long> longs = Longs.asList(1L);
        h = new PolymorphicHolder(longs);
        json = POLY_MAPPER.writeValueAsString(h);

        // so far so good. and back?
        result = POLY_MAPPER.readValue(json, PolymorphicHolder.class);
        assertNotNull(result.value);
        if (!(PrimitiveTypes.LongsType.isInstance(result.value))) {
            fail("Expected " + PrimitiveTypes.LongsTypeName + ", got " + result.value.getClass());
        }
        assertEquals(1, ((List<?>) result.value).size());

        // and then an empty version:
        List<Long> emptyList = Longs.asList(new long[0]);
        h = new PolymorphicHolder(emptyList);
        json = POLY_MAPPER.writeValueAsString(h);
        result = POLY_MAPPER.readValue(json, PolymorphicHolder.class);
        assertNotNull(result.value);
        if (!(Collections.emptyList().getClass().isInstance(result.value))) {
            fail("Expected " + Collections.emptyList().getClass().getName() + ", got " + result.value.getClass());
        }
        assertEquals(0, ((List<?>) result.value).size());
    }

}
