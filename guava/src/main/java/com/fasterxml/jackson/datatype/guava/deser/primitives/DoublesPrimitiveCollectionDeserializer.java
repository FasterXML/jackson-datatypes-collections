package com.fasterxml.jackson.datatype.guava.deser.primitives;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.guava.util.PrimitiveTypes;
import com.google.common.primitives.Doubles;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class DoublesPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Double, List<Double>, Collection<Double>> {
    public DoublesPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.DoublesType, Double.class);
    }

    @Override
    protected Double asPrimitive(JsonParser parser) throws IOException {
        return parser.getDoubleValue();
    }

    @Override
    protected List<Double> finish(Collection<Double> doubles) {
        return Doubles.asList(Doubles.toArray(doubles));
    }
}