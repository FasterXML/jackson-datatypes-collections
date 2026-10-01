package tools.jackson.datatype.fastutil.deser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Describes a family of fastutil container types that share the same element type
 * (like {@code IntCollection}, {@code IntList}, {@code IntSet}...) or the same key and
 * value types (like {@code Int2LongMap}, {@code Int2LongSortedMap}), and knows how to
 * construct an instance of any concrete or abstract member of that family during
 * deserialization.
 *<p>
 * Resolution rules, given the requested type:
 *<ol>
 * <li>One of the registered interfaces: the registered default implementation is used
 *   (for example {@code IntArrayList} for {@code IntList})</li>
 * <li>A concrete type with a public no-arguments constructor
 *   (for example {@code IntLinkedOpenHashSet}) is instantiated directly</li>
 * <li>Unmodifiable, synchronized, singleton and empty wrappers nested in fastutil
 *   helper classes (for example {@code IntLists.UnmodifiableList}, as found in type ids
 *   written with polymorphic typing) are rebuilt by deserializing into an
 *   order-preserving implementation and wrapping it using the matching
 *   {@code unmodifiable(...)} or {@code synchronize(...)} factory method</li>
 * <li>Concrete types with a public constructor that takes a member of the family
 *   (for example {@code IntImmutableList(IntList)}) are built from an
 *   order-preserving implementation</li>
 * <li>Other abstract types are mapped to the first registered default implementation
 *   that is assignable to them</li>
 *</ol>
 *
 * @param <C> Base type of all containers in the family
 */
public final class ContainerFamily<C>
{
    private final static String FASTUTIL_PACKAGE_PREFIX = "it.unimi.dsi.fastutil.";

    private final Class<?> _baseType;

    // Most specific interfaces first
    private final List<Kind<C>> _kinds = new ArrayList<>();

    public ContainerFamily(Class<?> baseType) {
        _baseType = baseType;
    }

    /**
     * Registers an interface of this family; interfaces need to be registered from
     * the most specific to the least specific one.
     *
     * @param iface Interface type
     * @param defaultImpl Factory for the implementation to use when deserializing
     *    into {@code iface}
     * @param orderedImpl Factory for an implementation that preserves insertion order,
     *    used as the intermediate value when deserializing into wrappers
     */
    public ContainerFamily<C> add(Class<?> iface,
            Supplier<? extends C> defaultImpl, Supplier<? extends C> orderedImpl) {
        _kinds.add(new Kind<>(iface, defaultImpl, orderedImpl));
        return this;
    }

    public Class<?> getBaseType() {
        return _baseType;
    }

    public boolean handles(Class<?> rawType) {
        return _baseType.isAssignableFrom(rawType);
    }

    /**
     * @return Creator for given type, if it belongs to this family and can be
     *   constructed; {@code null} otherwise
     */
    public Creator<C> findCreator(Class<?> rawType)
    {
        if (!handles(rawType)) {
            return null;
        }
        for (Kind<C> kind : _kinds) {
            if (kind.iface == rawType) {
                return new Creator<>(kind.defaultImpl, null);
            }
        }
        if (rawType.isInterface() || Modifier.isAbstract(rawType.getModifiers())) {
            for (Kind<C> kind : _kinds) {
                if (rawType.isInstance(kind.defaultImpl.get())) {
                    return new Creator<>(kind.defaultImpl, null);
                }
            }
            return null;
        }
        // Wrappers are created using factory methods of their (public) enclosing
        // class, so wrapper classes themselves need not be public
        Creator<C> creator = _findWrapperCreator(rawType);
        if (creator == null && Modifier.isPublic(rawType.getModifiers())) {
            creator = _findDefaultConstructorCreator(rawType);
            if (creator == null) {
                creator = _findCopyConstructorCreator(rawType);
            }
        }
        return creator;
    }

    @SuppressWarnings("unchecked")
    private Creator<C> _findDefaultConstructorCreator(Class<?> rawType)
    {
        final Constructor<?> ctor;
        try {
            ctor = rawType.getConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
        return new Creator<>(() -> {
            try {
                return (C) ctor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw _creationFailure(rawType, e);
            }
        }, null);
    }

    @SuppressWarnings("unchecked")
    private Creator<C> _findWrapperCreator(Class<?> rawType)
    {
        Class<?> enclosing = rawType.getEnclosingClass();
        if (enclosing == null || !enclosing.getName().startsWith(FASTUTIL_PACKAGE_PREFIX)) {
            return null;
        }
        final String simpleName = rawType.getSimpleName();
        final String factoryName;
        if (simpleName.startsWith("Synchronized")) {
            factoryName = "synchronize";
        } else if (simpleName.startsWith("Unmodifiable")
                || simpleName.startsWith("Singleton")
                || simpleName.startsWith("Empty")) {
            factoryName = "unmodifiable";
        } else {
            return null;
        }
        for (Kind<C> kind : _kinds) {
            if (!kind.iface.isAssignableFrom(rawType)) {
                continue;
            }
            final Method factory;
            try {
                factory = enclosing.getMethod(factoryName, kind.iface);
            } catch (NoSuchMethodException e) {
                continue;
            }
            if (!Modifier.isStatic(factory.getModifiers())) {
                continue;
            }
            return new Creator<>(kind.orderedImpl, value -> {
                try {
                    return (C) factory.invoke(null, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw _creationFailure(rawType, e);
                }
            });
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Creator<C> _findCopyConstructorCreator(Class<?> rawType)
    {
        for (Kind<C> kind : _kinds) {
            if (!kind.iface.isAssignableFrom(rawType)) {
                continue;
            }
            Constructor<?> ctor;
            try {
                ctor = rawType.getConstructor(kind.iface);
            } catch (NoSuchMethodException e) {
                try {
                    ctor = rawType.getConstructor(_baseType);
                } catch (NoSuchMethodException e2) {
                    continue;
                }
            }
            final Constructor<?> copyCtor = ctor;
            return new Creator<>(kind.orderedImpl, value -> {
                try {
                    return (C) copyCtor.newInstance(value);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw _creationFailure(rawType, e);
                }
            });
        }
        return null;
    }

    private static IllegalStateException _creationFailure(Class<?> rawType, Exception e) {
        Throwable t = (e instanceof InvocationTargetException) ? e.getCause() : e;
        return new IllegalStateException("Failed to create instance of "
                + rawType.getName() + ": " + t, t);
    }

    private static final class Kind<C>
    {
        final Class<?> iface;
        final Supplier<? extends C> defaultImpl;
        final Supplier<? extends C> orderedImpl;

        Kind(Class<?> iface, Supplier<? extends C> defaultImpl, Supplier<? extends C> orderedImpl) {
            this.iface = iface;
            this.defaultImpl = defaultImpl;
            this.orderedImpl = orderedImpl;
        }
    }

    /**
     * Creates the intermediate container to populate, and converts the populated
     * container into the final value.
     */
    public static final class Creator<C>
    {
        private final Supplier<? extends C> _intermediate;
        // null if the intermediate value is returned as-is
        private final UnaryOperator<C> _finish;

        Creator(Supplier<? extends C> intermediate, UnaryOperator<C> finish) {
            _intermediate = intermediate;
            _finish = finish;
        }

        public C create() {
            return _intermediate.get();
        }

        public C finish(C intermediate) {
            return (_finish == null) ? intermediate : _finish.apply(intermediate);
        }
    }
}
