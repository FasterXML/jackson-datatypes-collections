package com.fasterxml.jackson.datatype.guava.failing;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.guava.ModuleTestBase;
import com.google.common.collect.ArrayListMultimap;

public class MultiMap104Test extends ModuleTestBase
{
    // [datatypes-collections#104]
    static class Outside104 {
        @JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS)
        public Object aProperty;
    }

    private final ObjectMapper MAPPER = mapperWithModule();
    
    // [datatypes-collections#104]
    public void testPolymorphicArrayMapEmpty() throws Exception {
        final ArrayListMultimap<String,Object> multimap = ArrayListMultimap.create();
        multimap.put("aKey", 1);
        _testPolymorphicArrayMap(multimap);
    }

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
