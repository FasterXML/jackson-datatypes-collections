package com.fasterxml.jackson.datatype.hppc.deser;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.carrotsearch.hppc.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.datatype.hppc.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class TestContainerDeserializers extends ModuleTestBase
{

    @Test
    public void testIntDeserializers() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        // first, direct
        IntArrayList array = mapper.readValue("[1,-3]", IntArrayList.class);
        assertArrayEquals(new int[] { 1, -3 }, array.toArray());
        IntHashSet set = mapper.readValue("[-1234,0]", IntHashSet.class);

        // 08-Apr-2014, tatu: Order indeterminate actually, has change between 0.4 and 0.5
        _assertSets(new int[] { -1234, 0 }, set.toArray());

        IntArrayDeque dq = mapper.readValue("[0,13]", IntArrayDeque.class);
        assertArrayEquals(new int[] { -0, 13 }, dq.toArray());

        // then via abstract class defaulting
        IntIndexedContainer array2 = mapper.readValue("[1,-3]", IntIndexedContainer.class);
        assertArrayEquals(new int[] { 1, -3 }, array2.toArray());
        IntSet set2 = mapper.readValue("[-1234,0]", IntSet.class);
        _assertSets(new int[] { -1234, 0 }, set2.toArray());
        IntDeque dq2 = mapper.readValue("[0,13]", IntDeque.class);
        assertArrayEquals(new int[] { -0, 13 }, dq2.toArray());
    }

    private void _assertSets(int[] exp, int[] actual)
    {
        assertEquals(exp.length, actual.length);

        int[] exp2 = Arrays.copyOf(exp, exp.length);
        int[] act2 = Arrays.copyOf(actual, actual.length);

        Arrays.sort(exp2);
        Arrays.sort(act2);

        assertArrayEquals(exp2, act2);
    }
}
