package com.fasterxml.jackson.datatype.guava.deser.table;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.Table;
import java.io.IOException;

/**
 * @author Abhishekkr3003
 */
public abstract class MutableTableDeserializer<T extends Table<Object, Object, Object>>
    extends TableDeserializer<T> {
    
    protected MutableTableDeserializer(MapLikeType type) {
        super(type);
    }
    
    protected MutableTableDeserializer(MapLikeType _type, KeyDeserializer _rowDeserializer,
        KeyDeserializer _colDeserializer, TypeDeserializer _valueTypeDeserializer,
        JsonDeserializer<?> _valueDeserializer, NullValueProvider nvp) {
        super(
            _type, _rowDeserializer, _colDeserializer, _valueTypeDeserializer, _valueDeserializer,
            nvp
        );
    }
    
    protected abstract T createTable();
    
    protected abstract JsonDeserializer<?> _createContextual(MapLikeType t, KeyDeserializer rkd,
        KeyDeserializer ckd, TypeDeserializer vtd, JsonDeserializer<?> vd, NullValueProvider np);
    
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        T table = createTable();
        
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
        return table;
    }
}
