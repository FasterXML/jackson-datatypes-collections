package com.fasterxml.jackson.datatype.hppc.deser;

import java.util.Arrays;

import tools.jackson.databind.ObjectMapper;

import org.junit.Assert;

import com.carrotsearch.hppc.*;

import com.fasterxml.jackson.datatype.hppc.ModuleTestBase;

public class TestContainerDeserializers extends ModuleTestBase
{
    public void testIntDeserializers() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        // first, direct
        IntArrayList array = mapper.readValue("[1,-3]", IntArrayList.class);
        Assert.assertArrayEquals(new int[] { 1, -3 }, array.toArray());
        IntHashSet set = mapper.readValue("[-1234,0]", IntHashSet.class);

        // 08-Apr-2014, tatu: Order indeterminate actually, has change between 0.4 and 0.5
        _assertSets(new int[] { -1234, 0 }, set.toArray());

        IntArrayDeque dq = mapper.readValue("[0,13]", IntArrayDeque.class);
        Assert.assertArrayEquals(new int[] { -0, 13 }, dq.toArray());

        // then via abstract class defaulting
        IntIndexedContainer array2 = mapper.readValue("[1,-3]", IntIndexedContainer.class);
        Assert.assertArrayEquals(new int[] { 1, -3 }, array2.toArray());
        IntSet set2 = mapper.readValue("[-1234,0]", IntSet.class);
        _assertSets(new int[] { -1234, 0 }, set2.toArray());
        IntDeque dq2 = mapper.readValue("[0,13]", IntDeque.class);
        Assert.assertArrayEquals(new int[] { -0, 13 }, dq2.toArray());
    }

    private void _assertSets(int[] exp, int[] actual)
    {
        assertEquals(exp.length, actual.length);

        int[] exp2 = Arrays.copyOf(exp, exp.length);
        int[] act2 = Arrays.copyOf(actual, actual.length);

        Arrays.sort(exp2);
        Arrays.sort(act2);

        Assert.assertArrayEquals(exp2, act2);
    }
}
