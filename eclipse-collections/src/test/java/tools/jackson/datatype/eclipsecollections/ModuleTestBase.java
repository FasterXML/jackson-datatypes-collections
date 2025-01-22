package tools.jackson.datatype.eclipsecollections;

import java.util.Arrays;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class ModuleTestBase
{
    protected ObjectMapper mapperWithModule() {
        return mapperBuilder().build();
    }

    protected MapperBuilder<?,?> mapperBuilder() {
        return JsonMapper.builder()
                .addModule(new EclipseCollectionsModule());
    }
    
    protected void verifyException(Throwable e, String... matches) {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : matches) {
            String lmatch = match.toLowerCase();
            if (lmsg.indexOf(lmatch) >= 0) {
                return;
            }
        }
        fail("Expected an exception with one of substrings (" + Arrays.asList(matches) + "): got one with message \"" +
             msg + "\"");
    }

    protected final <T> void testCollection(
            T expected, String json, TypeReference<?>... types)
    {
        for (TypeReference<?> type : types) {
            ObjectMapper objectMapper = mapperWithModule();
            Object value = objectMapper.readValue(json, type);
            assertEquals(expected, value);
            Class<?> collectionClass =
                    objectMapper.getTypeFactory().constructType(type).getRawClass();
            assertInstanceOf(collectionClass, value);
        }
    }

    protected final <T> void testCollection(T expected, String json, Class<?>... types)
    {
        for (Class<?> type : types) {
            Object value = mapperWithModule().readValue(json, type);
            assertEquals(expected, value);
            assertInstanceOf(type, value);
        }
    }
}
