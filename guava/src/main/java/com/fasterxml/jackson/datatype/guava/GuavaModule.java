package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.guava.deser.RangeKeyDeserializer;
import com.fasterxml.jackson.datatype.guava.ser.GuavaBeanSerializerModifier;
import com.google.common.collect.BoundType;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Basic Jackson {@link Module} that adds support for Guava types.
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
 *</ul>
 */
public class GuavaModule extends com.fasterxml.jackson.databind.Module // can't use just SimpleModule, due to generic types
{
    private final String NAME = "GuavaModule";

    /**
     * Configuration setting that determines whether `Optional.absent()` is
     * considered "same as null" for serialization purposes; that is, to be
     * filtered same as nulls are.
     * If enabled, absent values are treated like nulls; if disabled, they are not.
     * In either case, absent values are always considered "empty".
     *<p>
     * Default value is `true` for backwards compatibility (2.5 and prior
     * only had this behavior).
     *<p>
     * Note that this setting MUST be changed BEFORE registering the module:
     * changes after registration will have no effect.
     */
    protected boolean _cfgHandleAbsentAsNull = true;
    protected BoundType _defaultBoundType;
    
    public GuavaModule() {
        super();
    }

    @Override public String getModuleName() { return NAME; }
    @Override public Version version() { return PackageVersion.VERSION; }
    
    @Override
    public void setupModule(SetupContext context)
    {
        context.addDeserializers(new GuavaDeserializers(_defaultBoundType));
        context.addKeyDeserializers(new GuavaKeyDeserializers());
        context.addSerializers(new GuavaSerializers());
        context.addTypeModifier(new GuavaTypeModifier());

        // 28-Apr-2015, tatu: Allow disabling "treat Optional.absent() like Java nulls"
        if (_cfgHandleAbsentAsNull) {
            context.addBeanSerializerModifier(new GuavaBeanSerializerModifier());
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
     * 
     * @since 2.6
     */
    public GuavaModule configureAbsentsAsNulls(boolean state) {
        _cfgHandleAbsentAsNull = state;
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
     
     * @since 2.7
     */
    public GuavaModule defaultBoundType(BoundType boundType) {
        checkNotNull(boundType);
        _defaultBoundType = boundType;
        return this;
    }
    
    @Override
    public int hashCode() {
        return NAME.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
