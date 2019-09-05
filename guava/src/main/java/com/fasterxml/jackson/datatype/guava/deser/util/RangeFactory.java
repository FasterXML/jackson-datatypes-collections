
package com.fasterxml.jackson.datatype.guava.deser.util;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;

/**
 * A factory for creating Guava {@link Range}s that is compatible with Guava 14 and later.
 *<p>
 * NOTE: up until Jackson 2.9.x, supported versions from Guava 10 and higher; support for
 * older versions dropped in Jackson 2.10.
 */
public class RangeFactory
{
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
        return Range.range(lowerEndpoint, lowerBoundType, upperEndpoint, upperBoundType);
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
        return Range.downTo(lowerEndpoint, lowerBoundType);
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
        return Range.upTo(upperEndpoint, upperBoundType);
    }

    public static <C extends Comparable<?>> Range<C> all()
    {
        return Range.all();
    }

    public static <C extends Comparable<?>> Range<C> singleton(final C value)
    {
        return Range.singleton(value);
    }

    // prevent instantiation
    private RangeFactory() { }
}
