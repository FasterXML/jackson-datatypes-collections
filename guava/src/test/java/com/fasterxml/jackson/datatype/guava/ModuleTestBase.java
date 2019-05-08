package com.fasterxml.jackson.datatype.guava;

import java.util.Arrays;

import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

public abstract class ModuleTestBase extends junit.framework.TestCase
{
    public static class NoCheckSubTypeValidator
        extends PolymorphicTypeValidator.Base
    {
        private static final long serialVersionUID = 1L;
    
        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }
    
    protected ModuleTestBase() { }

    protected ObjectMapper mapperWithModule() {
        return builderWithModule().build();
    }

    protected ObjectMapper mapperWithModule(boolean absentsAsNulls) {
        return builderWithModule(absentsAsNulls).build();
    }
    
    protected MapperBuilder<?,?> builderWithModule() {
        return builderWithModule(false);
    }

    protected MapperBuilder<?,?> builderWithModule(boolean absentsAsNulls)
    {
        return JsonMapper.builder()
                .addModule(new GuavaModule()
                        .configureAbsentsAsNulls(absentsAsNulls));
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
