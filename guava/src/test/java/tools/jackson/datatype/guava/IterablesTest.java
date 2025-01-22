package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import static org.junit.jupiter.api.Assertions.*;

public class IterablesTest extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    static class IterableWrapper {
        public Iterable<String> values;

        public IterableWrapper(Iterable<String> v) { values = v; }
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    @Test
    public void testIterablesSerialization() throws Exception
    {
        String json = MAPPER.writeValueAsString(Iterables.limit(Iterables.cycle(1,2,3), 3));
        assertNotNull(json);
        assertEquals("[1,2,3]", json);
    }

    // for [#60]
    @Test
    public void testIterablesWithTransform() throws Exception
    {
        Iterable<String> input = Iterables.transform(ImmutableList.of("mr", "bo", "jangles"),
                new Function<String, String>() {
                  @Override
                  public String apply(String x) {
                      return new StringBuffer(x).reverse().toString();
                  }
                });
        String json = MAPPER.writeValueAsString(input);
        assertEquals(a2q("['rm','ob','selgnaj']"), json);

        // and then as property?
        json = MAPPER.writeValueAsString(new IterableWrapper(input));
        assertEquals(a2q("{'values':['rm','ob','selgnaj']}"), json);
    }
}
