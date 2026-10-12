package tools.jackson.datatype.eclipsecollections;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.impl.factory.Maps;

import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

// [datatypes-collections#40]: ignored map entries must be matched against
// the serialized key, not the key object. Note: Eclipse Collections map implementations
// are also `java.util.Map`s and by default serialized by databind `MapSerializer`;
// `RefRefMapIterableSerializer` is used with static typing for `MapIterable` types
public class MapIgnoredKeysTest extends ModuleTestBase
{
    static class StringKeys {
        @JsonIgnoreProperties({ "b" })
        public ImmutableMap<String, Integer> map = Maps.immutable.of("a", 1, "b", 2);
    }

    static class IntKeys {
        @JsonIgnoreProperties({ "2" })
        public ImmutableMap<Integer, String> map = Maps.immutable.of(1, "a", 2, "b");
    }

    enum Color { RED, GREEN }

    static class EnumKeys {
        @JsonIgnoreProperties({ "GREEN" })
        public ImmutableMap<Color, Integer> map = Maps.immutable.of(Color.RED, 1, Color.GREEN, 2);
    }

    private final ObjectMapper MAPPER = mapperBuilder()
            .enable(MapperFeature.USE_STATIC_TYPING)
            .build();

    @Test
    public void testIgnoredStringKeys() throws Exception
    {
        assertEquals(a2q("{'map':{'a':1}}"), MAPPER.writeValueAsString(new StringKeys()));
    }

    @Test
    public void testIgnoredIntKeys() throws Exception
    {
        assertEquals(a2q("{'map':{'1':'a'}}"), MAPPER.writeValueAsString(new IntKeys()));
    }

    @Test
    public void testIgnoredEnumKeys() throws Exception
    {
        assertEquals(a2q("{'map':{'RED':1}}"), MAPPER.writeValueAsString(new EnumKeys()));
    }

}
