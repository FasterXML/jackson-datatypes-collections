package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.util.Arrays;

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
}
