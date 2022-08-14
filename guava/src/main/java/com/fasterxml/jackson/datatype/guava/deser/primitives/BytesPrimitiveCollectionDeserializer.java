package com.fasterxml.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.guava.util.PrimitiveTypes;
import com.google.common.primitives.Bytes;

import java.util.Collection;
import java.util.List;

public class BytesPrimitiveCollectionDeserializer
        extends BaseGuavaPrimitivesCollectionDeserializer<Byte, List<Byte>, Collection<Byte>> {
    public BytesPrimitiveCollectionDeserializer() {
        super(PrimitiveTypes.BytesType, Byte.class);
    }

    @Override
    protected Byte asPrimitive(JsonParser parser) throws JacksonException {
        return parser.getByteValue();
    }

    @Override
    protected List<Byte> finish(Collection<Byte> bytes) {
        return Bytes.asList(Bytes.toArray(bytes));
    }
}
