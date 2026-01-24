package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.*;

import com.google.common.primitives.ImmutableLongArray;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ImmutableLongArrayTest extends ModuleTestBase
{
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
