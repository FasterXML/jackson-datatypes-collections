package tools.jackson.datatype.eclipsecollections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;

public abstract class ModuleTestBase {

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
            Assert.assertEquals(expected, value);
            Class<?> collectionClass =
                    objectMapper.getTypeFactory().constructType(type).getRawClass();
            assertThat(value, CoreMatchers.instanceOf(collectionClass));
        }
    }

    protected final <T> void testCollection(T expected, String json, Class<?>... types)
    {
        for (Class<?> type : types) {
            Object value = mapperWithModule().readValue(json, type);
            Assert.assertEquals(expected, value);
            assertThat(value, CoreMatchers.instanceOf(type));
        }
    }
}
