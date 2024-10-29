package com.fasterxml.jackson.datatype.guava.deser.table;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.google.common.collect.Table;
import java.io.IOException;

/**
 * @author Abhishekkr3003
 */
public abstract class GuavaTableDeserializer<T extends Table<Object, Object, Object>>
    extends StdDeserializer<T> implements ContextualDeserializer {
    private static final long serialVersionUID = 1L;
    
    private final JavaType _type;
    private final KeyDeserializer _rowDeserializer;
    private final KeyDeserializer _colDeserializer;
    private final TypeDeserializer _valueTypeDeserializer;
    private final JsonDeserializer<?> _valueDeserializer;
    
    // since 2.9.5: in 3.x demote to `ContainerDeserializerBase`
    private final NullValueProvider _nullProvider;
    private final boolean _skipNullValues;
    
    public GuavaTableDeserializer(JavaType _type, KeyDeserializer _rowDeserializer,
        KeyDeserializer _colDeserializer, TypeDeserializer _valueTypeDeserializer,
        JsonDeserializer<?> _valueDeserializer) {
        this(
            _type, _rowDeserializer, _colDeserializer, _valueTypeDeserializer, _valueDeserializer,
            null
        );
    }
    
    public GuavaTableDeserializer(JavaType _type, KeyDeserializer _rowDeserializer,
        KeyDeserializer _colDeserializer, TypeDeserializer _valueTypeDeserializer,
        JsonDeserializer<?> _valueDeserializer, NullValueProvider nvp) {
        super(_type);
        this._type = _type;
        this._rowDeserializer = _rowDeserializer;
        this._colDeserializer = _colDeserializer;
        this._valueTypeDeserializer = _valueTypeDeserializer;
        this._valueDeserializer = _valueDeserializer;
        this._nullProvider = nvp;
        _skipNullValues = (nvp == null) ? false : NullsConstantProvider.isSkipper(nvp);
    }
    
    public GuavaTableDeserializer(JavaType type) {
        super(type);
        _type = type;
        _rowDeserializer = null;
        _colDeserializer = null;
        _valueDeserializer = null;
        _valueTypeDeserializer = null;
        _nullProvider = null;
        _skipNullValues = false;
    }
    
    protected abstract T createTable();
    
    /**
     * We need to use this method to properly handle possible contextual variants of key and value
     * deserializers, as well as type deserializers.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
        BeanProperty property) throws JsonMappingException {
        KeyDeserializer rkd = _rowDeserializer;
        if (rkd == null) {
            rkd = ctxt.findKeyDeserializer(_type.containedTypeOrUnknown(0), property);
        }
        KeyDeserializer ckd = _colDeserializer;
        if (ckd == null) {
            ckd = ctxt.findKeyDeserializer(_type.containedTypeOrUnknown(1), property);
        }
        JsonDeserializer<?> valueDeser = _valueDeserializer;
        final JavaType vt = _type.containedTypeOrUnknown(2);
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        } else { // if directly assigned, probably not yet contextual, so:
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
        }
        // Type deserializer is slightly different; must be passed, but needs to become contextual:
        TypeDeserializer vtd = _valueTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        return _createContextual(
            _type, rkd, ckd, vtd, valueDeser, findContentNullProvider(ctxt, property, valueDeser));
    }
    
    protected abstract JsonDeserializer<?> _createContextual(JavaType t, KeyDeserializer rkd,
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
    
    private void expect(JsonParser p, JsonToken token) throws IOException {
        if (p.getCurrentToken() != token) {
            throw new JsonMappingException(
                p,
                "Expecting " + token + " to start `TABLE` value, found " + p.currentToken(),
                p.currentLocation()
            );
        }
    }
}
