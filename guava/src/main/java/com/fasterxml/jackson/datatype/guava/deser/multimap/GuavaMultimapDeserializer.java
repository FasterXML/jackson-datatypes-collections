package com.fasterxml.jackson.datatype.guava.deser.multimap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * @author mvolkhart
 */
public abstract class GuavaMultimapDeserializer<T extends Multimap<Object,
        Object>> extends JsonDeserializer<T> implements ContextualDeserializer {

    private static final List<String> METHOD_NAMES = ImmutableList.of("copyOf", "create");
    private final MapLikeType type;
    private final KeyDeserializer keyDeserializer;
    private final TypeDeserializer elementTypeDeserializer;
    private final JsonDeserializer<?> elementDeserializer;
    /**
     * Since we have to use a method to transform from a known multi-map type into actual one, we'll
     * resolve method just once, use it. Note that if this is set to null, we can just construct a
     * {@link com.google.common.collect.LinkedListMultimap} instance and be done with it.
     */
    private final Method creatorMethod;

    public GuavaMultimapDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) {
        this(type, keyDeserializer, elementTypeDeserializer, elementDeserializer,
                findTransformer(type.getRawClass()));
    }

    public GuavaMultimapDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer,
            Method creatorMethod) {
        this.type = type;
        this.keyDeserializer = keyDeserializer;
        this.elementTypeDeserializer = elementTypeDeserializer;
        this.elementDeserializer = elementDeserializer;
        this.creatorMethod = creatorMethod;
    }

    private static Method findTransformer(Class<?> rawType) {
        // Very first thing: if it's a "standard multi-map type", can avoid copying
        if (rawType == LinkedListMultimap.class || rawType == ListMultimap.class || rawType ==
                Multimap.class) {
            return null;
        }

        // First, check type itself for matching methods
        for (String methodName : METHOD_NAMES) {
            try {
                Method m = rawType.getMethod(methodName, Multimap.class);
                if (m != null) {
                    return m;
                }
            } catch (NoSuchMethodException e) {
            }
            // pass SecurityExceptions as-is:
            // } catch (SecurityException e) { }
        }

        // If not working, possibly super types too (should we?)
        for (String methodName : METHOD_NAMES) {
            try {
                Method m = rawType.getMethod(methodName, Multimap.class);
                if (m != null) {
                    return m;
                }
            } catch (NoSuchMethodException e) {
            }
            // pass SecurityExceptions as-is:
            // } catch (SecurityException e) { }
        }

        return null;
    }

    protected abstract T createMultimap();

    /**
     * We need to use this method to properly handle possible contextual variants of key and value
     * deserializers, as well as type deserializers.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property) throws JsonMappingException {
        KeyDeserializer kd = keyDeserializer;
        if (kd == null) {
            kd = ctxt.findKeyDeserializer(type.getKeyType(), property);
        }
        JsonDeserializer<?> ed = elementDeserializer;
        if (ed == null) {
            ed = ctxt.findContextualValueDeserializer(type.getContentType(), property);
        }
        // Type deserializer is slightly different; must be passed, but needs to become contextual:
        TypeDeserializer etd = elementTypeDeserializer;
        if (etd != null && property != null) {
            etd = etd.forProperty(property);
        }
        return (_createContextual(type, kd, etd, ed, creatorMethod));
    }

    protected abstract JsonDeserializer<?> _createContextual(MapLikeType t,
            KeyDeserializer kd, TypeDeserializer typeDeserializer,
            JsonDeserializer<?> ed, Method method);

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {

    	//check if ACCEPT_SINGLE_VALUE_AS_ARRAY feature is enabled
        if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            return deserializeFromSingleValue(jp, ctxt);
        }
        // if not deserialize the normal way
        else {
            return deserializeContents(jp, ctxt);
        }

    }

    private T deserializeContents(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        T multimap = createMultimap();

        expect(jp, JsonToken.START_OBJECT);

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            final Object key;
            if (keyDeserializer != null) {
                key = keyDeserializer.deserializeKey(jp.getCurrentName(), ctxt);
            } else {
                key = jp.getCurrentName();
            }

            jp.nextToken();
            expect(jp, JsonToken.START_ARRAY);

            while (jp.nextToken() != JsonToken.END_ARRAY) {
                final Object value;
                if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
                    value = null;
                } else if (elementTypeDeserializer != null) {
                    value = elementDeserializer.deserializeWithType(jp, ctxt, elementTypeDeserializer);
                } else {
                    value = elementDeserializer.deserialize(jp, ctxt);
                }
                multimap.put(key, value);
            }
        }
        if (creatorMethod == null) {
            return multimap;
        }
        try {
            @SuppressWarnings("unchecked")
            T map = (T) creatorMethod.invoke(null, multimap);
            return map;
        } catch (InvocationTargetException e) {
            throw new JsonMappingException(jp, "Could not map to " + type, _peel(e));
        } catch (IllegalArgumentException e) {
            throw new JsonMappingException(jp, "Could not map to " + type, _peel(e));
        } catch (IllegalAccessException e) {
            throw new JsonMappingException(jp, "Could not map to " + type, _peel(e));
        }
    }

    private T deserializeFromSingleValue(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        T multimap = createMultimap();

        expect(jp, JsonToken.START_OBJECT);

        while (jp.nextToken() != JsonToken.END_OBJECT) {
            final Object key;
            if (keyDeserializer != null) {
                key = keyDeserializer.deserializeKey(jp.getCurrentName(), ctxt);
            } else {
                key = jp.getCurrentName();
            }

            jp.nextToken();

            // if there is an array, parse the array and add the elements
            if (jp.currentToken() == JsonToken.START_ARRAY) {

                while (jp.nextToken() != JsonToken.END_ARRAY) {
                    // get the current token value
                    final Object value = getCurrentTokenValue(jp, ctxt);
                    // add the token value to the map
                    multimap.put(key, value);
                }
            }
            // if the element is a String, then add it as a List
            else {
                // get the current token value
                final Object value = getCurrentTokenValue(jp, ctxt);
                // add the single value as a list
                multimap.put(key, Collections.singletonList(value));
            }
        }
        if (creatorMethod == null) {
            return multimap;
        }
        try {
            @SuppressWarnings("unchecked")
            T map = (T) creatorMethod.invoke(null, multimap);
            return map;
        } catch (InvocationTargetException e) {
            throw new JsonMappingException(jp, "Could not map to " + type, _peel(e));
        } catch (IllegalArgumentException e) {
            throw new JsonMappingException(jp, "Could not map to " + type, _peel(e));
        } catch (IllegalAccessException e) {
            throw new JsonMappingException(jp, "Could not map to " + type, _peel(e));
        }
    }

    private Object getCurrentTokenValue(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {

        final Object value;
        if (jp.getCurrentToken() == JsonToken.VALUE_NULL) {
            value = null;
        } else if (elementTypeDeserializer != null) {
            value = elementDeserializer.deserializeWithType(jp, ctxt, elementTypeDeserializer);
        } else {
            value = elementDeserializer.deserialize(jp, ctxt);
        }
        return value;
    }

    private void expect(JsonParser jp, JsonToken token) throws IOException {
        if (jp.getCurrentToken() != token) {
            throw new JsonMappingException(jp, "Expecting " + token + ", found " + jp.getCurrentToken(),
                    jp.getCurrentLocation());
        }
    }

    private Throwable _peel(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }
}
