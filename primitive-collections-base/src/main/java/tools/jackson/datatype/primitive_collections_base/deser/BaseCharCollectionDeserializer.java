package tools.jackson.datatype.primitive_collections_base.deser;

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

    /**
     * Variant of {@link #createIntermediate()} that has access to the context, for
     * reporting problems with creating the value: by default simply calls
     * {@link #createIntermediate()}.
     *
     * @since 3.3
     */
    protected Intermediate createIntermediate(DeserializationContext ctxt)
        throws JacksonException
    {
        return createIntermediate();
    }

    /**
     * Variant of {@link #finish(Object)} that has access to the context, for
     * reporting problems with creating the value: by default simply calls
     * {@link #finish(Object)}.
     *
     * @since 3.3
     */
    protected T finish(DeserializationContext ctxt, Intermediate intermediate)
        throws JacksonException
    {
        return finish(intermediate);
    }

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
        Intermediate intermediate = createIntermediate(ctxt);

        if (p.isExpectedStartArrayToken()) {
            JsonToken t;
            while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                String str;
                if (t == JsonToken.VALUE_STRING) {
                    str = p.getString();
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
            return finish(ctxt, intermediate);
        }

        char[] chars = p.getStringCharacters();
        addAll(intermediate, chars, p.getStringOffset(), p.getStringLength());
        return finish(ctxt, intermediate);
    }
}
