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
        Object first = null;
        boolean hasFirst = false;

        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            // Only JSON nulls are skipped; null from value deserializer is not
            if (t == JsonToken.VALUE_NULL && _skipNullValues) {
                continue;
            }
            Object value = _deserializeSingleValue(p, ctxt);
            if (value == null) {
                // Null values need builder for proper error handling
                ImmutableCollection.Builder<Object> builder = createBuilder();
                if (hasFirst) {
                    _add(ctxt, builder, first);
                }
                _tryToAddNull(p, ctxt, builder);
                return _finishWithBuilder(p, ctxt, builder);
            }
            if (!hasFirst) {
                first = value;
                hasFirst = true;
            } else {
                ImmutableCollection.Builder<Object> builder = createBuilder();
                _add(ctxt, builder, first);
                _add(ctxt, builder, value);
                return _finishWithBuilder(p, ctxt, builder);
            }
        }

        if (!hasFirst) {
            return _createEmpty(ctxt);
        }
        return _createWithSingleElement(ctxt, first);
    }

    private Object _deserializeSingleValue(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (p.currentToken() == JsonToken.VALUE_NULL) {
            return _resolveNullToValue(ctxt);
        }
        return _valueTypeDeserializer == null
            ? _valueDeserializer.deserialize(p, ctxt)
            : _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
    }

    private T _finishWithBuilder(JsonParser p, DeserializationContext ctxt,
            ImmutableCollection.Builder<Object> builder) throws JacksonException
    {
        JsonToken t;
        while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
            if (t == JsonToken.VALUE_NULL && _skipNullValues) {
                continue;
            }
            Object value = _deserializeSingleValue(p, ctxt);
            if (value == null) {
                _tryToAddNull(p, ctxt, builder);
            } else {
                _add(ctxt, builder, value);
            }
        }
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

    private void _add(DeserializationContext ctxt, ImmutableCollection.Builder<Object> builder,
            Object value) throws JacksonException
    {
        _verifyComparable(ctxt, value);
        try {
            builder.add(value);
        } catch (ClassCastException e) {
            // Sorted builders may sort (and fail) while elements are still being added
            _reportBuildFailure(ctxt, e.getMessage());
        }
    }

    /**
     * Helper method sub-classes can use for implementing
     * {@link #_createWithSingleElement}, to get the same error handling as
     * for multi-element content.
     */
    protected T _buildWithSingleElement(DeserializationContext ctxt, Object value)
        throws JacksonException
    {
        _verifyComparable(ctxt, value);
        ImmutableCollection.Builder<Object> builder = createBuilder();
        try {
            builder.add(value);
            @SuppressWarnings("unchecked")
            T collection = (T) builder.build();
            return collection;
        } catch (ClassCastException e) {
            return _reportBuildFailure(ctxt, e.getMessage());
        }
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

    protected Object _resolveNullToValue(DeserializationContext ctxt)
        throws JacksonException
    {
        Object value = _nullProvider.getNullValue(ctxt);

        // Since Guava (immutable) collections are not very happy with {@code null} values,
        // we may eventually need to do additional mapping (see [databind#53]).
        // But for now just use null provider:

        /*
        if (value == null) {
            value = _valueDeserializer.getNullValue(ctxt);
         }
         */
        return value;
    }

    /**
     * Some/many Guava containers do not allow addition of {@code null} values,
     * so isolate handling here.
     */
    protected void _tryToAddNull(JsonParser p, DeserializationContext ctxt,
            ImmutableCollection.Builder<Object> builder)
    {
        // Ideally we'd have better idea of where nulls are accepted, but first
        // let's just produce something better than NPE:
        try {
            builder.add((Object) null);
        } catch (NullPointerException e) {
            ctxt.handleUnexpectedToken(_valueType, JsonToken.VALUE_NULL, p,
                    "Guava `Collection` of type %s does not accept `null` values",
                    ClassUtil.getTypeDescription(getValueType(ctxt)));
        }
    }
}
