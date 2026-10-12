package tools.jackson.datatype.guava;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.util.StdConverter;

import static org.junit.jupiter.api.Assertions.*;

public class RangeSetTest extends ModuleTestBase {

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
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

    @Test
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
    @Test
    public void testDeserializeFromNull() throws Exception
    {
        final String json = a2q("[ {'lowerEndpoint':1,'lowerBoundType':'CLOSED'}, null ]");
        try {
            RangeSet<?> rs = MAPPER.readValue(json,
                    new TypeReference<ImmutableRangeSet<Integer>>() {});
            fail("Should not pass, got: "+rs);
        } catch (MismatchedInputException e) {
            verifyException(e, "`RangeSet` does not accept `null` values");
        }
    }

    @Test
    public void testDeserializeNullWithProblemHandler() throws Exception
    {
        final String json = a2q("[ {'lowerEndpoint':1,'lowerBoundType':'CLOSED'}, null ]");
        final TypeReference<ImmutableRangeSet<Integer>> typeRef = new TypeReference<>() {};
        final JavaType[] targetType = new JavaType[1];
        ObjectMapper mapper = builderWithModule()
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleUnexpectedToken(DeserializationContext ctxt,
                            JavaType t, JsonToken token, JsonParser p, String failureMsg) {
                        targetType[0] = t;
                        return Range.singleton(-5);
                    }
                })
                .build();
        assertEquals(ImmutableRangeSet.builder().add(Range.atLeast(1)).add(Range.singleton(-5)).build(),
                mapper.readValue(json, typeRef));
        assertEquals(Range.class, targetType[0].getRawClass());

        // and `null` from handler drops the element
        mapper = builderWithModule()
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleUnexpectedToken(DeserializationContext ctxt,
                            JavaType t, JsonToken token, JsonParser p, String failureMsg) {
                        return null;
                    }
                })
                .build();
        assertEquals(ImmutableRangeSet.of(Range.atLeast(1)), mapper.readValue(json, typeRef));
    }

    /*
    /**********************************************************************
    /* Handling also supported by databind `CollectionDeserializer`
    /**********************************************************************
     */

    static class SimpleRangeConverter extends StdConverter<String, Range<Integer>> {
        @Override
        public Range<Integer> convert(String value) {
            String[] parts = value.split("-");
            return Range.closed(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
        }
    }

    static class ConvertedRangesBean {
        @JsonDeserialize(contentConverter = SimpleRangeConverter.class)
        public RangeSet<Integer> ranges;
    }

    @Test
    public void testEmptyStringCoercion() throws Exception {
        final TypeReference<RangeSet<Integer>> typeRef = new TypeReference<>() {};

        // Not allowed by default
        try {
            MAPPER.readValue(q(""), typeRef);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type");
        }

        ObjectMapper mapper = builderWithModule()
                .withCoercionConfig(LogicalType.Collection,
                        c -> c.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
                .build();
        assertEquals(ImmutableRangeSet.of(), mapper.readValue(q(""), typeRef));

        mapper = builderWithModule()
                .withCoercionConfig(LogicalType.Collection,
                        c -> c.setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull))
                .build();
        assertNull(mapper.readValue(q(""), typeRef));
    }

    @Test
    public void testBlankStringCoercion() throws Exception {
        ObjectMapper mapper = builderWithModule()
                .withCoercionConfig(LogicalType.Collection,
                        c -> c.setAcceptBlankAsEmpty(true)
                            .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsEmpty))
                .build();
        assertEquals(ImmutableRangeSet.of(),
                mapper.readValue(q("  "), new TypeReference<RangeSet<Integer>>() {}));
    }

    @Test
    public void testContentConverter() throws Exception {
        ConvertedRangesBean bean = MAPPER.readValue(a2q("{'ranges':['1-3','5-7']}"),
                ConvertedRangesBean.class);
        assertEquals(ImmutableRangeSet.builder()
                .add(Range.closed(1, 3)).add(Range.closed(5, 7)).build(),
                bean.ranges);
    }

    @Test
    public void testErrorPathIncludesIndex() throws Exception {
        try {
            MAPPER.readValue(a2q("[{'lowerEndpoint':1,'lowerBoundType':'CLOSED'},"
                    +"{'lowerEndpoint':'x','lowerBoundType':'CLOSED'}]"),
                    new TypeReference<RangeSet<Integer>>() {});
            fail("Should not pass");
        } catch (DatabindException e) {
            assertEquals(1, e.getPath().get(0).getIndex(), "Path: "+e.getPath());
        }
    }
}
