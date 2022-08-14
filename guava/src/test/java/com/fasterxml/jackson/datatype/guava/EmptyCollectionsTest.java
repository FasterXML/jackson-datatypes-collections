package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.annotation.Nulls;

import tools.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class EmptyCollectionsTest extends ModuleTestBase
{
    // [datatypes-collections#67]
    public static class ImmutableListContainer67 {
        public ImmutableList<ImmutableList<String>> lists = ImmutableList.of();
    }

    public static class ImmutableMapContainer67 {
        public ImmutableMap<String, ImmutableMap<String, String>> maps = ImmutableMap.of();
    }
    
    private final ObjectMapper EMPTY_MAPPER = builderWithModule()
            .changeDefaultNullHandling(cfg -> 
                cfg.withContentNulls(Nulls.AS_EMPTY)
                    .withValueNulls(Nulls.AS_EMPTY))
            .build();

    // [datatypes-collections#67]
    public void testEmptyForLists() throws Exception
    {
        ImmutableListContainer67 result;

        // First, value `null` into empty:
        result = EMPTY_MAPPER.readValue(a2q("{'lists':null}"), ImmutableListContainer67.class);
        assertNotNull(result.lists);
        assertEquals(0, result.lists.size());

        // then content null:
        result = EMPTY_MAPPER.readValue(a2q("{'lists':[ null ]}"), ImmutableListContainer67.class);
        assertNotNull(result.lists);
        assertEquals(1, result.lists.size());
        assertNotNull(result.lists.get(0));
        assertEquals(0, result.lists.get(0).size());

        // and finally round-trip too
        String json = EMPTY_MAPPER.writeValueAsString(new ImmutableListContainer67());
        result = EMPTY_MAPPER.readValue(json, ImmutableListContainer67.class);
        assertNotNull(result.lists);
        assertEquals(0, result.lists.size());
    }

    public void testEmptyForMaps() throws Exception
    {
        ImmutableMapContainer67 result;

        // First, value `null` into empty:
        result = EMPTY_MAPPER.readValue(a2q("{'maps':null}"), ImmutableMapContainer67.class);
        assertNotNull(result.maps);
        assertEquals(0, result.maps.size());

        // then content null:
        result = EMPTY_MAPPER.readValue(a2q("{'maps':{ 'key' : null }}"), ImmutableMapContainer67.class);
        assertNotNull(result.maps);
        assertEquals(1, result.maps.size());
        assertNotNull(result.maps.get("key"));
        assertEquals(0, result.maps.get("key").size());

        // and finally round-trip too
        String json = EMPTY_MAPPER.writeValueAsString(new ImmutableMapContainer67());
        result = EMPTY_MAPPER.readValue(json, ImmutableMapContainer67.class);
        assertNotNull(result.maps);
        assertEquals(0, result.maps.size());
    }
}
