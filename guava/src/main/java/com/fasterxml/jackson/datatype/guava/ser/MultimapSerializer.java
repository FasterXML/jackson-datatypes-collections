package com.fasterxml.jackson.datatype.guava.ser;

import java.util.*;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.type.WritableTypeId;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.jdk.MapProperty;
import com.fasterxml.jackson.databind.ser.std.StdContainerSerializer;
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
    extends StdContainerSerializer<Multimap<?, ?>>
{
    private final MapLikeType _type;
    private final JsonSerializer<Object> _keySerializer;
    private final TypeSerializer _valueTypeSerializer;
    private final JsonSerializer<Object> _valueSerializer;

    /**
     * Set of entries to omit during serialization, if any
     */
    protected final Set<String> _ignoredEntries;

    /**
     * Id of the property filter to use, if any; null if none.
     */
    protected final Object _filterId;

    /**
     * Flag set if output is forced to be sorted by keys (usually due
     * to annotation).
     *<p>
     * NOTE: not yet used as of 2.9
     */
    protected final boolean _sortKeys;
    
    public MultimapSerializer(MapLikeType type, BeanDescription beanDesc,
            JsonSerializer<Object> keySerializer, TypeSerializer vts,
            JsonSerializer<Object> valueSerializer,
            Set<String> ignoredEntries, Object filterId)
    {
        super(type, null);
        _type = type;
        _keySerializer = keySerializer;
        _valueTypeSerializer = vts;
        _valueSerializer = valueSerializer;
        _ignoredEntries = ignoredEntries;
        _filterId = filterId;
        _sortKeys = false;
    }

    @SuppressWarnings("unchecked")
    protected MultimapSerializer(MultimapSerializer src, BeanProperty property,
                JsonSerializer<?> keySerializer, TypeSerializer vts, JsonSerializer<?> valueSerializer,
                Set<String> ignoredEntries, Object filterId, boolean sortKeys)
    {
        super(src, property);
        _type = src._type;
        _keySerializer = (JsonSerializer<Object>) keySerializer;
        _valueTypeSerializer = vts;
        _valueSerializer = (JsonSerializer<Object>) valueSerializer;
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
    protected StdContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSer) {
        return new MultimapSerializer(this, _property, _keySerializer,
                typeSer, _valueSerializer, _ignoredEntries, _filterId, _sortKeys);
    }

    /*
    /**********************************************************************
    /* Post-processing (contextualization)
    /**********************************************************************
     */

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider ctxt,
            BeanProperty property)
    {
        JsonSerializer<?> valueSer = _valueSerializer;
        if (valueSer == null) { // if type is final, can actually resolve:
            JavaType valueType = _type.getContentType();
            if (valueType.isFinal()) {
                valueSer = ctxt.findContentValueSerializer(valueType, property);
            }
        } else {
            valueSer = valueSer.createContextual(ctxt, property);
        }

        final SerializationConfig config = ctxt.getConfig();
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        final AnnotatedMember propertyAcc = (property == null) ? null : property.getMember();
        JsonSerializer<?> keySer = null;
        Object filterId = _filterId;
        
        // First: if we have a property, may have property-annotation overrides
        if (propertyAcc != null && intr != null) {
            Object serDef = intr.findKeySerializer(config, propertyAcc);
            if (serDef != null) {
                keySer = ctxt.serializerInstance(propertyAcc, serDef);
            }
            serDef = intr.findContentSerializer(config, propertyAcc);
            if (serDef != null) {
                valueSer = ctxt.serializerInstance(propertyAcc, serDef);
            }
            filterId = intr.findFilterId(config, propertyAcc);
        }
        if (valueSer == null) {
            valueSer = _valueSerializer;
        }
        // [datatype-guava#124]: May have a content converter
        valueSer = findContextualConvertingSerializer(ctxt, property, valueSer);
        if (valueSer == null) {
            // One more thing -- if explicit content type is annotated,
            //   we can consider it a static case as well.
            JavaType valueType = _type.getContentType();
            if (valueType.useStaticType()) {
                valueSer = ctxt.findContentValueSerializer(valueType, property);
            }
        } else {
            valueSer = ctxt.handleSecondaryContextualization(valueSer, property);
        }
        if (keySer == null) {
            keySer = _keySerializer;
        }
        if (keySer == null) {
            keySer = ctxt.findKeySerializer(_type.getKeyType(), property);
        } else {
            keySer = ctxt.handleSecondaryContextualization(keySer, property);
        }
        // finally, TypeSerializers may need contextualization as well
        TypeSerializer typeSer = _valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(ctxt, property);
        }

        Set<String> ignored = _ignoredEntries;
        boolean sortKeys = false;

        if (intr != null && propertyAcc != null) {
            JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnoralByName(config, propertyAcc);
            if (ignorals != null) {
                Set<String> newIgnored = ignorals.findIgnoredForSerialization();
                if ((newIgnored != null) && !newIgnored.isEmpty()) {
                    ignored = (ignored == null) ? new HashSet<String>() : new HashSet<>(ignored);
                    for (String str : newIgnored) {
                        ignored.add(str);
                    }
                }
            }
            Boolean b = intr.findSerializationSortAlphabetically(config, propertyAcc);
            sortKeys = (b != null) && b.booleanValue();
        }
        // 19-May-2016, tatu: Also check per-property format features, even if
        //    this isn't yet used (as per [guava#7])
        JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
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
    /**********************************************************************
    /* Accessors for ContainerSerializer
    /**********************************************************************
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
    public boolean isEmpty(SerializerProvider prov, Multimap<?,?> value) {
        return value.isEmpty();
    }

    /*
    /**********************************************************************
    /* Post-processing (contextualization)
    /**********************************************************************
     */
    
    @Override
    public void serialize(Multimap<?, ?> value, JsonGenerator gen, SerializerProvider provider)
        throws JacksonException
    {
        gen.writeStartObject();
        // [databind#631]: Assign current value, to be accessible by custom serializers
        gen.assignCurrentValue(value);
        if (!value.isEmpty()) {
 // 20-Mar-2017, tatu: And this is where [datatypes-collections#7] would be
//          plugged in...
//            if (_sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
//                value = _orderEntries(value, gen, provider);
//            }

            if (_filterId != null) {
                serializeFilteredFields(value, gen, provider);
            } else {
                serializeFields(value, gen, provider);
            }
        }        
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(Multimap<?,?> value, JsonGenerator gen,
            SerializerProvider ctxt, TypeSerializer typeSer)
        throws JacksonException
    {
        gen.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, ctxt,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        if (!value.isEmpty()) {
// 20-Mar-2017, tatu: And this is where [datatypes-collections#7] would be
//     plugged in...
//            if (_sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
//              value = _orderEntries(value, gen, provider);
//          }
            if (_filterId != null) {
                serializeFilteredFields(value, gen, ctxt);
            } else {
                serializeFields(value, gen, ctxt);
            }
        }
        typeSer.writeTypeSuffix(gen, ctxt, typeIdDef);
    }

    private final void serializeFields(Multimap<?, ?> mmap, JsonGenerator
            gen, SerializerProvider provider)
        throws JacksonException
    {
        final Set<String> ignored = _ignoredEntries;
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
                    provider.defaultSerializeNullValue(gen);
                    continue;
                }
                JsonSerializer<Object> valueSer = _valueSerializer;
                if (valueSer == null) {
                    Class<?> cc = vv.getClass();
                    valueSer = _dynamicValueSerializers.serializerFor(cc);
                    if (valueSer == null) {
                        valueSer = _findAndAddDynamic(provider, cc);
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

    private final void serializeFilteredFields(Multimap<?, ?> mmap, JsonGenerator gen, SerializerProvider provider)
        throws JacksonException
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
            prop.reset(key, value, _keySerializer, valueSer);
            try {
                filter.serializeAsProperty(mmap, gen, provider, prop);
            } catch (Exception e) {
                String keyDesc = ""+key;
                wrapAndThrow(provider, e, value, keyDesc);
            }
        }
    }

    /*
    /**********************************************************************
    /* Schema related functionality
    /**********************************************************************
     */

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    {
        JsonMapFormatVisitor v2 = (visitor == null) ? null : visitor.expectMapFormat(typeHint);        
        if (v2 != null) {
            v2.keyFormat(_keySerializer, _type.getKeyType());
            JsonSerializer<?> valueSer = _valueSerializer;
            final JavaType vt = _type.getContentType();
            final SerializerProvider prov = visitor.getProvider();
            if (valueSer == null) {
                valueSer = _findAndAddDynamic(prov, vt);
            }
            final JsonSerializer<?> valueSer2 = valueSer;
            v2.valueFormat(new JsonFormatVisitable() {
                final JavaType arrayType = prov.getTypeFactory().constructArrayType(vt);
                @Override
                public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper v3, JavaType hint3)
                {
                    JsonArrayFormatVisitor v4 = v3.expectArrayFormat(arrayType);
                    if (v4 != null) {
                        v4.itemsFormat(valueSer2, vt);
                    }
                }
            }, vt);
        }
    }
}
