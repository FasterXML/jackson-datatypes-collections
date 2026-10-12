package tools.jackson.datatype.guava.deser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;

/**
 * Comparator used for sorted containers with untyped ({@code Object}) elements:
 * natural ordering, except that {@link Number}s of different types
 * (like {@code Integer}, {@code Long} and {@code Double}, as produced by
 * untyped deserialization) are compared by numeric value.
 * Numerically equal values of different types are ordered by type name, to keep
 * ordering consistent with {@code equals()}.
 *<p>
 * Elements that are not mutually comparable result in {@link ClassCastException},
 * same as with natural ordering.
 */
final class UntypedElementComparator implements Comparator<Object>, java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    public static final UntypedElementComparator INSTANCE = new UntypedElementComparator();

    private UntypedElementComparator() { }

    private Object readResolve() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compare(Object a, Object b)
    {
        if ((a instanceof Number) && (b instanceof Number) && (a.getClass() != b.getClass())) {
            int diff = _compareNumbers((Number) a, (Number) b);
            if (diff != 0) {
                return diff;
            }
            return a.getClass().getName().compareTo(b.getClass().getName());
        }
        return ((Comparable<Object>) a).compareTo(b);
    }

    @SuppressWarnings("unchecked")
    private static int _compareNumbers(Number a, Number b)
    {
        if (_isNonFinite(a) || _isNonFinite(b)) {
            return Double.compare(a.doubleValue(), b.doubleValue());
        }
        BigDecimal bdA = _toBigDecimal(a);
        BigDecimal bdB = _toBigDecimal(b);
        if ((bdA == null) || (bdB == null)) { // unknown Number type: natural ordering
            return ((Comparable<Object>) a).compareTo(b);
        }
        return bdA.compareTo(bdB);
    }

    private static boolean _isNonFinite(Number n) {
        if ((n instanceof Double) || (n instanceof Float)) {
            return !Double.isFinite(n.doubleValue());
        }
        return false;
    }

    private static BigDecimal _toBigDecimal(Number n)
    {
        if (n instanceof BigDecimal) {
            return (BigDecimal) n;
        }
        if (n instanceof BigInteger) {
            return new BigDecimal((BigInteger) n);
        }
        if ((n instanceof Integer) || (n instanceof Long)
                || (n instanceof Short) || (n instanceof Byte)) {
            return BigDecimal.valueOf(n.longValue());
        }
        if ((n instanceof Double) || (n instanceof Float)) {
            // exact conversion, no rounding
            return new BigDecimal(n.doubleValue());
        }
        return null;
    }
}
