package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import java.io.IOException;

public class TestRangeSet extends ModuleTestBase {

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testSerializeDeserialize() throws IOException {

        final RangeSet<Integer> rangeSet = TreeRangeSet.create();
        rangeSet.add(Range.closedOpen(1, 2));
        rangeSet.add(Range.openClosed(3, 4));

        final String json = MAPPER.writeValueAsString(rangeSet);
        final TypeFactory tf = MAPPER.getTypeFactory();
        final JavaType type = tf.constructParametricType(RangeSet.class, Integer.class);
        final ObjectReader reader = MAPPER.readerFor(type);

        final RangeSet<Integer> deserialized = reader.readValue(json);

        assertEquals(rangeSet, deserialized);
    }
}
