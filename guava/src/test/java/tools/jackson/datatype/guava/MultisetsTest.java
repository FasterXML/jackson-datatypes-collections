package tools.jackson.datatype.guava;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;
import com.google.common.collect.*;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.TypeReference;
import tools.jackson.core.util.JsonGeneratorDelegate;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.InvalidNullException;
import tools.jackson.databind.jsonFormatVisitors.*;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.datatype.guava.deser.MultisetConfig;

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

    private final ObjectMapper MAPPER_ELEMENTS = builderWithModule(new GuavaModule().configureMultisetsAsEntries(false))
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

        mapper = builderWithModule(new GuavaModule().configureMultisetsAsEntries(false))
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
        _verifyInvalid(a2q("[{'element':'abc','count':'x'}]"), "expected positive integer, got `x`");
        _verifyInvalid(a2q("[{'element':'abc','count':99999999999}]"), "value out of range for `int`: 99999999999");
    }

    @Test
    public void testUnknownEntryProperties() throws Exception
    {
        final TypeReference<?>[] refs = new TypeReference<?>[] {
                new TypeReference<HashMultiset<String>>() { },
                new TypeReference<ImmutableMultiset<String>>() { } };
        String json = a2q("[{'element':'a','extra':{'nested':[1,2]},'count':2},"
                + "{'element':'b','count':1}]");
        ObjectMapper strictMapper = builderWithModule()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        ObjectMapper ignoringMapper = builderWithModule()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        for (TypeReference<?> ref : refs) {
            MismatchedInputException e = assertThrows(MismatchedInputException.class,
                    () -> strictMapper.readValue(json, ref));
            verifyException(e, "extra");

            Multiset<?> set = (Multiset<?>) ignoringMapper.readValue(json, ref);
            assertEquals(3, set.size());
            assertEquals(2, set.count("a"));
            assertEquals(1, set.count("b"));
        }
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
        verifyException(e, "Failed to build `com.google.common.collect.TreeMultiset<");

        e = assertThrows(MismatchedInputException.class,
                () -> MAPPER.readValue(a2q("[{'element':{'name':'Ann','age':30},'count':1}]"),
                        new TypeReference<SortedMultiset<Person>>() { }));
        verifyException(e, "Failed to build `com.google.common.collect.SortedMultiset<");

        ObjectMapper polyMapper = builderWithModule()
                .polymorphicTypeValidator(new NoCheckSubTypeValidator())
                .build();
        // large enough input for builder to sort while entries are still being added
        for (int count : new int[] { 1, 100 }) {
            e = assertThrows(MismatchedInputException.class,
                    () -> polyMapper.readValue(_mixedTypeEntries(count), SortedMultisetWrapper.class));
            verifyException(e, "Failed to build `com.google.common.collect.ImmutableSortedMultiset<");
        }
    }

    // Single element is never compared by sorted builders, so needs explicit check
    @Test
    public void testSingleNonComparableEntry() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        for (Class<?> type : new Class<?>[] { ImmutableSortedMultiset.class, TreeMultiset.class }) {
            for (String json : new String[] {
                    "[{'element':{'x':1},'count':2}]",
                    "{'element':{'x':1},'count':2}" }) {
                MismatchedInputException e = assertThrows(MismatchedInputException.class,
                        () -> mapper.readValue(a2q(json), type));
                verifyException(e, "Failed to build `"+type.getName()+"<");
            }
        }
    }

    @Test
    public void testIncomparableElementsAsRepeatedElements() throws Exception
    {
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> MAPPER_ELEMENTS.readValue("[1,\"a\"]",
                        new TypeReference<TreeMultiset<Object>>() { }));
        verifyException(e, "Failed to build `com.google.common.collect.TreeMultiset<");

        e = assertThrows(MismatchedInputException.class,
                () -> MAPPER_ELEMENTS.readValue(a2q("[{'name':'Ann','age':30}]"),
                        new TypeReference<SortedMultiset<Person>>() { }));
        verifyException(e, "Failed to build `com.google.common.collect.SortedMultiset<");

        ObjectMapper mapper = builderWithModule(new GuavaModule().configureMultisetsAsEntries(false))
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        e = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue(a2q("{'name':'Ann','age':30}"),
                        new TypeReference<SortedMultiset<Person>>() { }));
        verifyException(e, "Failed to build `com.google.common.collect.SortedMultiset<");
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

        ObjectMapper mapper = builderWithModule(new GuavaModule().configureMultisetsAsEntries(false))
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        e = assertThrows(MismatchedInputException.class,
                () -> mapper.readValue("\"\"", new TypeReference<TreeMultiset<Integer>>() { }));
        verifyException(e, "does not accept `null` values");

        Multiset<Integer> set = MAPPER_ELEMENTS.readValue("[1,\"\"]",
                new TypeReference<HashMultiset<Integer>>() { });
        assertEquals(1, set.count(null));
    }

    @Test
    public void testNullEntryProblemHandler() throws Exception
    {
        final TypeReference<?>[] refs = new TypeReference<?>[] {
                new TypeReference<TreeMultiset<Integer>>() { },
                new TypeReference<ImmutableMultiset<Integer>>() { },
                new TypeReference<ImmutableSortedMultiset<Integer>>() { } };
        for (Integer replacement : new Integer[] { 7, null }) {
            ObjectMapper mapper = builderWithModule()
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .addHandler(new DeserializationProblemHandler() {
                        @Override
                        public Object handleUnexpectedToken(DeserializationContext ctxt,
                                JavaType targetType, JsonToken t,
                                JsonParser p, String failureMsg) {
                            assertEquals(Integer.class, targetType.getRawClass());
                            // element can only be added once "count" is known: so
                            // parser (and reported token) is at the end of the entry
                            assertEquals(JsonToken.END_OBJECT, t);
                            assertEquals(t, p.currentToken());
                            return replacement;
                        }
                    })
                    .build();
            for (TypeReference<?> ref : refs) {
                for (String json : new String[] {
                        "[{'element':'','count':3},{'element':1,'count':2}]",
                        "{'element':'','count':3}" }) {
                    Multiset<?> set = (Multiset<?>) mapper.readValue(a2q(json), ref);
                    assertEquals(replacement == null ? 0 : 3, set.count(7));
                    assertEquals(json.startsWith("[") ? 2 : 0, set.count(1));
                    assertEquals((replacement == null ? 0 : 3)
                            + (json.startsWith("[") ? 2 : 0), set.size());
                }
            }
        }
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
        ObjectMapper mapper = builderWithModule(new GuavaModule().configureMaxMultisetSize(5))
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

    // "count" accepts numeric Strings and integral floating-point numbers
    @Test
    public void testCountCoercion() throws Exception
    {
        final TypeReference<HashMultiset<String>> ref = new TypeReference<>() { };
        final Multiset<String> expected = ImmutableMultiset.of("abc", "abc");
        assertEquals(expected, MAPPER.readValue(a2q("[{'element':'abc','count':'2'}]"), ref));
        assertEquals(expected, MAPPER.readValue(a2q("[{'element':'abc','count':2.0}]"), ref));
    }

    static class FailNullsWrapper {
        @JsonSetter(contentNulls = Nulls.FAIL)
        public Multiset<String> values;
    }

    // `null` entries are rejected with `Nulls.FAIL`
    @Test
    public void testFailNullEntries() throws Exception
    {
        InvalidNullException e = assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue(a2q("{'values':[null,{'element':'a','count':3}]}"),
                        FailNullsWrapper.class));
        verifyException(e, "Invalid `null` value");
    }

    // `null` entries (as opposed to `null` elements) are skipped with `Nulls.SKIP`
    @Test
    public void testSkipNullEntries() throws Exception
    {
        SkipNullsWrapper w = MAPPER.readValue(a2q(
                "{'values':[null,{'element':'a','count':3}]}"),
                SkipNullsWrapper.class);
        assertEquals(ImmutableMultiset.of("a", "a", "a"), w.values);
    }

    // Malformed entries are passed to `DeserializationProblemHandler`s
    @Test
    public void testMalformedEntryWithProblemHandler() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleUnexpectedToken(DeserializationContext ctxt,
                            JavaType targetType, JsonToken t,
                            JsonParser p, String failureMsg) {
                        // handler must consume the value it handles
                        p.skipChildren();
                        return null;
                    }
                })
                .build();
        for (TypeReference<?> ref : new TypeReference<?>[] {
                new TypeReference<HashMultiset<String>>() { },
                new TypeReference<ImmutableMultiset<String>>() { } }) {
            Multiset<?> set = (Multiset<?>) mapper.readValue(a2q(
                    "[null,'abc',[1,2],{'element':'a','count':2}]"), ref);
            assertEquals(ImmutableMultiset.of("a", "a"), set);
        }
    }

    /*
    /**********************************************************************
    /* Unit tests for null handling of elements, all Multiset types
    /**********************************************************************
     */

    private final static TypeReference<?>[] ALL_TYPES = new TypeReference<?>[] {
            new TypeReference<HashMultiset<String>>() { },
            new TypeReference<LinkedHashMultiset<String>>() { },
            new TypeReference<TreeMultiset<String>>() { },
            new TypeReference<ImmutableMultiset<String>>() { },
            new TypeReference<ImmutableSortedMultiset<String>>() { } };

    // Types that do not accept `null` elements
    private static boolean _rejectsNulls(TypeReference<?> ref) {
        String desc = ref.getType().getTypeName();
        return !desc.contains("HashMultiset");
    }

    // Element `null`s in entries, for each content null setting; both as Array and
    // as single entry (with `ACCEPT_SINGLE_VALUE_AS_ARRAY`)
    @Test
    public void testNullElementMatrix() throws Exception
    {
        final String[] inputs = {
                "[{'element':null,'count':2},{'element':'a','count':1}]",
                "{'element':null,'count':2}" };
        for (Nulls nulls : new Nulls[] { Nulls.DEFAULT, Nulls.SKIP, Nulls.FAIL, Nulls.AS_EMPTY }) {
            ObjectMapper mapper = builderWithModule()
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(nulls))
                    .build();
            for (TypeReference<?> ref : ALL_TYPES) {
                for (String input : inputs) {
                    final String json = a2q(input);
                    final String desc = nulls + "/" + ref.getType().getTypeName() + "/" + json;
                    final int aCount = json.startsWith("[") ? 1 : 0;
                    switch (nulls) {
                    case SKIP:
                        _assertCounts(mapper.readValue(json, ref), desc, aCount, 0, 0);
                        break;
                    case FAIL:
                        assertThrows(InvalidNullException.class,
                                () -> mapper.readValue(json, ref), desc);
                        break;
                    case AS_EMPTY:
                        _assertCounts(mapper.readValue(json, ref), desc, aCount, 2, 0);
                        break;
                    default:
                        if (_rejectsNulls(ref)) {
                            MismatchedInputException e = assertThrows(MismatchedInputException.class,
                                    () -> mapper.readValue(json, ref), desc);
                            verifyException(e, "does not accept `null` values");
                        } else {
                            _assertCounts(mapper.readValue(json, ref), desc, aCount, 0, 2);
                        }
                    }
                }
            }
        }
    }

    // `null` produced by element deserializer (not JSON `null`): never skipped
    @Test
    public void testDeserializedNullElementMatrix() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .addModule(new tools.jackson.databind.module.SimpleModule()
                        .addDeserializer(String.class, new XToNullDeserializer()))
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(Nulls.SKIP))
                .build();
        for (TypeReference<?> ref : ALL_TYPES) {
            for (String input : new String[] {
                    "[{'element':'x','count':2},{'element':'a','count':1}]",
                    "{'element':'x','count':2}" }) {
                final String json = a2q(input);
                final String desc = ref.getType().getTypeName() + "/" + json;
                if (_rejectsNulls(ref)) {
                    MismatchedInputException e = assertThrows(MismatchedInputException.class,
                            () -> mapper.readValue(json, ref), desc);
                    verifyException(e, "does not accept `null` values");
                } else {
                    _assertCounts(mapper.readValue(json, ref), desc,
                            json.startsWith("[") ? 1 : 0, 0, 2);
                }
            }
        }
    }

    static class XToNullDeserializer extends ValueDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) {
            String str = p.getString();
            return "x".equals(str) ? null : str;
        }
    }

    private void _assertCounts(Object result, String desc, int aCount, int emptyCount, int nullCount)
    {
        Multiset<?> set = (Multiset<?>) result;
        assertEquals(aCount, set.count("a"), desc);
        assertEquals(emptyCount, set.count(""), desc);
        if (!(set instanceof SortedMultiset<?>)) {
            assertEquals(nullCount, set.count(null), desc);
        }
        assertEquals(aCount + emptyCount + nullCount, set.size(), desc);
    }

    @Test
    public void testInvalidMaxSizeConfig() throws Exception
    {
        for (int maxSize : new int[] { 0, -1 }) {
            assertThrows(IllegalArgumentException.class,
                    () -> new MultisetConfig(true, maxSize));
            assertThrows(IllegalArgumentException.class,
                    () -> new GuavaDeserializers(null, true, maxSize));
        }
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

    static class ContentOnlyInclusionWrapper {
        @JsonInclude(content = JsonInclude.Include.NON_EMPTY)
        public Multiset<String> values = HashMultiset.create();
    }

    // Single entry must not be unwrapped if filtered out (nothing would be written)
    @Test
    public void testContentInclusionUnwrapSingle() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
                .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .build();
        ContentOnlyInclusionWrapper w = new ContentOnlyInclusionWrapper();
        w.values.add("", 2);
        assertEquals(a2q("{'values':[]}"), mapper.writeValueAsString(w));

        w.values.clear();
        w.values.add("a", 3);
        assertEquals(a2q("{'values':{'element':'a','count':3}}"), mapper.writeValueAsString(w));
    }

    // Array size passed to generator must not count entries that are filtered out
    @Test
    public void testContentInclusionArraySize() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .enable(SerializationFeature.APPLY_JSON_INCLUDE_FOR_CONTAINERS)
                .build();
        ContentOnlyInclusionWrapper w = new ContentOnlyInclusionWrapper();
        w.values.add("", 2);
        w.values.add("a", 3);
        assertEquals(Arrays.asList(-1), _arraySizes(mapper, w));
        // but without filtering, size is known
        assertEquals(Arrays.asList(2), _arraySizes(MAPPER, w.values));
    }

    private List<Integer> _arraySizes(ObjectMapper mapper, Object value) throws Exception
    {
        final List<Integer> sizes = new ArrayList<>();
        StringWriter sw = new StringWriter();
        try (JsonGenerator g = new JsonGeneratorDelegate(mapper.createGenerator(sw)) {
            @Override
            public JsonGenerator writeStartArray(Object currValue) {
                sizes.add(-1);
                return super.writeStartArray(currValue);
            }

            @Override
            public JsonGenerator writeStartArray(Object currValue, int size) {
                sizes.add(size);
                return super.writeStartArray(currValue, size);
            }
        }) {
            mapper.writeValue(g, value);
        }
        return sizes;
    }

    /*
    /**********************************************************************
    /* Unit tests for schema generation
    /**********************************************************************
     */

    // Schema should describe array of {"element", "count"} Objects, not of elements
    @Test
    public void testSchemaForEntries() throws Exception
    {
        final List<String> events = new ArrayList<>();
        MAPPER.acceptJsonFormatVisitor(new TypeReference<HashMultiset<String>>() { },
                new JsonFormatVisitorWrapper.Base() {
            @Override
            public JsonArrayFormatVisitor expectArrayFormat(JavaType type) {
                events.add("array");
                return new JsonArrayFormatVisitor.Base(getContext()) {
                    @Override
                    public void itemsFormat(JsonFormatVisitable handler, JavaType elementType) {
                        handler.acceptJsonFormatVisitor(_entryVisitor(events), elementType);
                    }
                };
            }
        });
        assertEquals(Arrays.asList("array", "object", "element:string", "count:integer"), events);
    }

    private JsonFormatVisitorWrapper _entryVisitor(List<String> events)
    {
        return new JsonFormatVisitorWrapper.Base() {
            @Override
            public JsonObjectFormatVisitor expectObjectFormat(JavaType type) {
                events.add("object");
                return new JsonObjectFormatVisitor.Base(getContext()) {
                    @Override
                    public void property(String name, JsonFormatVisitable handler, JavaType propertyTypeHint) {
                        handler.acceptJsonFormatVisitor(new JsonFormatVisitorWrapper.Base(getContext()) {
                            @Override
                            public JsonStringFormatVisitor expectStringFormat(JavaType type) {
                                events.add(name + ":string");
                                return null;
                            }

                            @Override
                            public JsonIntegerFormatVisitor expectIntegerFormat(JavaType type) {
                                events.add(name + ":integer");
                                return null;
                            }
                        }, propertyTypeHint);
                    }
                };
            }
        };
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
