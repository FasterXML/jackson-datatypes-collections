package tools.jackson.datatype.eclipsecollections.deser.pair;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.BeanDescription;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.util.ClassUtil;
import tools.jackson.datatype.eclipsecollections.util.ImplementationTypes;

/**
 * Handling of concrete Eclipse Collections tuple implementation classes (like
 * {@code PairImpl} or {@code IntLongPairImpl}, as named by polymorphic type ids):
 * use instantiator of the most specific supported tuple interface, verifying that
 * it produces instances of the requested implementation class.
 */
final class TupleImplementations
{
    private TupleImplementations() { }

    /**
     * @param implType Type of the (possible) implementation class
     * @param tupleTypes Supported tuple interface types
     * @param finder Function for finding instantiator for given supported tuple
     *    interface (and matching parameterized type)
     *
     * @return Instantiator to use, if any; {@code null} if type is not handled
     */
    static ValueInstantiator findInstantiator(JavaType implType,
            Collection<Class<?>> tupleTypes,
            BiFunction<Class<?>, JavaType, ValueInstantiator> finder)
    {
        final Class<?> implClass = implType.getRawClass();
        if (!ImplementationTypes.isEclipseImplementationClass(implClass)) {
            return null;
        }
        List<Class<?>> supportedTypes = ImplementationTypes.mostSpecificTypes(implClass, tupleTypes);
        if (supportedTypes.isEmpty()) {
            return null;
        }
        if (supportedTypes.size() > 1) {
            return new Unsupported(implType, String.format(
                    "Cannot deserialize value of type %s: implements multiple unrelated supported types (%s)",
                    ClassUtil.nameOf(implClass), supportedTypes.stream()
                        .map(ClassUtil::nameOf)
                        .collect(Collectors.joining(", "))));
        }
        JavaType supportedType = implType.findSuperType(supportedTypes.get(0));
        if (supportedType == null) {
            return null;
        }
        ValueInstantiator inst = finder.apply(supportedTypes.get(0), supportedType);
        if (inst == null) {
            return null;
        }
        return new Verifying(implClass, inst);
    }

    /**
     * Instantiator that verifies that the delegate produces instances of
     * the requested implementation class.
     */
    static class Verifying extends ValueInstantiator.Delegating
    {
        private static final long serialVersionUID = 1L;

        protected final Class<?> _implClass;

        Verifying(Class<?> implClass, ValueInstantiator delegate) {
            super(delegate);
            _implClass = implClass;
        }

        @Override
        public ValueInstantiator createContextual(DeserializationContext ctxt,
                BeanDescription.Supplier beanDescRef)
        {
            ValueInstantiator d = _delegate.createContextual(ctxt, beanDescRef);
            return (d == _delegate) ? this : new Verifying(_implClass, d);
        }

        @Override
        public Object createFromObjectWith(DeserializationContext ctxt, Object[] args)
            throws JacksonException
        {
            Object value = super.createFromObjectWith(ctxt, args);
            if (value == null || _implClass.isInstance(value)) {
                return value;
            }
            return ctxt.reportInputMismatch(_implClass,
                    "Cannot deserialize value of type %s: closest supported type produces values of type %s",
                    ClassUtil.nameOf(_implClass), ClassUtil.classNameOf(value));
        }
    }

    /**
     * Instantiator used for implementation classes that cannot be supported:
     * fails with an explanation (instead of falling back to plain bean handling).
     */
    static class Unsupported extends ValueInstantiator.Base
    {
        private final JavaType _type;
        private final String _message;

        Unsupported(JavaType type, String message) {
            super(type);
            _type = type;
            _message = message;
        }

        @Override
        public boolean canCreateUsingDefault() {
            return true;
        }

        @Override
        public Object createUsingDefault(DeserializationContext ctxt) throws JacksonException {
            return ctxt.reportBadDefinition(_type, _message);
        }
    }
}
