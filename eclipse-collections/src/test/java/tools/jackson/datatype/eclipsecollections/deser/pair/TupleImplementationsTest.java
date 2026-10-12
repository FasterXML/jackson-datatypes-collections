package tools.jackson.datatype.eclipsecollections.deser.pair;

import org.junit.jupiter.api.Test;

import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.tuple.Tuples;

import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for verification of values produced for tuple implementation classes:
 * cannot be triggered with actual Eclipse Collections types, so uses
 * instantiator that produces values of a different type.
 */
public class TupleImplementationsTest
{
    private static final String JSON = "{\"one\":\"a\",\"two\":\"b\"}";

    // Produces `PairImpl`
    private static ValueInstantiator pairInstantiator(ObjectMapper mapper) {
        JavaType pairType = mapper.getTypeFactory().constructType(Pair.class);
        return new PairInstantiators.PairInstantiator(pairType) {
            @Override
            public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                return Tuples.pair(args[0], args[1]);
            }

            @Override
            JavaType oneType(DeserializationConfig config) {
                return pairType.containedTypeOrUnknown(0);
            }

            @Override
            JavaType twoType(DeserializationConfig config) {
                return pairType.containedTypeOrUnknown(1);
            }
        };
    }

    private static ObjectMapper mapperFor(Class<?> implClass) {
        ValueInstantiator inst = new TupleImplementations.Verifying(implClass,
                pairInstantiator(JsonMapper.builder().build()));
        return JsonMapper.builder()
                .addModule(new SimpleModule().addValueInstantiator(implClass, inst))
                .build();
    }

    @Test
    public void testMatchingType() throws Exception
    {
        Class<?> pairImpl = Class.forName("org.eclipse.collections.impl.tuple.PairImpl");
        assertEquals(Tuples.pair("a", "b"), mapperFor(pairImpl).readValue(JSON, pairImpl));
    }

    @Test
    public void testMismatchingType() throws Exception
    {
        // `TwinImpl` extends `PairImpl`, but instantiator produces plain `PairImpl`
        Class<?> twinImpl = Class.forName("org.eclipse.collections.impl.tuple.TwinImpl");
        MismatchedInputException e = assertThrows(MismatchedInputException.class,
                () -> mapperFor(twinImpl).readValue(JSON, twinImpl));
        assertTrue(e.getMessage().contains("closest supported type produces values of type"),
                e.getMessage());
        assertEquals(twinImpl, e.getTargetType());
    }

    @Test
    public void testValueClass() throws Exception
    {
        Class<?> twinImpl = Class.forName("org.eclipse.collections.impl.tuple.TwinImpl");
        ValueInstantiator inst = new TupleImplementations.Verifying(twinImpl,
                pairInstantiator(JsonMapper.builder().build()));
        assertEquals(twinImpl, inst.getValueClass());
    }
}
