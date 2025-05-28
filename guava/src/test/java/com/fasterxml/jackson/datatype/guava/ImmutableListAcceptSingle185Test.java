package com.fasterxml.jackson.datatype.guava;

import java.util.List;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

// [datatype-guava#185] : `GuavaCollectionDeserializer` does not respect
// `JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY`
public class ImmutableListAcceptSingle185Test
    extends ModuleTestBase
{
    static class Line {
        public String data;
    }

    static class GuavaContainer185 {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public ImmutableList<Line> lines;
    }

    static class JavaContainer185 {
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public List<Line> lines;
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    // Sanity Check, JDK List works by default
    @Test
    public void testJDKListWithSingleValue()
            throws Exception
    {
        String json = "{\"lines\":{\"data\":\"something-jdk\"}}";

        JavaContainer185 javaContainer = MAPPER.readValue(json, JavaContainer185.class);
        assertEquals(1, javaContainer.lines.size());
        assertEquals("something-jdk", javaContainer.lines.get(0).data);
    }

    // Guava's ImmutableList does not work, but should
    @Test
    public void testGuavaImmutableListWithSingleValue()
            throws Exception
    {
        String json = "{\"lines\":{\"data\":\"something-guava\"}}";

        GuavaContainer185 container = MAPPER.readValue(json, GuavaContainer185.class);
        assertEquals(1, container.lines.size());
        assertEquals("something-guava", container.lines.get(0).data);
    }
}
