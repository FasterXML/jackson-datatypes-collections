package com.fasterxml.jackson.datatype.guava.optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;
import com.google.common.base.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for #64, in new mode.
 */
public class OptionalUnwrappedTest extends ModuleTestBase
{
    static class Child {
        public String name = "Bob";
    }

    static class Parent {
        private Child child = new Child();

        @JsonUnwrapped
        public Child getChild() { return child; }
    }

    static class OptionalParent {
        @JsonUnwrapped(prefix="XX.")
        public Optional<Child> child = Optional.of(new Child());
    }

    // Test for "new" settings of absent != nulls, available on 2.6 and later
    @Test
    public void testUntypedWithOptionalsNotNulls() throws Exception
    {
        final ObjectMapper mapper = mapperWithModule(false);
        String jsonExp = aposToQuotes("{'XX.name':'Bob'}");
        String jsonAct = mapper.writeValueAsString(new OptionalParent());
        assertEquals(jsonExp, jsonAct);
    }

    // Test for "old" settings (2.5 and earlier only option; available on later too)
    // Fixed via [datatypes-collections#136]
    @Test
    public void testUntypedWithNullEqOptionals() throws Exception
    {
        final ObjectMapper mapper = mapperWithModule(true);
        String jsonExp = aposToQuotes("{'XX.name':'Bob'}");
        String jsonAct = mapper.writeValueAsString(new OptionalParent());
        assertEquals(jsonExp, jsonAct);
    }
}
