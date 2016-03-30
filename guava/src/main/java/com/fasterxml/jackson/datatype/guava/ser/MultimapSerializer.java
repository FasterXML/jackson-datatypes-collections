package com.fasterxml.jackson.datatype.guava.ser;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.std.MapProperty;
import com.fasterxml.jackson.databind.type.MapLikeType;

import com.google.common.collect.Multimap;

/**
 * Serializer for Guava's {@link Multimap} values. Output format encloses all
 * value sets in JSON Array, regardless of number of values; this to reduce
 * complexity (and inaccuracy) of trying to handle cases where values themselves
 * would be serialized as arrays (in which cases determining whether given array
 * is a wrapper or value gets complicated and unreliable).
 *<p>
 * Missing features, compared to standard Java Maps:
 *<ul>
 *  <li>Inclusion checks for content entries (non-null, non-empty)
 *   </li>
 *  <li>Sorting of entries
 *   </li>
 * </ul>
 */
public class MultimapSerializer
    extends ContainerSerializer<Multimap<?, ?>>
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
     * @since 2.5
     */
    protected final Set<String> _ignoredEntries;

    /**
     * If value type can not be statically determined, mapping from
     * runtime value types to serializers are stored in this object.
     *
     * @since 2.5
     */
    protected PropertySerializerMap _dynamicValueSerializers;

    /**
     * Id of the property filter to use, if any; null if none.
     *
     * @since 2.5
     */
    protected final Object _filterId;

    /**
     * Flag set if output is forced to be sorted by keys (usually due
     * to annotation).
     *<p>
     * NOTE: not yet used.
     *
     * @since 2.5
     */
    protected final boolean _sortKeys;
    
    public MultimapSerializer(MapLikeType type, BeanDescription beanDesc,
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
     * @since 2.5
     */
    @SuppressWarnings("unchecked")
    protected MultimapSerializer(MultimapSerializer src, BeanProperty property,
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

    protected MultimapSerializer withResolved(BeanProperty property,
            JsonSerializer<?> keySer, TypeSerializer vts, JsonSerializer<?> valueSer,
            Set<String> ignored, Object filterId, boolean sortKeys)
    {
        return new MultimapSerializer(this, property, keySer, vts, valueSer,
                ignored, filterId, sortKeys);
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSer) {
        return new MultimapSerializer(this, _property, _keySerializer,
                typeSer, _valueSerializer, _ignoredEntries, _filterId, _sortKeys);
    }

    /*
    /**********************************************************
    /* Post-processing (contextualization)
    /**********************************************************
     */

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider,
            BeanProperty property) throws JsonMappingException
    {
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
        valueSer = findConvertingContentSerializer(provider, property, valueSer);
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
            String[] moreToIgnore = intr.findPropertiesToIgnore(propertyAcc, true);
            if (moreToIgnore != null) {
                ignored = (ignored == null) ? new HashSet<String>() : new HashSet<String>(ignored);
                for (String str : moreToIgnore) {
                    ignored.add(str);
                }
            }
            Boolean b = intr.findSerializationSortAlphabetically(propertyAcc);
            sortKeys = (b != null) && b.booleanValue();
        }
        return withResolved(property, keySer, typeSer, valueSer,
                ignored, filterId, sortKeys);
    }

    /*
    /**********************************************************
    /* Accessors for ContainerSerializer
    /**********************************************************
     */
    
    @Override
    public JsonSerializer<?> getContentSerializer() {
        return _valueSerializer;
    }

    @Override
    public JavaType getContentType() {
        return _type.getContentType();
    }

    @Override
    public boolean hasSingleElement(Multimap<?,?> map) {
        return map.size() == 1;
    }

    @Override
    @Deprecated // since 2.5
    public boolean isEmpty(Multimap<?,?> value) {
        return isEmpty(null, value);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Multimap<?,?> value) {
        return value.isEmpty();
    }

    /*
    /**********************************************************
    /* Post-processing (contextualization)
    /**********************************************************
     */
    
    @Override
    public void serialize(Multimap<?, ?> value, JsonGenerator gen, SerializerProvider provider)
        throws IOException
    {
        gen.writeStartObject();
        // [databind#631]: Assign current value, to be accessible by custom serializers
        gen.setCurrentValue(value);
        if (!value.isEmpty()) {
            if (_filterId != null) {
                serializeOptionalFields(value, gen, provider);
            } else {
                serializeFields(value, gen, provider);
            }
        }        
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(Multimap<?,?> value, JsonGenerator gen, SerializerProvider provider,
            TypeSerializer typeSer)
        throws IOException
    {
        typeSer.writeTypePrefixForObject(value, gen);
        gen.setCurrentValue(value);
        if (!value.isEmpty()) {
            if (_filterId != null) {
                serializeOptionalFields(value, gen, provider);
            } else {
                serializeFields(value, gen, provider);
            }
        }
        typeSer.writeTypeSuffixForObject(value, gen);
    }

    private final void serializeFields(Multimap<?, ?> mmap, JsonGenerator gen, SerializerProvider provider)
            throws IOException
    {
        final Set<String> ignored = _ignoredEntries;
        PropertySerializerMap serializers = _dynamicValueSerializers;
        for (Entry<?, ? extends Collection<?>> entry : mmap.asMap().entrySet()) {
            // First, serialize key
            Object key = entry.getKey();
            if ((ignored != null) && ignored.contains(key)) {
                continue;
            }
            if (key == null) {
                provider.findNullKeySerializer(_type.getKeyType(), _property)
                    .serialize(null, gen, provider);
            } else {
                _keySerializer.serialize(key, gen, provider);
            }
            // note: value is a List, but generic type is for contents... so:
            gen.writeStartArray();
            for (Object vv : entry.getValue()) {
                if (vv == null) {
                    provider.defaultSerializeNull(gen);
                    continue;
                }
                JsonSerializer<Object> valueSer = _valueSerializer;
                if (valueSer == null) {
                    Class<?> cc = vv.getClass();
                    valueSer = serializers.serializerFor(cc);
                    if (valueSer == null) {
                        valueSer = _findAndAddDynamic(serializers, cc, provider);
                        serializers = _dynamicValueSerializers;
                    }
                }
                if (_valueTypeSerializer == null) {
                    valueSer.serialize(vv, gen, provider);
                } else {
                    valueSer.serializeWithType(vv, gen, provider, _valueTypeSerializer);
                }
            }
            gen.writeEndArray();
        }
    }

    private final void serializeOptionalFields(Multimap<?, ?> mmap, JsonGenerator gen, SerializerProvider provider)
            throws IOException
    {
        final Set<String> ignored = _ignoredEntries;
        PropertyFilter filter = findPropertyFilter(provider, _filterId, mmap);  
        final MapProperty prop = new MapProperty(_valueTypeSerializer, _property);
        for (Entry<?, ? extends Collection<?>> entry : mmap.asMap().entrySet()) {
            // First, serialize key
            Object key = entry.getKey();
            if ((ignored != null) && ignored.contains(key)) {
                continue;
            }
            Collection<?> value = entry.getValue();
            JsonSerializer<Object> valueSer;
            if (value == null) {
                // !!! TODO: null suppression?
                valueSer = provider.getDefaultNullValueSerializer();
            } else {
                valueSer = _valueSerializer;
            }
            prop.reset(key, _keySerializer, valueSer);
            try {
                filter.serializeAsField(value, gen, provider, prop);
            } catch (Exception e) {
                String keyDesc = ""+key;
                wrapAndThrow(provider, e, value, keyDesc);
            }
        }
    }
    
    /*
    /**********************************************************
    /* Schema related functionality
    /**********************************************************
     */

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
        throws JsonMappingException
    {
        JsonMapFormatVisitor v2 = (visitor == null) ? null : visitor.expectMapFormat(typeHint);        
        if (v2 != null) {
            v2.keyFormat(_keySerializer, _type.getKeyType());
            JsonSerializer<?> valueSer = _valueSerializer;
            JavaType vt = _type.getContentType();
            if (valueSer == null) {
                valueSer = _findAndAddDynamic(_dynamicValueSerializers,
                            vt, visitor.getProvider());
            }
            v2.valueFormat(valueSer, vt);
        }
    }

    /*
    /**********************************************************
    /* Internal helper methods
    /**********************************************************
     */
    
    protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map,
            Class<?> type, SerializerProvider provider) throws JsonMappingException
    {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, _property);
        // did we get a new map of serializers? If so, start using it
        if (map != result.map) {
            _dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }

    protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map,
            JavaType type, SerializerProvider provider) throws JsonMappingException
    {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, _property);
        if (map != result.map) {
            _dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }
}
