package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class ModuleTestBase
{
    protected ObjectMapper mapperWithModule() {
        return new ObjectMapper().registerModule(new EclipseCollectionsModule());
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
