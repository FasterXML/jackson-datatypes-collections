package tools.jackson.datatype.guava.deser;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.*;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.deser.impl.NullsConstantProvider;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.util.AccessPattern;
import tools.jackson.databind.util.ClassUtil;
import tools.jackson.datatype.guava.util.NullElementHelper;

import com.google.common.collect.ImmutableRangeSet;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;

public class RangeSetDeserializer
    extends StdDeserializer<RangeSet<?>>
{
    /**
     * Deserializer for individual {@link Range} elements
     */
    private final ValueDeserializer<Object> _deserializer;

    /**
     * If {@link Range} elements have polymorphic type information, type
     * deserializer to use
     */
    private final TypeDeserializer _typeDeserializer;

    private final NullValueProvider _nullProvider;

    private final boolean _skipNullValues;

    private final Boolean _unwrapSingle;

    public RangeSetDeserializer() {
        super(RangeSet.class);
        _deserializer = null;
        _typeDeserializer = null;
        _nullProvider = null;
        _skipNullValues = false;
        _unwrapSingle = null;
    }

    protected RangeSetDeserializer(RangeSetDeserializer base,
            ValueDeserializer<Object> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle)
    {
        super(base);
        _deserializer = deser;
        _typeDeserializer = typeDeser;
        _nullProvider = nuller;
        _skipNullValues = NullsConstantProvider.isSkipper(nuller);
        _unwrapSingle = unwrapSingle;
    }

    @Override
    public LogicalType logicalType() {
        return LogicalType.Collection;
    }

    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.CONSTANT;
    }

    @Override
    public Object getEmptyValue(DeserializationContext ctxt) {
        return ImmutableRangeSet.of();
    }

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    {
        JavaType rangeType = _findRangeType(ctxt, ctxt.getContextualType());
        if (rangeType == null) {
            if (property != null) {
                rangeType = _findRangeType(ctxt, property.getType());
            }
            // Cannot locate generic type to use? Leave as-is, fail on attempt to deserialize
            if (rangeType == null) {
                return this;
            }
        }
        // Content converter (`@JsonDeserialize(contentConverter=...)`), if any
        @SuppressWarnings("unchecked")
        ValueDeserializer<Object> deser = (ValueDeserializer<Object>)
                findConvertingContentDeserializer(ctxt, property, null);
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(rangeType, property);
        }
        TypeDeserializer typeDeser = ctxt.findTypeDeserializer(rangeType);
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        NullValueProvider nuller = findContentNullProvider(ctxt, property, deser);
        if (nuller == null) {
            nuller = deser;
        }
        Boolean unwrapSingle = findFormatFeature(ctxt, property, Collection.class,
                JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        return new RangeSetDeserializer(this, deser, typeDeser, nuller, unwrapSingle);
    }

    private JavaType _findRangeType(DeserializationContext ctxt, JavaType base)
    {
        Class<?> raw = base.getRawClass();
        final TypeFactory tf = ctxt.getTypeFactory();
        if (RangeSet.class.isAssignableFrom(raw)) {
            JavaType valueType = tf.findFirstTypeParameter(base, RangeSet.class);
            if (valueType != null) {
                return tf.constructParametricType(Range.class, valueType);
            }
        }
        return null;
    }

    @Override
    public RangeSet<?> deserialize(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        if (_deserializer == null) {
            ctxt.reportBadDefinition(handledType(),
"Not contextualized to have value deserializer or value type of `RangeSet` was not available via type parameters");
        }
        ImmutableRangeSet.Builder<Comparable<?>> builder = ImmutableRangeSet.builder();
        if (p.isExpectedStartArrayToken()) {
            JsonToken t;
            int index = 0;
            while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                try {
                    _readRange(p, ctxt, t, builder);
                } catch (Exception e) {
                    // As with databind `CollectionDeserializer`: add index to path
                    boolean wrap = ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
                    if (!wrap) {
                        ClassUtil.throwIfRTE(e);
                    }
                    throw DatabindException.wrapWithPath(ctxt, e,
                            new JacksonException.Reference(builder, index));
                }
                ++index;
            }
        } else {
            // Empty/blank String may be coerced into empty (or null) `RangeSet`
            // (same as with databind `CollectionDeserializer`)
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                final String str = p.getString();
                CoercionAction act = null;
                String desc = null;
                if (str.isEmpty()) {
                    act = ctxt.findCoercionAction(logicalType(), handledType(),
                            CoercionInputShape.EmptyString);
                    desc = "empty String (\"\")";
                } else if (_isBlank(str)) {
                    act = ctxt.findCoercionFromBlankString(logicalType(), handledType(),
                            CoercionAction.Fail);
                    desc = "blank String (all whitespace)";
                }
                // `Fail` falls through: may still be accepted as single value
                if ((act != null) && (act != CoercionAction.Fail)) {
                    return (RangeSet<?>) _deserializeFromEmptyString(p, ctxt, act,
                            handledType(), desc);
                }
            }
            final boolean canWrap = (_unwrapSingle == Boolean.TRUE) ||
                    ((_unwrapSingle == null) && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
            if (!canWrap) {
                return (RangeSet<?>) ctxt.handleUnexpectedToken(getValueType(ctxt), p);
            }
            _readRange(p, ctxt, p.currentToken(), builder);
        }
        try {
            return builder.build();
        } catch (IllegalArgumentException e) {
            return ctxt.reportInputMismatch(this,
                    "Failed to build %s from deserialized entries: %s",
                    ClassUtil.getTypeDescription(getValueType(ctxt)), e.getMessage());
        }
    }

    private void _readRange(JsonParser p, DeserializationContext ctxt, JsonToken t,
            ImmutableRangeSet.Builder<Comparable<?>> builder)
        throws JacksonException
    {
        final Object range;
        if (t == JsonToken.VALUE_NULL) {
            if (_skipNullValues) {
                return;
            }
            range = _nullProvider.getNullValue(ctxt);
        } else if (_typeDeserializer == null) {
            range = _deserializer.deserialize(p, ctxt);
        } else {
            range = _deserializer.deserializeWithType(p, ctxt, _typeDeserializer);
        }
        _addRange(p, ctxt, builder, range);
    }

    /**
     * Some/many Guava containers do not allow addition of {@code null} values,
     * so isolate handling here.
     */
    protected void _tryToAddNull(JsonParser p, DeserializationContext ctxt,
            ImmutableRangeSet.Builder<Comparable<?>> builder)
        throws JacksonException
    {
        _addRange(p, ctxt, builder, null);
    }

    private void _addRange(JsonParser p, DeserializationContext ctxt,
            ImmutableRangeSet.Builder<Comparable<?>> builder, Object range)
        throws JacksonException
    {
        NullElementHelper.addElement(p, ctxt, p.currentToken(),
                ctxt.constructType(Range.class), "`RangeSet`",
                range, r -> {
                    @SuppressWarnings("unchecked")
                    Range<Comparable<?>> typed = (Range<Comparable<?>>) r;
                    return builder.add(typed);
                });
    }
}
