package tools.jackson.datatype.guava.deser.table;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonToken;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.deser.impl.NullsConstantProvider;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.MapLikeType;

import com.google.common.collect.Table;

/**
 * @author Abhishekkr3003
 */
public abstract class TableDeserializer<T extends Table<Object, Object, Object>>
    extends StdDeserializer<T> {
    
    protected final MapLikeType _type;
    protected final KeyDeserializer _rowDeserializer;
    protected final KeyDeserializer _colDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueDeserializer<?> _valueDeserializer;
    
    // since 2.9.5: in 3.x demote to `ContainerDeserializerBase`
    protected final NullValueProvider _nullProvider;
    protected final boolean _skipNullValues;
    
    protected TableDeserializer(MapLikeType _type, KeyDeserializer _rowDeserializer,
        KeyDeserializer _colDeserializer, TypeDeserializer _valueTypeDeserializer,
        ValueDeserializer<?> _valueDeserializer) {
        this(
            _type, _rowDeserializer, _colDeserializer, _valueTypeDeserializer, _valueDeserializer,
            null
        );
    }
    
    protected TableDeserializer(MapLikeType _type, KeyDeserializer _rowDeserializer,
        KeyDeserializer _colDeserializer, TypeDeserializer _valueTypeDeserializer,
        ValueDeserializer<?> _valueDeserializer, NullValueProvider nvp) {
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
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
        BeanProperty property) throws JacksonException
    {
        KeyDeserializer rkd = _rowDeserializer;
        if (rkd == null) {
            rkd = ctxt.findKeyDeserializer(_type.getKeyType(), property);
        }
        MapLikeType columnValueType = (MapLikeType) _type.getContentType();
        KeyDeserializer ckd = _colDeserializer;
        if (ckd == null) {
            ckd = ctxt.findKeyDeserializer(columnValueType.getKeyType(), property);
        }
        ValueDeserializer<?> valueDeser = _valueDeserializer;
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
    
    protected abstract ValueDeserializer<?> _createContextual(MapLikeType t, KeyDeserializer rkd,
        KeyDeserializer ckd, TypeDeserializer vtd, ValueDeserializer<?> vd, NullValueProvider np);
    

    protected void expect(DeserializationContext context, JsonToken expected, JsonToken actual)
    {
        if (actual != expected) {
            context.reportInputMismatch(this, String.format("Problem deserializing %s: expecting %s, found %s",
                    handledType().getName(), expected, actual));
        }
    }
}
