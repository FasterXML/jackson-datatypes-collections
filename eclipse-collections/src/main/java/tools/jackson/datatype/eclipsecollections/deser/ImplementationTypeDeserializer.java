package tools.jackson.datatype.eclipsecollections.deser;

import java.util.LinkedHashSet;
import java.util.Set;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.std.DelegatingDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.util.ClassUtil;

/**
 * Deserializer used for concrete Eclipse Collections implementation types
 * (like {@code IntIntHashMap}, or {@code ImmutableTripletonList} named by a
 * polymorphic type id): delegates to the deserializer of the most specific supported
 * interface the type implements, and verifies that the result is compatible
 * with the requested implementation type.
 *<p>
 * Implementation classes cannot generally be constructed directly, and the class
 * produced depends on content (for example, {@code ImmutableList} with 2 elements
 * is always produced as {@code ImmutableDoubletonList}, even if serialized
 * from an {@code ImmutableArrayList}). So result is accepted if it is either an
 * instance of the requested type, or implements all Eclipse Collections API
 * interfaces the requested type implements: this way it can be assigned to any
 * property declared using API types.
 */
public class ImplementationTypeDeserializer extends DelegatingDeserializer
{
    protected final Class<?> _implType;

    /**
     * Eclipse Collections API interfaces implemented by {@link #_implType}
     */
    protected final Class<?>[] _apiTypes;

    public ImplementationTypeDeserializer(Class<?> implType, ValueDeserializer<?> delegatee) {
        this(implType, delegatee, _findApiTypes(implType));
    }

    protected ImplementationTypeDeserializer(Class<?> implType, ValueDeserializer<?> delegatee,
            Class<?>[] apiTypes) {
        super(delegatee);
        _implType = implType;
        _apiTypes = apiTypes;
    }

    @Override
    protected ValueDeserializer<?> newDelegatingInstance(ValueDeserializer<?> newDelegatee) {
        return new ImplementationTypeDeserializer(_implType, newDelegatee, _apiTypes);
    }

    @Override
    public Class<?> handledType() {
        return _implType;
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        return _verify(ctxt, super.deserialize(p, ctxt));
    }

    @Override
    public Object deserialize(JsonParser p, DeserializationContext ctxt, Object intoValue)
        throws JacksonException
    {
        return _verify(ctxt, super.deserialize(p, ctxt, intoValue));
    }

    @Override
    public Object deserializeWithType(JsonParser p, DeserializationContext ctxt,
            TypeDeserializer typeDeserializer)
        throws JacksonException
    {
        return _verify(ctxt, super.deserializeWithType(p, ctxt, typeDeserializer));
    }

    protected Object _verify(DeserializationContext ctxt, Object value)
        throws JacksonException
    {
        if (value == null || _implType.isInstance(value)) {
            return value;
        }
        boolean compatible = true;
        for (Class<?> apiType : _apiTypes) {
            if (!apiType.isInstance(value)) {
                compatible = false;
                break;
            }
        }
        if (compatible) {
            return value;
        }
        return ctxt.reportInputMismatch(_implType,
                "Cannot deserialize value of type %s: closest supported type produces values of type %s",
                ClassUtil.nameOf(_implType), ClassUtil.classNameOf(value));
    }

    private static Class<?>[] _findApiTypes(Class<?> implType) {
        Set<Class<?>> result = new LinkedHashSet<>();
        for (Class<?> cls = implType; cls != null; cls = cls.getSuperclass()) {
            _addApiTypes(cls, result);
        }
        return result.toArray(new Class<?>[0]);
    }

    private static void _addApiTypes(Class<?> cls, Set<Class<?>> result) {
        for (Class<?> iface : cls.getInterfaces()) {
            if (iface.getName().startsWith("org.eclipse.collections.api.")) {
                result.add(iface);
            }
            _addApiTypes(iface, result);
        }
    }
}
