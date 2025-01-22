package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ArrayListMultimap;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.*;

import static org.junit.jupiter.api.Assertions.*;

public class MultiMap104Test extends ModuleTestBase
{
    // [datatypes-collections#104]
    static class Outside104 {
        @JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS)
        public Object aProperty;
    }

    private final ObjectMapper MAPPER = builderWithModule()
            .polymorphicTypeValidator(new NoCheckSubTypeValidator())
            .build();

    
    // [datatypes-collections#104]
    @Test
    public void testPolymorphicArrayMapEmpty() throws Exception {
        final ArrayListMultimap<String,Object> multimap = ArrayListMultimap.create();
        multimap.put("aKey", 1);
        _testPolymorphicArrayMap(multimap);
    }

    @Test
    public void testPolymorphicArrayMapNonEmpty() throws Exception {
        _testPolymorphicArrayMap(ArrayListMultimap.create());
    }

    private void _testPolymorphicArrayMap(ArrayListMultimap<String,Object> multimap)
        throws Exception
    {
         final Outside104 outside = new Outside104();
         outside.aProperty = multimap;
         final String json = MAPPER.writeValueAsString(outside);
         Outside104 result = MAPPER.readValue(json, Outside104.class);
         assertNotNull(result);
    }
}
