package tools.jackson.datatype.guava.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationConfig;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.util.AccessPattern;
import tools.jackson.databind.util.ClassUtil;

import com.google.common.collect.ImmutableCollection;

abstract class GuavaImmutableCollectionDeserializer<T extends ImmutableCollection<Object>>
        extends GuavaCollectionDeserializer<T>
{
    GuavaImmutableCollectionDeserializer(JavaType selfType,
            ValueDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    protected abstract ImmutableCollection.Builder<Object> createBuilder();

    // Can not modify Immutable collections now can we
    @Override
    public Boolean supportsUpdate(DeserializationConfig config) {
        return Boolean.FALSE;
    }

    @Override
    public AccessPattern getEmptyAccessPattern() {
        // But we should be able to just share immutable empty instance
        return AccessPattern.CONSTANT;
    }

    @Override
    public T getEmptyValue(DeserializationContext ctxt) {
        return _createEmpty(ctxt);
    }

    @Override
    protected T _deserializeContents(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        // Builder only constructed once there are 2 elements (or a `null` one)
        Object first = null;
        ImmutableCollection.Builder<Object> builder = null;

        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            Object value = _deserializeElement(p, ctxt, t);
            if (value == SKIPPED_ELEMENT) {
                continue;
            }
            if (builder == null) {
                if ((first == null) && (value != null)) {
                    first = value;
                    continue;
                }
                builder = createBuilder();
                if (first != null) {
                    _add(ctxt, builder, first);
                }
            }
            _addToBuilder(p, ctxt, builder, value);
        }

        if (builder != null) {
            return _build(ctxt, builder);
        }
        if (first == null) {
            return _createEmpty(ctxt);
        }
        return _createWithSingleElement(ctxt, first);
    }

    private void _addToBuilder(JsonParser p, DeserializationContext ctxt,
            ImmutableCollection.Builder<Object> builder, Object value)
        throws JacksonException
    {
        // Avoid lambda allocation for the common non-null case
        if (value == null) {
            _addElement(p, ctxt, p.currentToken(), null, v -> _add(ctxt, builder, v));
        } else {
            _add(ctxt, builder, value);
        }
    }

    private T _build(DeserializationContext ctxt, ImmutableCollection.Builder<Object> builder)
        throws JacksonException
    {
        // No class outside of the package will be able to subclass us,
        // and we provide the proper builder for the subclasses we implement.
        try {
            @SuppressWarnings("unchecked")
            T collection = (T) builder.build();
            return collection;
        } catch (ClassCastException e) {
            // Sorted builders fail this way if elements are not mutually comparable
            return _reportBuildFailure(ctxt, e.getMessage());
        }
    }

    private ImmutableCollection.Builder<Object> _add(DeserializationContext ctxt,
            ImmutableCollection.Builder<Object> builder, Object value)
        throws JacksonException
    {
        _verifyComparable(ctxt, value);
        try {
            builder.add(value);
        } catch (ClassCastException e) {
            // Sorted builders may sort (and fail) while elements are still being added
            _reportBuildFailure(ctxt, e.getMessage());
        }
        return builder;
    }

    /**
     * Helper method sub-classes can use for implementing
     * {@link #_createWithSingleElement}, to get the same error handling as
     * for multi-element content.
     */
    protected T _buildWithSingleElement(DeserializationContext ctxt, Object value)
        throws JacksonException
    {
        // NOTE: `null` value fails with NPE, handled by caller
        return _build(ctxt, _add(ctxt, createBuilder(), value));
    }

    /**
     * Overridable method that sorted containers override to return {@code true}:
     * if so, all (non-null) elements are verified to be {@link Comparable}.
     * This is needed since sorted builders never compare a single element.
     */
    protected boolean _requiresComparableElements() {
        return false;
    }

    private void _verifyComparable(DeserializationContext ctxt, Object value)
        throws JacksonException
    {
        if (_requiresComparableElements()
                && (value != null) && !(value instanceof Comparable<?>)) {
            _reportBuildFailure(ctxt, String.format("element of type %s is not `Comparable`",
                    ClassUtil.classNameOf(value)));
        }
    }
}
