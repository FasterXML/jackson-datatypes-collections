package tools.jackson.datatype.fastutil;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Reflection-based helpers to enumerate and instantiate fastutil container types,
 * used to cover all element (and key/value) type combinations in tests.
 */
final class FastutilTypes
{
    static final List<String> PRIMITIVES = Arrays.asList(
            "Boolean", "Byte", "Short", "Char", "Int", "Long", "Float", "Double");

    static final List<String> MAP_KEYS = Arrays.asList(
            "Byte", "Short", "Char", "Int", "Long", "Float", "Double", "Object");

    static final List<String> MAP_VALUES = Arrays.asList(
            "Boolean", "Byte", "Short", "Char", "Int", "Long", "Float", "Double", "Object");

    static final List<String> COLLECTION_SUFFIXES = Arrays.asList(
            "Collection", "List", "Set", "SortedSet", "BigList",
            "ArrayList", "ImmutableList", "BigArrayBigList",
            "OpenHashSet", "LinkedOpenHashSet", "ArraySet", "RBTreeSet", "AVLTreeSet", "OpenHashBigSet");

    static final List<String> MAP_SUFFIXES = Arrays.asList(
            "Map", "SortedMap",
            "OpenHashMap", "LinkedOpenHashMap", "ArrayMap", "RBTreeMap", "AVLTreeMap");

    private FastutilTypes() { }

    static String packageName(String type) {
        switch (type) {
        case "Boolean": return "it.unimi.dsi.fastutil.booleans";
        case "Byte": return "it.unimi.dsi.fastutil.bytes";
        case "Short": return "it.unimi.dsi.fastutil.shorts";
        case "Char": return "it.unimi.dsi.fastutil.chars";
        case "Int": return "it.unimi.dsi.fastutil.ints";
        case "Long": return "it.unimi.dsi.fastutil.longs";
        case "Float": return "it.unimi.dsi.fastutil.floats";
        case "Double": return "it.unimi.dsi.fastutil.doubles";
        case "Object": return "it.unimi.dsi.fastutil.objects";
        default: throw new IllegalArgumentException(type);
        }
    }

    /**
     * @return Class with given name, or {@code null} if fastutil has no such type
     *    (for example {@code BooleanSortedSet})
     */
    static Class<?> findClass(String packageOf, String simpleName) {
        try {
            return Class.forName(packageName(packageOf) + "." + simpleName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    static Class<?> collectionClass(String primitive, String suffix) {
        return findClass(primitive, primitive + suffix);
    }

    static Class<?> mapClass(String key, String value, String suffix) {
        return findClass(key, key + "2" + value + suffix);
    }

    /**
     * @return Distinct sample values for given element type, in insertion order
     */
    static List<Object> sampleValues(String type) {
        switch (type) {
        case "Boolean": return Arrays.asList(true, false);
        case "Byte": return Arrays.asList((byte) 3, (byte) -128, (byte) 127);
        case "Short": return Arrays.asList((short) 3, Short.MIN_VALUE, Short.MAX_VALUE);
        case "Char": return Arrays.asList('x', 'a', 'é');
        case "Int": return Arrays.asList(3, Integer.MIN_VALUE, Integer.MAX_VALUE);
        case "Long": return Arrays.asList(3L, Long.MIN_VALUE, Long.MAX_VALUE);
        case "Float": return Arrays.asList(3.5f, -0.25f, 1e10f);
        case "Double": return Arrays.asList(3.5d, -0.25d, 1e100d);
        case "Object": return Arrays.asList("x", "a", "b");
        default: throw new IllegalArgumentException(type);
        }
    }

    /**
     * @return Implementation to use for creating an instance of given type
     */
    static Class<?> implementationFor(Class<?> type) {
        if (!type.isInterface()) {
            return type;
        }
        String name = type.getSimpleName();
        String pkg = type.getPackage().getName();
        String impl;
        if (name.endsWith("SortedMap")) {
            impl = name.replace("SortedMap", "RBTreeMap");
        } else if (name.endsWith("Map")) {
            impl = name.replace("Map", "OpenHashMap");
        } else if (name.endsWith("SortedSet")) {
            impl = name.replace("SortedSet", "RBTreeSet");
        } else if (name.endsWith("Set")) {
            impl = name.replace("Set", "OpenHashSet");
        } else if (name.endsWith("BigList")) {
            impl = name.replace("BigList", "BigArrayBigList");
        } else if (name.endsWith("List")) {
            impl = name.replace("List", "ArrayList");
        } else if (name.endsWith("Collection")) {
            impl = name.replace("Collection", "ArrayList");
        } else {
            throw new IllegalArgumentException(name);
        }
        try {
            return Class.forName(pkg + "." + impl);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("unchecked")
    static Collection<Object> newCollection(Class<?> type, List<Object> values) throws Exception {
        Class<?> impl = implementationFor(type);
        if (impl.getSimpleName().endsWith("ImmutableList")) {
            Constructor<?> ctor = impl.getConstructor(Collection.class);
            return (Collection<Object>) ctor.newInstance(values);
        }
        Collection<Object> coll = (Collection<Object>) impl.getConstructor().newInstance();
        coll.addAll(values);
        return coll;
    }

    @SuppressWarnings("unchecked")
    static Map<Object, Object> newMap(Class<?> type, List<Object> keys, List<Object> values)
        throws Exception
    {
        Class<?> impl = implementationFor(type);
        Map<Object, Object> map = (Map<Object, Object>) impl.getConstructor().newInstance();
        for (int i = 0; i < keys.size(); ++i) {
            map.put(keys.get(i), values.get(i % values.size()));
        }
        return map;
    }
}
