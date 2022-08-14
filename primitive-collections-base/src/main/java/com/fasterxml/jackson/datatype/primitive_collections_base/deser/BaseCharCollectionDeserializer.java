package com.fasterxml.jackson.datatype.primitive_collections_base.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;

/**
 * This class doesn't extend {@link BaseCollectionDeserializer} because we serialize from/to strings
 * instead
 */
public abstract class BaseCharCollectionDeserializer<T, Intermediate> extends StdDeserializer<T> {
    protected BaseCharCollectionDeserializer(Class<? super T> cls) {
        super(cls);
    }

    protected abstract Intermediate createIntermediate();

    protected abstract T finish(Intermediate intermediate);

    protected abstract void add(Intermediate intermediate, char c);

    protected abstract void addAll(Intermediate intermediate, char[] chars, int off, int len);

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        return typeDeserializer.deserializeTypedFromScalar(p, ctxt);
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        Intermediate intermediate = createIntermediate();

        if (p.isExpectedStartArrayToken()) {
            JsonToken t;
            while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                String str;
                if (t == JsonToken.VALUE_STRING) {
                    str = p.getText();
                } else {
                    CharSequence cs =
                            (CharSequence) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
                    str = cs.toString();
                }
                if (str.length() != 1) {
                    ctxt.reportInputMismatch(this,
                            "Cannot convert a JSON String of length %d into a char element of " +
                                    "char array",
                            str.length());
                }
                add(intermediate, str.charAt(0));
            }
            return finish(intermediate);
        }

        char[] chars = p.getTextCharacters();
        addAll(intermediate, chars, p.getTextOffset(), p.getTextLength());
        return finish(intermediate);
    }
}
