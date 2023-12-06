package tools.jackson.datatype.pcollections;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;

import static org.junit.Assert.*;

public abstract class ModuleTestBase
{
    protected ObjectMapper mapperWithModule() {
        return JsonMapper.builder()
                .addModule(new PCollectionsModule())
                .build();
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
        fail("Expected an exception with one of substrings ("+ Arrays.asList(matches)+"): got one with message \""+msg+"\"");
    }
}
