package tools.jackson.datatype.fastutil;

import java.util.Arrays;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.databind.type.CollectionType;
import tools.jackson.databind.type.MapType;

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

    /**
     * @return Serializer this module (rather than standard Jackson handling) provides
     *    for given fastutil type, if any
     */
    protected static ValueSerializer<?> findModuleSerializer(ObjectMapper mapper, Class<?> type) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        FastutilSerializers serializers = new FastutilSerializers();
        if (javaType instanceof MapType mapType) {
            return serializers.findMapSerializer(mapper.serializationConfig(), mapType,
                    null, null, null, null, null);
        }
        return serializers.findCollectionSerializer(mapper.serializationConfig(),
                (CollectionType) javaType, null, null, null, null);
    }

    /**
     * @return Deserializer this module (rather than standard Jackson handling) provides
     *    for given fastutil type, if any
     */
    protected static ValueDeserializer<?> findModuleDeserializer(ObjectMapper mapper, Class<?> type) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        FastutilDeserializers deserializers = new FastutilDeserializers();
        if (javaType instanceof MapType mapType) {
            return deserializers.findMapDeserializer(mapType, mapper.deserializationConfig(),
                    null, null, null, null);
        }
        return deserializers.findCollectionDeserializer((CollectionType) javaType,
                mapper.deserializationConfig(), null, null, null);
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
