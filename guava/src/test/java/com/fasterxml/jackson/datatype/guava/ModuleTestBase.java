package com.fasterxml.jackson.datatype.guava;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

public abstract class ModuleTestBase extends junit.framework.TestCase
{
    protected ModuleTestBase() { }

    protected ObjectMapper mapperWithModule() {
        return mapperWithModule(false);
    }

    protected ObjectMapper mapperWithModule(boolean absentsAsNulls)
    {
        ObjectMapper mapper = new ObjectMapper();
        GuavaModule module = new GuavaModule();
        module.configureAbsentsAsNulls(absentsAsNulls);
        mapper.registerModule(module);
        return mapper;
    }

    protected JsonMapper.Builder builderWithModule() {
        return builderWithModule(false);
    }
    
    protected JsonMapper.Builder builderWithModule(boolean absentsAsNulls) {
        GuavaModule module = new GuavaModule();
        module.configureAbsentsAsNulls(absentsAsNulls);
        return JsonMapper.builder()
                .addModule(module);
    }

    protected String aposToQuotes(String json) {
        return json.replace("'", "\"");
    }

    public String quote(String str) {
        return '"'+str+'"';
    }

    protected void verifyException(Throwable e, String... matches)
    {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : matches) {
            String lmatch = match.toLowerCase();
            if (lmsg.indexOf(lmatch) >= 0) {
                return;
            }
        }
        fail("Expected an exception with one of substrings ("+Arrays.asList(matches)+"): got one with message \""+msg+"\"");
    }
}
