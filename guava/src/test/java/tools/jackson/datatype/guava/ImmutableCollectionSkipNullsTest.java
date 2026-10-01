package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import com.google.common.collect.ImmutableList;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.exc.MismatchedInputException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that {@link Nulls#SKIP} for contents only skips JSON {@code null} tokens,
 * consistent with databind's {@code CollectionDeserializer}: {@code null} returned
 * by a value deserializer for a non-null token is still rejected.
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

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testSkipJsonNulls() throws Exception
    {
        for (String json : new String[] {
                "{'values':[null]}",
                "{'values':[null,'a']}",
                "{'values':['a',null]}",
                "{'values':['a',null,'b',null]}",
        }) {
            SkipNullsBean bean = MAPPER.readValue(a2q(json), SkipNullsBean.class);
            assertFalse(bean.values.contains(null), json);
        }
        assertEquals(ImmutableList.of(), MAPPER.readValue(a2q("{'values':[null]}"),
                SkipNullsBean.class).values);
        assertEquals(ImmutableList.of("a", "b"), MAPPER.readValue(a2q("{'values':['a',null,'b',null]}"),
                SkipNullsBean.class).values);
    }

    @Test
    public void testDeserializedNullNotSkipped() throws Exception
    {
        // first, only, second and later element positions take different code paths
        for (String json : new String[] {
                "{'values':['x']}",
                "{'values':['x','a']}",
                "{'values':['a','x']}",
                "{'values':['a','b','x']}",
        }) {
            try {
                MAPPER.readValue(a2q(json), SkipNullsBean.class);
                fail("Should not accept `null` from value deserializer for " + json);
            } catch (MismatchedInputException e) {
                verifyException(e, "does not accept `null` values");
            }
        }
    }
}
