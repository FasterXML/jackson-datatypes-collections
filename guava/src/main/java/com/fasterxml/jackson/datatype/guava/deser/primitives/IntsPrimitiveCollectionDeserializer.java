package com.fasterxml.jackson.datatype.guava.deser.primitives;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.guava.util.PrimitiveTypes;
import com.google.common.primitives.Ints;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class IntsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Integer, List<Integer>, Collection<Integer>> {
    public IntsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.IntsType, Integer.class);
    }

    @Override
    protected Integer asPrimitive(JsonParser parser) throws IOException {
        return parser.getIntValue();
    }

    @Override
    protected List<Integer> finish(Collection<Integer> integers) {
        return Ints.asList(Ints.toArray(integers));
    }
}