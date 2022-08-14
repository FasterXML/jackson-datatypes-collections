package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.datatype.guava.deser.BasePrimitiveCollectionDeserializer;

public abstract class BaseImmutableArrayDeserializer<ObjectType, ImmutablePrimitiveArray, IntermediateArrayBuilder>
        extends BasePrimitiveCollectionDeserializer<ObjectType, ImmutablePrimitiveArray, IntermediateArrayBuilder> {

    protected BaseImmutableArrayDeserializer(Class<? extends ImmutablePrimitiveArray> cls, Class<? super ObjectType> itemType) {
        super(cls, itemType);
    }

    @Override
    protected final void add(IntermediateArrayBuilder intermediateBuilder, JsonParser parser, DeserializationContext context) throws JacksonException {
        collect(intermediateBuilder, asPrimitive(parser));
    }

    protected abstract void collect(IntermediateArrayBuilder intermediateBuilder, ObjectType value);

    protected abstract ObjectType asPrimitive(JsonParser parser) throws JacksonException;
}
