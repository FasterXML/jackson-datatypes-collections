package com.fasterxml.jackson.datatype.guava;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;

// [datatype-guava#185] : `GuavaCollectionDeserializer` does not respect `JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY`
public class AcceptSingleImmutableList185Test
    extends ModuleTestBase
{

    static class Line {          // 서브-Pojo
        public String data;
    }

    static class GuavaContainer185 {     // 문제의 필드
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public ImmutableList<Line> lines;
    }

    static class JavaContainer185 {     // 문제의 필드
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public List<Line> lines;
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    // Sanity Check, JDK List works by default
    @Test
    public void testGuavaImmutableListTestAsArrayJava()
            throws Exception
    {
        String json = "{\"lines\":{\"data\":\"something-jdk\"}}";

        JavaContainer185 javaContainer = MAPPER.readValue(json, JavaContainer185.class);
        assertEquals(1, javaContainer.lines.size());
        assertEquals("something-jdk", javaContainer.lines.get(0).data);
    }

    // Guava's ImmutableList does not work, but should
    @Test
    public void testGuavaImmutableListTestAsArrayGuava()
            throws Exception
    {
        String json = "{\"lines\":{\"data\":\"something-guava\"}}";

        GuavaContainer185 container = MAPPER.readValue(json, GuavaContainer185.class);
        assertEquals(1, container.lines.size());
        assertEquals("something-guava", container.lines.get(0).data);
    }
}
