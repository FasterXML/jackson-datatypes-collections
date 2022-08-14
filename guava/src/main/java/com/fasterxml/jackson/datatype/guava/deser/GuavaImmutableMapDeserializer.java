package com.fasterxml.jackson.datatype.guava.deser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.util.AccessPattern;

import com.google.common.collect.ImmutableMap;

abstract class GuavaImmutableMapDeserializer<T extends ImmutableMap<Object, Object>> extends
        GuavaMapDeserializer<T>
{
    GuavaImmutableMapDeserializer(JavaType type, KeyDeserializer keyDeser,
            ValueDeserializer<?> valueDeser, TypeDeserializer valueTypeDeser,
            NullValueProvider nuller) {
        super(type, keyDeser, valueDeser, valueTypeDeser, nuller);
    }

    @Override
    public AccessPattern getEmptyAccessPattern() {
        // immutable, hence:
        return AccessPattern.CONSTANT;
    }

    protected abstract ImmutableMap.Builder<Object, Object> createBuilder();

    @Override
    protected T _deserializeEntries(JsonParser p, DeserializationContext ctxt)
        throws JacksonException
    {
        final KeyDeserializer keyDes = _keyDeserializer;
        final ValueDeserializer<?> valueDes = _valueDeserializer;
        final TypeDeserializer typeDeser = _valueTypeDeserializer;
    
        ImmutableMap.Builder<Object, Object> builder = createBuilder();
        for (; p.currentToken() == JsonToken.PROPERTY_NAME; p.nextToken()) {
            // Must point to field name now
            String fieldName = p.currentName();
            Object key = (keyDes == null) ? fieldName : keyDes.deserializeKey(fieldName, ctxt);
            // And then the value...
            JsonToken t = p.nextToken();
            // 28-Nov-2010, tatu: Should probably support "ignorable properties" in future...
            Object value;            
            if (t == JsonToken.VALUE_NULL) {
                if (!_skipNullValues) {
                    value = _nullProvider.getNullValue(ctxt);
                    // 14-Sep-2015, tatu: As per [datatype-guava#52], avoid exception due to null
                    // TODO: allow reporting problem via a feature, in future?
                    if (value != null) {
                        builder.put(key, value);
                    }
                }
                continue;
            }
            if (typeDeser == null) {
                value = valueDes.deserialize(p, ctxt);
            } else {
                value = valueDes.deserializeWithType(p, ctxt, typeDeser);
            }
            builder.put(key, value);
        }
        // No class outside of the package will be able to subclass us,
        // and we provide the proper builder for the subclasses we implement.
        @SuppressWarnings("unchecked")
        T map = (T) builder.build();
        return map;
    }
}
