package tools.jackson.datatype.guava;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.google.common.collect.*;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for handling of {@code null} values for Guava {@link Multimap}s and
 * {@link Table}s: types that do not accept {@code null}s should report them as
 * problems (instead of failing with {@link NullPointerException} or instantiation
 * problem), and {@link Nulls#SKIP} as well as problem handlers should work.
 */
public class MultimapTableNullsTest extends ModuleTestBase
{
    // Returns `null` for "x", value as-is otherwise
    static class XToNullDeserializer extends ValueDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) {
            String str = p.getString();
            return "x".equals(str) ? null : str;
        }
    }

    // Records token and target type it is called with, returns fixed replacement
    static class ReplacingHandler extends DeserializationProblemHandler {
        final Object replacement;
        JsonToken token;
        JavaType targetType;

        ReplacingHandler(Object replacement) {
            this.replacement = replacement;
        }

        @Override
        public Object handleUnexpectedToken(DeserializationContext ctxt,
                JavaType targetType, JsonToken t, JsonParser p, String failureMsg) {
            this.targetType = targetType;
            token = t;
            return replacement;
        }
    }

    private static final List<TypeReference<? extends Multimap<String, String>>> NULL_ACCEPTING_MULTIMAPS = Arrays.asList(
            new TypeReference<ArrayListMultimap<String, String>>() { },
            new TypeReference<LinkedListMultimap<String, String>>() { },
            new TypeReference<HashMultimap<String, String>>() { },
            new TypeReference<LinkedHashMultimap<String, String>>() { },
            new TypeReference<Multimap<String, String>>() { }
    );

    private static final List<TypeReference<? extends Multimap<String, String>>> NULL_REJECTING_MULTIMAPS = Arrays.asList(
            new TypeReference<ImmutableMultimap<String, String>>() { },
            new TypeReference<ImmutableListMultimap<String, String>>() { },
            new TypeReference<ImmutableSetMultimap<String, String>>() { },
            new TypeReference<TreeMultimap<String, String>>() { }
    );

    private static final List<TypeReference<? extends Table<String, String, String>>> TABLES = Arrays.asList(
            new TypeReference<HashBasedTable<String, String, String>>() { },
            new TypeReference<TreeBasedTable<String, String, String>>() { },
            new TypeReference<ImmutableTable<String, String, String>>() { }
    );

    private final ObjectMapper MAPPER = mapperWithModule();

    private final ObjectMapper SINGLE_VALUE_MAPPER = builderWithModule()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .build();

    private final ObjectMapper SKIP_MAPPER = builderWithModule()
            .changeDefaultNullHandling(n -> JsonSetter.Value.forContentNulls(Nulls.SKIP))
            .build();

    /*
    /**********************************************************************
    /* Test methods, Multimaps
    /**********************************************************************
     */

    @Test
    public void testMultimapNullsAccepted() throws Exception
    {
        for (TypeReference<? extends Multimap<String, String>> typeRef : NULL_ACCEPTING_MULTIMAPS) {
            Multimap<String, String> mm = MAPPER.readValue(a2q("{'a':['b',null]}"), typeRef);
            assertTrue(mm.containsEntry("a", null), typeRef.getType().toString());
            assertTrue(mm.containsEntry("a", "b"), typeRef.getType().toString());

            mm = SINGLE_VALUE_MAPPER.readValue(a2q("{'a':null}"), typeRef);
            assertTrue(mm.containsEntry("a", null), typeRef.getType().toString());
        }
    }

    @Test
    public void testMultimapNullsRejected() throws Exception
    {
        for (TypeReference<? extends Multimap<String, String>> typeRef : NULL_REJECTING_MULTIMAPS) {
            _verifyRejected(MAPPER, "{'a':['b',null]}", typeRef, "Multimap");
            _verifyRejected(MAPPER, "{'a':[null]}", typeRef, "Multimap");
            _verifyRejected(SINGLE_VALUE_MAPPER, "{'a':'b','c':null}", typeRef, "Multimap");
            _verifyRejected(_xToNullMapper(null), "{'a':['b','x']}", typeRef, "Multimap");
        }
    }

    @Test
    public void testMultimapSkipNulls() throws Exception
    {
        for (TypeReference<? extends Multimap<String, String>> typeRef : NULL_REJECTING_MULTIMAPS) {
            Multimap<String, String> mm = SKIP_MAPPER.readValue(a2q("{'a':[null,'b',null],'c':[null]}"), typeRef);
            assertEquals(ImmutableListMultimap.of("a", "b"), ImmutableListMultimap.copyOf(mm),
                    typeRef.getType().toString());
        }
        // also for single values
        ObjectMapper mapper = SKIP_MAPPER.rebuild()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        assertEquals(ImmutableListMultimap.of("a", "b"),
                mapper.readValue(a2q("{'a':'b','c':null}"),
                        new TypeReference<ArrayListMultimap<String, String>>() { }));
    }

    @Test
    public void testMultimapProblemHandler() throws Exception
    {
        final TypeReference<ImmutableListMultimap<String, String>> typeRef = new TypeReference<>() { };
        ReplacingHandler h = new ReplacingHandler("R");
        ObjectMapper mapper = builderWithModule().addHandler(h).build();
        assertEquals(ImmutableListMultimap.of("a", "b", "a", "R"),
                mapper.readValue(a2q("{'a':['b',null]}"), typeRef));
        assertEquals(JsonToken.VALUE_NULL, h.token);
        assertEquals(String.class, h.targetType.getRawClass());

        // value deserializer returning `null`: handler gets actual token
        h = new ReplacingHandler("R");
        assertEquals(ImmutableListMultimap.of("a", "b", "a", "R"),
                _xToNullMapper(h).readValue(a2q("{'a':['b','x']}"), typeRef));
        assertEquals(JsonToken.VALUE_STRING, h.token);

        // and returning `null` drops the value
        mapper = builderWithModule().addHandler(new ReplacingHandler(null)).build();
        assertEquals(ImmutableListMultimap.of("a", "b"),
                mapper.readValue(a2q("{'a':['b',null]}"), typeRef));
    }

    /*
    /**********************************************************************
    /* Test methods, Tables
    /**********************************************************************
     */

    @Test
    public void testTableNullsRejected() throws Exception
    {
        for (TypeReference<? extends Table<String, String, String>> typeRef : TABLES) {
            _verifyRejected(MAPPER, "{'r':{'c':null}}", typeRef, "Table");
            _verifyRejected(MAPPER, "{'r':{'d':'v','c':null}}", typeRef, "Table");
            _verifyRejected(_xToNullMapper(null), "{'r':{'c':'x'}}", typeRef, "Table");
        }
    }

    @Test
    public void testTableSkipNulls() throws Exception
    {
        for (TypeReference<? extends Table<String, String, String>> typeRef : TABLES) {
            Table<String, String, String> table = SKIP_MAPPER.readValue(
                    a2q("{'r':{'c':null,'d':'v'},'s':{'c':null}}"), typeRef);
            assertEquals(ImmutableTable.of("r", "d", "v"), ImmutableTable.copyOf(table),
                    typeRef.getType().toString());
        }
    }

    @Test
    public void testTableProblemHandler() throws Exception
    {
        for (TypeReference<? extends Table<String, String, String>> typeRef : TABLES) {
            final String desc = typeRef.getType().toString();
            ReplacingHandler h = new ReplacingHandler("R");
            ObjectMapper mapper = builderWithModule().addHandler(h).build();
            Table<String, String, String> table = mapper.readValue(a2q("{'r':{'c':null,'d':'v'}}"), typeRef);
            assertEquals("R", table.get("r", "c"), desc);
            assertEquals("v", table.get("r", "d"), desc);
            assertEquals(JsonToken.VALUE_NULL, h.token, desc);
            assertEquals(String.class, h.targetType.getRawClass(), desc);

            h = new ReplacingHandler("R");
            table = _xToNullMapper(h).readValue(a2q("{'r':{'c':'x'}}"), typeRef);
            assertEquals("R", table.get("r", "c"), desc);
            assertEquals(JsonToken.VALUE_STRING, h.token, desc);

            mapper = builderWithModule().addHandler(new ReplacingHandler(null)).build();
            table = mapper.readValue(a2q("{'r':{'c':null,'d':'v'}}"), typeRef);
            assertEquals(ImmutableTable.of("r", "d", "v"), ImmutableTable.copyOf(table), desc);
        }
    }

    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
     */

    private ObjectMapper _xToNullMapper(DeserializationProblemHandler h)
    {
        MapperBuilder<?,?> b = builderWithModule()
                .addModule(new SimpleModule().addDeserializer(String.class, new XToNullDeserializer()));
        if (h != null) {
            b = b.addHandler(h);
        }
        return b.build();
    }

    private void _verifyRejected(ObjectMapper mapper, String json, TypeReference<?> typeRef,
            String containerType) throws Exception
    {
        try {
            Object result = mapper.readValue(a2q(json), typeRef);
            fail("Should not accept `null` for " + typeRef.getType() + " from " + json + ", got: " + result);
        } catch (MismatchedInputException e) {
            verifyException(e, "Guava `" + containerType + "` of type ");
            verifyException(e, "does not accept `null` values");
        }
    }
}
