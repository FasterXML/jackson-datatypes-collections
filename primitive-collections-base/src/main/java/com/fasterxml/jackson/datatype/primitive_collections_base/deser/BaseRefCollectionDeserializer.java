package com.fasterxml.jackson.datatype.primitive_collections_base.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

public abstract class BaseRefCollectionDeserializer<T, Intermediate extends Collection<Object>>
        extends BaseCollectionDeserializer<T, Intermediate>
{
    protected final JavaType _elementType;
    protected final JsonDeserializer<?> _valueDeserializer;
    protected final TypeDeserializer _typeDeserializerForValue;

    protected BaseRefCollectionDeserializer(
            // ? super T so we can support generics in T
            Class<? super T> containerType,
            JavaType elementType,
            TypeDeserializer typeDeserializer,
            JsonDeserializer<?> deserializer
    ) {
        super(containerType);
        this._elementType = Objects.requireNonNull(elementType);
        this._typeDeserializerForValue = typeDeserializer;
        this._valueDeserializer = deserializer;
    }

    protected abstract BaseRefCollectionDeserializer<?, ?> withResolved(
            TypeDeserializer typeDeserializerForValue,
            JsonDeserializer<?> valueDeserializer
    );

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property)
            throws JsonMappingException {
        JsonDeserializer<?> deser = _valueDeserializer;
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
            throws IOException {
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
