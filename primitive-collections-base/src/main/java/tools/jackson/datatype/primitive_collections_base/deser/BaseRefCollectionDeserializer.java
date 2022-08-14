package tools.jackson.datatype.primitive_collections_base.deser;

import java.util.Collection;
import java.util.Objects;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.*;
import tools.jackson.databind.jsontype.TypeDeserializer;

public abstract class BaseRefCollectionDeserializer<T, Intermediate extends Collection<Object>>
    extends BaseCollectionDeserializer<T, Intermediate>
{
    protected final JavaType _elementType;
    protected final ValueDeserializer<?> _valueDeserializer;
    protected final TypeDeserializer _typeDeserializerForValue;

    protected BaseRefCollectionDeserializer(
            // ? super T so we can support generics in T
            Class<? super T> containerType,
            JavaType elementType,
            TypeDeserializer typeDeserializer,
            ValueDeserializer<?> deserializer
    ) {
        super(containerType);
        this._elementType = Objects.requireNonNull(elementType);
        this._typeDeserializerForValue = typeDeserializer;
        this._valueDeserializer = deserializer;
    }

    protected abstract BaseRefCollectionDeserializer<?, ?> withResolved(
            TypeDeserializer typeDeserializerForValue,
            ValueDeserializer<?> valueDeserializer
    );

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
    {
        ValueDeserializer<?> deser = _valueDeserializer;
        TypeDeserializer typeDeser = _typeDeserializerForValue;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(_elementType, property);
        }
        if (typeDeser != null) {
            typeDeser = typeDeser.forProperty(property);
        }
        if (deser == _valueDeserializer && typeDeser == _typeDeserializerForValue) {
            return this;
        }
        return withResolved(typeDeser, deser);
    }

    @Override
    protected void add(Intermediate intermediate, JsonParser parser, DeserializationContext ctx)
        throws JacksonException
    {
        Object value;
        if (parser.currentToken() == JsonToken.VALUE_NULL) {
            value = null;
        } else if (_typeDeserializerForValue == null) {
            value = _valueDeserializer.deserialize(parser, ctx);
        } else {
            value = _valueDeserializer.deserializeWithType(parser, ctx, _typeDeserializerForValue);
        }
        intermediate.add(value);
    }
}
