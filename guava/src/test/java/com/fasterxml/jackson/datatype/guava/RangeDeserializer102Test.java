package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

public class RangeDeserializer102Test extends ModuleTestBase {

    public void testBoundTypeIsOnlyOneOfOpenOrClosedInUpperCase() {
        assertEquals(2, BoundType.values().length);
        assertEquals("OPEN", BoundType.values()[0].name());
        assertEquals("CLOSED", BoundType.values()[1].name());
    }

    public void testDeserializeAcceptCaseInsensitiveBoundTypeSuccess() throws Exception {
        String json = a2q("{'lowerEndpoint': 1, 'lowerBoundType': 'closed', 'upperEndpoint': 2, 'upperBoundType': 'oPeN'}");
        ObjectMapper mapper = mapperWithModule().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        Range range = mapper.readValue(json, Range.class);

        assertEquals(1, range.lowerEndpoint());
        assertEquals(BoundType.CLOSED, range.lowerBoundType());
        assertEquals(2, range.upperEndpoint());
        assertEquals(BoundType.OPEN, range.upperBoundType());
    }

    public void testDeserializeBoundTypeFailWithFirstInvalidValue() throws Exception {
        String json = a2q("{'lowerEndpoint': 1, 'lowerBoundType': 'closed', 'upperEndpoint': 2, 'upperBoundType': 'oPeN'}");
        ObjectMapper mapper = mapperWithModule().disable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        try {
            mapper.readValue(json, Range.class);
            fail("should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type `com.google.common.collect.BoundType` from " +
                "String \"closed\": not a valid BoundType name (should be one oF: [OPEN, CLOSED])");
        }
    }

    public void testDeserializeBoundTypeFailWithFirstInvalidValueFlip() throws Exception {
        String json = a2q("{'upperEndpoint': 2, 'upperBoundType': 'oPeN', 'lowerEndpoint': 1, 'lowerBoundType': 'closed'}");
        ObjectMapper mapper = mapperWithModule().disable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        try {
            mapper.readValue(json, Range.class);
            fail("should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type `com.google.common.collect.BoundType` from " +
                "String \"oPeN\": not a valid BoundType name (should be one oF: [OPEN, CLOSED])");
        }
    }
}
