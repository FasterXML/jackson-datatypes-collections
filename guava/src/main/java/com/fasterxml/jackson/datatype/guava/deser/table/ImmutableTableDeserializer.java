package com.fasterxml.jackson.datatype.guava.deser.table;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.AccessPattern;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import java.io.IOException;

/**
 * Provides deserialization for the Guava ImmutableTable class.
 *
 * @author Abhishekkr3003
 */
public class ImmutableTableDeserializer
    extends TableDeserializer<ImmutableTable<Object, Object, Object>> {
    private static final long serialVersionUID = 2L;
    
    public ImmutableTableDeserializer(JavaType type) {
        super(type);
    }
    
    protected ImmutableTableDeserializer(JavaType type, KeyDeserializer rowDeserializer,
        KeyDeserializer colDeserializer, JsonDeserializer<?> valueDeserializer,
        TypeDeserializer valueTypeDeserializer, NullValueProvider nuller) {
        super(type, rowDeserializer, colDeserializer, valueTypeDeserializer, valueDeserializer,
            nuller
        );
    }
    
    @Override
    public AccessPattern getEmptyAccessPattern() {
        // immutable, hence:
        return AccessPattern.CONSTANT;
    }
    
    
    @Override
    public Object getEmptyValue(DeserializationContext ctxt) throws JsonMappingException {
        return ImmutableMap.of();
    }
    
    protected ImmutableTable.Builder<Object, Object, Object> createBuilder() {
        return ImmutableTable.builder();
    }
    
    @Override
    public ImmutableTable<Object, Object, Object> deserialize(JsonParser p,
        DeserializationContext ctxt) throws IOException {
        ImmutableTable.Builder<Object, Object, Object> table = createBuilder();
        
        JsonToken currToken = p.currentToken();
        if (currToken != JsonToken.FIELD_NAME && currToken != JsonToken.END_OBJECT) {
            expect(p, JsonToken.START_OBJECT);
            currToken = p.nextToken();
        }
        
        for (; currToken == JsonToken.FIELD_NAME; currToken = p.nextToken()) {
            final Object rowKey;
            if (_rowDeserializer != null) {
                rowKey = _rowDeserializer.deserializeKey(p.currentName(), ctxt);
            } else {
                rowKey = p.currentName();
            }
            
            p.nextToken();
            expect(p, JsonToken.START_OBJECT);
            
            for (
                currToken = p.nextToken(); currToken == JsonToken.FIELD_NAME;
                currToken = p.nextToken()) {
                final Object colKey;
                if (_colDeserializer != null) {
                    colKey = _colDeserializer.deserializeKey(p.currentName(), ctxt);
                } else {
                    colKey = p.currentName();
                }
                
                p.nextToken();
                
                final Object value;
                if (p.currentToken() == JsonToken.VALUE_NULL) {
                    if (_skipNullValues) {
                        continue;
                    }
                    value = _nullProvider.getNullValue(ctxt);
                } else if (_valueTypeDeserializer != null) {
                    value = _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
                } else {
                    value = _valueDeserializer.deserialize(p, ctxt);
                }
                table.put(rowKey, colKey, value);
            }
            expect(p, JsonToken.END_OBJECT);
        }
        return table.build();
    }
    
    @Override
    protected JsonDeserializer<?> _createContextual(JavaType type, KeyDeserializer rowDeserializer,
        KeyDeserializer columnDeserializer, TypeDeserializer valueTypeDeserializer,
        JsonDeserializer<?> valueDeserializer, NullValueProvider nullValueProvider) {
        return new ImmutableTableDeserializer(type, rowDeserializer, columnDeserializer,
            valueDeserializer, valueTypeDeserializer, nullValueProvider
        );
    }
}
