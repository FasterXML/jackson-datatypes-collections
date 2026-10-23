package tools.jackson.datatype.fastutil;

import org.junit.jupiter.api.Test;

import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.ser.std.StdSerializer;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for custom (property-annotated) serializers and deserializers of primitive
 * elements, keys and values, which take boxed values.
 */
public class CustomHandlersTest extends ModuleTestBase
{
    // Reads ints from hex Strings like "0x1F"
    static class HexIntDeserializer extends StdDeserializer<Integer> {
        public HexIntDeserializer() { super(Integer.class); }

        @Override
        public Integer deserialize(JsonParser p, DeserializationContext ctxt) {
            return Integer.parseInt(p.getString().substring(2), 16);
        }
    }

    static class HexKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) {
            return Integer.parseInt(key.substring(2), 16);
        }
    }

    static class HexIntSerializer extends StdSerializer<Integer> {
        public HexIntSerializer() { super(Integer.class); }

        @Override
        public void serialize(Integer value, JsonGenerator g, SerializationContext ctxt) {
            g.writeString("0x" + Integer.toHexString(value));
        }
    }

    static class UpperCharSerializer extends StdSerializer<Character> {
        public UpperCharSerializer() { super(Character.class); }

        @Override
        public void serialize(Character value, JsonGenerator g, SerializationContext ctxt) {
            g.writeString(String.valueOf(Character.toUpperCase(value)));
        }
    }

    static class ListBean {
        @JsonDeserialize(contentUsing = HexIntDeserializer.class)
        @JsonSerialize(contentUsing = HexIntSerializer.class)
        public IntList list;
    }

    static class SetBean {
        @JsonDeserialize(contentUsing = HexIntDeserializer.class)
        public IntSortedSet set;
    }

    static class CharsBean {
        @JsonSerialize(contentUsing = UpperCharSerializer.class)
        public CharList chars = CharArrayList.of('a', 'b');
    }

    static class MapBean {
        @JsonDeserialize(keyUsing = HexKeyDeserializer.class, contentUsing = HexIntDeserializer.class)
        public Int2IntMap map;
    }

    static class KeyMapBean {
        @JsonDeserialize(keyUsing = HexKeyDeserializer.class)
        public Int2LongSortedMap map;
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    void testCustomElementDeserializer() throws Exception
    {
        ListBean bean = MAPPER.readValue(a2q("{'list':['0x1F','0x2']}"), ListBean.class);
        assertEquals(IntArrayList.of(31, 2), assertInstanceOf(IntArrayList.class, bean.list));

        SetBean setBean = MAPPER.readValue(a2q("{'set':['0x2','0x1']}"), SetBean.class);
        assertEquals(new IntRBTreeSet(new int[] { 1, 2 }),
                assertInstanceOf(IntRBTreeSet.class, setBean.set));
    }

    @Test
    void testCustomKeyAndValueDeserializers() throws Exception
    {
        MapBean bean = MAPPER.readValue(a2q("{'map':{'0x1F':'0x2'}}"), MapBean.class);
        assertInstanceOf(Int2IntOpenHashMap.class, bean.map);
        assertEquals(2, bean.map.get(31));

        KeyMapBean keyBean = MAPPER.readValue(a2q("{'map':{'0x10':3,'0x1':4}}"), KeyMapBean.class);
        assertInstanceOf(Int2LongRBTreeMap.class, keyBean.map);
        assertEquals(3L, keyBean.map.get(16));
        assertEquals(1, keyBean.map.firstIntKey());
    }

    @Test
    void testCustomElementSerializer() throws Exception
    {
        ListBean bean = new ListBean();
        bean.list = IntArrayList.of(31, 2);
        String json = MAPPER.writeValueAsString(bean);
        assertEquals(a2q("{'list':['0x1f','0x2']}"), json);
        assertEquals(IntArrayList.of(31, 2), MAPPER.readValue(json, ListBean.class).list);

        assertEquals(a2q("{'chars':['A','B']}"), MAPPER.writeValueAsString(new CharsBean()));
    }
}
