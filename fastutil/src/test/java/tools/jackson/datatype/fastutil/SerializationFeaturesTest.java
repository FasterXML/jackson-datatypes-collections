package tools.jackson.datatype.fastutil;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.ser.std.StdSerializer;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for serialization features and per-property settings, which need to work
 * the same way as for equivalent {@code java.util} types.
 */
public class SerializationFeaturesTest extends ModuleTestBase
{
    static class UpperCaseSerializer extends StdSerializer<String> {
        public UpperCaseSerializer() { super(String.class); }

        @Override
        public void serialize(String value, JsonGenerator g, SerializationContext ctxt) {
            g.writeString(value.toUpperCase());
        }
    }

    static class NonNullContentBean {
        @JsonInclude(content = JsonInclude.Include.NON_NULL)
        public Int2ObjectMap<String> map = new Int2ObjectLinkedOpenHashMap<>();
    }

    static class NonDefaultContentBean {
        @JsonInclude(content = JsonInclude.Include.NON_DEFAULT)
        public Int2IntMap map = new Int2IntLinkedOpenHashMap();
    }

    static class ContentUsingBean {
        @JsonSerialize(contentUsing = UpperCaseSerializer.class)
        public Int2ObjectMap<String> map = new Int2ObjectLinkedOpenHashMap<>();
    }

    static class SortedEntriesBean {
        @JsonFormat(with = JsonFormat.Feature.WRITE_SORTED_MAP_ENTRIES)
        public Int2IntMap map = new Int2IntOpenHashMap();
    }

    static class UnwrappedListBean {
        @JsonFormat(with = JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)
        public IntList unwrapped = IntArrayList.of(1);

        public IntList wrapped = IntArrayList.of(2);
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    void testOrderMapEntriesByKeys() throws Exception
    {
        ObjectMapper mapper = mapperBuilder()
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .build();
        Int2IntMap ints = new Int2IntOpenHashMap();
        for (int key : new int[] { 3000, -5, 7, 123456, 42 }) {
            ints.put(key, 1);
        }
        assertEquals(a2q("{'-5':1,'7':1,'42':1,'3000':1,'123456':1}"), mapper.writeValueAsString(ints));

        Object2IntMap<String> objects = new Object2IntOpenHashMap<>();
        for (String key : new String[] { "b", "c", "a", "bb", "aa" }) {
            objects.put(key, 1);
        }
        assertEquals(a2q("{'a':1,'aa':1,'b':1,'bb':1,'c':1}"), mapper.writeValueAsString(objects));
    }

    @Test
    void testOrderMapEntriesByKeysAsStandard() throws Exception
    {
        // Jackson does not reorder `SortedMap`s (which also include linked hash maps
        // of fastutil): output needs to be the same as without the module
        ObjectMapper withModule = mapperBuilder()
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .build();
        ObjectMapper withoutModule = JsonMapper.builder()
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .build();
        Int2IntMap linked = new Int2IntLinkedOpenHashMap();
        linked.put(2, 1);
        linked.put(1, 2);
        Int2IntSortedMap reversed = new Int2IntRBTreeMap((a, b) -> Integer.compare(b, a));
        reversed.put(1, 1);
        reversed.put(2, 2);
        for (Object map : new Object[] { linked, reversed }) {
            assertEquals(withoutModule.writeValueAsString(map), withModule.writeValueAsString(map));
        }
        assertEquals(a2q("{'2':1,'1':2}"), withModule.writeValueAsString(linked));
    }

    @Test
    void testSortedMapEntriesFormat() throws Exception
    {
        SortedEntriesBean bean = new SortedEntriesBean();
        for (int key : new int[] { 3000, -5, 7, 123456, 42 }) {
            bean.map.put(key, 1);
        }
        assertEquals(a2q("{'map':{'-5':1,'7':1,'42':1,'3000':1,'123456':1}}"),
                MAPPER.writeValueAsString(bean));
    }

    @Test
    void testContentInclusion() throws Exception
    {
        NonNullContentBean nonNull = new NonNullContentBean();
        nonNull.map.put(1, null);
        nonNull.map.put(2, "x");
        assertEquals(a2q("{'map':{'2':'x'}}"), MAPPER.writeValueAsString(nonNull));

        NonDefaultContentBean nonDefault = new NonDefaultContentBean();
        nonDefault.map.put(1, 0);
        nonDefault.map.put(2, 3);
        assertEquals(a2q("{'map':{'2':3}}"), MAPPER.writeValueAsString(nonDefault));
    }

    @Test
    void testContentSerializer() throws Exception
    {
        ContentUsingBean bean = new ContentUsingBean();
        bean.map.put(1, "abc");
        assertEquals(a2q("{'map':{'1':'ABC'}}"), MAPPER.writeValueAsString(bean));
    }

    @Test
    void testUnwrapSingleElementPerProperty() throws Exception
    {
        assertEquals(a2q("{'unwrapped':1,'wrapped':[2]}"),
                MAPPER.writeValueAsString(new UnwrappedListBean()));
    }
}
