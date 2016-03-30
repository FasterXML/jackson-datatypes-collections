
package com.fasterxml.jackson.datatype.guava.deser.util;

import com.google.common.collect.BoundType;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * A factory for creating Guava {@link Range}s that is compatible with Guava 10 and later.
 *
 * If Guava 10, 11, 12, or 13 is being used, the factory methods in the com.google.common.collect.Ranges class (a beta
 * class that was removed in Guava 14) are used to reflectively instantiate Ranges. If Guava 14 or later is being used,
 * the factory methods in the Range class itself (added in Guava 14) are used to instantiate Ranges.
 */
public class RangeFactory
{
    private static final String LEGACY_RANGES_CLASS_NAME = "com.google.common.collect.Ranges";

    private static final String LEGACY_RANGE_METHOD_NAME = "range";
    private static final String LEGACY_DOWN_TO_METHOD_NAME = "downTo";
    private static final String LEGACY_UP_TO_METHOD_NAME = "upTo";
    private static final String LEGACY_ALL_METHOD_NAME = "all";

    private static Method legacyRangeMethod;
    private static Method legacyDownToMethod;
    private static Method legacyUpToMethod;
    private static Method legacyAllMethod;

    static
    {
        initLegacyRangeFactoryMethods();
    }

    private static void initLegacyRangeFactoryMethods()
    {
        try {
            Class<?> rangesClass = Class.forName(LEGACY_RANGES_CLASS_NAME);
            legacyRangeMethod = findMethod(rangesClass, LEGACY_RANGE_METHOD_NAME, Comparable.class, BoundType.class,
                                           Comparable.class, BoundType.class);
            legacyDownToMethod = findMethod(rangesClass, LEGACY_DOWN_TO_METHOD_NAME, Comparable.class, BoundType.class);
            legacyUpToMethod = findMethod(rangesClass, LEGACY_UP_TO_METHOD_NAME, Comparable.class, BoundType.class);
            legacyAllMethod = findMethod(rangesClass, LEGACY_ALL_METHOD_NAME);
        } catch (ClassNotFoundException e) {
            // ignore
        }
    }

    // returns null if the method is not found
    private static Method findMethod(Class<?> clazz, String methodName, Class<?> ... paramTypes)
    {
        try {
            return clazz.getMethod(methodName, paramTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <C extends Comparable<?>> Range<C> open(C lowerEndpoint, C upperEndpoint)
    {
        return range(lowerEndpoint, BoundType.OPEN, upperEndpoint, BoundType.OPEN);
    }

    public static <C extends Comparable<?>> Range<C> openClosed(C lowerEndpoint, C upperEndpoint)
    {
        return range(lowerEndpoint, BoundType.OPEN, upperEndpoint, BoundType.CLOSED);
    }

    public static <C extends Comparable<?>> Range<C> closedOpen(C lowerEndpoint, C upperEndpoint)
    {
        return range(lowerEndpoint, BoundType.CLOSED, upperEndpoint, BoundType.OPEN);
    }

    public static <C extends Comparable<?>> Range<C> closed(C lowerEndpoint, C upperEndpoint)
    {
        return range(lowerEndpoint, BoundType.CLOSED, upperEndpoint, BoundType.CLOSED);
    }

    public static <C extends Comparable<?>> Range<C> range(final C lowerEndpoint, final BoundType lowerBoundType,
                           final C upperEndpoint, final BoundType upperBoundType)
    {
        return createRange(new Callable<Range<C>>() {
            @Override
            public Range<C> call() throws Exception {
                return Range.range(lowerEndpoint, lowerBoundType, upperEndpoint, upperBoundType);
            }
        }, legacyRangeMethod, lowerEndpoint, lowerBoundType, upperEndpoint, upperBoundType);
    }

    public static <C extends Comparable<?>> Range<C> greaterThan(C lowerEndpoint)
    {
        return downTo(lowerEndpoint, BoundType.OPEN);
    }

    public static <C extends Comparable<?>> Range<C> atLeast(C lowerEndpoint)
    {
        return downTo(lowerEndpoint, BoundType.CLOSED);
    }

    public static <C extends Comparable<?>> Range<C> downTo(final C lowerEndpoint, final BoundType lowerBoundType)
    {
        return createRange(new Callable<Range<C>>() {
            @Override
            public Range<C> call() throws Exception {
                return Range.downTo(lowerEndpoint, lowerBoundType);
            }
        }, legacyDownToMethod, lowerEndpoint, lowerBoundType);
    }

    public static <C extends Comparable<?>> Range<C> lessThan(C upperEndpoint)
    {
        return upTo(upperEndpoint, BoundType.OPEN);
    }

    public static <C extends Comparable<?>> Range<C> atMost(C upperEndpoint)
    {
        return upTo(upperEndpoint, BoundType.CLOSED);
    }

    public static <C extends Comparable<?>> Range<C> upTo(final C upperEndpoint, final BoundType upperBoundType)
    {
        return createRange(new Callable<Range<C>>() {
            @Override
            public Range<C> call() throws Exception {
                return Range.upTo(upperEndpoint, upperBoundType);
            }
        }, legacyUpToMethod, upperEndpoint, upperBoundType);
    }

    public static <C extends Comparable<?>> Range<C> all()
    {
        return createRange(new Callable<Range<C>>() {
            @Override
            public Range<C> call() throws Exception {
                return Range.all();
            }
        }, legacyAllMethod);
    }

    public static <C extends Comparable<?>> Range<C> singleton(final C value)
    {
        return createRange(new Callable<Range<C>>() {
            @Override
            public Range<C> call() throws Exception {
                return Range.singleton(value);
            }
        }, legacyRangeMethod, value, BoundType.CLOSED, value, BoundType.CLOSED);
    }

    private static <C extends Comparable<?>> Range<C> createRange(Callable<Range<C>> rangeCallable, Method legacyRangeFactoryMethod, Object ... params)
    {
        try {
            return rangeCallable.call();
        } catch (NoSuchMethodError noSuchMethodError) {
            if (legacyRangeFactoryMethod != null) {
                return invokeLegacyRangeFactoryMethod(legacyRangeFactoryMethod, params);
            } else {
                throw noSuchMethodError;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <C extends Comparable<?>> Range<C> invokeLegacyRangeFactoryMethod(Method method, Object... params)
    {
        try {
            //noinspection unchecked
            return (Range<C>) method.invoke(null, params);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke legacy Range factory method [" + method.getName() +
                                               "] with params " + Lists.newArrayList(params) + ".", e);
        }
    }

    // prevent instantiation
    private RangeFactory() { }
}
