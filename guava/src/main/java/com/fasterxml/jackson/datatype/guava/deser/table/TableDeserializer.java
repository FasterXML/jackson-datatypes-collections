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
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.Table;
import java.io.IOException;

/**
 * @author Abhishekkr3003
 */
public abstract class TableDeserializer<T extends Table<Object, Object, Object>>
    extends StdDeserializer<T> implements ContextualDeserializer {
    private static final long serialVersionUID = 1L;
    
    protected final MapLikeType _type;
    protected final KeyDeserializer _rowDeserializer;
    protected final KeyDeserializer _colDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final JsonDeserializer<?> _valueDeserializer;
    
    // since 2.9.5: in 3.x demote to `ContainerDeserializerBase`
    protected final NullValueProvider _nullProvider;
    protected final boolean _skipNullValues;
    
    protected TableDeserializer(MapLikeType _type, KeyDeserializer _rowDeserializer,
        KeyDeserializer _colDeserializer, TypeDeserializer _valueTypeDeserializer,
        JsonDeserializer<?> _valueDeserializer) {
        this(
            _type, _rowDeserializer, _colDeserializer, _valueTypeDeserializer, _valueDeserializer,
            null
        );
    }
    
    protected TableDeserializer(MapLikeType _type, KeyDeserializer _rowDeserializer,
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
    
    protected TableDeserializer(MapLikeType type) {
        super(type);
        _type = type;
        _rowDeserializer = null;
        _colDeserializer = null;
        _valueDeserializer = null;
        _valueTypeDeserializer = null;
        _nullProvider = null;
        _skipNullValues = false;
    }
    
    /**
     * We need to use this method to properly handle possible contextual variants of key and value
     * deserializers, as well as type deserializers.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
        BeanProperty property) throws JsonMappingException {
        KeyDeserializer rkd = _rowDeserializer;
        if (rkd == null) {
            rkd = ctxt.findKeyDeserializer(_type.getKeyType(), property);
        }
        MapLikeType columnValueType = (MapLikeType) _type.getContentType();
        KeyDeserializer ckd = _colDeserializer;
        if (ckd == null) {
            ckd = ctxt.findKeyDeserializer(columnValueType.getKeyType(), property);
        }
        JsonDeserializer<?> valueDeser = _valueDeserializer;
        final JavaType vt = columnValueType.getContentType();
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
        return _createContextual(_type,
                rkd, ckd, vtd, valueDeser, findContentNullProvider(ctxt, property, valueDeser));
    }
    
    protected abstract JsonDeserializer<?> _createContextual(MapLikeType t, KeyDeserializer rkd,
        KeyDeserializer ckd, TypeDeserializer vtd, JsonDeserializer<?> vd, NullValueProvider np);
    
    
    protected void expect(JsonParser p, JsonToken token) throws IOException {
        if (p.currentToken() != token) {
            throw new JsonMappingException(
                p,
                "Expecting " + token + " to start `TABLE` value, found " + p.currentToken(),
                p.currentLocation()
            );
        }
    }
}
