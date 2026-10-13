package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.google.common.collect.*;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectMapValues244Test extends ModuleTestBase
{
    @Test
    public void multimapWithExplicitRootType() {
        ObjectMapper mapper = mapperWithModule();
        Multimap<String, Object> input = ArrayListMultimap.create();
        input.put("a", 1);
        input.put("a", "x");
        input.put("b", null);
        TypeReference<Multimap<String, Object>> type = new TypeReference<>() { };
        assertEquals(a2q("{'a':[1,'x'],'b':[null]}"),
                mapper.writerFor(type).writeValueAsString(input));
    }

    @Test
    public void rangeMapWithExplicitRootType() {
        ObjectMapper mapper = mapperWithModule();
        RangeMap<Integer, Object> input = TreeRangeMap.create();
        input.put(Range.closed(1, 5), "a");
        input.put(Range.open(10, 20), 42);
        TypeReference<RangeMap<Integer, Object>> type = new TypeReference<>() { };
        assertEquals(a2q("{'[1..5]':'a','(10..20)':42}"),
                mapper.writerFor(type).writeValueAsString(input));
    }

    @Test
    public void multimapWithDefaultTyping() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL)
                .build();
        Multimap<String, Object> input = ArrayListMultimap.create();
        input.put("a", 1);
        input.put("a", "x");
        TypeReference<Multimap<String, Object>> type = new TypeReference<>() { };
        assertEquals(input, mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type));
    }

    @Test
    public void rangeMapWithDefaultTyping() {
        ObjectMapper mapper = builderWithModule()
                .activateDefaultTyping(new NoCheckSubTypeValidator(), DefaultTyping.NON_FINAL)
                .build();
        RangeMap<Integer, Object> input = TreeRangeMap.create();
        input.put(Range.closed(1, 5), "a");
        input.put(Range.open(10, 20), 42);
        TypeReference<RangeMap<Integer, Object>> type = new TypeReference<>() { };
        RangeMap<Integer, Object> result = mapper.readValue(mapper.writerFor(type).writeValueAsString(input), type);
        assertEquals(input.asMapOfRanges(), result.asMapOfRanges());
    }
}
