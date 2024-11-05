package tools.jackson.datatype.guava.deser.table;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.MapLikeType;
import tools.jackson.databind.util.AccessPattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;

/**
 * Provides deserialization for the Guava ImmutableTable class.
 *
 * @author Abhishekkr3003
 */
public class ImmutableTableDeserializer
    extends TableDeserializer<ImmutableTable<Object, Object, Object>>
{
    public ImmutableTableDeserializer(MapLikeType type) {
        super(type);
    }
    
    protected ImmutableTableDeserializer(MapLikeType type, KeyDeserializer rowDeserializer,
        KeyDeserializer colDeserializer, ValueDeserializer<?> valueDeserializer,
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
    public Object getEmptyValue(DeserializationContext ctxt) {
        return ImmutableMap.of();
    }
    
    protected ImmutableTable.Builder<Object, Object, Object> createBuilder() {
        return ImmutableTable.builder();
    }
    
    @Override
    public ImmutableTable<Object, Object, Object> deserialize(JsonParser p,
        DeserializationContext ctxt)
    {
        ImmutableTable.Builder<Object, Object, Object> table = createBuilder();
        
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
        return table.build();
    }
    
    @Override
    protected ValueDeserializer<?> _createContextual(MapLikeType type,
        KeyDeserializer rowDeserializer,
        KeyDeserializer columnDeserializer, TypeDeserializer valueTypeDeserializer,
        ValueDeserializer<?> valueDeserializer, NullValueProvider nullValueProvider) {
        return new ImmutableTableDeserializer(type, rowDeserializer, columnDeserializer,
            valueDeserializer, valueTypeDeserializer, nullValueProvider
        );
    }
}
