package tools.jackson.datatype.hppc;

import java.util.Arrays;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class ModuleTestBase
{
    protected ModuleTestBase() { }
    
    protected ObjectMapper mapperWithModule()
    {
        return JsonMapper.builder()
                .addModule(new HppcModule())
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
        fail("Expected an exception with one of substrings ("+Arrays.asList(matches)+"): got one with message \""+msg+"\"");
    }
}
