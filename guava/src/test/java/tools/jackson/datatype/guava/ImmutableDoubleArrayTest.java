package tools.jackson.datatype.guava;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.Nulls;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.util.JsonGeneratorDelegate;
import tools.jackson.databind.*;
import tools.jackson.databind.exc.InvalidNullException;

import com.google.common.primitives.ImmutableDoubleArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableDoubleArrayTest extends ModuleTestBase
{
    static class UnwrappedSingle {
        @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
        public ImmutableDoubleArray values;

        UnwrappedSingle(ImmutableDoubleArray v) { values = v; }
    }

    static class WrappedSingle {
        @JsonFormat(without = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
        public ImmutableDoubleArray values;

        WrappedSingle(ImmutableDoubleArray v) { values = v; }
    }

    static class TypedValue {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
        public Object value;

        TypedValue(Object v) { value = v; }
    }

    static class SingleRoundTrip {
        @JsonFormat(with = { JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED,
                JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY })
        public ImmutableDoubleArray values;
    }

    static class FailOnContentNulls {
        @JsonSetter(contentNulls = Nulls.FAIL)
        public ImmutableDoubleArray values;
    }

    private final ObjectMapper MAPPER = builderWithModule()
            .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

    @Test
    public void testSerialization() throws Exception {
        assertEquals("[]", MAPPER.writeValueAsString(ImmutableDoubleArray.of()));
        assertEquals("[42.0]", MAPPER.writeValueAsString(ImmutableDoubleArray.of(42.0)));
        assertEquals("[-1.0,0.0,1.0,2.0,3.1]", MAPPER.writeValueAsString(ImmutableDoubleArray.of(-1, 0, 1, 2, 3.1)));
    }

    @Test
    public void testSerializationWriteSingleElemArraysUnwrapped() throws Exception {
        ObjectMapper mapper = builderWithModule()
                .enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .build();
        assertEquals("[]", MAPPER.writeValueAsString(ImmutableDoubleArray.of()));
        assertEquals("42.0", mapper.writeValueAsString(ImmutableDoubleArray.of(42.0)));
        assertEquals("[-1.0,0.0,1.0,2.0,3.1]", MAPPER.writeValueAsString(ImmutableDoubleArray.of(-1, 0, 1, 2, 3.1)));
    }

    @Test
    public void testSerializationPerPropertyUnwrapSingle() throws Exception {
        assertEquals("{\"values\":42.0}",
                MAPPER.writeValueAsString(new UnwrappedSingle(ImmutableDoubleArray.of(42.0))));
        assertEquals("{\"values\":[1.5,2.0]}",
                MAPPER.writeValueAsString(new UnwrappedSingle(ImmutableDoubleArray.of(1.5, 2.0))));

        ObjectMapper mapper = builderWithModule().enable(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
                .build();
        assertEquals("{\"values\":[42.0]}",
                mapper.writeValueAsString(new WrappedSingle(ImmutableDoubleArray.of(42.0))));
    }

    @Test
    public void testSerializationPerTypeUnwrapSingle() throws Exception {
        ObjectMapper mapper = builderWithModule()
                .withConfigOverride(ImmutableDoubleArray.class,
                        o -> o.setFormat(JsonFormat.Value.empty()
                                .withFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)))
                .build();
        assertEquals("42.0", mapper.writeValueAsString(ImmutableDoubleArray.of(42.0)));
    }

    @Test
    public void testSerializationWithTypeId() throws Exception {
        // Type id "as property" falls back to wrapper array for array values
        assertEquals("{\"value\":[\"com.google.common.primitives.ImmutableDoubleArray\",[1.5,2.0]]}",
                MAPPER.writeValueAsString(new TypedValue(ImmutableDoubleArray.of(1.5, 2.0))));
    }

    // Length hint needed by binary formats for definite-length arrays
    // (not passed when type id written, since type prefix opens the array)
    @Test
    public void testSerializationPassesLengthHint() throws Exception {
        assertEquals(List.of(2), arrayLengthHints(MAPPER, ImmutableDoubleArray.of(1.5, 2.0)));
    }

    private List<Integer> arrayLengthHints(ObjectMapper mapper, Object value) throws Exception {
        final List<Integer> hints = new ArrayList<>();
        try (JsonGenerator g = new JsonGeneratorDelegate(mapper.createGenerator(new StringWriter())) {
            @Override
            public JsonGenerator writeStartArray(Object forValue, int size) {
                if (forValue instanceof ImmutableDoubleArray) {
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
    public void testPerPropertyUnwrapSingleRoundTrip() throws Exception {
        SingleRoundTrip input = new SingleRoundTrip();
        input.values = ImmutableDoubleArray.of(42.0);
        String json = MAPPER.writeValueAsString(input);
        assertEquals("{\"values\":42.0}", json);
        assertEquals(input.values, MAPPER.readValue(json, SingleRoundTrip.class).values);
    }

    @Test
    public void testPerPropertyContentNulls() throws Exception {
        // Global setting coerces nulls; per-property setting must override
        assertThrows(InvalidNullException.class,
                () -> MAPPER.readValue("{\"values\":[1,null]}", FailOnContentNulls.class));
    }

    @Test
    public void testDeserialization() throws Exception {
        assertNull(MAPPER.readValue("null", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(), MAPPER.readValue("[]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(1, 2, 3), MAPPER.readValue("[1, 2, 3]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(1.1, 2.1, 3.1), MAPPER.readValue("[1.1, 2.1, 3.1]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(0.0, 1234.1, -500), MAPPER.readValue("[null, \"1234.1\", -500]", ImmutableDoubleArray.class));
    }

    @Test
    public void testDeserializationWriteSingleElemArraysUnwrapped() throws Exception {
        ObjectMapper mapper = builderWithModule().enable(
                    DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                    DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .build();
        assertNull(mapper.readValue("null", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(), mapper.readValue("[]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(42.0), mapper.readValue("42.0", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(41.0, 42.0), mapper.readValue("[41.0, [42.0]]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(1, 2, 3), mapper.readValue("[1, 2, 3]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(1.1, 2.1, 3.1), mapper.readValue("[1.1, 2.1, 3.1]", ImmutableDoubleArray.class));
        assertEquals(ImmutableDoubleArray.of(0.0, 1234.1, -500), mapper.readValue("[null, \"1234.1\", -500]", ImmutableDoubleArray.class));
    }
}
