package tools.jackson.datatype.fastutil;

import java.util.*;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Round-trip tests that cover every element type for collections, and every
 * key/value type combination for maps, across interfaces and implementations.
 */
public class RoundTripMatrixTest extends ModuleTestBase
{
    static class Wrapper {
        public Object value;

        protected Wrapper() { }
        Wrapper(Object value) { this.value = value; }
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    static Stream<Arguments> collectionTypes() {
        List<Arguments> args = new ArrayList<>();
        for (String primitive : FastutilTypes.PRIMITIVES) {
            for (String suffix : FastutilTypes.COLLECTION_SUFFIXES) {
                Class<?> type = FastutilTypes.collectionClass(primitive, suffix);
                if (type != null) {
                    args.add(Arguments.of(primitive, type));
                }
            }
        }
        return args.stream();
    }

    static Stream<Arguments> mapTypes() {
        List<Arguments> args = new ArrayList<>();
        for (String key : FastutilTypes.MAP_KEYS) {
            for (String value : FastutilTypes.MAP_VALUES) {
                if ("Object".equals(key) && "Object".equals(value)) {
                    continue;
                }
                for (String suffix : FastutilTypes.MAP_SUFFIXES) {
                    Class<?> type = FastutilTypes.mapClass(key, value, suffix);
                    if (type != null) {
                        args.add(Arguments.of(key, value, type));
                    }
                }
            }
        }
        return args.stream();
    }

    @Test
    void testMatrixCoverage() {
        // 8 element types, some types (like `BooleanSortedSet`) do not exist
        assertThat(collectionTypes()).hasSizeGreaterThan(90);
        // 71 key/value combinations, 7 types each
        assertEquals(71 * 7, mapTypes().count());
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("collectionTypes")
    void testCollectionRoundTrip(String primitive, Class<?> type) throws Exception
    {
        List<Object> values = FastutilTypes.sampleValues(primitive);
        Collection<Object> input = FastutilTypes.newCollection(type, values);

        String json = MAPPER.writeValueAsString(input);
        if (input instanceof List || input instanceof it.unimi.dsi.fastutil.BigList) {
            // Ordered: can verify exact output, which must match that of an equivalent
            // `java.util.List` (except for `char`s which are written as a String)
            String expected = "Char".equals(primitive)
                    ? MAPPER.writeValueAsString(_charsToString(values))
                    : new ObjectMapper().writeValueAsString(values);
            assertEquals(expected, json);
        }

        Object result = MAPPER.readValue(json, type);
        assertInstanceOf(type, result);
        assertEquals(input, result);
        if (_isOrdered(input)) {
            assertEquals(new ArrayList<>(input), new ArrayList<>((Collection<?>) result));
        }
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("collectionTypes")
    void testCollectionRoundTripCharsAsArrays(String primitive, Class<?> type) throws Exception
    {
        ObjectMapper mapper = mapperBuilder()
                .enable(SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS)
                .build();
        Collection<Object> input = FastutilTypes.newCollection(type,
                FastutilTypes.sampleValues(primitive));
        String json = mapper.writeValueAsString(input);
        assertThat(json).startsWith("[");
        assertEquals(input, mapper.readValue(json, type));
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("collectionTypes")
    void testEmptyCollectionRoundTrip(String primitive, Class<?> type) throws Exception
    {
        Collection<Object> input = FastutilTypes.newCollection(type, Collections.emptyList());
        String json = MAPPER.writeValueAsString(input);
        assertEquals("Char".equals(primitive) ? "\"\"" : "[]", json);
        Object result = MAPPER.readValue(json, type);
        assertInstanceOf(type, result);
        assertEquals(input, result);
    }

    @ParameterizedTest(name = "{1}")
    @MethodSource("collectionTypes")
    void testCollectionRoundTripWithDefaultTyping(String primitive, Class<?> type) throws Exception
    {
        ObjectMapper mapper = defaultTypingMapper(DefaultTyping.JAVA_LANG_OBJECT,
                JsonTypeInfo.As.PROPERTY);
        Collection<Object> input = FastutilTypes.newCollection(type,
                FastutilTypes.sampleValues(primitive));
        String json = mapper.writeValueAsString(new Wrapper(input));
        assertThat(json).contains(input.getClass().getName());

        Wrapper result = mapper.readValue(json, Wrapper.class);
        assertInstanceOf(input.getClass(), result.value);
        assertEquals(input, result.value);
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("mapTypes")
    void testMapRoundTrip(String key, String value, Class<?> type) throws Exception
    {
        Map<Object, Object> input = FastutilTypes.newMap(type,
                FastutilTypes.sampleValues(key), FastutilTypes.sampleValues(value));
        assertEquals(3, input.size());

        String json = MAPPER.writeValueAsString(input);
        // Output must match that of an equivalent `java.util.Map`
        assertEquals(new ObjectMapper().writeValueAsString(new LinkedHashMap<>(input)), json);

        Object result = MAPPER.readValue(json, type);
        assertInstanceOf(type, result);
        assertEquals(input, result);
        // also verify that ordering is retained, where it exists
        if (_isOrdered(input)) {
            assertEquals(new ArrayList<>(input.keySet()),
                    new ArrayList<>(((Map<?, ?>) result).keySet()));
        }
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("mapTypes")
    void testEmptyMapRoundTrip(String key, String value, Class<?> type) throws Exception
    {
        Map<Object, Object> input = FastutilTypes.newMap(type,
                Collections.emptyList(), FastutilTypes.sampleValues(value));
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{}", json);
        Object result = MAPPER.readValue(json, type);
        assertInstanceOf(type, result);
        assertEquals(input, result);
    }

    @ParameterizedTest(name = "{2}")
    @MethodSource("mapTypes")
    void testMapRoundTripWithDefaultTyping(String key, String value, Class<?> type) throws Exception
    {
        ObjectMapper mapper = defaultTypingMapper(DefaultTyping.JAVA_LANG_OBJECT,
                JsonTypeInfo.As.PROPERTY);
        Map<Object, Object> input = FastutilTypes.newMap(type,
                FastutilTypes.sampleValues(key), FastutilTypes.sampleValues(value));
        String json = mapper.writeValueAsString(new Wrapper(input));
        assertThat(json).contains(input.getClass().getName());

        Wrapper result = mapper.readValue(json, Wrapper.class);
        assertInstanceOf(input.getClass(), result.value);
        assertEquals(input, result.value);
    }

    // Hash-based containers have no defined iteration order
    private static boolean _isOrdered(Object container) {
        String name = container.getClass().getSimpleName();
        return !name.contains("OpenHash") || name.contains("Linked");
    }

    private static String _charsToString(List<Object> values) {
        StringBuilder sb = new StringBuilder();
        for (Object v : values) {
            sb.append((char) (Character) v);
        }
        return sb.toString();
    }
}
