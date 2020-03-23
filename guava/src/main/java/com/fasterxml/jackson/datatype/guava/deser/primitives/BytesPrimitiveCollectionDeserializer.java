package com.fasterxml.jackson.datatype.guava.deser.primitives;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.guava.deser.BasePrimitiveCollectionDeserializer;
import com.fasterxml.jackson.datatype.guava.util.PrimitiveTypes;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BytesPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Byte, List<Byte>, Collection<Byte>> {
    public BytesPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.BytesType, Byte.class);
    }

    @Override
    protected Byte asPrimitive(JsonParser parser) throws IOException {
        return parser.getByteValue();
    }

    @Override
    protected List<Byte> finish(Collection<Byte> bytes) {
        return Bytes.asList(Bytes.toArray(bytes));
    }
}