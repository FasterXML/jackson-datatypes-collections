package com.fasterxml.jackson.datatype.guava.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;

/**
 * Base deserializer of a primitive collection or an immutable primitive array.
 *
 * @param <ObjectType>            The base object type. Eg: {@code Long}
 * @param <PrimitiveList>         The collection type that we deserialize. Eg: type of {@code Longs.asList(..)} or
 *                                {@code ImmutableLongArray}
 * @param <IntermediateContainer> The intermediate container where we collect the collection elements before returning
 *                                it as the {@code PrimitiveList} type. Eg. in case of collections would be a
 *                                {@code ArrayList<Long>} or in case of an immutable array would be
 *                                {@code ImmutableLongArray.Builder}
 */
public abstract class BasePrimitiveCollectionDeserializer<ObjectType, PrimitiveList, IntermediateContainer>
        extends StdDeserializer<PrimitiveList> {

    protected BasePrimitiveCollectionDeserializer(Class<? extends PrimitiveList> cls, Class<? super ObjectType> itemType) {
        super(cls);
    }

    protected BasePrimitiveCollectionDeserializer(JavaType type) {
        super(type);
    }

    protected abstract IntermediateContainer createIntermediateCollection();

    protected IntermediateContainer createIntermediateCollection(int expectedSize) {
        return createIntermediateCollection();
    }

    protected abstract void add(IntermediateContainer intermediateContainer, JsonParser parser,
                                DeserializationContext context) throws JacksonException;

    protected abstract PrimitiveList finish(IntermediateContainer intermediateContainer);

    @Override
    public Object deserializeWithType(JsonParser parser, DeserializationContext context,
                                      TypeDeserializer typeDeserializer) throws JacksonException {
        return typeDeserializer.deserializeTypedFromArray(parser, context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PrimitiveList deserialize(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        // Should usually point to START_ARRAY
        if (parser.isExpectedStartArrayToken()) {
            return _deserializeContents(parser, context);
        }
        // But may support implicit arrays from single values?
        if (context.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            return _deserializeFromSingleValue(parser, context);
        }
        return (PrimitiveList) context.handleUnexpectedToken(getValueType(context), parser);
    }

    protected PrimitiveList _deserializeContents(JsonParser parser, DeserializationContext context)
            throws JacksonException {
        IntermediateContainer collection = createIntermediateCollection();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            add(collection, parser, context);
        }
        return finish(collection);
    }

    protected PrimitiveList _deserializeFromSingleValue(JsonParser parser, DeserializationContext ctxt)
            throws JacksonException {
        IntermediateContainer intermediateContainer = createIntermediateCollection();
        add(intermediateContainer, parser, ctxt);
        return finish(intermediateContainer);
    }

}
