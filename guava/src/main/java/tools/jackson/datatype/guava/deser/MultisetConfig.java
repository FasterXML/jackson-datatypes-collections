package tools.jackson.datatype.guava.deser;

import java.io.Serializable;

/**
 * Immutable settings shared by Guava {@code Multiset} deserializers.
 *
 * @since 3.3
 */
public final class MultisetConfig implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * Default maximum size (sum of element counts) of a
     * {@link com.google.common.collect.Multiset} read from entries.
     */
    public static final int DEFAULT_MAX_SIZE = 10_000_000;

    /**
     * Default configuration: entries format, default maximum size.
     */
    public static final MultisetConfig DEFAULT = new MultisetConfig(true,
            DEFAULT_MAX_SIZE);

    private final boolean _asEntries;

    private final int _maxSize;

    /**
     * @param asEntries Whether entries are expected as {@code {"element":...,"count":...}}
     *    (if {@code true}) or as repeated elements (if {@code false})
     * @param maxSize Maximum number of elements (sum of counts) allowed when reading entries;
     *    must be positive
     *
     * @throws IllegalArgumentException if {@code maxSize} is not positive
     */
    public MultisetConfig(boolean asEntries, int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize must be positive, was "+maxSize);
        }
        _asEntries = asEntries;
        _maxSize = maxSize;
    }

    public boolean asEntries() { return _asEntries; }

    public int maxSize() { return _maxSize; }
}
