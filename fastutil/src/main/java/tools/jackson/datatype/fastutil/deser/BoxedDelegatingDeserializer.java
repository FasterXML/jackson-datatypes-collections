package tools.jackson.datatype.fastutil.deser;

import java.util.Collection;
import java.util.Map;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.AccessPattern;
import tools.jackson.databind.util.ClassUtil;
import tools.jackson.datatype.fastutil.util.Merging;

/**
 * Deserializer that reads content as another container type (with boxed keys and
 * values), using the deserializer Jackson finds for that type, and then copies it
 * into the fastutil container to return. Used where handling of individual primitive
 * values is not enough: custom deserializers for primitive elements, keys or values
 * (like {@code @JsonDeserialize(contentUsing = ...)} for an {@code IntList}), which
 * produce boxed values. Content is read as an {@code ArrayList} or {@code LinkedHashMap}
 * by standard Jackson deserializers, which use them.
 *
 * @param <C> Type of container returned
 */
public final class BoxedDelegatingDeserializer<C> extends StdDeserializer<C>
{
    private final JavaType _delegateType;
    private final ContainerFamily.Creator<C> _creator;

    // Primitive types of map keys, and of map values or collection elements, if
    // primitive; null for reference types (which may be null)
    private final Class<?> _primitiveKeyType, _primitiveValueType;

    // null until contextualized
    private final ValueDeserializer<Object> _delegate;

    /**
     * @param delegateType Type to read content as: {@code ArrayList} or
     *    {@code LinkedHashMap} with boxed element (or key and value) types
     * @param primitiveKeys Whether map keys are primitive (cannot be null)
     * @param primitiveValues Whether map values or collection elements are primitive
     */
    public BoxedDelegatingDeserializer(JavaType type, JavaType delegateType,
            ContainerFamily.Creator<C> creator, boolean primitiveKeys, boolean primitiveValues) {
        this(type, delegateType, creator,
                primitiveKeys ? ClassUtil.primitiveType(delegateType.getKeyType().getRawClass()) : null,
                primitiveValues ? ClassUtil.primitiveType(delegateType.getContentType().getRawClass()) : null,
                null);
    }

    private BoxedDelegatingDeserializer(JavaType type, JavaType delegateType,
            ContainerFamily.Creator<C> creator, Class<?> primitiveKeyType, Class<?> primitiveValueType,
            ValueDeserializer<Object> delegate) {
        super(type);
        _delegateType = delegateType;
        _creator = creator;
        _primitiveKeyType = primitiveKeyType;
        _primitiveValueType = primitiveValueType;
        _delegate = delegate;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    {
        ValueDeserializer<Object> delegate = ctxt.findContextualValueDeserializer(_delegateType, property);
        if (delegate == _delegate) {
            return this;
        }
        return new BoxedDelegatingDeserializer<>(getValueType(), _delegateType, _creator,
                _primitiveKeyType, _primitiveValueType, delegate);
    }

    @Override
    public C deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        Object values = _delegate.deserialize(p, ctxt);
        // delegate may coerce input to null (like empty String, if so configured)
        if (values == null) {
            return null;
        }
        C result = _creator.create(ctxt);
        _copy(ctxt, values, result);
        return _creator.finish(ctxt, result);
    }

    @Override
    public C deserialize(JsonParser p, DeserializationContext ctxt, C intoValue) throws JacksonException {
        Object values = _delegate.deserialize(p, ctxt);
        if (values != null) {
            // copy directly into existing value (no need to create a new one)
            try {
                _copy(ctxt, values, intoValue);
            } catch (UnsupportedOperationException e) {
                throw Merging.failure(ctxt, intoValue, e);
            }
        }
        return intoValue;
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        // Delegate knows the expected shape (JSON Array or Object)
        return _delegate.deserializeWithType(p, ctxt, typeDeserializer);
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) {
        return _creator.createEmpty(ctxt);
    }

    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }

    @Override
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.TRUE;
    }

    @Override
    public LogicalType logicalType() {
        return _delegateType.isMapLikeType() ? LogicalType.Map : LogicalType.Collection;
    }

    @SuppressWarnings("unchecked")
    private void _copy(DeserializationContext ctxt, Object source, C target) throws JacksonException
    {
        // Primitive sides cannot contain nulls, which (custom) deserializers may produce
        if (target instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) source).entrySet()) {
                Object key = entry.getKey();
                if (key == null && _primitiveKeyType != null) {
                    ctxt.reportInputMismatch(this, "Cannot add `null` key (as produced by deserializer) to %s",
                            ClassUtil.getTypeDescription(getValueType()));
                }
                ((Map<Object, Object>) map).put(key, _value(ctxt, entry.getValue()));
            }
        } else {
            for (Object value : (Collection<?>) source) {
                ((Collection<Object>) target).add(_value(ctxt, value));
            }
        }
    }

    // Nulls for primitive values handled the same way as without custom deserializers:
    // as specified by `FAIL_ON_NULL_FOR_PRIMITIVES` (default value if not failing)
    private Object _value(DeserializationContext ctxt, Object value) throws JacksonException {
        if (value == null && _primitiveValueType != null) {
            _verifyNullForPrimitive(ctxt);
            return ClassUtil.defaultValue(_primitiveValueType);
        }
        return value;
    }
}
