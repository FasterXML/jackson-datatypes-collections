package tools.jackson.datatype.guava;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidDefinitionException;
import tools.jackson.databind.exc.MismatchedInputException;

import com.google.common.collect.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify that RuntimeExceptions thrown during builder.build() calls
 * are properly caught and reported via DeserializationContext.reportInputMismatch()
 */
public class BuilderExceptionHandlingTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    private final ObjectMapper POLY_MAPPER = builderWithModule()
            .polymorphicTypeValidator(new NoCheckSubTypeValidator())
            .build();

    /**
     * ImmutableMap.Builder.build() throws IllegalArgumentException when there are duplicate keys
     */
    @Test
    public void testImmutableMapDuplicateKeysHandling()
    {
        // ImmutableMap does not allow duplicate keys, so this should trigger an error during build()
        String json = a2q("{'a':1,'b':2,'a':3}");
        try {
            MAPPER.readValue(json, ImmutableMap.class);
            fail("Should have thrown an exception for duplicate keys");
        } catch (MismatchedInputException e) {
            // Expected - should contain meaningful error message
            String msg = e.getMessage();
            assertTrue(msg.contains("Failed to build `com.google.common.collect.ImmutableMap<"),
                    "Error message should mention ImmutableMap build failure or duplicate keys, got: " + msg);
        }
    }

    /**
     * ImmutableBiMap.Builder.build() throws IllegalArgumentException for duplicate keys or values
     */
    @Test
    public void testImmutableBiMapDuplicateKeysHandling()
    {
        String json = a2q("{'a':1,'b':2,'a':3}");
        try {
            MAPPER.readValue(json, ImmutableBiMap.class);
            fail("Should have thrown an exception for duplicate keys");
        } catch (MismatchedInputException e) {
            String msg = e.getMessage();
            assertTrue(msg.contains("Failed to build `com.google.common.collect.ImmutableBiMap<"),
                    "Error message should mention build failure, got: " + msg);
        }
    }

    /**
     * ImmutableSortedMap.Builder.build() can throw IllegalArgumentException 
     * if the elements are not mutually comparable
     */
    @Test
    public void testImmutableSortedMapDuplicateKeysHandling()
    {
        String json = a2q("{'a':1,'b':2,'a':3}");
        try {
            MAPPER.readValue(json, ImmutableSortedMap.class);
            fail("Should have thrown an exception for duplicate keys");
        } catch (MismatchedInputException e) {
            String msg = e.getMessage();
            assertTrue(msg.contains("Failed to build `com.google.common.collect.ImmutableSortedMap<"),
                    "Error message should mention build failure or duplicate, got: " + msg);
        }
    }

    /**
     * Test that valid ImmutableMap deserialization still works correctly
     */
    @Test
    public void testImmutableMapValidData() throws Exception
    {
        String json = a2q("{'a':1,'b':2,'c':3}");
        ImmutableMap<?,?> result = MAPPER.readValue(json, ImmutableMap.class);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(1, result.get("a"));
        assertEquals(2, result.get("b"));
        assertEquals(3, result.get("c"));
    }

    /**
     * ImmutableTable.Builder.build() can throw IllegalArgumentException for duplicate row+column keys.
     * Note: This test uses duplicate JSON keys, which may be handled by the JSON parser itself
     * before reaching the builder. The test validates that if a RuntimeException does occur
     * during build(), it will be properly caught and reported.
     */
    @Test
    public void testImmutableTableDuplicateCellHandling()
    {
        // Table with duplicate row/column combination
        String json = a2q("{'row1':{'col1':'val1','col1':'val2'}}");
        try {
            MAPPER.readValue(json, ImmutableTable.class);
            fail("Should have thrown an exception for duplicate cells");
        } catch (MismatchedInputException e) {
            // Expected - during build() if it gets that far (test does not enable
            // duplicate detection)
            String msg = e.getMessage();
            assertTrue(msg.contains("Failed to build `com.google.common.collect.ImmutableTable`"),
                    "Error message should indicate build failure or duplicate, got: " + msg);
        }
    }

    /**
     * Test that valid ImmutableTable deserialization still works correctly
     */
    @Test
    public void testImmutableTableValidData() throws Exception
    {
        String json = a2q("{'row1':{'col1':'val1','col2':'val2'},'row2':{'col1':'val3'}}");
        ImmutableTable<?,?,?> result = MAPPER.readValue(json, ImmutableTable.class);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("val1", result.get("row1", "col1"));
        assertEquals("val2", result.get("row1", "col2"));
        assertEquals("val3", result.get("row2", "col1"));
    }

    /**
     * ImmutableRangeSet.Builder.build() throws IllegalArgumentException for overlapping ranges
     */
    @Test
    public void testRangeSetOverlappingRangesHandling()
    {
        // Two overlapping ranges: [1,5] and [3,7]
        String json = a2q("["
                + "{'lowerEndpoint':1,'lowerBoundType':'CLOSED','upperEndpoint':5,'upperBoundType':'CLOSED'},"
                + "{'lowerEndpoint':3,'lowerBoundType':'CLOSED','upperEndpoint':7,'upperBoundType':'CLOSED'}"
                + "]");
        try {
            MAPPER.readValue(json, new TypeReference<ImmutableRangeSet<Integer>>() {});
            fail("Should have thrown an exception for overlapping ranges");
        } catch (MismatchedInputException e) {
            String msg = e.getMessage();
            assertTrue(msg.contains("Failed to build `com.google.common.collect.RangeSet`"),
                    "Error message should mention RangeSet build failure, got: " + msg);
        }
    }

    /**
     * Test that valid ImmutableList deserialization still works correctly
     */
    @Test
    public void testImmutableListValidData() throws Exception
    {
        String json = "[1,2,3,4,5]";
        ImmutableList<?> result = MAPPER.readValue(json, ImmutableList.class);
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(1, result.get(0));
        assertEquals(5, result.get(4));
    }

    /**
     * Test that valid ImmutableSet deserialization still works correctly
     */
    @Test
    public void testImmutableSetValidData() throws Exception
    {
        String json = "[1,2,3,4,5]";
        ImmutableSet<?> result = MAPPER.readValue(json, ImmutableSet.class);
        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.contains(1));
        assertTrue(result.contains(5));
    }

    // Elements of a sorted container must be `Comparable`, so mixed element types
    // can only show up via polymorphic handling
    static class SortedSetWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public ImmutableSortedSet<Comparable<?>> values;
    }

    static class SortedMultisetWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public ImmutableSortedMultiset<Comparable<?>> values;
    }

    /**
     * Sorted builders throw ClassCastException when elements are not mutually comparable
     */
    @Test
    public void testImmutableSortedSetIncomparableElementsHandling()
    {
        try {
            POLY_MAPPER.readValue(_mixedTypes(1), SortedSetWrapper.class);
            fail("Should have thrown an exception for incomparable elements");
        } catch (MismatchedInputException e) {
            verifyException(e, "Failed to build `com.google.common.collect.ImmutableSortedSet<");
        }
    }

    @Test
    public void testImmutableSortedSetIncomparableElementsInLargeInputHandling()
    {
        // Large enough that the builder may sort while elements are still being added
        try {
            POLY_MAPPER.readValue(_mixedTypes(100), SortedSetWrapper.class);
            fail("Should have thrown an exception for incomparable elements");
        } catch (MismatchedInputException e) {
            verifyException(e, "Failed to build `com.google.common.collect.ImmutableSortedSet<");
        }
    }

    @Test
    public void testImmutableSortedMultisetIncomparableElementsHandling()
    {
        try {
            POLY_MAPPER.readValue(_mixedTypes(1), SortedMultisetWrapper.class);
            fail("Should have thrown an exception for incomparable elements");
        } catch (MismatchedInputException e) {
            verifyException(e, "Failed to build `com.google.common.collect.ImmutableSortedMultiset<");
        }
    }

    // [datatypes-collections#245]: untyped (`Object`) elements no longer rejected up front
    @Test
    public void testUntypedImmutableSortedSet() throws Exception
    {
        assertEquals(ImmutableSortedSet.of(1, 2, 3),
                MAPPER.readValue("[3,1,2]", ImmutableSortedSet.class));
        assertEquals(ImmutableSortedMultiset.of(1, 2, 2),
                MAPPER.readValue("[2,1,2]", ImmutableSortedMultiset.class));
    }

    @Test
    public void testUntypedImmutableSortedSetIncomparableElementsHandling()
    {
        try {
            MAPPER.readValue("[1,\"a\"]", ImmutableSortedSet.class);
            fail("Should have thrown an exception for incomparable elements");
        } catch (MismatchedInputException e) {
            verifyException(e, "Failed to build `com.google.common.collect.ImmutableSortedSet<");
        }
        try {
            // Untyped JSON Objects become `LinkedHashMap`s, which are not Comparable
            MAPPER.readValue("[{\"x\":1},{\"x\":2}]", ImmutableSortedMultiset.class);
            fail("Should have thrown an exception for incomparable elements");
        } catch (MismatchedInputException e) {
            verifyException(e, "Failed to build `com.google.common.collect.ImmutableSortedMultiset<");
        }
    }

    // Single element is never compared by sorted builders, so needs explicit check
    @Test
    public void testUntypedSortedSingleNonComparableElementHandling()
    {
        final ObjectMapper wrappingMapper = builderWithModule()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        for (Class<?> type : new Class<?>[] { ImmutableSortedSet.class,
                ImmutableSortedMultiset.class, TreeMultiset.class }) {
            final String typeName = type.getSimpleName();
            for (String json : new String[] { "[{\"x\":1}]", "[{\"x\":1},null]" }) {
                try {
                    builderWithModule()
                        .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(Nulls.SKIP))
                        .build()
                        .readValue(json, type);
                    fail("Should have thrown an exception for non-Comparable element of "+typeName);
                } catch (MismatchedInputException e) {
                    verifyException(e, "Failed to build `"+type.getName()+"<");
                }
            }
            try {
                wrappingMapper.readValue("{\"x\":1}", type);
                fail("Should have thrown an exception for non-Comparable element of "+typeName);
            } catch (MismatchedInputException e) {
                verifyException(e, "Failed to build `"+type.getName()+"<");
            }
        }
    }

    // Untyped numbers may be `Integer`, `Long`, `BigInteger` or `Double`:
    // need to be sortable together
    @Test
    public void testUntypedSortedMixedNumbers() throws Exception
    {
        final String json = "[2.5, 10000000000, 1, 100000000000000000000, -0.5, 1.0, 1]";
        ImmutableSortedSet<?> set = MAPPER.readValue(json, ImmutableSortedSet.class);
        assertEquals(Arrays.asList(-0.5, 1.0, 1, 2.5, 10000000000L,
                new BigInteger("100000000000000000000")),
                new ArrayList<>(set));

        ImmutableSortedMultiset<?> multiset = MAPPER.readValue(json, ImmutableSortedMultiset.class);
        assertEquals(Arrays.asList(-0.5, 1.0, 1, 1, 2.5, 10000000000L,
                new BigInteger("100000000000000000000")),
                new ArrayList<>(multiset));

        TreeMultiset<?> treeMultiset = MAPPER.readValue(json, TreeMultiset.class);
        assertEquals(new ArrayList<>(multiset), new ArrayList<>(treeMultiset));

        // and with default typing, mixed number types round-trip as-is
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL)
                .build();
        ImmutableSortedSet<?> input = MAPPER.readValue("[3000000000, 1, 2.5]", ImmutableSortedSet.class);
        String typedJson = mapper.writerFor(Object.class).writeValueAsString(input);
        assertEquals(new ArrayList<>(input),
                new ArrayList<>((ImmutableSortedSet<?>) mapper.readValue(typedJson, Object.class)));
    }

    // `TreeMultiset` should report incomparable elements same as immutable sorted containers
    @Test
    public void testTreeMultisetIncomparableElementsHandling()
    {
        try {
            MAPPER.readValue("[1,\"a\"]", TreeMultiset.class);
            fail("Should have thrown an exception for incomparable elements");
        } catch (MismatchedInputException e) {
            verifyException(e, "Failed to build `com.google.common.collect.TreeMultiset<");
        }
        try {
            POLY_MAPPER.readValue(_mixedTypes(1), TreeMultisetWrapper.class);
            fail("Should have thrown an exception for incomparable elements");
        } catch (MismatchedInputException e) {
            verifyException(e, "Failed to build `com.google.common.collect.TreeMultiset<");
        }
    }

    static class TreeMultisetWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
        public TreeMultiset<Comparable<?>> values;
    }

    static class NonComparable {
        public int x;
    }

    @Test
    public void testSortedSetOfNonComparableTypeStillRejected()
    {
        try {
            MAPPER.readValue("[]", new TypeReference<ImmutableSortedSet<NonComparable>>() {});
            fail("Should not accept element type that is not Comparable");
        } catch (InvalidDefinitionException e) {
            verifyException(e, "not Comparable");
        }
    }

    // [datatypes-collections#245]: with default typing, sorted containers read back as `Object`
    @Test
    public void testImmutableSortedContainersAsObjectWithDefaultTyping() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL)
                .build();
        for (Object input : new Object[] {
                ImmutableSortedSet.of("b", "a"),
                ImmutableSortedMultiset.of(2, 1, 2) }) {
            String json = mapper.writerFor(Object.class).writeValueAsString(input);
            Object result = mapper.readValue(json, Object.class);
            assertEquals(input, result);
            assertEquals(input.getClass().getSuperclass(), result.getClass().getSuperclass());
        }
    }

    // JSON with `count` Integer elements followed by one String element
    private static String _mixedTypes(int count) {
        StringBuilder sb = new StringBuilder("{\"values\":[");
        for (int i = 0; i < count; i++) {
            sb.append("[\"java.lang.Integer\",").append(i).append("],");
        }
        return sb.append("[\"java.lang.String\",\"a\"]]}").toString();
    }

}
