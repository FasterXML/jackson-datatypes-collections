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
 * values is not enough:
 *<ul>
 * <li>Custom deserializers for primitive elements, keys or values (like
 *   {@code @JsonDeserialize(contentUsing = ...)} for an {@code IntList}): content
 *   is read as an {@code ArrayList} or {@code LinkedHashMap} by standard Jackson
 *   deserializers, which use them</li>
 * <li>Maps with identity-based reference keys and primitive values (like
 *   {@code Reference2IntMap}), read as the matching {@code Object2IntMap}</li>
 *</ul>
 *
 * @param <C> Type of container returned
 */
public final class BoxedDelegatingDeserializer<C> extends StdDeserializer<C>
{
    private final JavaType _delegateType;
    private final ContainerFamily.Creator<C> _creator;
    // null until contextualized
    private final ValueDeserializer<Object> _delegate;

    public BoxedDelegatingDeserializer(JavaType type, JavaType delegateType,
            ContainerFamily.Creator<C> creator) {
        this(type, delegateType, creator, null);
    }

    private BoxedDelegatingDeserializer(JavaType type, JavaType delegateType,
            ContainerFamily.Creator<C> creator, ValueDeserializer<Object> delegate) {
        super(type);
        _delegateType = delegateType;
        _creator = creator;
        _delegate = delegate;
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    {
        ValueDeserializer<Object> delegate = ctxt.findContextualValueDeserializer(_delegateType, property);
        if (delegate == _delegate) {
            return this;
        }
        return new BoxedDelegatingDeserializer<>(getValueType(), _delegateType, _creator, delegate);
    }

    @Override
    public C deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        C result = _creator.create(ctxt);
        _copy(ctxt, _delegate.deserialize(p, ctxt), result);
        return _creator.finish(ctxt, result);
    }

    @Override
    public C deserialize(JsonParser p, DeserializationContext ctxt, C intoValue) throws JacksonException {
        C values = deserialize(p, ctxt);
        return (intoValue instanceof Map<?, ?>)
                ? Merging.putAll(ctxt, intoValue, (Map<?, ?>) values)
                : Merging.addAll(ctxt, intoValue, (Collection<?>) values);
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
        // Primitive containers cannot contain nulls, which custom deserializers may produce
        if (target instanceof Map<?, ?> map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) source).entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    _reportNull(ctxt);
                }
                ((Map<Object, Object>) map).put(entry.getKey(), entry.getValue());
            }
        } else {
            for (Object value : (Collection<?>) source) {
                if (value == null) {
                    _reportNull(ctxt);
                }
                ((Collection<Object>) target).add(value);
            }
        }
    }

    private void _reportNull(DeserializationContext ctxt) throws JacksonException {
        ctxt.reportInputMismatch(this, "Cannot add `null` (as produced by deserializer) to %s",
                ClassUtil.getTypeDescription(getValueType()));
    }
}
