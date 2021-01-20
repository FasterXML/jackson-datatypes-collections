package com.fasterxml.jackson.datatype.eclipsecollections.deser.pair;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;

import org.eclipse.collections.api.tuple.primitive.*;
import org.eclipse.collections.impl.tuple.primitive.PrimitiveTuples;

import java.util.function.Function;

import static com.fasterxml.jackson.datatype.eclipsecollections.deser.pair.PairInstantiators.*;

public class PairInstantiatorsPopulator {

    static void populate() {
        // primitive -> primitive
        /* with
            byte|char|short|int|long|float|double|boolean key
            short|byte|char|int|long|float|double|boolean value
        */
        purePrimitiveInstantiator(ByteShortPair.class, byte.class, short.class,
                (one, two) -> PrimitiveTuples.pair((byte) one, (short) two));
        /* endwith */

        // primitive -> object
        /* with byte|char|short|int|long|float|double|boolean key */
        Function<JavaType, ValueInstantiator> byteObjectPairLambda = (JavaType beanType) ->
                primitiveObjectInstantiator(beanType, byte.class,
                        (one, two) -> PrimitiveTuples.pair((byte) one, two));
        PairInstantiators.add(ByteObjectPair.class, byteObjectPairLambda);
        /* endwith */

        // object -> primitive
        /* with short|byte|char|int|long|float|double|boolean value */
        Function<JavaType, ValueInstantiator> objectShortPairLambda = (JavaType beanType) ->
                objectPrimitiveInstantiator(beanType, short.class,
                        (one, two) -> PrimitiveTuples.pair(one, (short) two));
        PairInstantiators.add(ObjectShortPair.class, objectShortPairLambda);
        /* endwith */
    }

    private PairInstantiatorsPopulator() {}
}
