package tools.jackson.datatype.pcollections;

import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class ModuleTestBase
{
    /**
     * Permissive {@link PolymorphicTypeValidator} for tests that enable default typing.
     */
    public static class NoCheckSubTypeValidator
        extends PolymorphicTypeValidator.Base
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    protected ObjectMapper mapperWithModule() {
        return builderWithModule().build();
    }

    protected JsonMapper.Builder builderWithModule() {
        return JsonMapper.builder()
                .addModule(new PCollectionsModule());
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
