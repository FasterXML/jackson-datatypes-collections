package tools.jackson.datatype.guava;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.util.JsonGeneratorDelegate;
import tools.jackson.databind.*;

import com.google.common.primitives.ImmutableLongArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ImmutableLongArrayTest extends ModuleTestBase
{
    static class UnwrappedSingle {
        @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
        public ImmutableLongArray values;

        UnwrappedSingle(ImmutableLongArray v) { values = v; }
    }

    static class WrappedSingle {
        @JsonFormat(without = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
        public ImmutableLongArray values;

        WrappedSingle(ImmutableLongArray v) { values = v; }
    }

    static class TypedValue {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
        public Object value;

        TypedValue(Object v) { value = v; }
    }

    private final ObjectMapper MAPPER = builderWithModule()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

    @Test
    public void testSerialization() throws Exception {
        assertEquals("[]", MAPPER.writeValueAsString(ImmutableLongArray.of()));
        assertEquals("[42]", MAPPER.writeValueAsString(ImmutableLongArray.of(42)));
        assertEquals("[-1,0,1,2,3]", MAPPER.writeValueAsString(ImmutableLongArray.of(-1, 0, 1, 2, 3)));
    }

    @Test
    public void testSerializationWriteSingleElemArraysUnwrapped() throws Exception {
        ObjectMapper mapper = builderWithModule().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                        .build();
        assertEquals("42", mapper.writeValueAsString(ImmutableLongArray.of(42)));
        assertEquals("[]", mapper.writeValueAsString(ImmutableLongArray.of()));
        assertEquals("[-1,0,1,2,3]", mapper.writeValueAsString(ImmutableLongArray.of(-1, 0, 1, 2, 3)));
    }

    @Test
    public void testSerializationPerPropertyUnwrapSingle() throws Exception {
        assertEquals("{\"values\":42}",
                MAPPER.writeValueAsString(new UnwrappedSingle(ImmutableLongArray.of(42L))));
        assertEquals("{\"values\":[1,2]}",
                MAPPER.writeValueAsString(new UnwrappedSingle(ImmutableLongArray.of(1L, 2L))));

        ObjectMapper mapper = builderWithModule().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .build();
        assertEquals("{\"values\":[42]}",
                mapper.writeValueAsString(new WrappedSingle(ImmutableLongArray.of(42L))));
    }

    @Test
    public void testSerializationPerTypeUnwrapSingle() throws Exception {
        ObjectMapper mapper = builderWithModule()
                .withConfigOverride(ImmutableLongArray.class,
                        o -> o.setFormat(JsonFormat.Value.empty()
                                .withFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)))
                .build();
        assertEquals("42", mapper.writeValueAsString(ImmutableLongArray.of(42L)));
    }

    @Test
    public void testSerializationWithTypeId() throws Exception {
        // Type id "as property" falls back to wrapper array for array values
        assertEquals("{\"value\":[\"com.google.common.primitives.ImmutableLongArray\",[1,2]]}",
                MAPPER.writeValueAsString(new TypedValue(ImmutableLongArray.of(1L, 2L))));
    }

    // Length hint needed by binary formats for definite-length arrays
    // (not passed when type id written, since type prefix opens the array)
    @Test
    public void testSerializationPassesLengthHint() throws Exception {
        assertEquals(List.of(2), arrayLengthHints(MAPPER, ImmutableLongArray.of(1L, 2L)));
    }

    private List<Integer> arrayLengthHints(ObjectMapper mapper, Object value) throws Exception {
        final List<Integer> hints = new ArrayList<>();
        try (JsonGenerator g = new JsonGeneratorDelegate(mapper.createGenerator(new StringWriter())) {
            @Override
            public JsonGenerator writeStartArray(Object forValue, int size) {
                if (forValue instanceof ImmutableLongArray) {
                    hints.add(size);
                }
                return super.writeStartArray(forValue, size);
            }
        }) {
            mapper.writeValue(g, value);
        }
        return hints;
    }

    @Test
    public void testDeserialization() throws Exception {
        assertNull(MAPPER.readValue("null", ImmutableLongArray.class));
        assertEquals(ImmutableLongArray.of(), MAPPER.readValue("[]", ImmutableLongArray.class));
        assertEquals(ImmutableLongArray.of(1, 2, 3), MAPPER.readValue("[1, 2, 3]", ImmutableLongArray.class));
        assertEquals(ImmutableLongArray.of(0, 1234, -5), MAPPER.readValue("[null, \"1234\", -5]", ImmutableLongArray.class));
    }

    @Test
    public void testDeserializationAcceptSingleValueAsArray() throws Exception {
        ObjectMapper mapper = builderWithModule().enable(
                    DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                    DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
        assertNull(mapper.readValue("null", ImmutableLongArray.class));
        assertEquals(ImmutableLongArray.of(42), mapper.readValue("42", ImmutableLongArray.class));
        assertEquals(ImmutableLongArray.of(41, 42), mapper.readValue("[41, [42]]", ImmutableLongArray.class));
        assertEquals(ImmutableLongArray.of(), mapper.readValue("[]", ImmutableLongArray.class));
        assertEquals(ImmutableLongArray.of(1, 2, 3), mapper.readValue("[1, 2, 3]", ImmutableLongArray.class));
        assertEquals(ImmutableLongArray.of(0, 1234, -5), mapper.readValue("[null, \"1234\", -5]", ImmutableLongArray.class));
    }
}
