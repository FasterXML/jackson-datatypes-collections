package tools.jackson.datatype.guava.fuzz;

import org.junit.Assert;

import com.google.common.collect.ImmutableList;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.datatype.guava.ModuleTestBase;

/**
 * Unit tests for verifying the fixes for OSS-Fuzz issues
 * work as expected
 * (see [datatypes-collections#138]).
 */
public class Fuzz138_65117Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    public void testOSSFuzzIssue65117() throws Exception
    {
        final TypeReference<?> ref = new TypeReference<ImmutableList<Integer>>() {};
        MismatchedInputException e = Assert.assertThrows(
                MismatchedInputException.class,
            () ->  MAPPER.readValue("[\"\"s(", ref));
        verifyException(e, "Guava `Collection` of type ");
        verifyException(e, "does not accept `null` values");
    }
}
