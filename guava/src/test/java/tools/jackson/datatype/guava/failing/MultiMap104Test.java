package tools.jackson.datatype.guava.failing;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.*;
import tools.jackson.datatype.guava.ModuleTestBase;

import com.google.common.collect.ArrayListMultimap;

public class MultiMap104Test extends ModuleTestBase
{
    // [datatypes-collections#104]
    static class Outside104 {
        @JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS)
        public Object aProperty;
    }

    // [datatypes-collections#104]
    public void testPolymorphicArrayMap() throws Exception
    {
        ObjectMapper mapper = builderWithModule()
                .polymorphicTypeValidator(new NoCheckSubTypeValidator())
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
