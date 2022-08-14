package tools.jackson.datatype.guava.optional;

import com.fasterxml.jackson.annotation.*;
import tools.jackson.databind.*;
import tools.jackson.datatype.guava.ModuleTestBase;

import com.google.common.base.Optional;

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
    public void testUntypedWithOptionalsNotNulls() throws Exception
    {
        final ObjectMapper mapper = mapperWithModule(false);
        String jsonExp = aposToQuotes("{'XX.name':'Bob'}");
        String jsonAct = mapper.writeValueAsString(new OptionalParent());
        assertEquals(jsonExp, jsonAct);
    }
}
