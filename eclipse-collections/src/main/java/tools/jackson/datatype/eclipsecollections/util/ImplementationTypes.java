package tools.jackson.datatype.eclipsecollections.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Helper methods for resolving concrete Eclipse Collections implementation classes
 * (like {@code IntIntHashMap} or {@code PairImpl}, as named by polymorphic type ids)
 * into supported API interface types. Shared by collection, map and tuple handling.
 *<p>
 * NOTE: only meant for use by this module: package is not exported.
 */
public final class ImplementationTypes
{
    private ImplementationTypes() { }

    /**
     * Only concrete classes of Eclipse Collections itself are considered implementation
     * types: user-defined types implementing Eclipse Collections interfaces are left to
     * standard databind handling (bean deserialization, creators).
     */
    public static boolean isEclipseImplementationClass(Class<?> rawClass) {
        return !rawClass.isInterface()
                && rawClass.getName().startsWith("org.eclipse.collections.");
    }

    /**
     * @return Types from {@code supportedTypes} assignable from given class that are not
     *    supertypes of any other such type: usually exactly one (or none if class is not
     *    supported). Result does not depend on ordering of {@code supportedTypes}.
     */
    public static List<Class<?>> mostSpecificTypes(Class<?> rawClass,
            Collection<Class<?>> supportedTypes) {
        List<Class<?>> candidates = new ArrayList<>();
        for (Class<?> supported : supportedTypes) {
            if (supported.isAssignableFrom(rawClass)) {
                candidates.add(supported);
            }
        }
        List<Class<?>> mostSpecific = new ArrayList<>();
        for (Class<?> candidate : candidates) {
            boolean isMostSpecific = true;
            for (Class<?> other : candidates) {
                if (other != candidate && candidate.isAssignableFrom(other)) {
                    isMostSpecific = false;
                    break;
                }
            }
            if (isMostSpecific) {
                mostSpecific.add(candidate);
            }
        }
        return mostSpecific;
    }
}
