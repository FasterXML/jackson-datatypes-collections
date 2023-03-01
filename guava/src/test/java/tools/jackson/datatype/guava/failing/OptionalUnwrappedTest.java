package tools.jackson.datatype.guava.failing;

import com.fasterxml.jackson.annotation.*;

import tools.jackson.databind.*;
import tools.jackson.datatype.guava.*;

import com.google.common.base.Optional;

/**
 * Unit test for remaining part of #64.
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

    // Test for "old" settings (2.5 and earlier only option; available on later too)
    public void testUntypedWithNullEqOptionals() throws Exception
    {
        final ObjectMapper mapper = mapperWithModule(true);
        String jsonExp = a2q("{'XX.name':'Bob'}");
        String jsonAct = mapper.writeValueAsString(new OptionalParent());
        assertEquals(jsonExp, jsonAct);
    }
}
