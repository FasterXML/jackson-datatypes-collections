package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.google.common.collect.ArrayListMultimap;

public class MultiMap104Test extends ModuleTestBase
{
    // [datatypes-collections#104]
    static class Outside104 {
        public Object aProperty;
    }

    // [datatypes-collections#104]
    public void testPolymorphicArrayMap() throws Exception
    {
         final ObjectMapper mapper = builderWithModule()
              .activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                      DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY)
              .build();
         final ArrayListMultimap<String,Object> multimap = ArrayListMultimap.create();
         multimap.put("aKey", 1);
         final Outside104 outside = new Outside104();
         outside.aProperty = multimap;
         final String s = mapper.writeValueAsString(outside);
         Outside104 result = mapper.readValue(s, Outside104.class);
         assertNotNull(result);
    }
}
