package tools.jackson.datatype.guava.deser;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.*;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.deser.impl.NullsConstantProvider;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.LogicalType;
import tools.jackson.databind.type.MapLikeType;
import com.google.common.cache.Cache;

public abstract class GuavaCacheDeserializer<T extends Cache<Object, Object>> 
    extends StdDeserializer<T>
{
    private final MapLikeType type;
    private final KeyDeserializer keyDeserializer;
    private final TypeDeserializer elementTypeDeserializer;
    private final ValueDeserializer<?> elementDeserializer;
    
    /*
     * @since 2.16 : in 3.x demote to `ContainerDeserializerBase`
     */
    private final NullValueProvider nullProvider;
    private final boolean skipNullValues;
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */

    public GuavaCacheDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer) {
        this(type, keyDeserializer, elementTypeDeserializer, elementDeserializer, null);
    }
    
    public GuavaCacheDeserializer(MapLikeType type, KeyDeserializer keyDeserializer,
            TypeDeserializer elementTypeDeserializer, ValueDeserializer<?> elementDeserializer,
            NullValueProvider nvp)
    {
        super(type);
        this.type = type;
        this.keyDeserializer = keyDeserializer;
        this.elementTypeDeserializer = elementTypeDeserializer;
        this.elementDeserializer = elementDeserializer;
        this.nullProvider = nvp;
        skipNullValues = (nvp == null) ? false : NullsConstantProvider.isSkipper(nvp);
    }
    
    /*
    /**********************************************************
    /* Post-processing (contextualization)
    /**********************************************************
     */

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty property)
    {
        KeyDeserializer kd = keyDeserializer;
        if (kd == null) {
            kd = ctxt.findKeyDeserializer(type.getKeyType(), property);
        }
        ValueDeserializer<?> valueDeser = elementDeserializer;
        final JavaType vt = type.getContentType();
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        } else { // if directly assigned, probably not yet contextual, so:
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
        }
        // Type deserializer is slightly different; must be passed, but needs to become contextual:
        TypeDeserializer vtd = elementTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        return _createContextual(type, kd, vtd, valueDeser, 
                findContentNullProvider(ctxt, property, valueDeser));
    }
    
    /*
    /**********************************************************************
    /* Abstract methods for subclasses
    /**********************************************************************
     */

    protected abstract T createCache();

    protected abstract ValueDeserializer<?> _createContextual(MapLikeType t, KeyDeserializer kd,
            TypeDeserializer vtd, ValueDeserializer<?> vd, NullValueProvider np);
    
    /*
    /**********************************************************************
    /* Implementations
    /**********************************************************************
     */

    @Override
    public LogicalType logicalType() {
        return LogicalType.Map;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) {
        return deserializeContents(p, ctxt);
    }

    private T deserializeContents(JsonParser p, DeserializationContext ctxt)
    {
        T cache = createCache();
        
        JsonToken currToken = p.currentToken();
        if (currToken != JsonToken.PROPERTY_NAME) {
            // 01-Mar-2023, tatu: [datatypes-collections#104] Handle empty Maps too
            if (currToken != JsonToken.END_OBJECT) {
                expect(ctxt, JsonToken.START_OBJECT, currToken);
                currToken = p.nextToken();
            }
        }
        
        for (; currToken == JsonToken.PROPERTY_NAME; currToken = p.nextToken()) {
            final Object key;
            if (keyDeserializer != null) {
                key = keyDeserializer.deserializeKey(p.currentName(), ctxt);
            } else {
                key = p.currentName();
            }
            
            p.nextToken();
            
            final Object value;
            if (p.currentToken() == JsonToken.VALUE_NULL) {
                if (skipNullValues) {
                    continue;
                }
                value = nullProvider.getNullValue(ctxt);
            } else if (elementTypeDeserializer != null) {
                value = elementDeserializer.deserializeWithType(p, ctxt, elementTypeDeserializer);
            } else {
                value = elementDeserializer.deserialize(p, ctxt);
            }
            cache.put(key, value);
        }
        return cache;
    }

    private void expect(DeserializationContext context, JsonToken expected, JsonToken actual)
    {
        if (actual != expected) {
            context.reportInputMismatch(this, String.format("Problem deserializing %s: expecting %s, found %s",
                    handledType().getName(), expected, actual));
        }
    }
}
