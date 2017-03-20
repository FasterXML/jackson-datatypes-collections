package com.fasterxml.jackson.datatype.guava.optional;

import com.fasterxml.jackson.annotation.JsonMerge;
import com.fasterxml.jackson.annotation.OptBoolean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;

import com.google.common.base.Optional;

public class OptionalMergingTest extends ModuleTestBase
{
    static class MergedStringReference
    {
        @JsonMerge(OptBoolean.TRUE)
        public Optional<String> value = Optional.of("default");
    }

    static class MergedPOJOReference
    {
        @JsonMerge(OptBoolean.TRUE)
        public Optional<POJO> value;

        protected MergedPOJOReference() {
            value = Optional.of(new POJO(7, 2));
        }

        public MergedPOJOReference(int x, int y) {
            value = Optional.of(new POJO(x, y));
        }
    }

    static class POJO {
        public int x, y;

        protected POJO() { }
        public POJO(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testStringReferenceMerging() throws Exception
    {
        MergedStringReference result = MAPPER.readValue(aposToQuotes("{'value':'override'}"),
                MergedStringReference.class);
        assertEquals("override", result.value.get());
    }

    public void testPOJOReferenceMerging() throws Exception
    {
        MergedPOJOReference result = MAPPER.readValue(aposToQuotes("{'value':{'y':-6}}"),
                MergedPOJOReference.class);
        assertEquals(7, result.value.get().x);
        assertEquals(-6, result.value.get().y);

        // Also should retain values we pass
        result = MAPPER.readerForUpdating(new MergedPOJOReference(10, 20))
                .readValue(aposToQuotes("{'value':{'x':11}}"));
        assertEquals(11, result.value.get().x);
        assertEquals(20, result.value.get().y);
    }
}
