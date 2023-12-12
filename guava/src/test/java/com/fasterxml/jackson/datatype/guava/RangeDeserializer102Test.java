package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

public class RangeDeserializer102Test extends ModuleTestBase
{
    private final ObjectMapper MAPPER_DEFAULT = mapperWithModule();
    private final ObjectMapper MAPPER_CASE_INSENSITIVE = builderWithModule()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build();

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

    // [datatypes-collections#102]: Accept lowerCase enums for `Range` `BoundType` serialization
    public void testDeserializeDefaultSuccess() throws Exception
    {
        _testDeserializeOk(MAPPER_DEFAULT, "CLOSED", "OPEN");
    }

    public void testDeserializeAcceptCaseInsensitiveBoundTypeSuccess() throws Exception
    {
        _testDeserializeOk(MAPPER_CASE_INSENSITIVE, "CLOSED", "OPEN");
        _testDeserializeOk(MAPPER_CASE_INSENSITIVE, "closed", "OPEN");
        _testDeserializeOk(MAPPER_CASE_INSENSITIVE, "CLOSED", "open");
        _testDeserializeOk(MAPPER_CASE_INSENSITIVE, "Closed", "Open");
        _testDeserializeOk(MAPPER_CASE_INSENSITIVE, "ClOseD", "opEN");
    }

    private void _testDeserializeOk(ObjectMapper mapper,
            String lowerType, String upperType) throws Exception {
        String json = a2q("{'lowerEndpoint': 1, 'lowerBoundType': '"+lowerType
                +"', 'upperEndpoint': 2, 'upperBoundType': '"+upperType+"'}");
        Range<?> range = mapper.readValue(json, Range.class);

        assertEquals(1, range.lowerEndpoint());
        assertEquals(BoundType.CLOSED, range.lowerBoundType());
        assertEquals(2, range.upperEndpoint());
        assertEquals(BoundType.OPEN, range.upperBoundType());
    }

    public void testDeserializeBoundTypeFailWithFirstInvalidValue() throws Exception {
        String json = a2q("{'lowerEndpoint': 1, 'lowerBoundType': 'closed', 'upperEndpoint': 2, 'upperBoundType': 'oPeN'}");
        try {
            MAPPER_DEFAULT.readValue(json, Range.class);
            fail("should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type `com.google.common.collect.BoundType` from " +
                "String \"closed\": not a valid BoundType name (should be one of: [OPEN, CLOSED])");
        }
    }

    public void testDeserializeBoundTypeFailWithFirstInvalidValueFlip() throws Exception {
        String json = a2q("{'upperEndpoint': 2, 'upperBoundType': 'oPeN', 'lowerEndpoint': 1, 'lowerBoundType': 'closed'}");
        try {
            MAPPER_DEFAULT.readValue(json, Range.class);
            fail("should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type `com.google.common.collect.BoundType` from " +
                "String \"oPeN\": not a valid BoundType name (should be one of: [OPEN, CLOSED])");
        }
    }
}
