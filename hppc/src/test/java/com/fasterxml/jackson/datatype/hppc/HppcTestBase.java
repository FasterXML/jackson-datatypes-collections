package com.fasterxml.jackson.datatype.hppc;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class HppcTestBase extends junit.framework.TestCase
{
    protected HppcTestBase() { }
    
    protected ObjectMapper mapperWithModule()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new HppcModule());
        return mapper;
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
