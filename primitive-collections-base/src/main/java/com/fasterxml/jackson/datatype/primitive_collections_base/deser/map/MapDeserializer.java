package com.fasterxml.jackson.datatype.primitive_collections_base.deser.map;

import java.util.function.Function;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

/**
 * @author yawkat
 */
public class MapDeserializer<T, I, K extends KeyHandler<K>, V extends ValueHandler<V>>
        extends JsonDeserializer<T>
{
    public static <T, I, K extends KeyHandler<K>, V extends ValueHandler<V>> MapDeserializer<T, I, K, V> create(
            JavaType keyType, JavaType valueType, TypeHandlerPair<I, K, V> typeHandlerPair,
            Function<I, T> finish) {
        K keyHandler = typeHandlerPair.keyHandler(keyType);
        V valueHandler = typeHandlerPair.valueHandler(valueType);
        return new MapDeserializer<>(keyHandler, valueHandler, typeHandlerPair, finish);
    }

    private final K keyHandler;
    private final V valueHandler;
    private final TypeHandlerPair<I, K, V> typeHandlerPair;
    // null if this is the identity function
    private final Function<I, T> finish;

    public MapDeserializer(K keyHandler, V valueHandler,
            TypeHandlerPair<I, K, V> typeHandlerPair, Function<I, T> finish) {
        this.keyHandler = keyHandler;
        this.valueHandler = valueHandler;
        this.typeHandlerPair = typeHandlerPair;
        this.finish = finish;
    }

    protected MapDeserializer<T, ?, ?, ?> withResolved(K keyHandler, V valueHandler) {
        return new MapDeserializer<>(keyHandler, valueHandler, typeHandlerPair, finish);
    }

    protected I createIntermediate() {
        return typeHandlerPair.createEmpty();
    }

    private void deserializeEntry(
            I target,
            DeserializationContext ctx,
            String key,
            JsonParser valueParser)
        throws JacksonException
    {
        typeHandlerPair.add(target, keyHandler, valueHandler, ctx, key, valueParser);
    }

    @SuppressWarnings("unchecked")
    protected T finish(I intermediate) {
        return finish == null ? (T) intermediate : finish.apply(intermediate);
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        K kc = keyHandler.createContextualKey(ctxt, property);
        V vc = valueHandler.createContextualValue(ctxt, property);
        //noinspection ObjectEquality
        if (kc == keyHandler && vc == valueHandler) {
            return this;
        } else {
            return withResolved(kc, vc);
        }
    }

    @Override
    public Object deserializeWithType(
            JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        // note: call "...FromObject" because expected output structure
        // for value is JSON Object (regardless of contortions used for type id)
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        // Ok: must point to START_OBJECT or FIELD_NAME
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) { // If START_OBJECT, move to next; may also be END_OBJECT
            t = p.nextToken();
        }
        if (t != JsonToken.FIELD_NAME && t != JsonToken.END_OBJECT) {
            // !!! 16-Sep-2019, tatu: Should use full generic type, for error message,
            //   but would require more refactoring (to extend `StdDeserializer` f.ex)
            return (T) ctxt.handleUnexpectedToken(ctxt.constructType(handledType()), p);
        }

        I map = createIntermediate();
        for (; p.currentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
            // Must point to field name now
            String fieldName = p.currentName();
            p.nextToken();
            deserializeEntry(map, ctxt, fieldName, p);
        }
        return finish(map);
    }
}

