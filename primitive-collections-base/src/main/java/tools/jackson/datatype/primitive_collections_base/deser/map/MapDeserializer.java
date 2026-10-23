package tools.jackson.datatype.primitive_collections_base.deser.map;

import java.util.function.BiFunction;
import java.util.function.Function;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.*;
import tools.jackson.databind.jsontype.TypeDeserializer;

/**
 * @author yawkat
 */
public class MapDeserializer<T, I, K extends KeyHandler<K>, V extends ValueHandler<V>>
        extends ValueDeserializer<T>
{
    public static <T, I, K extends KeyHandler<K>, V extends ValueHandler<V>> MapDeserializer<T, I, K, V> create(
            JavaType keyType, JavaType valueType, TypeHandlerPair<I, K, V> typeHandlerPair,
            Function<I, T> finish) {
        K keyHandler = typeHandlerPair.keyHandler(keyType);
        V valueHandler = typeHandlerPair.valueHandler(valueType);
        return new MapDeserializer<>(keyHandler, valueHandler, typeHandlerPair, finish);
    }

    /**
     * Variant of {@link #create(JavaType, JavaType, TypeHandlerPair, Function)} that
     * specifies type of maps produced, and where finishing function has access
     * to the context, for reporting problems with content.
     *
     * @since 3.3
     */
    public static <T, I, K extends KeyHandler<K>, V extends ValueHandler<V>> MapDeserializer<T, I, K, V> create(
            Class<?> handledType, JavaType keyType, JavaType valueType, TypeHandlerPair<I, K, V> typeHandlerPair,
            BiFunction<DeserializationContext, I, T> finish) {
        K keyHandler = typeHandlerPair.keyHandler(keyType);
        V valueHandler = typeHandlerPair.valueHandler(valueType);
        return new MapDeserializer<>(handledType, keyHandler, valueHandler, typeHandlerPair, finish);
    }

    // null if not known
    private final Class<?> handledType;
    // @since 3.3 (private before)
    protected final K keyHandler;
    // @since 3.3 (private before)
    protected final V valueHandler;
    // @since 3.3 (private before)
    protected final TypeHandlerPair<I, K, V> typeHandlerPair;
    // null if this is the identity function
    private final BiFunction<DeserializationContext, I, T> finish;

    public MapDeserializer(K keyHandler, V valueHandler,
            TypeHandlerPair<I, K, V> typeHandlerPair, Function<I, T> finish) {
        this(null, keyHandler, valueHandler, typeHandlerPair,
                (finish == null) ? null : (ctxt, intermediate) -> finish.apply(intermediate));
    }

    /**
     * @since 3.3
     */
    public MapDeserializer(Class<?> handledType, K keyHandler, V valueHandler,
            TypeHandlerPair<I, K, V> typeHandlerPair, BiFunction<DeserializationContext, I, T> finish) {
        this.handledType = handledType;
        this.keyHandler = keyHandler;
        this.valueHandler = valueHandler;
        this.typeHandlerPair = typeHandlerPair;
        this.finish = finish;
    }

    protected MapDeserializer<T, ?, ?, ?> withResolved(K keyHandler, V valueHandler) {
        return new MapDeserializer<>(handledType, keyHandler, valueHandler, typeHandlerPair, finish);
    }

    @Override
    public Class<?> handledType() {
        return handledType;
    }

    protected I createIntermediate() {
        return typeHandlerPair.createEmpty();
    }

    /**
     * Variant of {@link #createIntermediate()} that has access to the context, for
     * reporting problems with creating the map: by default simply calls
     * {@link #createIntermediate()}.
     *
     * @since 3.3
     */
    protected I createIntermediate(DeserializationContext ctxt) throws JacksonException {
        return createIntermediate();
    }

    /**
     * @since 3.3 (private before)
     */
    protected void deserializeEntry(
            I target,
            DeserializationContext ctx,
            String key,
            JsonParser valueParser)
        throws JacksonException
    {
        typeHandlerPair.add(target, keyHandler, valueHandler, ctx, key, valueParser);
    }

    /**
     * @deprecated Since 3.3 use {@link #finish(DeserializationContext, Object)}
     */
    @Deprecated
    protected T finish(I intermediate) {
        return finish(null, intermediate);
    }

    /**
     * @since 3.3
     */
    @SuppressWarnings("unchecked")
    protected T finish(DeserializationContext ctxt, I intermediate) {
        return finish == null ? (T) intermediate : finish.apply(ctxt, intermediate);
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    {
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
        // Ok: must point to START_OBJECT or PROPERTY_NAME
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_OBJECT) { // If START_OBJECT, move to next; may also be END_OBJECT
            t = p.nextToken();
        }
        if (t != JsonToken.PROPERTY_NAME && t != JsonToken.END_OBJECT) {
            // !!! 16-Sep-2019, tatu: Should use full generic type, for error message,
            //   but would require more refactoring (to extend `StdDeserializer` f.ex)
            return (T) ctxt.handleUnexpectedToken(ctxt.constructType(handledType()), p);
        }

        I map = createIntermediate(ctxt);
        deserializeEntries(p, ctxt, map);
        return finish(ctxt, map);
    }

    /**
     * Reads entries of a JSON Object into given map, starting from the first
     * {@code PROPERTY_NAME} (or {@code END_OBJECT} for empty Objects): may also be
     * used to populate an existing map (like when merging).
     *
     * @since 3.3
     */
    protected void deserializeEntries(JsonParser p, DeserializationContext ctxt, I target)
        throws JacksonException
    {
        for (; p.currentToken() == JsonToken.PROPERTY_NAME; p.nextToken()) {
            // Must point to field name now
            String fieldName = p.currentName();
            p.nextToken();
            deserializeEntry(target, ctxt, fieldName, p);
        }
    }
}

