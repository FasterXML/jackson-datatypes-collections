package tools.jackson.datatype.guava.fuzz;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableSortedMultiset;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.datatype.guava.GuavaModule;
import tools.jackson.datatype.guava.ModuleTestBase;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for verifying the fixes for OSS-Fuzz issues
 * work as expected
 * (see [datatypes-collections#124]).
 */
public class Fuzz124_64610Test
    extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    private final ObjectMapper MAPPER_ELEMENTS = JsonMapper.builder()
            .addModule(new GuavaModule().configureMultisetsAsEntries(false))
            .build();

    @Test
    public void testOSSFuzzIssue64610() throws Exception
    {
        final TypeReference<?> ref = new TypeReference<ImmutableSortedMultiset<String>>() {};
        MismatchedInputException e = assertThrows(
                MismatchedInputException.class,
            () ->  MAPPER_ELEMENTS.readValue("[null]", ref));
        assertTrue(e.getMessage().contains("Guava `Collection` of type "));
        assertTrue(e.getMessage().contains("does not accept `null` values"));

        e = assertThrows(
                MismatchedInputException.class,
            () ->  MAPPER.readValue("[{\"element\":null,\"count\":1}]", ref));
        assertTrue(e.getMessage().contains("Guava `Collection` of type "));
        assertTrue(e.getMessage().contains("does not accept `null` values"));
    }
}
