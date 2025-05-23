package com.fasterxml.jackson.datatype.hppc.ser;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.carrotsearch.hppc.*;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.datatype.hppc.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class TestContainerSerializers extends ModuleTestBase
{
    @Test
    public void testByteSerializer() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        final byte[] input = new byte[] {
                (byte)-12, (byte)0, (byte) -1, (byte) 0x7F
        };
        ByteArrayList array = new ByteArrayList();
        array.add(input);
        // will be base64 encoded
        
        assertEquals(mapper.writeValueAsString(input), mapper.writeValueAsString(array));

        // 07-May-2015, tatu: HPPC-0.7 dropped byte/float/double key associate sets/maps:
/*        
        ByteHashSet set = new ByteHashSet();
        set.add(new byte[] { (byte) 1, (byte) 2});
        String str = mapper.writeValueAsString(set);
        // hmmh. order of set is indeterminate, so need to compare two possibilities
        final String OK1 = "\"AgE=\"";
        final String OK2 = "\"AQI=\"";
        if (!(str.equals(OK1) || str.equals(OK2))) {
            fail("Should have gotten either '"+OK1+"' or '"+OK2+"', instead got '"+str+"'");
        }
        */
    }

    @Test
    public void testShortSerializer() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        ShortArrayList array = new ShortArrayList();
        array.add((short)-12, (short)0);
        assertEquals("[-12,0]", mapper.writeValueAsString(array));

        ShortHashSet set = new ShortHashSet();
        set.addAll((short)1, (short)2);
        String str = mapper.writeValueAsString(set);
        if (!"[1,2]".equals(str) && !"[2,1]".equals(str)) {
            fail("Incorrect serialization: "+str);
        }
    }

    @Test
    public void testIntSerializer() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        IntArrayList array = new IntArrayList();
        array.add(-12, 0);
        assertEquals("[-12,0]", mapper.writeValueAsString(array));

        IntHashSet set = new IntHashSet();
        set.addAll(1, 2);
        String str = mapper.writeValueAsString(set);
        if (!"[1,2]".equals(str) && !"[2,1]".equals(str)) {
            fail("Incorrect serialization: "+str);
        }
    }

    @Test
    public void testLongSerializer() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        LongArrayList array = new LongArrayList();
        array.add(-12L, 0L);
        assertEquals("[-12,0]", mapper.writeValueAsString(array));

        LongHashSet set = new LongHashSet();
        set.addAll(1L, 2L);
        String str = mapper.writeValueAsString(set);
        if (!"[1,2]".equals(str) && !"[2,1]".equals(str)) {
            fail("Incorrect serialization: "+str);
        }
    }

    @Test
    public void testCharSerializer() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        CharArrayList array = new CharArrayList();
        array.add('a', 'b', 'c');
        assertEquals("\"abc\"", mapper.writeValueAsString(array));

        CharHashSet set = new CharHashSet();
        set.addAll('d','e');
        String str = mapper.writeValueAsString(set);
        if (!"\"de\"".equals(str) && !"\"ed\"".equals(str)) {
            fail("Incorrect serialization: "+str);
        }
    }

    @Test
    public void testFloatSerializer() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        FloatArrayList array = new FloatArrayList();
        array.add(-12.25f, 0.5f);
        assertEquals("[-12.25,0.5]", mapper.writeValueAsString(array));

        // 07-May-2015, tatu: HPPC-0.7 dropped byte/float/double key associate sets/maps:
        /*
        FloatHashSet set = new FloatHashSet();
        set.add(1.75f, 0.5f);
        String str = mapper.writeValueAsString(set);
        if (!"[1.75,0.5]".equals(str) && !"[0.5,1.75]".equals(str)) {
            fail("Incorrect serialization: "+str);
        }
        */
    }

    @Test
    public void testDoubleSerializer() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        DoubleArrayList array = new DoubleArrayList();
        array.add(-12.25, 0.5);
        assertEquals("[-12.25,0.5]", mapper.writeValueAsString(array));

        // 07-May-2015, tatu: HPPC-0.7 dropped byte/float/double key associate sets/maps:
/*        
        DoubleHashSet set = new DoubleHashSet();
        set.add(1.75, 0.5);
        String str = mapper.writeValueAsString(set);
        if (!"[1.75,0.5]".equals(str) && !"[0.5,1.75]".equals(str)) {
            fail("Incorrect serialization: "+str);
        }
        */
    }

    /*
    /**********************************************************************
    /* Tests for non-numeric containers
    /**********************************************************************
     */

    @Test
    public void testObjectContainerSerializer() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        // first, untyped case
        ObjectArrayList<Object> list = new ObjectArrayList<Object>();
        list.add("foo");
        list.add(Integer.valueOf(3));
        list.add((Object) null);
        assertEquals("[\"foo\",3,null]", mapper.writeValueAsString(list));

        // TODO: polymorphic case (@JsonTypeInfo and/or default typing)
    }

    @Test
    public void testBitSetSerializer() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        BitSet bitset = new BitSet();
        bitset.set(1);
        bitset.set(4);

        // note: since storage is in units of 64 bits, we may get more than what we asked for, so:
        String str = mapper.writeValueAsString(bitset);
        assertTrue(str.startsWith("[false,true,false,false,true"));
    }
}
