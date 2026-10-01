package tools.jackson.datatype.eclipsecollections.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.util.AccessPattern;
import tools.jackson.databind.util.ClassUtil;

/**
 * Deserializer used for concrete Eclipse Collections implementation types
 * (like {@code IntIntHashMap}, or {@code ImmutableTripletonList} named by a
 * polymorphic type id): delegates to the deserializer of the most specific supported
 * interface the type implements, and verifies that the result is an instance
 * of the requested implementation type.
 */
public class ImplementationTypeDeserializer extends StdDeserializer<Object>
{
    protected final Class<?> _implType;

    protected final ValueDeserializer<?> _delegatee;

    public ImplementationTypeDeserializer(Class<?> implType, ValueDeserializer<?> delegatee) {
        super(implType);
        _implType = implType;
        _delegatee = delegatee;
    }

    @Override
    public void resolve(DeserializationContext ctxt) {
        _delegatee.resolve(ctxt);
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        ValueDeserializer<?> delegatee = _delegatee.createContextual(ctxt, property);
        if (delegatee == _delegatee) {
            return this;
        }
        return new ImplementationTypeDeserializer(_implType, delegatee);
    }

    @Override
    public ValueDeserializer<?> getDelegatee() {
        return _delegatee;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        return _verify(ctxt, _delegatee.deserialize(p, ctxt));
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        return _verify(ctxt, _delegatee.deserializeWithType(p, ctxt, typeDeserializer));
    }

    @Override
    public Object getNullValue(DeserializationContext ctxt) {
        return _delegatee.getNullValue(ctxt);
    }

    @Override
    public AccessPattern getNullAccessPattern() {
        return _delegatee.getNullAccessPattern();
    }

    @Override
    public LogicalType logicalType() {
        return _delegatee.logicalType();
    }

    @Override
    public boolean isCachable() {
        return _delegatee.isCachable();
    }

    protected Object _verify(DeserializationContext ctxt, Object value)
        throws JacksonException
    {
        if (value == null || _implType.isInstance(value)) {
            return value;
        }
        return ctxt.reportInputMismatch(_implType,
                "Cannot deserialize value of type %s: closest supported type produces values of type %s",
                ClassUtil.nameOf(_implType), ClassUtil.classNameOf(value));
    }
}
