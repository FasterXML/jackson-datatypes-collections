package tools.jackson.datatype.eclipsecollections;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Triple;
import org.eclipse.collections.api.tuple.Triplet;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.api.tuple.primitive.IntLongPair;
import org.eclipse.collections.api.tuple.primitive.IntObjectPair;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindContext;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for deserializing raw (untyped) Pair/Triple types, and concrete
 * implementation types (as named by type ids with default typing).
 */
public class PairDeserTest extends ModuleTestBase
{
    static class NoCheckSubTypeValidator extends PolymorphicTypeValidator.Base
    {
        private static final long serialVersionUID = 1L;

        @Override
        public Validity validateBaseType(DatabindContext ctxt, JavaType baseType) {
            return Validity.ALLOWED;
        }
    }

    static class Holder {
        public Pair<String, Integer> pair;
        public Twin<String> twin;
        public IntLongPair intLongPair;
        public ObjectIntPair<String> objectIntPair;
        public Triple<String, Integer, Boolean> triple;
        public Object anything;
    }

    private static final List<DefaultTyping> TYPINGS = Arrays.asList(
            DefaultTyping.NON_FINAL, DefaultTyping.OBJECT_AND_NON_CONCRETE);

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testRawTypes() throws Exception
    {
        assertEquals(Tuples.pair("a", 1),
                MAPPER.readValue(a2q("{'one':'a','two':1}"), Pair.class));
        assertEquals(Tuples.twin("a", "b"),
                MAPPER.readValue(a2q("{'one':'a','two':'b'}"), Twin.class));
        assertEquals(Tuples.triple("a", 1, true),
                MAPPER.readValue(a2q("{'one':'a','two':1,'three':true}"), Triple.class));
        // `Triplet` used to be deserialized as a (non-Triplet) `TripleImpl`
        assertInstanceOf(Triplet.class,
                MAPPER.readValue(a2q("{'one':'a','two':'b','three':'c'}"), Triplet.class));
        assertEquals(Tuples.triplet("a", "b", "c"),
                MAPPER.readValue(a2q("{'one':'a','two':'b','three':'c'}"), Triplet.class));
        assertEquals(PrimitiveTuples.pair("a", 1),
                MAPPER.readValue(a2q("{'one':'a','two':1}"), ObjectIntPair.class));
        assertEquals(PrimitiveTuples.pair(1, "a"),
                MAPPER.readValue(a2q("{'one':1,'two':'a'}"), IntObjectPair.class));
    }

    @Test
    public void testConcreteType() throws Exception
    {
        // implementation classes are not public
        Class<?> pairImpl = Class.forName("org.eclipse.collections.impl.tuple.PairImpl");
        assertEquals(Tuples.pair("a", 1), MAPPER.readValue(a2q("{'one':'a','two':1}"), pairImpl));
        Class<?> intLongPairImpl = Class.forName("org.eclipse.collections.impl.tuple.primitive.IntLongPairImpl");
        assertEquals(PrimitiveTuples.pair(1, 2L), MAPPER.readValue(a2q("{'one':1,'two':2}"), intLongPairImpl));
    }

    private <T> void roundTrip(T value, TypeReference<T> type) throws Exception {
        for (DefaultTyping typing : TYPINGS) {
            ObjectMapper mapper = mapperBuilder()
                    .activateDefaultTyping(new NoCheckSubTypeValidator(), typing)
                    .build();
            String json = mapper.writerFor(type).writeValueAsString(value);
            Object result = mapper.readValue(json, type);
            assertEquals(value, result, typing + ": " + json);
            assertEquals(value.getClass(), result.getClass(), typing + ": " + json);
        }
    }

    @Test
    public void testDefaultTyping() throws Exception
    {
        roundTrip(Tuples.pair("a", 1), new TypeReference<Pair<String, Integer>>() { });
        roundTrip(Tuples.twin("a", "b"), new TypeReference<Twin<String>>() { });
        roundTrip(PrimitiveTuples.pair(1, 2L), new TypeReference<IntLongPair>() { });
        roundTrip(PrimitiveTuples.pair("a", 1), new TypeReference<ObjectIntPair<String>>() { });
        roundTrip(Tuples.triple("a", 1, true), new TypeReference<Triple<String, Integer, Boolean>>() { });
        roundTrip(Tuples.triplet("a", "b", "c"), new TypeReference<Triplet<String>>() { });
    }

    @Test
    public void testPojoWithDefaultTyping() throws Exception
    {
        Holder input = new Holder();
        input.pair = Tuples.pair("a", 1);
        input.twin = Tuples.twin("x", "y");
        input.intLongPair = PrimitiveTuples.pair(1, 2L);
        input.objectIntPair = PrimitiveTuples.pair("b", 3);
        input.triple = Tuples.triple("c", 4, false);
        input.anything = Tuples.pair("d", 5L);

        for (DefaultTyping typing : TYPINGS) {
            ObjectMapper mapper = mapperBuilder()
                    .activateDefaultTyping(new NoCheckSubTypeValidator(), typing)
                    .build();
            String json = mapper.writeValueAsString(input);
            Holder result = mapper.readValue(json, Holder.class);
            assertEquals(input.pair, result.pair, json);
            assertEquals(input.twin, result.twin, json);
            assertEquals(input.intLongPair, result.intLongPair, json);
            assertEquals(input.objectIntPair, result.objectIntPair, json);
            assertEquals(input.triple, result.triple, json);
            assertEquals(input.anything, result.anything, json);
        }
    }
}
