package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

import com.fasterxml.jackson.datatype.guava.GuavaModule;

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
                .propertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .build();

        @SuppressWarnings("unchecked")
        Range<Integer> r = (Range<Integer>) mapper.readValue(json, Range.class);

        assertEquals(Integer.valueOf(12), r.lowerEndpoint());
        assertEquals(Integer.valueOf(33), r.upperEndpoint());

        assertEquals(BoundType.CLOSED, r.lowerBoundType());
        assertEquals(BoundType.CLOSED, r.upperBoundType());
    }
}

