package tools.jackson.datatype.fastutil;

import java.util.List;

import org.junit.jupiter.api.Test;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for fastutil containers of references, which are handled by standard Jackson
 * (de)serializers once abstract types are mapped to implementations.
 */
public class ReferenceContainersTest extends ModuleTestBase
{
    static class Bean {
        public ObjectList<String> list;
        public ObjectSortedSet<String> sortedSet;
        public Object2ObjectMap<String, IntList> map;
        public Int2ReferenceMap<String> intToRef;
        public Reference2IntMap<String> refToInt;
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    void testAbstractTypesMapped() throws Exception
    {
        _verify(ObjectArrayList.class, "['a','b']", new TypeReference<ObjectCollection<String>>() { });
        _verify(ObjectArrayList.class, "['a','b']", new TypeReference<ObjectList<String>>() { });
        _verify(ObjectBigArrayBigList.class, "['a','b']", new TypeReference<ObjectBigList<String>>() { });
        _verify(ObjectOpenHashSet.class, "['a','b']", new TypeReference<ObjectSet<String>>() { });
        _verify(ObjectRBTreeSet.class, "['b','a']", new TypeReference<ObjectSortedSet<String>>() { });
        _verify(ReferenceArrayList.class, "['a','b']", new TypeReference<ReferenceList<String>>() { });
        _verify(ReferenceOpenHashSet.class, "['a','b']", new TypeReference<ReferenceSet<String>>() { });
        _verify(Object2ObjectOpenHashMap.class, "{'a':'b'}", new TypeReference<Object2ObjectMap<String, String>>() { });
        _verify(Object2ObjectRBTreeMap.class, "{'a':'b'}", new TypeReference<Object2ObjectSortedMap<String, String>>() { });
        _verify(Reference2ReferenceOpenHashMap.class, "{'a':'b'}", new TypeReference<Reference2ReferenceMap<String, String>>() { });
        _verify(Reference2LongOpenHashMap.class, "{'a':1}", new TypeReference<Reference2LongMap<String>>() { });
        _verify(Int2ReferenceOpenHashMap.class, "{'1':'b'}", new TypeReference<Int2ReferenceMap<String>>() { });
        _verify(Int2ReferenceRBTreeMap.class, "{'1':'b'}", new TypeReference<Int2ReferenceSortedMap<String>>() { });
    }

    @Test
    void testBeanRoundTrip() throws Exception
    {
        Bean bean = new Bean();
        bean.list = new ObjectArrayList<>(List.of("x", "y"));
        bean.sortedSet = new ObjectRBTreeSet<>(List.of("b", "a"));
        bean.map = new Object2ObjectLinkedOpenHashMap<>();
        bean.map.put("k", IntList.of(1, 2));
        bean.intToRef = new Int2ReferenceOpenHashMap<>();
        bean.intToRef.put(3, "three");
        bean.refToInt = new Reference2IntOpenHashMap<>();
        bean.refToInt.put("four", 4);

        String json = MAPPER.writeValueAsString(bean);
        assertEquals(a2q("{'intToRef':{'3':'three'},'list':['x','y'],'map':{'k':[1,2]},"
                + "'refToInt':{'four':4},'sortedSet':['a','b']}"), json);

        Bean result = MAPPER.readValue(json, Bean.class);
        assertEquals(bean.list, result.list);
        assertEquals(bean.sortedSet, result.sortedSet);
        assertEquals(bean.map, result.map);
        // element types are resolved from generic declarations
        assertInstanceOf(IntArrayList.class, result.map.get("k"));
        // identity-based: compare contents by value
        assertEquals(1, result.intToRef.size());
        assertEquals("three", result.intToRef.get(3));
        assertEquals(1, result.refToInt.size());
        assertEquals("four", result.refToInt.keySet().iterator().next());
        assertEquals(4, result.refToInt.values().iterator().nextInt());
    }

    private void _verify(Class<?> expectedType, String json, TypeReference<?> type) throws Exception {
        Object result = MAPPER.readValue(a2q(json), type);
        assertInstanceOf(expectedType, result);
        if (result instanceof java.util.Map) {
            assertThat((java.util.Map<?, ?>) result).isNotEmpty();
        } else {
            assertThat((java.util.Collection<?>) result).isNotEmpty();
        }
    }
}
