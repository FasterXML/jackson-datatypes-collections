package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public abstract class BasePrimitiveCollectionDeserializer<ObjectType, PrimitiveList extends List<ObjectType>, IntermediateCollection extends Collection<ObjectType>>
        extends StdDeserializer<PrimitiveList> {

    protected BasePrimitiveCollectionDeserializer(Class<? extends PrimitiveList> cls, Class<? super ObjectType> itemType) {
        super(cls);
    }

    protected BasePrimitiveCollectionDeserializer(JavaType type) {
        super(type);
    }

    protected abstract IntermediateCollection createIntermediateCollection();

    protected IntermediateCollection createIntermediateCollection(int expectedSize) {
        return createIntermediateCollection();
    }

    protected abstract void add(IntermediateCollection intermediateCollection, JsonParser parser,
                                DeserializationContext context) throws IOException;

    protected abstract PrimitiveList finish(IntermediateCollection intermediateCollection);

    @Override
    public Object deserializeWithType(JsonParser parser, DeserializationContext context,
                                      TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(parser, context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public PrimitiveList deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
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
            throws IOException {
        IntermediateCollection collection = createIntermediateCollection();

        while (parser.nextToken() != JsonToken.END_ARRAY) {
            add(collection, parser, context);
        }
        return finish(collection);
    }

    protected PrimitiveList _deserializeFromSingleValue(JsonParser parser, DeserializationContext ctxt)
            throws IOException {
        IntermediateCollection intermediateCollection = createIntermediateCollection();
        add(intermediateCollection, parser, ctxt);
        return finish(intermediateCollection);
    }

}
