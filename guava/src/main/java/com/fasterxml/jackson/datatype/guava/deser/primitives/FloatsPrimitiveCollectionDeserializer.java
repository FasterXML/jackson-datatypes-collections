package com.fasterxml.jackson.datatype.guava.deser.primitives;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.guava.util.PrimitiveTypes;
import com.google.common.primitives.Floats;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class FloatsPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Float, List<Float>, Collection<Float>> {
    public FloatsPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.FloatsType, Float.class);
    }

    @Override
    protected Float asPrimitive(JsonParser parser) throws IOException {
        return parser.getFloatValue();
    }

    @Override
    protected List<Float> finish(Collection<Float> floats) {
        return Floats.asList(Floats.toArray(floats));
    }
}