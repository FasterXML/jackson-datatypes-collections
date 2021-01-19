package com.fasterxml.jackson.datatype.eclipsecollections;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;

import static org.junit.Assert.fail;

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
            T expected, String json, TypeReference<?>... types) throws IOException {
        for (TypeReference<?> type : types) {
            ObjectMapper objectMapper = mapperWithModule();
            Object value = objectMapper.readValue(json, type);
            Assert.assertEquals(expected, value);
            Class<?> collectionClass =
                    objectMapper.getTypeFactory().constructType(type).getRawClass();
            Assert.assertThat(value, CoreMatchers.instanceOf(collectionClass));
        }
    }

    protected final <T> void testCollection(T expected, String json, Class<?>... types)
            throws IOException {
        for (Class<?> type : types) {
            Object value = mapperWithModule().readValue(json, type);
            Assert.assertEquals(expected, value);
            Assert.assertThat(value, CoreMatchers.instanceOf(type));
        }
    }
}
