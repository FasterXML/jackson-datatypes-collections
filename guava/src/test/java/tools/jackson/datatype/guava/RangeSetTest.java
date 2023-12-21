package tools.jackson.datatype.guava;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.TypeFactory;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

public class RangeSetTest extends ModuleTestBase {

    private final ObjectMapper MAPPER = mapperWithModule();

    public void testSerializeDeserialize() throws Exception {

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

    public void testSerializeDeserializeImmutableRangeSet() throws Exception {
        final ImmutableRangeSet<Integer> rangeSet = ImmutableRangeSet.<Integer>builder()
                .add(Range.closedOpen(1, 2))
                .build();

        // test serialization
        final String json = MAPPER.writeValueAsString(rangeSet);
        assertEquals(a2q("[{'lowerEndpoint':1,'lowerBoundType':'CLOSED',"
                                + "'upperEndpoint':2,'upperBoundType':'OPEN'}]"), json);
        
        // test deserialization, back
        assertEquals(rangeSet, MAPPER.readValue(json, new TypeReference<RangeSet<Integer>>() {}));
        assertEquals(rangeSet, MAPPER.readValue(json, new TypeReference<ImmutableRangeSet<Integer>>() {}));
    }

    // [datatypes-collections#142]: nulls in RangeSet JSON
    public void testDeserializeFromNull() throws Exception
    {
        final String json = a2q("[ {'lowerEndpoint':1,'lowerBoundType':'CLOSED'}, null ]");
        try {
            RangeSet<?> rs = MAPPER.readValue(json,
                    new TypeReference<ImmutableRangeSet<Integer>>() {});
            fail("Should not pass, got: "+rs);
        } catch (MismatchedInputException e) {
            verifyException(e, "Guava `RangeSet` does not accept `null` values");
        }
    }
}
