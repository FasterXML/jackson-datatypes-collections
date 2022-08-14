package com.fasterxml.jackson.datatype.primitive_collections_base.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;

public abstract class BaseCollectionDeserializer<T, Intermediate> extends StdDeserializer<T>
{
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
        throws JacksonException;

    protected abstract T finish(Intermediate intermediate);

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        // Should usually point to START_ARRAY
        if (p.isExpectedStartArrayToken()) {
            return _deserializeContents(p, ctxt);
        }
        // But may support implicit arrays from single values?
        if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            return _deserializeFromSingleValue(p, ctxt);
        }
        return (T) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }

    protected T _deserializeContents(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        Intermediate collection = createIntermediate();

        while (p.nextToken() != JsonToken.END_ARRAY) {
            add(collection, p, ctxt);
        }
        return finish(collection);
    }

    protected T _deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        Intermediate intermediate = createIntermediate();
        add(intermediate, p, ctxt);
        return finish(intermediate);
    }

}
