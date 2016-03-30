package com.fasterxml.jackson.datatype.guava.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.common.collect.ImmutableMap;

abstract class GuavaImmutableMapDeserializer<T extends ImmutableMap<Object, Object>> extends
        GuavaMapDeserializer<T> {

    GuavaImmutableMapDeserializer(MapType type, KeyDeserializer keyDeser, TypeDeserializer typeDeser,
            JsonDeserializer<?> deser) {
        super(type, keyDeser, typeDeser, deser);
    }

    protected abstract ImmutableMap.Builder<Object, Object> createBuilder();

    @Override
    protected T _deserializeEntries(JsonParser p, DeserializationContext ctxt)
        throws IOException, JsonProcessingException
    {
        final KeyDeserializer keyDes = _keyDeserializer;
        final JsonDeserializer<?> valueDes = _valueDeserializer;
        final TypeDeserializer typeDeser = _typeDeserializerForValue;
    
        ImmutableMap.Builder<Object, Object> builder = createBuilder();
        for (; p.getCurrentToken() == JsonToken.FIELD_NAME; p.nextToken()) {
            // Must point to field name now
            String fieldName = p.getCurrentName();
            Object key = (keyDes == null) ? fieldName : keyDes.deserializeKey(fieldName, ctxt);
            // And then the value...
            JsonToken t = p.nextToken();
            // 28-Nov-2010, tatu: Should probably support "ignorable properties" in future...
            Object value;            
            if (t == JsonToken.VALUE_NULL) {
                _handleNull(ctxt, key, _valueDeserializer, builder);
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

    /**
     * Overridable helper method called when a JSON null value is encountered.
     * Since Guava Maps typically do not allow null values, special handling
     * is needed; default is to simply ignore and skip such values, but alternative
     * could be to throw an exception.
     */
    protected void _handleNull(DeserializationContext ctxt, Object key,
            JsonDeserializer<?> valueDeser,
            ImmutableMap.Builder<Object, Object> builder) throws IOException
    {
        // 14-Sep-2015, tatu: As per [datatype-guava#52], avoid exception due to null
        // TODO: allow reporting problem via a feature, in future?
        
        // Actually, first, see if there's an alternative to Java null
        Object nvl = valueDeser.getNullValue(ctxt);
        if (nvl != null) {
            builder.put(key, nvl);
        }
    }
}
