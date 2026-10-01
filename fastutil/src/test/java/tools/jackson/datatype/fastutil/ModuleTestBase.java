package tools.jackson.datatype.fastutil;

import java.util.Arrays;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class ModuleTestBase
{
    protected static final PolymorphicTypeValidator PTV = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("it.unimi.dsi.fastutil.")
            .allowIfSubType("java.")
            .allowIfSubType("tools.jackson.datatype.fastutil.")
            .build();

    protected static JsonMapper.Builder mapperBuilder() {
        return JsonMapper.builder()
                .addModule(new FastutilModule());
    }

    protected static ObjectMapper mapperWithModule() {
        return mapperBuilder().build();
    }

    protected static ObjectMapper defaultTypingMapper(DefaultTyping typing, JsonTypeInfo.As inclusion) {
        return mapperBuilder()
                .activateDefaultTyping(PTV, typing, inclusion)
                .build();
    }

    protected static void verifyException(Throwable e, String... matches) {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : matches) {
            String lmatch = match.toLowerCase();
            if (lmsg.contains(lmatch)) {
                return;
            }
        }
        fail("Expected an exception with one of substrings (" + Arrays.asList(matches)
                + "): got one with message \"" + msg + "\"");
    }

    protected static String a2q(String json) {
        return json.replace("'", "\"");
    }
}
