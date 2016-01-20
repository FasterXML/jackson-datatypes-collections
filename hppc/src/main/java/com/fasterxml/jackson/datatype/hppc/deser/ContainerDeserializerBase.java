package com.fasterxml.jackson.datatype.hppc.deser;

import java.io.IOException;
import java.lang.reflect.Constructor;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.ClassUtil;

@SuppressWarnings("serial")
public abstract class ContainerDeserializerBase<T>
    extends StdDeserializer<T>
{
    /**
     * We will use the default constructor of the class for
     * instantiation
     */
    protected final Constructor<T> _defaultCtor;

    @SuppressWarnings("unchecked")
    protected ContainerDeserializerBase(JavaType type, DeserializationConfig config)
    {
        super(type);
        boolean fixAccess = config.isEnabled(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        _defaultCtor = (Constructor<T>) ClassUtil.findConstructor(type.getRawClass(), fixAccess);
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt,
        TypeDeserializer typeDeserializer)
        throws IOException, JsonProcessingException
    {
        return typeDeserializer.deserializeTypedFromArray(jp, ctxt);
    }

    @Override
    public T deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        // Ok: usually must point to START_ARRAY (or equivalent)
        // (note: caller handles nulls)
        if (!jp.isExpectedStartArrayToken()) {
            return handleNonArray(jp, ctxt);
        }
        T container = createContainerInstance(ctxt);
        deserializeContents(jp, ctxt, container);
        return container;
    }
    
    protected T handleNonArray(JsonParser jp, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        // default impl will just throw an exception; except if 'accept single as collection' is enabled...
        if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            T single = handleSingleAsArray(jp, ctxt, createContainerInstance(ctxt));
            if (single != null) {
                return single;
            }
        }
        throw ctxt.mappingException(_valueClass);
    }

    protected T createContainerInstance(DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        try {
            return _defaultCtor.newInstance();
        } catch (Exception e) {
            throw ctxt.instantiationException(handledType(), e);
        }
    }
    
    protected T handleSingleAsArray(JsonParser jp, DeserializationContext ctxt, T container)
        throws IOException, JsonProcessingException
    {
        return null;
    }

    // // // Abstract methods for sub-classes to implement

    public abstract void deserializeContents(JsonParser jp, DeserializationContext ctxt, T container)
        throws IOException, JsonProcessingException;
}
