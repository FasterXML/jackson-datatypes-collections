package com.fasterxml.jackson.datatype.guava;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

// [datatypes-collections#56]: support naming strategy
public class Range56Test extends ModuleTestBase
{

    // [datatypes-collections#56]: support naming strategy
    public void testSnakeCaseNamingStrategy() throws Exception
    {
        String json = "{\"lower_endpoint\": 12, \"lower_bound_type\": \"CLOSED\", \"upper_endpoint\": 33, \"upper_bound_type\": \"CLOSED\"}";

        GuavaModule mod = new GuavaModule().defaultBoundType(BoundType.CLOSED);
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(mod)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();

        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) mapper.readValue(json, Range.class);

        assertEquals(Integer.valueOf(12), r.lowerEndpoint());
        assertEquals(Integer.valueOf(33), r.upperEndpoint());

        assertEquals(BoundType.CLOSED, r.lowerBoundType());
        assertEquals(BoundType.CLOSED, r.upperBoundType());
    }
}

