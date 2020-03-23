package com.fasterxml.jackson.datatype.guava.deser.primitives;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.guava.deser.BasePrimitiveCollectionDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseGuavaPrimitivesCollectionDeserializer<ObjectType, PrimitiveList extends List<ObjectType>, IntermediateCollection extends Collection<ObjectType>>
        extends BasePrimitiveCollectionDeserializer<ObjectType, PrimitiveList, IntermediateCollection> {

    protected BaseGuavaPrimitivesCollectionDeserializer(Class<? extends PrimitiveList> cls, Class<? super ObjectType> itemType) {
        super(cls, itemType);
    }

    @Override
    protected IntermediateCollection createIntermediateCollection() {
        return (IntermediateCollection) new ArrayList<ObjectType>();
    }

    @Override
    protected void add(IntermediateCollection intermediateCollection, JsonParser parser, DeserializationContext context) throws IOException {
        intermediateCollection.add(asPrimitive(parser));
    }

    protected abstract ObjectType asPrimitive(JsonParser parser) throws IOException;
}
