package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author mvolkhart
 */
public class RangeMapDeserializer<T extends RangeMap<Comparable<?>, Object>>
        extends StdDeserializer<T> implements ContextualDeserializer {
    private static final long serialVersionUID = 1L;

    private static final List<String> METHOD_NAMES = ImmutableList.of("copyOf", "create");
    private final MapLikeType type;
    private final KeyDeserializer keyDeserializer;
    private final TypeDeserializer elementTypeDeserializer;
    private final JsonDeserializer<?> elementDeserializer;

    // since 2.9.5: in 3.x demote to `ContainerDeserializerBase`
    private final NullValueProvider nullProvider;
    private final boolean isImmutable;
    private final boolean skipNullValues;

    /**
     * Since we have to use a method to transform from a known range-map type into actual one, we'll
     * resolve method just once, use it. Note that if this is set to null, we can just construct a
     * {@link TreeRangeMap} instance and be done with it.
     */
    private final Method creatorMethod;

    public RangeMapDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
                                TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer,
                                boolean isImmutable
    ) {
        this(type, keyDeserializer, elementTypeDeserializer, elementDeserializer,
                findTransformer(type.getRawClass()), null, isImmutable);
    }

    public RangeMapDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
                                TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer,
                                Method creatorMethod, NullValueProvider nvp, boolean isImmutable) {
        super(type);
        this.type = type;
        this.keyDeserializer = keyDeserializer;
        this.elementTypeDeserializer = elementTypeDeserializer;
        this.elementDeserializer = elementDeserializer;
        this.creatorMethod = creatorMethod;
        this.nullProvider = nvp;
        this.isImmutable = isImmutable;
        skipNullValues = (nvp == null) ? false : NullsConstantProvider.isSkipper(nvp);
    }

    private static Method findTransformer(Class<?> rawType) {
        if (rawType == TreeRangeMap.class) {
            return null;
        }

        // First, check type itself for matching methods
        for (String methodName : METHOD_NAMES) {
            try {
                Method m = rawType.getDeclaredMethod(methodName, RangeMap.class);
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
                Method m = rawType.getMethod(methodName, RangeMap.class);
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

    @Override // since 2.12
    public LogicalType logicalType() {
        return LogicalType.Map;
    }

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
        JsonDeserializer<?> valueDeser = elementDeserializer;
        final JavaType vt = type.getContentType();
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        } else { // if directly assigned, probably not yet contextual, so:
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
        }
        // Type deserializer is slightly different; must be passed, but needs to become contextual:
        TypeDeserializer vtd = elementTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }

        return new RangeMapDeserializer<>(type, kd, vtd, valueDeser, creatorMethod,
                findContentNullProvider(ctxt, property, valueDeser), isImmutable);
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

        //check if ACCEPT_SINGLE_VALUE_AS_ARRAY feature is enabled
        if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            return deserializeFromSingleValue(p, ctxt);
        }
        // if not deserialize the normal way
        return deserializeContents(p, ctxt);
    }

    private T deserializeContents(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        RangeMap rangeMap = TreeRangeMap.create();

        JsonToken currToken = p.currentToken();
        if (currToken != JsonToken.FIELD_NAME) {
            // 01-Mar-2023, tatu: [datatypes-collections#104] Handle empty Maps too
            if (currToken != JsonToken.END_OBJECT) {
                expect(p, JsonToken.START_OBJECT);
                currToken = p.nextToken();
            }
        }

        for (; currToken == JsonToken.FIELD_NAME; currToken = p.nextToken()) {
            final Range<Comparable<?>> key = (Range<Comparable<?>>) keyDeserializer.deserializeKey(p.currentName(), ctxt);

            p.nextToken();
            final Object value;
            if (p.currentToken() == JsonToken.VALUE_NULL) {
                if (skipNullValues) {
                    continue;
                }
                value = nullProvider.getNullValue(ctxt);
            } else if (elementTypeDeserializer != null) {
                value = elementDeserializer.deserializeWithType(p, ctxt, elementTypeDeserializer);
            } else {
                value = elementDeserializer.deserialize(p, ctxt);
            }
            rangeMap.put(key, value);
        }

        if (creatorMethod == null) {
            return (T) rangeMap;
        }
        try {
            @SuppressWarnings("unchecked")
            T map = (T) creatorMethod.invoke(null, rangeMap);
            return map;
        } catch (InvocationTargetException e) {
            throw new JsonMappingException(p, "Could not map to " + type, _peel(e));
        } catch (IllegalArgumentException e) {
            throw new JsonMappingException(p, "Could not map to " + type, _peel(e));
        } catch (IllegalAccessException e) {
            throw new JsonMappingException(p, "Could not map to " + type, _peel(e));
        }
    }

    private T deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        RangeMap rangeMap = TreeRangeMap.create();

        expect(p, JsonToken.START_OBJECT);

        while (p.nextToken() != JsonToken.END_OBJECT) {
            final Range<Comparable<?>> key = (Range<Comparable<?>>) keyDeserializer.deserializeKey(p.currentName(), ctxt);

            p.nextToken();

            // if there is an array, parse the array and add the elements
            if (p.currentToken() == JsonToken.START_ARRAY) {

                while (p.nextToken() != JsonToken.END_ARRAY) {
                    // get the current token value
                    final Object value = getCurrentTokenValue(p, ctxt);
                    // add the token value to the map
                    rangeMap.put(key, value);
                }
            }
            // if the element is a String, then add it as a List
            else {
                // get the current token value
                final Object value = getCurrentTokenValue(p, ctxt);
                // add the single value
                rangeMap.put(key, value);
            }
        }
        if (creatorMethod == null) {
            return (T) rangeMap;
        }
        try {
            @SuppressWarnings("unchecked")
            T map = (T) creatorMethod.invoke(null, rangeMap);
            return map;
        } catch (InvocationTargetException e) {
            throw new JsonMappingException(p, "Could not map to " + type, _peel(e));
        } catch (IllegalArgumentException e) {
            throw new JsonMappingException(p, "Could not map to " + type, _peel(e));
        } catch (IllegalAccessException e) {
            throw new JsonMappingException(p, "Could not map to " + type, _peel(e));
        }
    }

    private Object getCurrentTokenValue(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        if (p.currentToken() == JsonToken.VALUE_NULL) {
            return null;
        }
        if (elementTypeDeserializer != null) {
            return elementDeserializer.deserializeWithType(p, ctxt, elementTypeDeserializer);
        }
        return elementDeserializer.deserialize(p, ctxt);
    }

    private void expect(JsonParser p, JsonToken token) throws IOException {
        if (p.currentToken() != token) {
            throw new JsonMappingException(p, "Expecting " + token + " to start `RangeMap` value, found " + p.currentToken(),
                    p.currentLocation());
        }
    }

    private Throwable _peel(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }
}
