package tools.jackson.datatype.guava;

import com.google.common.collect.BoundType;

import tools.jackson.core.Version;

import tools.jackson.databind.JacksonModule;
import tools.jackson.datatype.guava.ser.GuavaBeanSerializerModifier;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Basic Jackson {@link tools.jackson.databind.JacksonModule} that adds support for Guava types.
 *<p>
 * Current configurability includes:
 *<ul>
 * <li><code>configureAbsentsAsNulls</code> (default: <code>false</code>):
 *    Determines whether inclusion strategy of <code>NON_NULL</code> should additionally consider
 *    <code>Optional.absent()</code> values (as POJO properties) to be excluded; if true, they will
 *     be excluded, if false, they will be included.
 *     Note that the defaults for other "Optional" types are different; Guava setting is chosen solely
 *     for backwards compatibility.
 *  </li>
 * <li><code>configureMultisetsAsEntries</code> (default: <code>true</code>):
 *    Determines whether <code>Multiset</code>s are serialized as a JSON Array of
 *    <code>{"element":...,"count":...}</code> entries (if true), or by repeating each
 *    element as many times as it occurs (if false).
 *    Note that a <code>Multiset</code> held in a property of broader declared type
 *    (like <code>Collection</code> or <code>Object</code>) is still written as entries,
 *    but cannot be read back into that declared type; disable to keep the old format.
 *  </li>
 * <li><code>configureMaxMultisetSize</code> (default: <code>10_000_000</code>):
 *    Maximum number of elements (sum of counts) allowed when deserializing a
 *    <code>Multiset</code> from entries.
 *  </li>
 *</ul>
 */
public class GuavaModule extends JacksonModule // can't use just SimpleModule, due to generic types
    implements java.io.Serializable
{
    /**
     * Default maximum size (sum of element counts) of a
     * {@link com.google.common.collect.Multiset} read from entries.
     *
     * This default can be overridden with {@link #configureMaxMultisetSize(int)}.
     *
     * @since 3.3
     */
    public final static int DEFAULT_MAX_MULTISET_SIZE = 10_000_000;

    private static final long serialVersionUID = 3L;

    private final String NAME = "GuavaModule";

    /**
     * Configuration setting that determines whether `Optional.absent()` is
     * considered "same as null" for serialization purposes; that is, to be
     * filtered same as nulls are.
     * If enabled, absent values are treated like nulls; if disabled, they are not.
     * In either case, absent values are always considered "empty".
     *<p>
     * Default value is `false` (NOTE: in 2.x it defaulted to `true`)
     *<p>
     * Note that this setting MUST be changed BEFORE registering the module:
     * changes after registration will have no effect.
     */
    protected boolean _cfgHandleAbsentAsNull = false;

    /**
     * Configuration setting that determines whether {@link com.google.common.collect.Multiset}s
     * are serialized as a JSON Array of entries ({@code [{"element":"a","count":2}]});
     * if disabled, each element is repeated as many times as it occurs
     * ({@code ["a","a"]}). Same format is expected when deserializing.
     *<p>
     * Default value is {@code true}.
     */
    protected boolean _cfgMultisetsAsEntries = true;

    /**
     * Configuration setting that determines maximum number of elements (sum of counts)
     * allowed when deserializing a {@link com.google.common.collect.Multiset} from entries:
     * since a single entry may specify a large count, a small input could otherwise
     * produce a huge {@code Multiset}.
     *<p>
     * Default value is {@link #DEFAULT_MAX_MULTISET_SIZE}.
     */
    protected int _cfgMaxMultisetSize = DEFAULT_MAX_MULTISET_SIZE;

    protected BoundType _defaultBoundType;
    
    public GuavaModule() {
        super();
    }

    @Override public String getModuleName() { return NAME; }
    @Override public Version version() { return PackageVersion.VERSION; }
    
    @Override
    public void setupModule(SetupContext context)
    {
        context.addDeserializers(new GuavaDeserializers(_defaultBoundType, _cfgMultisetsAsEntries,
                _cfgMaxMultisetSize));
        context.addKeyDeserializers(new GuavaKeyDeserializers());
        context.addSerializers(new GuavaSerializers(_cfgMultisetsAsEntries));
        context.addTypeModifier(new GuavaTypeModifier());

        // 28-Apr-2015, tatu: Allow disabling "treat Optional.absent() like Java nulls"
        if (_cfgHandleAbsentAsNull) {
            context.addSerializerModifier(new GuavaBeanSerializerModifier());
        }
    }

    /**
     * Configuration method that may be used to change configuration setting
     * <code>_cfgHandleAbsentAsNull</code>: enabling means that `Optional.absent()` values
     * are handled like Java nulls (wrt filtering on serialization); disabling that
     * they are only treated as "empty" values, but not like native Java nulls.
     * Recommended setting for this value is `false`, for compatibility with other
     * "optional" values (like JDK 8 optionals); but the default is `true` for
     * backwards compatibility.
     * 
     * @return This module instance, useful for chaining calls
     */
    public GuavaModule configureAbsentsAsNulls(boolean state) {
        _cfgHandleAbsentAsNull = state;
        return this;
    }

    /**
     * Configuration method that may be used to change configuration setting
     * <code>_cfgMultisetsAsEntries</code>: enabling (default) means that
     * {@link com.google.common.collect.Multiset}s are serialized as entries
     * of element and count; disabling that each element is repeated as many
     * times as it occurs (format used before 3.3).
     *<p>
     * Note that format used for serialization depends on the actual type of value,
     * but for deserialization on the declared type: so a {@code Multiset} held in a
     * property declared as, for example, {@code Collection<String>} is written as
     * entries but cannot be read back into that type. Disable this setting to keep
     * the old format in such cases.
     *
     * @return This module instance, useful for chaining calls
     *
     * @since 3.3
     */
    public GuavaModule configureMultisetsAsEntries(boolean state) {
        _cfgMultisetsAsEntries = state;
        return this;
    }

    /**
     * Configuration method that may be used to change configuration setting
     * <code>_cfgMaxMultisetSize</code>: maximum number of elements (sum of counts)
     * allowed when deserializing a {@link com.google.common.collect.Multiset}
     * from entries.
     *
     * @param maxSize Maximum number of elements; must be positive
     *
     * @return This module instance, useful for chaining calls
     *
     * @since 3.3
     */
    public GuavaModule configureMaxMultisetSize(int maxSize) {
        checkArgument(maxSize > 0, "maxSize must be positive, was %s", maxSize);
        _cfgMaxMultisetSize = maxSize;
        return this;
    }

    /**
     * Configuration method that may be used to change the {@link BoundType} to be used
     * when deserializing {@link com.google.common.collect.Range} objects. This configuration
     * will is used when the object to be deserialized has no bound type attribute.
     * The default {@link BoundType} is CLOSED.
     *
     * @param boundType {@link BoundType}
     *
     * @return This module instance, useful for chaining calls
     */
    public GuavaModule defaultBoundType(BoundType boundType) {
        checkNotNull(boundType);
        _defaultBoundType = boundType;
        return this;
    }
}
