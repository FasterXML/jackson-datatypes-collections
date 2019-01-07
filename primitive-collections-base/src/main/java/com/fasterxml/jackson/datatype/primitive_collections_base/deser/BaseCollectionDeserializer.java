package com.fasterxml.jackson.datatype.primitive_collections_base.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;

public abstract class BaseCollectionDeserializer<T, Intermediate> extends StdDeserializer<T> {
    private static final long serialVersionUID = 1L;

    protected BaseCollectionDeserializer(Class<? super T> cls) {
        super(cls);
    }

    protected BaseCollectionDeserializer(JavaType type) {
        super(type);
    }

    protected abstract Intermediate createIntermediate();

    protected Intermediate createIntermediate(int expectedSize) {
        return createIntermediate();
    }

    protected abstract void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
            throws IOException;

    protected abstract T finish(Intermediate intermediate);

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
            throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        // Should usually point to START_ARRAY
        if (p.isExpectedStartArrayToken()) {
            return _deserializeContents(p, ctxt);
        }
        // But may support implicit arrays from single values?
        if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            return _deserializeFromSingleValue(p, ctxt);
        }
        return (T) ctxt.handleUnexpectedToken(handledType(), p);
    }

    protected T _deserializeContents(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        Intermediate collection = createIntermediate();

        while (p.nextToken() != JsonToken.END_ARRAY) {
            add(collection, p, ctxt);
        }
        return finish(collection);
    }

    protected T _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        Intermediate intermediate = createIntermediate();
        add(intermediate, p, ctxt);
        return finish(intermediate);
    }

}
