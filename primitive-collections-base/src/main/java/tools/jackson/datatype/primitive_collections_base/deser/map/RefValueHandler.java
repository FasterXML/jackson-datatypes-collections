package tools.jackson.datatype.primitive_collections_base.deser.map;

import com.fasterxml.jackson.annotation.Nulls;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;

/**
 * @author yawkat
 */
public class RefValueHandler implements ValueHandler<RefValueHandler>
{
    private final JavaType _valueType;
    private final ValueDeserializer<?> _valueDeserializer;
    private final TypeDeserializer _typeDeserializerForValue;

    // Whether JSON nulls are deserialized as "empty" values
    // (`@JsonSetter(contentNulls = Nulls.AS_EMPTY)`)
    private final boolean _nullsAsEmpty;

    public RefValueHandler(
            JavaType valueType,
            ValueDeserializer<?> _valueDeserializer,
            TypeDeserializer _typeDeserializerForValue
    ) {
        this(valueType, _valueDeserializer, _typeDeserializerForValue, false);
    }

    private RefValueHandler(JavaType valueType, ValueDeserializer<?> valueDeserializer,
            TypeDeserializer typeDeserializerForValue, boolean nullsAsEmpty)
    {
        if (valueType == null) { throw new IllegalArgumentException("valueType == null"); }

        this._valueType = valueType;
        this._valueDeserializer = valueDeserializer;
        this._typeDeserializerForValue = typeDeserializerForValue;
        this._nullsAsEmpty = nullsAsEmpty;
    }

    @Override
    public RefValueHandler createContextualValue(DeserializationContext ctxt, BeanProperty property)
    {
        ValueDeserializer<?> deser;
        if (_valueDeserializer == null) {
            deser = ctxt.findContextualValueDeserializer(_valueType, property);
        } else { // if directly assigned (custom deserializer), probably not yet contextual
            deser = ctxt.handleSecondaryContextualization(_valueDeserializer, property, _valueType);
        }
        TypeDeserializer typeDeser = _typeDeserializerForValue == null ?
                ctxt.findTypeDeserializer(_valueType) : _typeDeserializerForValue;
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        final Nulls contentNulls = (property == null)
                ? ctxt.getConfig().getDefaultNullHandling().getContentNulls()
                : property.getMetadata().getContentNulls();
        final boolean nullsAsEmpty = (contentNulls == Nulls.AS_EMPTY);
        //noinspection ObjectEquality
        if (deser == _valueDeserializer && typeDeser == _typeDeserializerForValue
                && nullsAsEmpty == _nullsAsEmpty) {
            return this;
        }
        return new RefValueHandler(_valueType, deser, typeDeser, nullsAsEmpty);
    }

    public Object value(DeserializationContext ctx, JsonParser parser)
        throws JacksonException
    {
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            return _nullsAsEmpty ? _valueDeserializer.getEmptyValue(ctx)
                    : _valueDeserializer.getNullValue(ctx);
        }
        if (_typeDeserializerForValue == null) {
            return _valueDeserializer.deserialize(parser, ctx);
        }
        return _valueDeserializer.deserializeWithType(parser, ctx, _typeDeserializerForValue);
    }
}
