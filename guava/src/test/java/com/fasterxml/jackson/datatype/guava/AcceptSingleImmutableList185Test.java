package com.fasterxml.jackson.datatype.guava;

import java.util.List;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertThrows;

public class AcceptSingleImmutableList185Test
    extends ModuleTestBase
{

    static class Line {          // 서브-Pojo
        public String data;
    }

    static class Container185 {     // 문제의 필드
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public ImmutableList<Line> lines;
    }

    static class JavaContainer185 {     // 문제의 필드
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        public List<Line> lines;
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testGuavaImmutableListTestAsArray()
            throws Exception
    {
        String json = "{\"lines\":{\"data\":\"something\"}}";

        JavaContainer185 javaContainer = MAPPER.readValue(json, JavaContainer185.class);
        assertEquals(1, javaContainer.lines.size());
        assertEquals("something", javaContainer.lines.get(0).data);

        Container185 container = MAPPER.readValue(json, Container185.class);
        assertEquals(1, container.lines.size());
        assertEquals("something", container.lines.get(0).data);
    }

    @Test
    public void testGuavaImmutableListTestAsArrayGuava()
            throws Exception
    {
        String json = "{\"lines\":{\"data\":\"something\"}}";

        Container185 container = MAPPER.readValue(json, Container185.class);
        assertEquals(1, container.lines.size());
        assertEquals("something", container.lines.get(0).data);
    }
}
