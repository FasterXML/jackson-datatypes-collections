package tools.jackson.datatype.guava;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.google.common.cache.Cache;
import com.google.common.collect.*;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that {@link Nulls#SKIP} for contents only skips JSON {@code null} tokens,
 * consistent with databind's {@code CollectionDeserializer}: {@code null} returned
 * by a value deserializer for a non-null token is still rejected, for all
 * Guava containers that do not accept {@code null}s, and regardless of whether
 * content is an array or a single value ("accept single value as array").
 */
public class ImmutableCollectionSkipNullsTest extends ModuleTestBase
{
    // Returns `null` for "x", value as-is otherwise
    static class XToNullDeserializer extends ValueDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) {
            String str = p.getString();
            return "x".equals(str) ? null : str;
        }
    }

    static class SkipNullsBean {
        @JsonSetter(contentNulls = Nulls.SKIP)
        @JsonDeserialize(contentUsing = XToNullDeserializer.class)
        public ImmutableList<String> values;
    }

    static class SkipNullsSortedBean {
        @JsonSetter(contentNulls = Nulls.SKIP)
        @JsonDeserialize(contentUsing = XToNullDeserializer.class)
        public ImmutableSortedSet<String> values;
    }

    static class Point implements Comparable<Point> {
        public int x;

        Point() { }
        Point(int x) { this.x = x; }

        @Override
        public boolean equals(Object o) {
            return (o instanceof Point) && ((Point) o).x == x;
        }

        @Override
        public int hashCode() { return x; }

        @Override
        public String toString() { return "Point("+x+")"; }

        @Override
        public int compareTo(Point o) { return Integer.compare(x, o.x); }
    }

    // Reads JSON Object (or Array) fully but returns `null` (leaving parser
    // at END_OBJECT / END_ARRAY)
    static class ObjectToNullDeserializer extends ValueDeserializer<Point> {
        @Override
        public Point deserialize(JsonParser p, DeserializationContext ctxt) {
            p.skipChildren();
            return null;
        }
    }

    // Same as above, but for `Range`s
    @SuppressWarnings("rawtypes")
    static class RangeToNullDeserializer extends ValueDeserializer<Range> {
        @Override
        public Range deserialize(JsonParser p, DeserializationContext ctxt) {
            p.skipChildren();
            return null;
        }
    }

    // Records token and target type it is called with (as well as token parser
    // is positioned at), returns fixed replacement
    static class ReplacingHandler extends DeserializationProblemHandler {
        final Object replacement;
        JsonToken token;
        JsonToken parserToken;
        JavaType targetType;

        ReplacingHandler(Object replacement) {
            this.replacement = replacement;
        }

        @Override
        public Object handleUnexpectedToken(DeserializationContext ctxt,
                JavaType targetType, JsonToken t, JsonParser p, String failureMsg) {
            this.targetType = targetType;
            token = t;
            parserToken = p.currentToken();
            return replacement;
        }
    }

    // Containers that do not accept `null` elements
    private static final List<TypeReference<? extends Collection<String>>> NULL_REJECTING = Arrays.asList(
            new TypeReference<ImmutableList<String>>() { },
            new TypeReference<ImmutableSet<String>>() { },
            new TypeReference<ImmutableSortedSet<String>>() { },
            new TypeReference<ImmutableMultiset<String>>() { },
            new TypeReference<ImmutableSortedMultiset<String>>() { },
            new TypeReference<TreeMultiset<String>>() { }
    );

    // first, only, second and later element positions (and single value) take
    // different code paths
    private static final String[] WITH_X = new String[] {
            "['x']",
            "['x','a']",
            "['a','x']",
            "['a','b','x']",
            "'x'",
    };

    private final ObjectMapper MAPPER = mapperWithModule();

    private final ObjectMapper X_TO_NULL_MAPPER = _xToNullMapper(null);

    /*
    /**********************************************************************
    /* Test methods, `Nulls.SKIP`
    /**********************************************************************
     */

    @Test
    public void testSkipJsonNulls() throws Exception
    {
        _verifySkip("{'values':[null]}");
        _verifySkip("{'values':[null,'a']}", "a");
        _verifySkip("{'values':['a',null]}", "a");
        _verifySkip("{'values':['a',null,'b',null]}", "a", "b");
    }

    @Test
    public void testSkipJsonNullsSorted() throws Exception
    {
        for (String json : new String[] {
                "{'values':[null,'b',null,'a']}",
                "{'values':['b',null,'a']}",
        }) {
            assertEquals(ImmutableSortedSet.of("a", "b"),
                    MAPPER.readValue(a2q(json), SkipNullsSortedBean.class).values, json);
        }
        assertEquals(ImmutableSortedSet.of(),
                MAPPER.readValue(a2q("{'values':[null]}"), SkipNullsSortedBean.class).values);
    }

    @Test
    public void testDeserializedNullNotSkipped() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        for (String content : WITH_X) {
            String json = a2q("{'values':" + content + "}");
            _verifyNullRejected(() -> mapper.readValue(json, SkipNullsBean.class), json);
            _verifyNullRejected(() -> mapper.readValue(json, SkipNullsSortedBean.class), json);
        }
        // but non-null single value works as expected
        assertEquals(ImmutableList.of("a"),
                mapper.readValue(a2q("{'values':'a'}"), SkipNullsBean.class).values);
        assertEquals(ImmutableSortedSet.of("a"),
                mapper.readValue(a2q("{'values':'a'}"), SkipNullsSortedBean.class).values);
    }

    /*
    /**********************************************************************
    /* Test methods, `null` from value deserializer, no skipping
    /**********************************************************************
     */

    @Test
    public void testDeserializedNullRejected() throws Exception
    {
        for (TypeReference<? extends Collection<String>> typeRef : NULL_REJECTING) {
            for (String content : WITH_X) {
                String json = a2q(content);
                String desc = typeRef.getType() + " " + json;
                _verifyNullRejected(() -> X_TO_NULL_MAPPER.readValue(json, typeRef), desc);
            }
        }
    }

    @Test
    public void testDeserializedNullAccepted() throws Exception
    {
        Multiset<String> set = X_TO_NULL_MAPPER.readValue(a2q("['a','x']"),
                new TypeReference<HashMultiset<String>>() { });
        assertEquals(HashMultiset.create(Arrays.asList("a", null)), set);
        set = X_TO_NULL_MAPPER.readValue(a2q("'x'"),
                new TypeReference<LinkedHashMultiset<String>>() { });
        assertEquals(1, set.count(null));
    }

    @Test
    public void testDeserializedNullMapValueRejected() throws Exception
    {
        final TypeReference<ImmutableMap<String, String>> typeRef = new TypeReference<>() { };
        for (String json : new String[] {
                "{'k':'x'}",
                "{'a':'b','k':'x'}",
                // [datatypes-collections#273]: as are JSON `null`s (no longer dropped)
                "{'k':null}",
                "{'a':'b','k':null}",
        }) {
            try {
                X_TO_NULL_MAPPER.readValue(a2q(json), typeRef);
                fail("Should not accept `null` from value deserializer for " + json);
            } catch (MismatchedInputException e) {
                verifyException(e, "Guava `Map` of type ");
                verifyException(e, "does not accept `null` values");
            }
        }
        // unless skipped
        assertEquals(ImmutableMap.of("a", "b"),
                X_TO_NULL_MAPPER.rebuild()
                        .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(Nulls.SKIP))
                        .build()
                        .readValue(a2q("{'a':'b','k':null}"), typeRef));
    }

    /*
    /**********************************************************************
    /* Test methods, problem handler
    /**********************************************************************
     */

    @Test
    public void testProblemHandlerReplacement() throws Exception
    {
        ReplacingHandler h = new ReplacingHandler("R");
        ObjectMapper mapper = _xToNullMapper(h);

        assertEquals(ImmutableList.of("a", "R", "b"),
                mapper.readValue(a2q("['a','x','b']"), new TypeReference<ImmutableList<String>>() { }));
        // handler is given actual token and element type
        assertEquals(JsonToken.VALUE_STRING, h.token);
        assertEquals(String.class, h.targetType.getRawClass());

        assertEquals(ImmutableList.of("R"),
                mapper.readValue(a2q("'x'"), new TypeReference<ImmutableList<String>>() { }));
        assertEquals(ImmutableSortedSet.of("R", "b"),
                mapper.readValue(a2q("['b','x']"), new TypeReference<ImmutableSortedSet<String>>() { }));
        assertEquals(ImmutableSortedSet.of("R"),
                mapper.readValue(a2q("'x'"), new TypeReference<ImmutableSortedSet<String>>() { }));
        assertEquals(TreeMultiset.create(Arrays.asList("R", "a")),
                mapper.readValue(a2q("['x','a']"), new TypeReference<TreeMultiset<String>>() { }));
        assertEquals(ImmutableMap.of("k", "R"),
                mapper.readValue(a2q("{'k':'x'}"), new TypeReference<ImmutableMap<String, String>>() { }));
        assertEquals(ImmutableMap.of("k", "R"),
                mapper.readValue(a2q("{'k':null}"), new TypeReference<ImmutableMap<String, String>>() { }));
        assertEquals(JsonToken.VALUE_NULL, h.token);
    }

    @Test
    public void testProblemHandlerDropsElement() throws Exception
    {
        ObjectMapper mapper = _xToNullMapper(new ReplacingHandler(null));

        assertEquals(ImmutableList.of("a", "b"),
                mapper.readValue(a2q("['a','x','b']"), new TypeReference<ImmutableList<String>>() { }));
        assertEquals(ImmutableList.of(),
                mapper.readValue(a2q("'x'"), new TypeReference<ImmutableList<String>>() { }));
        assertEquals(ImmutableSortedSet.of(),
                mapper.readValue(a2q("'x'"), new TypeReference<ImmutableSortedSet<String>>() { }));
        assertEquals(ImmutableMap.of("a", "b"),
                mapper.readValue(a2q("{'a':'b','k':'x'}"), new TypeReference<ImmutableMap<String, String>>() { }));
        assertEquals(ImmutableMap.of("a", "b"),
                mapper.readValue(a2q("{'a':'b','k':null}"), new TypeReference<ImmutableMap<String, String>>() { }));
    }

    // Handler must be called with parser positioned at reported token, even
    // if `null` was deserialized from a structured value
    @Test
    public void testProblemHandlerStructuredValue() throws Exception
    {
        final Point R = new Point(42);

        ReplacingHandler h = new ReplacingHandler(R);
        assertEquals(ImmutableList.of(R, R),
                _readWithObjectToNull(h, "[{'x':1},{'x':2}]",
                        new TypeReference<ImmutableList<Point>>() { }));
        _verifyStructuredToken(h, "ImmutableList");

        h = new ReplacingHandler(R);
        assertEquals(ImmutableList.of(R),
                _readWithObjectToNull(h, "{'x':1}",
                        new TypeReference<ImmutableList<Point>>() { }));
        _verifyStructuredToken(h, "ImmutableList, single value");

        h = new ReplacingHandler(R);
        assertEquals(HashMultiset.create(Arrays.asList(R, R)),
                HashMultiset.create(_readWithObjectToNull(h, "[{'x':1},{'x':2}]",
                        new TypeReference<ImmutableMultiset<Point>>() { })));
        _verifyStructuredToken(h, "ImmutableMultiset");

        // TreeMultiset (unlike Hash/LinkedHashMultiset) rejects `null`s
        h = new ReplacingHandler(R);
        assertEquals(TreeMultiset.create(Arrays.asList(R, R)),
                _readWithObjectToNull(h, "[{'x':1},{'x':2}]",
                        new TypeReference<TreeMultiset<Point>>() { }));
        _verifyStructuredToken(h, "TreeMultiset");

        h = new ReplacingHandler(R);
        assertEquals(ImmutableMap.of("k", R),
                _readWithObjectToNull(h, "{'k':{'x':1}}",
                        new TypeReference<ImmutableMap<String, Point>>() { }));
        _verifyStructuredToken(h, "ImmutableMap");

        h = new ReplacingHandler(R);
        Cache<String, Point> cache = _readWithObjectToNull(h, "{'k':{'x':1}}",
                new TypeReference<Cache<String, Point>>() { });
        assertEquals(R, cache.getIfPresent("k"));
        _verifyStructuredToken(h, "Cache");
    }

    // Same as above, but for Array values (parser left at END_ARRAY)
    @Test
    public void testProblemHandlerStructuredArrayValue() throws Exception
    {
        final Point R = new Point(42);

        ReplacingHandler h = new ReplacingHandler(R);
        assertEquals(ImmutableList.of(R, R),
                _readWithObjectToNull(h, "[[1],[2,3]]",
                        new TypeReference<ImmutableList<Point>>() { }));
        _verifyStructuredToken(h, JsonToken.END_ARRAY, "ImmutableList");

        h = new ReplacingHandler(R);
        assertEquals(TreeMultiset.create(Arrays.asList(R, R)),
                _readWithObjectToNull(h, "[[1],[2,3]]",
                        new TypeReference<TreeMultiset<Point>>() { }));
        _verifyStructuredToken(h, JsonToken.END_ARRAY, "TreeMultiset");

        h = new ReplacingHandler(R);
        assertEquals(ImmutableMap.of("k", R),
                _readWithObjectToNull(h, "{'k':[1,2]}",
                        new TypeReference<ImmutableMap<String, Point>>() { }));
        _verifyStructuredToken(h, JsonToken.END_ARRAY, "ImmutableMap");

        h = new ReplacingHandler(R);
        Cache<String, Point> cache = _readWithObjectToNull(h, "{'k':[1,2]}",
                new TypeReference<Cache<String, Point>>() { });
        assertEquals(R, cache.getIfPresent("k"));
        _verifyStructuredToken(h, JsonToken.END_ARRAY, "Cache");
    }

    // RangeSet reads ranges one by one too, so it reports the same token
    @Test
    public void testProblemHandlerStructuredRangeSetValue() throws Exception
    {
        final Range<Integer> R = Range.closed(1, 3);
        for (String json : new String[] { "[{'lowerEndpoint':1}]", "[[1,2]]", "{'lowerEndpoint':1}" }) {
            ReplacingHandler h = new ReplacingHandler(R);
            RangeSet<Integer> result = builderWithModule()
                    .addModule(new SimpleModule().addDeserializer(Range.class, new RangeToNullDeserializer()))
                    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                    .addHandler(h)
                    .build()
                    .readValue(a2q(json), new TypeReference<RangeSet<Integer>>() { });
            assertEquals(ImmutableRangeSet.of(R), result, json);
            assertEquals(json.contains("[1") ? JsonToken.END_ARRAY : JsonToken.END_OBJECT,
                    h.token, json);
            assertEquals(h.token, h.parserToken, json);
            assertEquals(Range.class, h.targetType.getRawClass(), json);
        }
    }

    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    interface Reader {
        Object read() throws Exception;
    }

    private ObjectMapper _xToNullMapper(DeserializationProblemHandler h)
    {
        var b = builderWithModule()
                .addModule(new SimpleModule().addDeserializer(String.class, new XToNullDeserializer()))
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        if (h != null) {
            b = b.addHandler(h);
        }
        return b.build();
    }

    private <T> T _readWithObjectToNull(DeserializationProblemHandler h, String json,
            TypeReference<T> typeRef) throws Exception
    {
        return builderWithModule()
                .addModule(new SimpleModule().addDeserializer(Point.class, new ObjectToNullDeserializer()))
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .addHandler(h)
                .build()
                .readValue(a2q(json), typeRef);
    }

    private void _verifyStructuredToken(ReplacingHandler h, String desc)
    {
        _verifyStructuredToken(h, JsonToken.END_OBJECT, desc);
    }

    private void _verifyStructuredToken(ReplacingHandler h, JsonToken expToken, String desc)
    {
        assertEquals(expToken, h.token, desc);
        assertEquals(h.token, h.parserToken, desc);
        assertEquals(Point.class, h.targetType.getRawClass(), desc);
    }

    private void _verifySkip(String json, String... expected) throws Exception
    {
        assertEquals(ImmutableList.copyOf(expected),
                MAPPER.readValue(a2q(json), SkipNullsBean.class).values, json);
    }

    private void _verifyNullRejected(Reader r, String desc) throws Exception
    {
        try {
            Object result = r.read();
            fail("Should not accept `null` from value deserializer for " + desc + ", got: " + result);
        } catch (MismatchedInputException e) {
            verifyException(e, "Guava `Collection` of type ");
            verifyException(e, "does not accept `null` values");
        }
    }
}
