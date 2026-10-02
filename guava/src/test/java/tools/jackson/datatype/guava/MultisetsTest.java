package tools.jackson.datatype.guava;

import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import com.google.common.collect.*;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.*;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests to verify handling of various {@link Multiset}s.
 */
public class MultisetsTest extends ModuleTestBase
{
    record Person(String name, int age) { }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
    @JsonSubTypes({ @JsonSubTypes.Type(value = Dog.class, name = "dog"),
        @JsonSubTypes.Type(value = Cat.class, name = "cat") })
    interface Animal { }

    record Dog(String name) implements Animal { }

    record Cat(String name) implements Animal { }

    /*
    /**********************************************************************
    /* Unit tests for verifying handling in absence of module registration
    /**********************************************************************
     */

    /**
     * Multi-sets can actually be serialized as regular collections, without
     * problems.
     */
    @Test
    public void testWithoutSerializers() throws Exception
    {

        ObjectMapper mapper = new ObjectMapper();
        Multiset<String> set = LinkedHashMultiset.create();
        // hash-based multi-sets actually keeps 'same' instances together, otherwise insertion-ordered:
        set.add("abc");
        set.add("foo");
        set.add("abc");
        String json = mapper.writeValueAsString(set);
        assertEquals("[\"abc\",\"abc\",\"foo\"]", json);
    }

    // 11-Jul-2017, tatu: Not sure if this test makes sense actually...
    @Test
    public void testWithoutDeserializers() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            /*Multiset<String> set =*/ mapper.readValue("[\"abc\",\"abc\",\"foo\"]",
                    new TypeReference<Multiset<String>>() { });
            fail("Should have failed");
        } catch (InvalidDefinitionException e) {
            // Exception changed a bit in 2.18.2, need to match
            //verifyException(e, "cannot find a deserializer");
            verifyException(e, "Cannot construct instance of ");
            verifyException(e, "No creators");
            verifyException(e, Multiset.class.getName());
        }
    }

    /*
    /**********************************************************************
    /* Unit tests for actual registered module
    /**********************************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    private final ObjectMapper MAPPER_ELEMENTS = JsonMapper.builder()
            .addModule(new GuavaModule().configureMultisetsAsEntries(false))
            .build();

    @Test
    public void testDefaultMultiset() throws Exception
    {
        _testMultiset(new TypeReference<Multiset<String>>() { });
    }

    @Test
    public void testDefaultSortedMultiset() throws Exception {
        _testMultiset(new TypeReference<SortedMultiset<String>>() { });
    }

    @Test
    public void testLinkedHashMultiset() throws Exception {
        _testMultiset(new TypeReference<LinkedHashMultiset<String>>() { });
    }

    @Test
    public void testHashMultiset() throws Exception {
        _testMultiset(new TypeReference<HashMultiset<String>>() { });
    }

    @Test
    public void testTreeMultiset() throws Exception {
        _testMultiset(new TypeReference<TreeMultiset<String>>() { });
    }

    @Test
    public void testImmutableMultiset() throws Exception {
        _testMultiset(new TypeReference<ImmutableMultiset<String>>() { });
    }

    @Test
    public void testImmutableSortedMultiset() throws Exception {
        _testMultiset(new TypeReference<ImmutableSortedMultiset<String>>() { });
    }

    private <T extends Multiset<String>> void _testMultiset(TypeReference<T> typeRef)
        throws Exception
    {
        T set = MAPPER.readValue(a2q("[{'element':'abc','count':2},{'count':1,'element':'foo'}]"), typeRef);
        _verifyAbcAbcFoo(set);
        assertEquals(set, MAPPER.readValue(MAPPER.writeValueAsString(set), typeRef));
        assertTrue(MAPPER.readValue("[]", typeRef).isEmpty());

        // This may or may not work, so...
        try {
            set = MAPPER.readValue(a2q("[{'element':'abc','count':1},{'element':null,'count':2}]"), typeRef);
            assertEquals(1, set.count("abc"));
            assertEquals(2, set.count(null));
        } catch (MismatchedInputException e) {
            verifyException(e, "Guava `Collection` of type ");
            verifyException(e, "does not accept `null` values");
        }

        // and same with format where elements are repeated
        set = MAPPER_ELEMENTS.readValue("[\"abc\",\"abc\",\"foo\"]", typeRef);
        _verifyAbcAbcFoo(set);

        try {
            set = MAPPER_ELEMENTS.readValue("[\"abc\", null]", typeRef);
            assertEquals(1, set.count("abc"));
            assertEquals(1, set.count(null));
        } catch (MismatchedInputException e) {
            verifyException(e, "Guava `Collection` of type ");
            verifyException(e, "does not accept `null` values");
        }
    }

    private void _verifyAbcAbcFoo(Multiset<String> set) {
        assertEquals(3, set.size());
        assertEquals(1, set.count("foo"));
        assertEquals(2, set.count("abc"));
        assertEquals(0, set.count("bar"));
    }

    @Test
    public void testSerialize() throws Exception
    {
        Multiset<String> set = LinkedHashMultiset.create();
        set.add("apple", 5);
        set.add("pear", 2);
        assertEquals(a2q("[{'element':'apple','count':5},{'element':'pear','count':2}]"),
                MAPPER.writeValueAsString(set));
        assertEquals(a2q("['apple','apple','apple','apple','apple','pear','pear']"),
                MAPPER_ELEMENTS.writeValueAsString(set));
        assertEquals("[]", MAPPER.writeValueAsString(LinkedHashMultiset.create()));
    }

    @Test
    public void testPojoElements() throws Exception
    {
        Multiset<Person> set = HashMultiset.create();
        set.add(new Person("Ann", 30), 3);
        String json = MAPPER.writeValueAsString(set);
        assertEquals(a2q("[{'element':{'name':'Ann','age':30},'count':3}]"), json);
        assertEquals(set, MAPPER.readValue(json, new TypeReference<Multiset<Person>>() { }));
    }

    @Test
    public void testPolymorphicElements() throws Exception
    {
        Multiset<Animal> set = LinkedHashMultiset.create();
        set.add(new Dog("Rex"), 2);
        set.add(new Cat("Tom"));
        String json = MAPPER.writerFor(new TypeReference<Multiset<Animal>>() { })
                .writeValueAsString(set);
        assertEquals(a2q("[{'element':{'@type':'dog','name':'Rex'},'count':2},"
                +"{'element':{'@type':'cat','name':'Tom'},'count':1}]"), json);
        assertEquals(set, MAPPER.readValue(json, new TypeReference<Multiset<Animal>>() { }));
        assertEquals(set, MAPPER.readValue(json, new TypeReference<ImmutableMultiset<Animal>>() { }));
    }

    @Test
    public void testFromSingle() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();
        Multiset<String> set = mapper.readValue(a2q("{'element':'abc','count':2}"),
                new TypeReference<Multiset<String>>() { });
        assertEquals(2, set.size());
        assertEquals(2, set.count("abc"));

        set = mapper.readValue(a2q("{'element':'abc','count':2}"),
                new TypeReference<ImmutableMultiset<String>>() { });
        assertEquals(2, set.count("abc"));

        mapper = JsonMapper.builder()
            .addModule(new GuavaModule().configureMultisetsAsEntries(false))
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();
        set = mapper.readValue("\"abc\"",
                new TypeReference<Multiset<String>>() { });
        assertEquals(1, set.size());
        assertTrue(set.contains("abc"));
    }

    @Test
    public void testSingleEntryUnwrapped() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
            .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();
        Multiset<String> set = HashMultiset.create();
        set.add("abc", 3);
        String json = mapper.writeValueAsString(set);
        assertEquals(a2q("{'element':'abc','count':3}"), json);
        assertEquals(set, mapper.readValue(json, new TypeReference<Multiset<String>>() { }));
    }

    @Test
    public void testInvalidEntries() throws Exception
    {
        _verifyInvalid("[\"abc\",\"abc\"]", "expected JSON Object with properties \"element\" and \"count\"");
        _verifyInvalid(a2q("[{'element':'abc'}]"), "missing \"count\" property");
        _verifyInvalid(a2q("[{'count':2}]"), "missing \"element\" property");
        _verifyInvalid(a2q("[{'element':'abc','count':0}]"), "expected positive integer, got 0");
        _verifyInvalid(a2q("[{'element':'abc','count':-1}]"), "expected positive integer, got -1");
        _verifyInvalid(a2q("[{'element':'abc','count':'2'}]"), "expected positive integer, got VALUE_STRING");
        _verifyInvalid(a2q("[{'element':'abc','count':1.5}]"), "expected positive integer, got VALUE_NUMBER_FLOAT");
    }

    static class SortedMultisetWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public ImmutableSortedMultiset<Comparable<?>> values;
    }

    @Test
    public void testIncomparableElements() throws Exception
    {
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(a2q("[{'element':1,'count':1},{'element':'a','count':1}]"),
                        new TypeReference<TreeMultiset<Object>>() { }));
        verifyException(e, "Failed to build `TreeMultiset`");

        e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(a2q("[{'element':{'name':'Ann','age':30},'count':1}]"),
                        new TypeReference<SortedMultiset<Person>>() { }));
        verifyException(e, "Failed to build `SortedMultiset`");

        ObjectMapper polyMapper = builderWithModule()
                .polymorphicTypeValidator(new NoCheckSubTypeValidator())
                .build();
        // large enough input for builder to sort while entries are still being added
        for (int count : new int[] { 1, 100 }) {
            e = assertThrows(MismatchedInputException.class,
                    () -> polyMapper.readValue(_mixedTypeEntries(count), SortedMultisetWrapper.class));
            verifyException(e, "Failed to build `ImmutableSortedMultiset`");
        }
    }

    @Test
    public void testIncomparableElementsAsRepeatedElements() throws Exception
    {
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER_ELEMENTS.readValue("[1,\"a\"]",
                        new TypeReference<TreeMultiset<Object>>() { }));
        verifyException(e, "Failed to build `TreeMultiset`");

        e = assertThrows(MismatchedInputException.class,
                () -> MAPPER_ELEMENTS.readValue(a2q("[{'name':'Ann','age':30}]"),
                        new TypeReference<SortedMultiset<Person>>() { }));
        verifyException(e, "Failed to build `SortedMultiset`");

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new GuavaModule().configureMultisetsAsEntries(false))
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        e = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(a2q("{'name':'Ann','age':30}"),
                        new TypeReference<SortedMultiset<Person>>() { }));
        verifyException(e, "Failed to build `SortedMultiset`");
    }

    // empty String is coerced into `null` by element deserializer
    @Test
    public void testNullFromElementDeserializer() throws Exception
    {
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER_ELEMENTS.readValue("[1,\"\"]",
                        new TypeReference<TreeMultiset<Integer>>() { }));
        verifyException(e, "does not accept `null` values");

        e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(a2q("[{'element':'','count':1}]"),
                        new TypeReference<TreeMultiset<Integer>>() { }));
        verifyException(e, "does not accept `null` values");

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new GuavaModule().configureMultisetsAsEntries(false))
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        e = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue("\"\"", new TypeReference<TreeMultiset<Integer>>() { }));
        verifyException(e, "does not accept `null` values");

        Multiset<Integer> set = MAPPER_ELEMENTS.readValue("[1,\"\"]",
                new TypeReference<HashMultiset<Integer>>() { });
        assertEquals(1, set.count(null));
    }

    private static String _mixedTypeEntries(int count) {
        StringBuilder sb = new StringBuilder("{\"values\":[");
        for (int i = 0; i < count; i++) {
            sb.append("{\"element\":[\"java.lang.Integer\",").append(i).append("],\"count\":1},");
        }
        return sb.append("{\"element\":[\"java.lang.String\",\"a\"],\"count\":1}]}").toString();
    }

    @Test
    public void testMaxSize() throws Exception
    {
        final TypeReference<?>[] refs = new TypeReference<?>[] {
                new TypeReference<HashMultiset<String>>() { },
                new TypeReference<TreeMultiset<String>>() { },
                new TypeReference<ImmutableMultiset<String>>() { },
                new TypeReference<ImmutableSortedMultiset<String>>() { } };
        // default limit
        for (TypeReference<?> ref : refs) {
            MismatchedInputException e = assertThrows(MismatchedInputException.class,
                    () -> MAPPER.readValue(a2q("[{'element':'a','count':2147483647}]"), ref));
            verifyException(e, "`Multiset` size (2147483647) exceeds the maximum allowed (10000000");
        }

        // custom limit: applies to sum of counts of all entries
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new GuavaModule().configureMaxMultisetSize(5))
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        for (TypeReference<?> ref : refs) {
            Multiset<?> set = (Multiset<?>) mapper.readValue(
                    a2q("[{'element':'a','count':3},{'element':'b','count':2}]"), ref);
            assertEquals(5, set.size());

            MismatchedInputException e = assertThrows(MismatchedInputException.class,
                    () -> mapper.readValue(a2q("[{'element':'a','count':3},{'element':'a','count':3}]"), ref));
            verifyException(e, "`Multiset` size (6) exceeds the maximum allowed (5");

            e = assertThrows(MismatchedInputException.class,
                    () -> mapper.readValue(a2q("{'element':'a','count':6}"), ref));
            verifyException(e, "`Multiset` size (6) exceeds the maximum allowed (5");
        }

        assertThrows(IllegalArgumentException.class,
                () -> new GuavaModule().configureMaxMultisetSize(0));
    }

    // Serialization uses actual type of value, deserialization declared type
    static class CollectionWrapper {
        public Collection<String> values;
    }

    @Test
    public void testMultisetDeclaredAsCollection() throws Exception
    {
        CollectionWrapper w = new CollectionWrapper();
        w.values = HashMultiset.create(Arrays.asList("a", "a"));

        String json = MAPPER.writeValueAsString(w);
        assertEquals(a2q("{'values':[{'element':'a','count':2}]}"), json);
        assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(json, CollectionWrapper.class));

        // with old format, round-trip works
        String jsonElements = MAPPER_ELEMENTS.writeValueAsString(w);
        assertEquals(a2q("{'values':['a','a']}"), jsonElements);
        assertEquals(Arrays.asList("a", "a"),
                MAPPER_ELEMENTS.readValue(jsonElements, CollectionWrapper.class).values);
    }

    static class SkipNullsWrapper {
        @JsonSetter(contentNulls = Nulls.SKIP)
        public Multiset<String> values;
    }

    @Test
    public void testSkipNullElements() throws Exception
    {
        SkipNullsWrapper w = MAPPER.readValue(a2q(
                "{'values':[{'element':null,'count':2},{'element':'a','count':3}]}"),
                SkipNullsWrapper.class);
        assertEquals(ImmutableMultiset.of("a", "a", "a"), w.values);
    }

    static class ContentInclusionWrapper {
        @JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
        public Multiset<String> values = HashMultiset.create();
    }

    @Test
    public void testContentInclusion() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
                .build();
        ContentInclusionWrapper w = new ContentInclusionWrapper();
        w.values.add("", 2);
        w.values.add(null, 2);
        // all elements suppressed: same as for `Collection`s, considered empty
        assertEquals("{}", mapper.writeValueAsString(w));

        w.values.add("a", 3);
        assertEquals(a2q("{'values':[{'element':'a','count':3}]}"), mapper.writeValueAsString(w));
    }

    private void _verifyInvalid(String json, String expectedMessage) throws Exception
    {
        for (TypeReference<?> ref : new TypeReference<?>[] {
                new TypeReference<HashMultiset<String>>() { },
                new TypeReference<ImmutableMultiset<String>>() { } }) {
            MismatchedInputException e = assertThrows(MismatchedInputException.class,
                    () -> MAPPER.readValue(json, ref));
            verifyException(e, expectedMessage);
        }
    }
}
