package com.fasterxml.jackson.datatype.guava.optional;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;
import com.google.common.collect.ImmutableList;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalFromNullInListTest extends ModuleTestBase
{
    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testImmutableListOfOptionals() throws Exception {
        ImmutableList<AtomicReference<?>> list = MAPPER.readValue("[1,null,3]",
                new TypeReference<ImmutableList<AtomicReference<?>>>() { });
        assertEquals(3, list.size());

        AtomicReference<?> ref = list.get(0);
        assertNotNull(ref);
        assertEquals(Integer.valueOf(1), ref.get());

        ref = list.get(1);
        assertNotNull(ref);
        assertNull(ref.get());
        
        ref = list.get(2);
        assertNotNull(ref);
        assertEquals(Integer.valueOf(3), ref.get());
   }
}
