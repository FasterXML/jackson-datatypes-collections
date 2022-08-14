package tools.jackson.datatype.hppc.deser;

import java.lang.reflect.Constructor;

import tools.jackson.core.*;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.util.ClassUtil;

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
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        // Ok: usually must point to START_ARRAY (or equivalent)
        // (note: caller handles nulls)
        if (!p.isExpectedStartArrayToken()) {
            return handleNonArray(p, ctxt);
        }
        T container = createContainerInstance(ctxt);
        deserializeContents(p, ctxt, container);
        return container;
    }

    @SuppressWarnings("unchecked")
    protected T handleNonArray(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        // default impl will just throw an exception; except if 'accept single as collection' is enabled...
        if (ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)) {
            T single = handleSingleAsArray(p, ctxt, createContainerInstance(ctxt));
            if (single != null) {
                return single;
            }
        }
        return (T) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
    }

    protected T createContainerInstance(DeserializationContext ctxt)
        throws JacksonException
    {
        try {
            return _defaultCtor.newInstance();
        } catch (Exception e) {
            throw ctxt.instantiationException(handledType(), e);
        }
    }
    
    protected T handleSingleAsArray(JsonParser p, DeserializationContext ctxt, T container)
        throws JacksonException
    {
        return null;
    }

    // // // Abstract methods for sub-classes to implement

    public abstract void deserializeContents(JsonParser p, DeserializationContext ctxt, T container)
        throws JacksonException;
}
