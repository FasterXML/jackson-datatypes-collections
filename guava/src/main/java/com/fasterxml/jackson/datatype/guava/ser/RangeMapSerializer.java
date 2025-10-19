package com.fasterxml.jackson.datatype.guava.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitable;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.ser.std.MapProperty;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Serializer for Guava's {@link RangeMap} values. Output format encloses
 * values in JSON Object.
 *
 * @author mcvayc
 * @since 2.21
 */
public class RangeMapSerializer
        extends ContainerSerializer<RangeMap<Comparable<?>, ?>>
        implements ContextualSerializer {
    private static final long serialVersionUID = 1L;

    private final MapLikeType _type;
    private final BeanProperty _property;
    private final JsonSerializer<Object> _keySerializer;
    private final TypeSerializer _valueTypeSerializer;
    private final JsonSerializer<Object> _valueSerializer;

    /**
     * Set of entries to omit during serialization, if any
     */
    protected final Set<String> _ignoredEntries;

    /**
     * If value type can not be statically determined, mapping from
     * runtime value types to serializers are stored in this object.
     */
    protected PropertySerializerMap _dynamicValueSerializers;

    /**
     * Id of the property filter to use, if any; null if none.
     */
    protected final Object _filterId;

    /**
     * Flag set if output is forced to be sorted by keys (usually due
     * to annotation).
     */
    protected final boolean _sortKeys;

    /**
     * @since 2.21
     */
    public RangeMapSerializer(MapLikeType type, BeanDescription beanDesc,
                              JsonSerializer<Object> keySerializer, TypeSerializer vts, JsonSerializer<Object> valueSerializer,
                              Set<String> ignoredEntries, Object filterId) {
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
    protected RangeMapSerializer(RangeMapSerializer src, BeanProperty property,
                                 JsonSerializer<?> keySerializer,
                                 TypeSerializer vts, JsonSerializer<?> valueSerializer,
                                 Set<String> ignoredEntries, Object filterId, boolean sortKeys) {
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

    protected RangeMapSerializer withResolved(BeanProperty property,
                                              JsonSerializer<?> keySer, TypeSerializer vts, JsonSerializer<?> valueSer,
                                              Set<String> ignored, Object filterId, boolean sortKeys) {
        return new RangeMapSerializer(this, property, keySer, vts, valueSer,
                ignored, filterId, sortKeys);
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSer) {
        return new RangeMapSerializer(this, _property, _keySerializer,
                typeSer, _valueSerializer, _ignoredEntries, _filterId, _sortKeys);
    }

    /*
    /**********************************************************
    /* Post-processing (contextualization)
    /**********************************************************
     */

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider,
                                              BeanProperty property) throws JsonMappingException {
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
        // May have a content converter
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
        // Also check per-property format features, even if this isn't yet used (as per [guava#7])
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
    public boolean hasSingleElement(RangeMap<Comparable<?>, ?> map) {
        return map.asMapOfRanges().size() == 1;
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, RangeMap<Comparable<?>, ?> value) {
        return value.asMapOfRanges().isEmpty();
    }

    /*
    /**********************************************************
    /* Post-processing (contextualization)
    /**********************************************************
     */

    @Override
    public void serialize(RangeMap<Comparable<?>, ?> value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();
        // Assign current value, to be accessible by custom serializers
        gen.assignCurrentValue(value);
        serializeValue(value, gen, provider);
        gen.writeEndObject();
    }

    private void serializeValue(RangeMap<Comparable<?>, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (!isEmpty(value)) {
            if (_sortKeys || provider.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
                value = _orderEntriesByKey(value, gen, provider);
            }

            Map<Range<Comparable<?>>, ?> map = value.asMapOfRanges();

            if (_filterId != null) {
                serializeFilteredFields(map, gen, provider);
            } else {
                serializeFields(map, gen, provider);
            }
        }
    }

    @Override
    public void serializeWithType(RangeMap<Comparable<?>, ?> value, JsonGenerator gen,
                                  SerializerProvider provider, TypeSerializer typeSer)
            throws IOException {
        gen.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        serializeValue(value, gen, provider);
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    private void serializeFields(Map<Range<Comparable<?>>, ?> rmap, JsonGenerator
            gen, SerializerProvider provider)
            throws IOException {
        final Set<String> ignored = _ignoredEntries;
        PropertySerializerMap serializers = _dynamicValueSerializers;
        for (Entry<Range<Comparable<?>>, ?> entry : rmap.entrySet()) {
            // First, serialize key
            Range<?> key = entry.getKey();
            if ((ignored != null) && ignored.contains(key.toString())) {
                continue;
            }
            if (key == null) {
                provider.findNullKeySerializer(_type.getKeyType(), _property)
                        .serialize(null, gen, provider);
            } else {
                _keySerializer.serialize(key, gen, provider);
            }
            Object value = entry.getValue();
            if (value == null) {
                provider.defaultSerializeNull(gen);
                continue;
            }
            JsonSerializer<Object> valueSer = _valueSerializer;
            if (valueSer == null) {
                Class<?> cc = value.getClass();
                valueSer = serializers.serializerFor(cc);
                if (valueSer == null) {
                    valueSer = _findAndAddDynamic(serializers, cc, provider);
                    serializers = _dynamicValueSerializers;
                }
            }
            if (_valueTypeSerializer == null) {
                valueSer.serialize(value, gen, provider);
            } else {
                valueSer.serializeWithType(value, gen, provider, _valueTypeSerializer);
            }
        }
    }

    private void serializeFilteredFields(Map<Range<Comparable<?>>, ?> rmap, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        final Set<String> ignored = _ignoredEntries;
        PropertyFilter filter = findPropertyFilter(provider, _filterId, rmap);
        final MapProperty prop = new MapProperty(_valueTypeSerializer, _property);
        for (Entry<Range<Comparable<?>>, ?> entry : rmap.entrySet()) {
            // First, serialize key
            Range<?> key = entry.getKey();
            if ((ignored != null) && ignored.contains(key)) {
                continue;
            }
            Object value = entry.getValue();
            JsonSerializer<Object> valueSer;
            if (value == null) {
                // !!! TODO: null suppression?
                valueSer = provider.getDefaultNullValueSerializer();
            } else {
                valueSer = _valueSerializer;
            }
            prop.reset(key, value, _keySerializer, valueSer);
            try {
                filter.serializeAsField(rmap, gen, provider, prop);
            } catch (Exception e) {
                String keyDesc = "" + key;
                wrapAndThrow(provider, e, value, keyDesc);
            }
        }
    }

    /*
    /**********************************************************
    /* Schema related functionality
    /**********************************************************
     */

    /**
     * @since 2.21
     */
    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException {
        JsonMapFormatVisitor v2 = (visitor == null) ? null : visitor.expectMapFormat(typeHint);
        if (v2 != null) {
            v2.keyFormat(_keySerializer, _type.getKeyType());
            JsonSerializer<?> valueSer = _valueSerializer;
            final JavaType vt = _type.getContentType();
            final SerializerProvider prov = visitor.getProvider();
            if (valueSer == null) {
                valueSer = _findAndAddDynamic(_dynamicValueSerializers, vt, prov);
            }
            final JsonSerializer<?> valueSer2 = valueSer;
            v2.valueFormat(new JsonFormatVisitable() {
                final JavaType arrayType = prov.getTypeFactory().constructArrayType(vt);

                @Override
                public void acceptJsonFormatVisitor(
                        JsonFormatVisitorWrapper v3, JavaType hint3)
                        throws JsonMappingException {
                    JsonArrayFormatVisitor v4 = v3.expectArrayFormat(arrayType);
                    if (v4 != null) {
                        v4.itemsFormat(valueSer2, vt);
                    }
                }
            }, vt);
        }
    }

    /*
    /**********************************************************
    /* Internal helper methods
    /**********************************************************
     */

    /**
     * @since 2.21
     */
    protected <X> RangeMap<Comparable<?>, X> _orderEntriesByKey(RangeMap<Comparable<?>, X> value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        try {
            TreeRangeMap<Comparable<?>, X> ordered = TreeRangeMap.create();
            ordered.putAll(value);
            return ordered;
        } catch (ClassCastException e) {
            // Either key or value type not Comparable?
            // Should we actually wrap & propagate failure or... ?
            return value;
        } catch (NullPointerException e) {
            // Most likely null key that TreeRangeMap won't accept. So... ?
            provider.reportMappingProblem("Failed to sort RangeMap entries due to `NullPointerException`: `null` key?");
            return null;
        }
    }

    protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map,
                                                              Class<?> type, SerializerProvider provider) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, _property);
        // did we get a new map of serializers? If so, start using it
        if (map != result.map) {
            _dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }

    protected final JsonSerializer<Object> _findAndAddDynamic(PropertySerializerMap map,
                                                              JavaType type, SerializerProvider provider) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, _property);
        if (map != result.map) {
            _dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }
}
