package tools.jackson.datatype.guava.optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.guava.ModuleTestBase;

import com.google.common.base.Optional;

public class OptionalFromEmptyTest extends ModuleTestBase
{
    static class OptionalBeanWithEmpty {
        protected Optional<String> _value;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public OptionalBeanWithEmpty(@JsonProperty("value")
            @JsonSetter(nulls = Nulls.AS_EMPTY) Optional<String> ref) {
            _value = ref;
        }
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    // [datatype-guava#48]
    public void testDeserNull() throws Exception {
        Optional<?> value = MAPPER.readValue("\"\"", new TypeReference<Optional<Integer>>() {});
        assertEquals(false, value.isPresent());
    }

    // [datatypes-collections#145]
    public void testDeserEmptyViaConstructor() throws Exception {
        OptionalBeanWithEmpty bean = MAPPER.readValue("{}",
                OptionalBeanWithEmpty.class);
        assertNotNull(bean._value);
        assertFalse(bean._value.isPresent());
    }
}
