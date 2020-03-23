package com.fasterxml.jackson.datatype.guava.deser.primitives;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.guava.util.PrimitiveTypes;
import com.google.common.primitives.Booleans;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class BooleansPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Boolean, List<Boolean>, Collection<Boolean>> {

    public BooleansPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.BooleansType, Boolean.class);
    }

    @Override
    protected Boolean asPrimitive(JsonParser parser) throws IOException {
        return parser.getBooleanValue();
    }

    @Override
    protected List<Boolean> finish(Collection<Boolean> booleans) {
        return Booleans.asList(Booleans.toArray(booleans));
    }
}