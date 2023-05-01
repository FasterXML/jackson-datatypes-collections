package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;

import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.cache.Cache;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Serializer for Guava's {@link Cache} values. Output format encloses all
 * value sets in JSON Array, regardless of number of values; this to reduce
 * complexity (and inaccuracy) of trying to handle cases where values themselves
 * would be serialized as arrays (in which cases determining whether given array
 * is a wrapper or value gets complicated and unreliable).
 * <p>
 * Missing features, compared to standard Java Maps:
 * <ul>
 *  <li>Inclusion checks for content entries (non-null, non-empty)
 *   </li>
 *  <li>Sorting of entries
 *   </li>
 * </ul>
 *
 * @since 2.16
 */
public class CacheSerializer extends ContainerSerializer<Cache<?, ?>>
    implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;

    private final MapLikeType _type;
    private final BeanProperty _property;
    private final JsonSerializer<Object> _keySerializer;
    private final TypeSerializer _valueTypeSerializer;
    private final JsonSerializer<Object> _valueSerializer;

    /**
     * Set of entries to omit during serialization, if any
     *
     * @since 2.16
     */
    protected final Set<String> _ignoredEntries;

    /**
     * If value type can not be statically determined, mapping from
     * runtime value types to serializers are stored in this object.
     *
     * @since 2.16
     */
    protected PropertySerializerMap _dynamicValueSerializers;

    /**
     * Id of the property filter to use, if any; null if none.
     *
     * @since 2.16
     */
    protected final Object _filterId;

    /**
     * Flag set if output is forced to be sorted by keys (usually due
     * to annotation).
     *<p>
     * NOTE: not yet used.
     *
     * @since 2.16
     */
    protected final boolean _sortKeys;
    
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */
    
    public CacheSerializer(MapLikeType type, BeanDescription beanDesc,
            JsonSerializer<Object> keySerializer, TypeSerializer vts, JsonSerializer<Object> valueSerializer,
            Set<String> ignoredEntries, Object filterId)
    {
        super(type.getRawClass(), false);
        _type = type;
        _property = null;
        _keySerializer = keySerializer;
        _valueTypeSerializer = vts;
        _valueSerializer = valueSerializer;
        _ignoredEntries = ignoredEntries;
        _filterId = filterId;
        _sortKeys = false;

        _dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
    }
    /**
     * @since 2.16
     */
    protected CacheSerializer(CacheSerializer src, BeanProperty property,
                JsonSerializer<?> keySerializer, TypeSerializer vts, JsonSerializer<?> valueSerializer,
                Set<String> ignoredEntries, Object filterId, boolean sortKeys)
    {
        super(src);
        _type = src._type;
        _property = property;
        _keySerializer = (JsonSerializer<Object>) keySerializer;
        _valueTypeSerializer = vts;
        _valueSerializer = (JsonSerializer<Object>) valueSerializer;
        _dynamicValueSerializers = src._dynamicValueSerializers;
        _ignoredEntries = ignoredEntries;
        _filterId = filterId;
        _sortKeys = sortKeys;
    }

    /**
    * @since 2.16
    */
    protected CacheSerializer withResolved(BeanProperty property,
                                              JsonSerializer<?> keySer, TypeSerializer vts, JsonSerializer<?> valueSer,
                                              Set<String> ignored, Object filterId, boolean sortKeys)
    {
        return new CacheSerializer(this, property, keySer, vts, valueSer,
            ignored, filterId, sortKeys);
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSer) {
        return new CacheSerializer(this, _property, _keySerializer,
            typeSer, _valueSerializer, _ignoredEntries, _filterId, _sortKeys);
    }
    
    /*
    /**********************************************************
    /* Accessors for ContainerSerializer
    /**********************************************************
     */

    @Override
    public JavaType getContentType() {
        return _type.getContentType();
    }

    @Override
    public JsonSerializer<?> getContentSerializer() {
        return _valueSerializer;
    }

    @Override
    public boolean hasSingleElement(Cache<?, ?> cache) {
        return cache.size() == 1;
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Cache<?, ?> value) {
        return value.size() == 0;
    }
    
    /*
    /**********************************************************
    /* Post-processing (contextualization)
    /**********************************************************
     */
    
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        JsonSerializer<?> valueSer = _valueSerializer;
        if (valueSer == null) { // if type is final, can actually resolve:
            JavaType valueType = _type.getContentType();
            if (valueType.isFinal()) {
                valueSer = provider.findValueSerializer(valueType, property);
            }
        } else if (valueSer instanceof ContextualSerializer) {
            valueSer = ((ContextualSerializer) valueSer).createContextual(provider, property);
        }

        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        final AnnotatedMember propertyAcc = (property == null) ? null : property.getMember();
        JsonSerializer<?> keySer = null;
        Object filterId = _filterId;

        // First: if we have a property, may have property-annotation overrides
        if (propertyAcc != null && intr != null) {
            Object serDef = intr.findKeySerializer(propertyAcc);
            if (serDef != null) {
                keySer = provider.serializerInstance(propertyAcc, serDef);
            }
            serDef = intr.findContentSerializer(propertyAcc);
            if (serDef != null) {
                valueSer = provider.serializerInstance(propertyAcc, serDef);
            }
            filterId = intr.findFilterId(propertyAcc);
        }
        if (valueSer == null) {
            valueSer = _valueSerializer;
        }
        // [datatype-guava#124]: May have a content converter
        valueSer = findContextualConvertingSerializer(provider, property, valueSer);
        if (valueSer == null) {
            // One more thing -- if explicit content type is annotated,
            //   we can consider it a static case as well.
            JavaType valueType = _type.getContentType();
            if (valueType.useStaticType()) {
                valueSer = provider.findValueSerializer(valueType, property);
            }
        } else {
            valueSer = provider.handleSecondaryContextualization(valueSer, property);
        }
        if (keySer == null) {
            keySer = _keySerializer;
        }
        if (keySer == null) {
            keySer = provider.findKeySerializer(_type.getKeyType(), property);
        } else {
            keySer = provider.handleSecondaryContextualization(keySer, property);
        }
        // finally, TypeSerializers may need contextualization as well
        TypeSerializer typeSer = _valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
        }

        Set<String> ignored = _ignoredEntries;
        boolean sortKeys = false;

        if (intr != null && propertyAcc != null) {
            JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnoralByName(provider.getConfig(), propertyAcc);
            if (ignorals != null) {
                Set<String> newIgnored = ignorals.findIgnoredForSerialization();
                if ((newIgnored != null) && !newIgnored.isEmpty()) {
                    ignored = (ignored == null) ? new HashSet<String>() : new HashSet<>(ignored);
                    for (String str : newIgnored) {
                        ignored.add(str);
                    }
                }
            }
            Boolean b = intr.findSerializationSortAlphabetically(propertyAcc);
            sortKeys = (b != null) && b.booleanValue();
        }
        // 19-May-2016, tatu: Also check per-property format features, even if
        //    this isn't yet used (as per [guava#7])
        JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
        if (format != null) {
            Boolean B = format.getFeature(JsonFormat.Feature.WRITE_SORTED_MAP_ENTRIES);
            if (B != null) {
                sortKeys = B.booleanValue();
            }
        }

        return withResolved(property, keySer, typeSer, valueSer,
            ignored, filterId, sortKeys);
    }
    
    /*
    /**********************************************************
    /* Abstract method implementations
    /**********************************************************
     */

    @Override
    public void serialize(Cache<?, ?> value, JsonGenerator gen, SerializerProvider provider)
        throws IOException
    {
        gen.writeStartObject();
        gen.setCurrentValue(value);        
        _writeContents(value, gen, provider);
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(Cache<?, ?> value, JsonGenerator gen, SerializerProvider ctxt,
            TypeSerializer typeSer)
        throws IOException
    {
        gen.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        _writeContents(value, gen, ctxt);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }
    
    /*
    /**********************************************************
    /* Internal helper methods
    /**********************************************************
     */

    protected void _writeContents(Cache<?, ?> cache, JsonGenerator gen, SerializerProvider provider)
        throws IOException
    {
        Map<?, ?> value = cache.asMap();
        if (value.isEmpty()) {
            return;
        }
        for (Map.Entry<?, ?> entry : cache.asMap().entrySet()) {
            Object key = entry.getKey();
            Object v = entry.getValue();
            gen.writeFieldName(key.toString());
            provider.defaultSerializeValue(v, gen);
        }
//        if (_sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
//            value = _orderEntriesByKey(value, gen, provider);
//        }
//        if (_filterId != null) {
//            serializeFilteredFields(value, gen, provider);
//        } else {
//            serializeFields(value, gen, provider);
//        }
    }
    
}
