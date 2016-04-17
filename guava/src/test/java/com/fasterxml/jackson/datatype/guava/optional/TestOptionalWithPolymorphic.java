package com.fasterxml.jackson.datatype.guava.optional;

import java.util.Map;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

public class TestOptionalWithPolymorphic extends ModuleTestBase
{
    static class ContainerA {
        @JsonProperty private Optional<String> name = Optional.absent();
        @JsonProperty private Optional<Strategy> strategy = Optional.absent();
    }

    static class ContainerB {
        @JsonProperty private Optional<String> name = Optional.absent();
        @JsonProperty private Strategy strategy = null;
    }
         
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({
    @JsonSubTypes.Type(name = "Foo", value = Foo.class),
    @JsonSubTypes.Type(name = "Bar", value = Bar.class),
    @JsonSubTypes.Type(name = "Baz", value = Baz.class)
    })
    interface Strategy {
    }
     
    static class Foo implements Strategy {
        @JsonProperty private final int foo;
        @JsonCreator Foo(@JsonProperty("foo") int foo) {
            this.foo = foo;
        }
    }
         
    static class Bar implements Strategy {
        @JsonProperty private final boolean bar;
        @JsonCreator Bar(@JsonProperty("bar") boolean bar) {
            this.bar = bar;
        }
    }
         
    static class Baz implements Strategy {
        @JsonProperty private final String baz;
        @JsonCreator Baz(@JsonProperty("baz") String baz) {
            this.baz = baz;
        }
    }

    static class AbstractOptional {
        @JsonDeserialize(contentAs=Integer.class)
        public Optional<java.io.Serializable> value;
    }

    static class TypeInfoOptional {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type")
        public Optional<Strategy> value;
    }

    /*
    /**********************************************************************
    /* Test methods
    /**********************************************************************
     */

    final ObjectMapper MAPPER = mapperWithModule();
    
    public void testOptionalMapsFoo() throws Exception {

        ImmutableMap<String, Object> foo = ImmutableMap.<String, Object>builder()
            .put("name", "foo strategy")
            .put("strategy", ImmutableMap.builder()
            .put("type", "Foo")
            .put("foo", 42)
            .build())
            .build();
        _test(MAPPER, foo);
    }

    public void testOptionalMapsBar() throws Exception {

        ImmutableMap<String, Object> bar = ImmutableMap.<String, Object>builder()
            .put("name", "bar strategy")
            .put("strategy", ImmutableMap.builder()
            .put("type", "Bar")
            .put("bar", true)
            .build())
            .build();
        _test(MAPPER, bar);
    }

    public void testOptionalMapsBaz() throws Exception {
        ImmutableMap<String, Object> baz = ImmutableMap.<String, Object>builder()
            .put("name", "baz strategy")
            .put("strategy", ImmutableMap.builder()
            .put("type", "Baz")
            .put("baz", "hello world!")
            .build())
            .build();
        _test(MAPPER, baz);
    }

    public void testOptionalWithTypeAnnotation() throws Exception
    {
        AbstractOptional result = MAPPER.readValue("{\"value\" : 5}",
                AbstractOptional.class);
        assertNotNull(result);
        assertNotNull(result.value);
        Object ob = result.value.get();
        assertEquals(Integer.class, ob.getClass());
        assertEquals(Integer.valueOf(5), ob);
    }

    public void testOptionalPropagatesTypeInfo() throws Exception
    {
        TypeInfoOptional data = new TypeInfoOptional();
        data.value = Optional.<Strategy>of(new Foo(42));
        assertEquals(aposToQuotes("{'value':{'$type':'Foo','foo':42}}"), MAPPER.writeValueAsString(data));
    }

    private void _test(ObjectMapper m, Map<String, ?> map) throws Exception
    {
        String json = m.writeValueAsString(map);

        ContainerA objA = m.readValue(json, ContainerA.class);
        assertNotNull(objA);

        ContainerB objB = m.readValue(json, ContainerB.class);
        assertNotNull(objB);
    }
}
