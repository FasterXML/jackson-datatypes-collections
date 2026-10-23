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

import com.google.common.primitives.ImmutableIntArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ImmutableIntArrayTest extends ModuleTestBase
{
    static class UnwrappedSingle {
        @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
        public ImmutableIntArray values;

        UnwrappedSingle(ImmutableIntArray v) { values = v; }
    }

    static class WrappedSingle {
        @JsonFormat(without = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
        public ImmutableIntArray values;

        WrappedSingle(ImmutableIntArray v) { values = v; }
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
        assertEquals("[]", MAPPER.writeValueAsString(ImmutableIntArray.of()));
        assertEquals("[42]", MAPPER.writeValueAsString(ImmutableIntArray.of(42)));
        assertEquals("[-1,0,1,2,3]", MAPPER.writeValueAsString(ImmutableIntArray.of(-1, 0, 1, 2, 3)));
    }

    @Test
    public void testSerializationWriteSingleElemArraysUnwrapped() throws Exception {
        ObjectMapper mapper = builderWithModule().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                        .build();
        assertEquals("42", mapper.writeValueAsString(ImmutableIntArray.of(42)));
        assertEquals("[]", mapper.writeValueAsString(ImmutableIntArray.of()));
        assertEquals("[-1,0,1,2,3]", mapper.writeValueAsString(ImmutableIntArray.of(-1, 0, 1, 2, 3)));
    }

    @Test
    public void testSerializationPerPropertyUnwrapSingle() throws Exception {
        assertEquals("{\"values\":42}",
                MAPPER.writeValueAsString(new UnwrappedSingle(ImmutableIntArray.of(42))));
        assertEquals("{\"values\":[1,2]}",
                MAPPER.writeValueAsString(new UnwrappedSingle(ImmutableIntArray.of(1, 2))));

        ObjectMapper mapper = builderWithModule().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .build();
        assertEquals("{\"values\":[42]}",
                mapper.writeValueAsString(new WrappedSingle(ImmutableIntArray.of(42))));
    }

    @Test
    public void testSerializationWithTypeId() throws Exception {
        // Type id "as property" falls back to wrapper array for array values
        assertEquals("{\"value\":[\"com.google.common.primitives.ImmutableIntArray\",[1,2]]}",
                MAPPER.writeValueAsString(new TypedValue(ImmutableIntArray.of(1, 2))));
    }

    // Length hint must be passed for both untyped and typed arrays (needed for
    // definite-length arrays by binary formats)
    @Test
    public void testSerializationPassesLengthHint() throws Exception {
        assertEquals(List.of(3), arrayLengthHints(MAPPER, ImmutableIntArray.of(1, 2, 3)));
        assertEquals(List.of(3), arrayLengthHints(MAPPER, new TypedValue(ImmutableIntArray.of(1, 2, 3))));
    }

    private List<Integer> arrayLengthHints(ObjectMapper mapper, Object value) throws Exception {
        final List<Integer> hints = new ArrayList<>();
        try (JsonGenerator g = new JsonGeneratorDelegate(mapper.createGenerator(new StringWriter())) {
            @Override
            public JsonGenerator writeStartArray(Object forValue, int size) {
                if (forValue instanceof ImmutableIntArray) {
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
        assertNull(MAPPER.readValue("null", ImmutableIntArray.class));
        assertEquals(ImmutableIntArray.of(), MAPPER.readValue("[]", ImmutableIntArray.class));
        assertEquals(ImmutableIntArray.of(1, 2, 3), MAPPER.readValue("[1, 2, 3]", ImmutableIntArray.class));
        assertEquals(ImmutableIntArray.of(0, 1234, -5), MAPPER.readValue("[null, \"1234\", -5]", ImmutableIntArray.class));
    }

    @Test
    public void testDeserializationAcceptSingleValueAsArray() throws Exception {
        ObjectMapper mapper = builderWithModule().enable(
                    DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                    DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
        assertNull(mapper.readValue("null", ImmutableIntArray.class));
        assertEquals(ImmutableIntArray.of(42), mapper.readValue("42", ImmutableIntArray.class));
        assertEquals(ImmutableIntArray.of(41, 42), mapper.readValue("[41, [42]]", ImmutableIntArray.class));
        assertEquals(ImmutableIntArray.of(), mapper.readValue("[]", ImmutableIntArray.class));
        assertEquals(ImmutableIntArray.of(1, 2, 3), mapper.readValue("[1, 2, 3]", ImmutableIntArray.class));
        assertEquals(ImmutableIntArray.of(0, 1234, -5), mapper.readValue("[null, \"1234\", -5]", ImmutableIntArray.class));
    }
}
