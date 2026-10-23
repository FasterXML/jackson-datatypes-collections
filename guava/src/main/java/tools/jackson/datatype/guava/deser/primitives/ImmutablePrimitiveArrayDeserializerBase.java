package tools.jackson.datatype.guava.deser.primitives;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.jdk.PrimitiveArrayDeserializers;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.AccessPattern;

/**
 * Shared base for deserializers of Guava immutable primitive arrays
 * ({@code ImmutableIntArray}, {@code ImmutableLongArray}, {@code ImmutableDoubleArray}),
 * delegating to the standard JDK primitive array deserializer.
 * Delegate is contextualized so that per-property settings
 * ({@code @JsonFormat(with = ACCEPT_SINGLE_VALUE_AS_ARRAY)},
 * {@code @JsonSetter(nulls/contentNulls)}) are applied.
 *<p>
 * Note: per-type config overrides are resolved by the delegate for the
 * primitive array type (like {@code int[]}), not for the Guava type.
 *
 * @param <T> Guava immutable array type
 * @param <A> Matching JDK primitive array type
 *
 * @since 3.3
 */
public abstract class ImmutablePrimitiveArrayDeserializerBase<T, A>
    extends StdDeserializer<T>
{
    protected final Class<A> _arrayType;

    protected final ValueDeserializer<A> _arrayDeserializer;

    @SuppressWarnings("unchecked")
    protected ImmutablePrimitiveArrayDeserializerBase(Class<T> type, Class<A> arrayType) {
        super(type);
        _arrayType = arrayType;
        _arrayDeserializer = (ValueDeserializer<A>) PrimitiveArrayDeserializers.forType(
                arrayType.getComponentType());
    }

    protected ImmutablePrimitiveArrayDeserializerBase(ImmutablePrimitiveArrayDeserializerBase<T, A> src,
            ValueDeserializer<A> arrayDeserializer) {
        super(src);
        _arrayType = src._arrayType;
        _arrayDeserializer = arrayDeserializer;
    }

    /*
    /**********************************************************************
    /* Abstract methods for sub-classes
    /**********************************************************************
     */

    protected abstract ImmutablePrimitiveArrayDeserializerBase<T, A> _withDelegate(
            ValueDeserializer<A> arrayDeserializer);

    protected abstract T _fromArray(A array);

    @Override
    public abstract T getEmptyValue(DeserializationContext ctxt);

    /*
    /**********************************************************************
    /* Contextualization
    /**********************************************************************
     */

    @SuppressWarnings("unchecked")
    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property)
    {
        ValueDeserializer<?> deser = ctxt.handleSecondaryContextualization(_arrayDeserializer,
                property, ctxt.constructType(_arrayType));
        if (deser == _arrayDeserializer) {
            return this;
        }
        return _withDelegate((ValueDeserializer<A>) deser);
    }

    /*
    /**********************************************************************
    /* Metadata
    /**********************************************************************
     */

    @Override
    public LogicalType logicalType() {
        return LogicalType.Array;
    }

    @Override
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.FALSE;
    }

    @Override
    public boolean isCachable() {
        return true;
    }

    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    /*
    /**********************************************************************
    /* Deserialization
    /**********************************************************************
     */

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) {
        return _fromArray(_arrayDeserializer.deserialize(p, ctxt));
    }
}
