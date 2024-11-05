package tools.jackson.datatype.guava.deser.table;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.MapLikeType;

import com.google.common.collect.Table;

/**
 * @author Abhishekkr3003
 */
public abstract class MutableTableDeserializer<T extends Table<Object, Object, Object>>
    extends TableDeserializer<T>
{
    protected MutableTableDeserializer(MapLikeType type) {
        super(type);
    }
    
    protected MutableTableDeserializer(MapLikeType _type, KeyDeserializer _rowDeserializer,
        KeyDeserializer _colDeserializer, TypeDeserializer _valueTypeDeserializer,
        ValueDeserializer<?> _valueDeserializer, NullValueProvider nvp) {
        super(
            _type, _rowDeserializer, _colDeserializer, _valueTypeDeserializer, _valueDeserializer,
            nvp
        );
    }
    
    protected abstract T createTable();

    @Override
    protected abstract ValueDeserializer<?> _createContextual(MapLikeType t, KeyDeserializer rkd,
        KeyDeserializer ckd, TypeDeserializer vtd, ValueDeserializer<?> vd, NullValueProvider np);
    
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) {
        T table = createTable();
        
        JsonToken currToken = p.currentToken();
        if (currToken != JsonToken.PROPERTY_NAME && currToken != JsonToken.END_OBJECT) {
            expect(ctxt, JsonToken.START_OBJECT, currToken);
            currToken = p.nextToken();
        }
        
        for (; currToken == JsonToken.PROPERTY_NAME; currToken = p.nextToken()) {
            final Object rowKey;
            if (_rowDeserializer != null) {
                rowKey = _rowDeserializer.deserializeKey(p.currentName(), ctxt);
            } else {
                rowKey = p.currentName();
            }
            
            currToken = p.nextToken();
            expect(ctxt, JsonToken.START_OBJECT, currToken);
            
            for (
                currToken = p.nextToken(); currToken == JsonToken.PROPERTY_NAME;
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
            expect(ctxt, JsonToken.END_OBJECT, p.currentToken());
        }
        return table;
    }
}
