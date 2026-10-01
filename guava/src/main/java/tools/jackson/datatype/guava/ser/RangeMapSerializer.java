package tools.jackson.datatype.guava.ser;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonToken;
import tools.jackson.core.type.WritableTypeId;
import tools.jackson.databind.*;
import tools.jackson.databind.introspect.AnnotatedMember;
import tools.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import tools.jackson.databind.jsonFormatVisitors.JsonMapFormatVisitor;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.std.StdContainerSerializer;
import tools.jackson.databind.ser.PropertyFilter;
import tools.jackson.databind.ser.impl.PropertySerializerMap;
import tools.jackson.databind.ser.jdk.MapProperty;
import tools.jackson.databind.type.MapLikeType;

/**
 * Serializer for Guava's {@link RangeMap} values. Output format encloses
 * values in JSON Object.
 *
 * @author mcvayc
 */
public class RangeMapSerializer
    extends StdContainerSerializer<RangeMap<Comparable<?>, ?>>
{
    private final MapLikeType _type;
    private final BeanProperty _property;
    private final ValueSerializer<Object> _keySerializer;
    private final TypeSerializer _valueTypeSerializer;
    private final ValueSerializer<Object> _valueSerializer;

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

    public RangeMapSerializer(MapLikeType type, BeanDescription.Supplier beanDesc,
            ValueSerializer<Object> keySerializer, TypeSerializer vts, ValueSerializer<Object> valueSerializer,
            Set<String> ignoredEntries, Object filterId) {
        super(type, null);
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

    @SuppressWarnings("unchecked")
    protected RangeMapSerializer(RangeMapSerializer src, BeanProperty property,
            ValueSerializer<?> keySerializer,
            TypeSerializer vts, ValueSerializer<?> valueSerializer,
            Set<String> ignoredEntries, Object filterId, boolean sortKeys) {
        super(src);
        _type = src._type;
        _property = property;
        _keySerializer = (ValueSerializer<Object>) keySerializer;
        _valueTypeSerializer = vts;
        _valueSerializer = (ValueSerializer<Object>) valueSerializer;
        _dynamicValueSerializers = src._dynamicValueSerializers;
        _ignoredEntries = ignoredEntries;
        _filterId = filterId;
        _sortKeys = sortKeys;
    }

    protected RangeMapSerializer withResolved(BeanProperty property,
            ValueSerializer<?> keySer, TypeSerializer vts, ValueSerializer<?> valueSer,
            Set<String> ignored, Object filterId, boolean sortKeys) {
        return new RangeMapSerializer(this, property, keySer, vts, valueSer,
                ignored, filterId, sortKeys);
    }

    @Override
    protected StdContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSer) {
        return new RangeMapSerializer(this, _property, _keySerializer,
                typeSer, _valueSerializer, _ignoredEntries, _filterId, _sortKeys);
    }

    /*
    /**********************************************************
    /* Post-processing (contextualization)
    /**********************************************************
     */

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt,
            BeanProperty property) throws JacksonException
    {
        ValueSerializer<?> valueSer = _valueSerializer;
        final SerializationConfig config = ctxt.getConfig();
        if (valueSer == null) { // if type is final, can actually resolve:
            JavaType valueType = _type.getContentType();
            if (valueType.isFinal()) {
                valueSer = ctxt.findContentValueSerializer(valueType, property);
            }
        } else {
            valueSer = valueSer.createContextual(ctxt, property);
        }

        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        final AnnotatedMember propertyAcc = (property == null) ? null : property.getMember();
        ValueSerializer<?> keySer = null;
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
        // May have a content converter
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
                    ignored = (ignored == null) ? new HashSet<>() : new HashSet<>(ignored);
                    for (String str : newIgnored) {
                        ignored.add(str);
                    }
                }
            }
            Boolean b = intr.findSerializationSortAlphabetically(config, propertyAcc);
            sortKeys = (b != null) && b.booleanValue();
        }
        // Also check per-property format features, even if this isn't yet used (as per [guava#7])
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
    /**********************************************************
    /* Accessors for ContainerSerializer
    /**********************************************************
     */

    @Override
    public ValueSerializer<?> getContentSerializer() {
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
    public boolean isEmpty(SerializationContext prov, RangeMap<Comparable<?>, ?> value) {
        return value.asMapOfRanges().isEmpty();
    }

    /*
    /**********************************************************
    /* Post-processing (contextualization)
    /**********************************************************
     */

    @Override
    public void serialize(RangeMap<Comparable<?>, ?> value, JsonGenerator gen, SerializationContext provider)
            throws JacksonException
    {
        gen.writeStartObject();
        // Assign current value, to be accessible by custom serializers
        gen.assignCurrentValue(value);
        serializeValue(value, gen, provider);
        gen.writeEndObject();
    }

    private void serializeValue(RangeMap<Comparable<?>, ?> value, JsonGenerator gen, SerializationContext ctxt)
        throws JacksonException
    {
        if (!isEmpty(ctxt, value)) {
            if (_sortKeys || ctxt.isEnabled(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)) {
                value = _orderEntriesByKey(value, gen, ctxt);
            }

            Map<Range<Comparable<?>>, ?> map = value.asMapOfRanges();

            if (_filterId != null) {
                serializeFilteredFields(map, gen, ctxt);
            } else {
                serializeFields(map, gen, ctxt);
            }
        }
    }

    @Override
    public void serializeWithType(RangeMap<Comparable<?>, ?> value, JsonGenerator gen,
            SerializationContext ctxt, TypeSerializer typeSer)
            throws JacksonException
    {
        gen.assignCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, ctxt,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        serializeValue(value, gen, ctxt);
        typeSer.writeTypeSuffix(gen, ctxt, typeIdDef);
    }

    private void serializeFields(Map<Range<Comparable<?>>, ?> rmap, JsonGenerator
            gen, SerializationContext ctxt)
            throws JacksonException
    {
        final Set<String> ignored = _ignoredEntries;
        PropertySerializerMap serializers = _dynamicValueSerializers;
        for (Entry<Range<Comparable<?>>, ?> entry : rmap.entrySet()) {
            // First, serialize key
            Range<?> key = entry.getKey();
            if ((ignored != null) && ignored.contains(key.toString())) {
                continue;
            }
            if (key == null) {
                ctxt.findNullKeySerializer(_type.getKeyType(), _property)
                        .serialize(null, gen, ctxt);
            } else {
                _keySerializer.serialize(key, gen, ctxt);
            }
            Object value = entry.getValue();
            if (value == null) {
                ctxt.defaultSerializeNullValue(gen);
                continue;
            }
            ValueSerializer<Object> valueSer = _valueSerializer;
            if (valueSer == null) {
                Class<?> cc = value.getClass();
                valueSer = serializers.serializerFor(cc);
                if (valueSer == null) {
                    valueSer = _findAndAddDynamic(serializers, cc, ctxt);
                    serializers = _dynamicValueSerializers;
                }
            }
            if (_valueTypeSerializer == null) {
                valueSer.serialize(value, gen, ctxt);
            } else {
                valueSer.serializeWithType(value, gen, ctxt, _valueTypeSerializer);
            }
        }
    }

    private void serializeFilteredFields(Map<Range<Comparable<?>>, ?> rmap, JsonGenerator gen, SerializationContext provider)
            throws JacksonException
    {
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
            ValueSerializer<Object> valueSer;
            if (value == null) {
                // !!! TODO: null suppression?
                valueSer = provider.getDefaultNullValueSerializer();
            } else {
                valueSer = _valueSerializer;
            }
            prop.reset(key, value, _keySerializer, valueSer);
            try {
                filter.serializeAsProperty(rmap, gen, provider, prop);
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
            throws JacksonException
    {
        JsonMapFormatVisitor v2 = (visitor == null) ? null : visitor.expectMapFormat(typeHint);
        if (v2 != null) {
            v2.keyFormat(_keySerializer, _type.getKeyType());
            ValueSerializer<?> valueSer = _valueSerializer;
            final JavaType vt = _type.getContentType();
            final SerializationContext prov = visitor.getContext();
            if (valueSer == null) {
                valueSer = _findAndAddDynamic(_dynamicValueSerializers, vt, prov);
            }
            final ValueSerializer<?> valueSer2 = valueSer;
            final JavaType arrayType = prov.getTypeFactory().constructArrayType(vt);
            v2.valueFormat((v3, hint3) -> {
                JsonArrayFormatVisitor v4 = v3.expectArrayFormat(arrayType);
                if (v4 != null) {
                    v4.itemsFormat(valueSer2, vt);
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
    protected <X> RangeMap<Comparable<?>, X> _orderEntriesByKey(RangeMap<Comparable<?>, X> value, JsonGenerator gen, SerializationContext provider)
            throws JacksonException
    {
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

    protected final ValueSerializer<Object> _findAndAddDynamic(PropertySerializerMap map,
            Class<?> type, SerializationContext provider)
    {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, _property);
        // did we get a new map of serializers? If so, start using it
        if (map != result.map) {
            _dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }

    protected final ValueSerializer<Object> _findAndAddDynamic(PropertySerializerMap map,
            JavaType type, SerializationContext provider) {
        PropertySerializerMap.SerializerAndMapResult result = map.findAndAddSecondarySerializer(type, provider, _property);
        if (map != result.map) {
            _dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }
}
