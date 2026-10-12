package tools.jackson.datatype.guava.deser.multimap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.deser.std.ContainerDeserializerBase;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.ClassUtil;
import tools.jackson.datatype.guava.util.NullElementHelper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

/**
 * @author mvolkhart
 */
public abstract class GuavaMultimapDeserializer<T extends Multimap<Object, Object>>
    extends ContainerDeserializerBase<T>
{
    private static final List<String> METHOD_NAMES = ImmutableList.of("copyOf", "create");

    private final KeyDeserializer _keyDeserializer;
    private final TypeDeserializer _valueTypeDeserializer;
    private final ValueDeserializer<Object> _valueDeserializer;

    /**
     * Since we may have to use a method to transform from the multi-map type built by
     * {@link #createMultimap()} into actual one, we'll resolve method just once, use it.
     * Method is only invoked if the built instance is not already of the requested type;
     * if this is set to null, built instance is returned as-is.
     */
    private final Method creatorMethod;

    /**
     * Whether the final multimap type does not accept {@code null} values: if so,
     * they would only fail when copied from the intermediate multimap, so need
     * to be reported before adding.
     */
    private final boolean _rejectsNullValues;

    public GuavaMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer) {
        this(type, keyDeserializer, elementTypeDeserializer, elementDeserializer,
                findTransformer(type.getRawClass()), null, null);
    }

    // @since 3.1.0 (added `unwrapSingle`)
    @SuppressWarnings("unchecked")
    protected GuavaMultimapDeserializer(JavaType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer,
            Method creatorMethod, NullValueProvider nvp, Boolean unwrapSingle)
    {
        super(type, nvp, unwrapSingle);
        this._keyDeserializer = keyDeserializer;
        this._valueTypeDeserializer = elementTypeDeserializer;
        this._valueDeserializer = (ValueDeserializer<Object>) elementDeserializer;
        this.creatorMethod = creatorMethod;
        _rejectsNullValues = _rejectsNullValues(type.getRawClass());
    }

    private static Method findTransformer(Class<?> rawType) {
        // First, check type itself for matching methods
        for (String methodName : METHOD_NAMES) {
            try {
                Method m = rawType.getDeclaredMethod(methodName, Multimap.class);
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

    @Override
    public LogicalType logicalType() {
        return LogicalType.Map;
    }

    @Override
    public ValueDeserializer<Object> getContentDeserializer() {
        return _valueDeserializer;
    }

    /**
     * We need to use this method to properly handle possible contextual variants of key and value
     * deserializers, as well as type deserializers.
     */
    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property)
    {
        KeyDeserializer kd = _keyDeserializer;
        if (kd == null) {
            kd = ctxt.findKeyDeserializer(_containerType.getKeyType(), property);
        }
        ValueDeserializer<?> valueDeser = _valueDeserializer;
        final JavaType vt = _containerType.getContentType();
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        } else { // if directly assigned, probably not yet contextual, so:
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
        }
        // Type deserializer is slightly different; must be passed, but needs to become contextual:
        TypeDeserializer vtd = _valueTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        // [datatypes-collections#211]: Handle @JsonFormat(with = ACCEPT_SINGLE_VALUE_AS_ARRAY)
        Boolean unwrapSingle = findFormatFeature(ctxt, property, Multimap.class,
                JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return _createContextual(_containerType, kd, vtd, valueDeser, creatorMethod,
                findContentNullProvider(ctxt, property, valueDeser), unwrapSingle);
    }

    protected abstract ValueDeserializer<?> _createContextual(JavaType t,
            KeyDeserializer kd, TypeDeserializer vtd,
            ValueDeserializer<?> vd, Method method, NullValueProvider np, Boolean unwrapSingle);

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        // [datatypes-collections#211]: Check per-property @JsonFormat first, then global feature
        final boolean acceptSingle = (_unwrapSingle == Boolean.TRUE) ||
                ((_unwrapSingle == null) && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (acceptSingle) {
            return deserializeFromSingleValue(p, ctxt);
        }
        // if not deserialize the normal way
        return deserializeContents(p, ctxt);
    }

    private T deserializeContents(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        T multimap = createMultimap();

        JsonToken currToken = p.currentToken();
        if (currToken != JsonToken.PROPERTY_NAME) {
            // 01-Mar-2023, tatu: [datatypes-collections#104] Handle empty Maps too
            if (currToken != JsonToken.END_OBJECT) {
                expect(ctxt, p, JsonToken.START_OBJECT);
                currToken = p.nextToken();
            }
        }

        for (; currToken == JsonToken.PROPERTY_NAME; currToken = p.nextToken()) {
            final Object key;
            if (_keyDeserializer != null) {
                key = _keyDeserializer.deserializeKey(p.currentName(), ctxt);
            } else {
                key = p.currentName();
            }

            p.nextToken();
            expect(ctxt, p, JsonToken.START_ARRAY);

            JsonToken t;
            while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                _put(p, ctxt, t, multimap, key);
            }
        }
        return _convertToTarget(ctxt, multimap);
    }

    private T deserializeFromSingleValue(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        expect(ctxt, p, JsonToken.START_OBJECT);

        final T multimap = createMultimap();
        while (p.nextToken() != JsonToken.END_OBJECT) {
            final Object key;
            if (_keyDeserializer != null) {
                key = _keyDeserializer.deserializeKey(p.currentName(), ctxt);
            } else {
                key = p.currentName();
            }

            JsonToken t = p.nextToken();

            // if there is an array, parse the array and add the elements
            if (t == JsonToken.START_ARRAY) {
                while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                    _put(p, ctxt, t, multimap, key);
                }
            }
            // if the element is a String, then add it as a List
            else {
                _put(p, ctxt, t, multimap, key);
            }
        }
        return _convertToTarget(ctxt, multimap);
    }

    /**
     * Helper method for converting multimap built by {@link #createMultimap()} into
     * requested type, if (and only if) necessary.
     */
    private T _convertToTarget(DeserializationContext ctxt, T multimap)
    {
        // [datatypes-collections#253]: Avoid redundant copy if already of requested type
        if ((creatorMethod == null) || handledType().isInstance(multimap)) {
            return multimap;
        }
        try {
            @SuppressWarnings("unchecked")
            T map = (T) creatorMethod.invoke(null, multimap);
            return map;
        } catch (InvocationTargetException | IllegalArgumentException | IllegalAccessException e) {
            @SuppressWarnings("unchecked")
            T result = (T) ctxt.handleInstantiationProblem(handledType(), multimap, e);
            return result;
        }
    }

    /**
     * Helper method for deserializing a single value (starting with given token)
     * and adding it to the multimap, unless skipped. Note that {@code null}s are
     * accepted by all intermediate multimap types, but not by all final types
     * (that are created as copies).
     */
    private void _put(JsonParser p, DeserializationContext ctxt, JsonToken t,
            T multimap, Object key)
        throws JacksonException
    {
        Object value;
        if (t == JsonToken.VALUE_NULL) {
            if (_skipNullValues) {
                return;
            }
            value = _nullProvider.getNullValue(ctxt);
        } else if (_valueTypeDeserializer != null) {
            value = _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
        } else {
            value = _valueDeserializer.deserialize(p, ctxt);
        }
        if ((value == null) && _rejectsNullValues) {
            value = NullElementHelper.handleNull(p, ctxt, p.currentToken(),
                    _containerType.getContentType(),
                    "Guava `Multimap` of type "+ClassUtil.getTypeDescription(_containerType));
            // `null` if dropped by problem handler
            if (value == null) {
                return;
            }
        }
        multimap.put(key, value);
    }

    private static boolean _rejectsNullValues(Class<?> raw) {
        return ImmutableMultimap.class.isAssignableFrom(raw)
                || TreeMultimap.class.isAssignableFrom(raw);
    }

    private void expect(DeserializationContext ctxt, JsonParser p, JsonToken token)
    {
        if (p.currentToken() != token) {
            ctxt.reportInputMismatch(handledType(),
"Expecting %s to start `MultiMap` value, encountered %s",
token, p.currentToken());
        }
    }
}
