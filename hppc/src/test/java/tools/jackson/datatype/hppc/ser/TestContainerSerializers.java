package tools.jackson.datatype.hppc.ser;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.hppc.ModuleTestBase;

import org.junit.jupiter.api.Test;

import com.carrotsearch.hppc.*;

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
        ObjectArrayList<Object> list = new ObjectArrayList<>();
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

        // only up to the highest set bit, not the allocated capacity
        assertEquals("[false,true,false,false,true]", mapper.writeValueAsString(bitset));

        // pre-sized: capacity far beyond the highest set bit
        BitSet large = new BitSet(1_000_000);
        large.set(2);
        assertEquals("[false,false,true]", mapper.writeValueAsString(large));

        assertEquals("[]", mapper.writeValueAsString(new BitSet()));
        assertEquals("[]", mapper.writeValueAsString(new BitSet(1_000)));

        // only bit 0
        BitSet first = new BitSet();
        first.set(0);
        assertEquals("[true]", mapper.writeValueAsString(first));
    }

    @Test
    public void testBitSetSerializerWordBoundaries() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        for (int index : new int[] { 62, 63, 64, 65, 127, 128, 200 }) {
            BitSet bitset = new BitSet(256);
            bitset.set(index);
            assertEquals(_bitsJson(index + 1, index), mapper.writeValueAsString(bitset),
                    "bit #"+index);
        }

        // bits on both sides of a word boundary
        BitSet bitset = new BitSet();
        bitset.set(0);
        bitset.set(63);
        bitset.set(64);
        assertEquals(_bitsJson(65, 0, 63, 64), mapper.writeValueAsString(bitset));
    }

    @Test
    public void testBitSetSerializerTrailingZeroWords() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        // clearing the highest bit leaves trailing zero words in use
        BitSet bitset = new BitSet();
        bitset.set(70);
        bitset.clear(70);
        bitset.set(3);
        assertEquals("[false,false,false,true]", mapper.writeValueAsString(bitset));
    }

    // Serialization must not modify the BitSet; and must include bits set via
    // `getAndSet()`, which does not update `wlen`
    @Test
    public void testBitSetSerializerDoesNotModify() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        BitSet bitset = new BitSet(128);
        bitset.set(1);
        final int wlen = bitset.wlen;
        assertEquals("[false,true]", mapper.writeValueAsString(bitset));
        assertEquals(wlen, bitset.wlen);

        bitset.getAndSet(100);
        assertEquals(_bitsJson(101, 1, 100), mapper.writeValueAsString(bitset));

        // also when `wlen` was trimmed before set via `getAndSet()`
        BitSet other = new BitSet(128);
        other.set(0);
        other.clear();
        other.set(0);
        other.getAndSet(100);
        assertEquals(_bitsJson(101, 0, 100), mapper.writeValueAsString(other));
    }

    private static String _bitsJson(int length, int... setBits) {
        java.util.BitSet set = new java.util.BitSet();
        for (int bit : setBits) {
            set.set(bit);
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < length; ++i) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(set.get(i));
        }
        return sb.append(']').toString();
    }
}
