package com.fasterxml.jackson.datatype.eclipsecollections.ser.map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.collections.api.map.MapIterable;

/**
 * @author yawkat
 */
public final class RefRefMapSerializer extends ContainerSerializer<MapIterable<?, ?>> implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;

    private final JavaType _type;
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

    public RefRefMapSerializer(
            JavaType type,
            JsonSerializer<Object> keySerializer, TypeSerializer vts, JsonSerializer<Object> valueSerializer,
            Set<String> ignoredEntries
    ) {
        super(type.getRawClass(), false);
        _type = type;
        _property = null;
        _keySerializer = keySerializer;
        _valueTypeSerializer = vts;
        _valueSerializer = valueSerializer;
        _ignoredEntries = ignoredEntries;

        _dynamicValueSerializers = PropertySerializerMap.emptyForProperties();
    }

    @SuppressWarnings("unchecked")
    protected RefRefMapSerializer(
            RefRefMapSerializer src, BeanProperty property,
            JsonSerializer<?> keySerializer, TypeSerializer vts, JsonSerializer<?> valueSerializer,
            Set<String> ignoredEntries
    ) {
        super(src);
        _type = src._type;
        _property = property;
        _keySerializer = (JsonSerializer<Object>) keySerializer;
        _valueTypeSerializer = vts;
        _valueSerializer = (JsonSerializer<Object>) valueSerializer;
        _dynamicValueSerializers = src._dynamicValueSerializers;
        _ignoredEntries = ignoredEntries;
    }

    protected RefRefMapSerializer withResolved(
            BeanProperty property,
            JsonSerializer<?> keySer, TypeSerializer vts, JsonSerializer<?> valueSer,
            Set<String> ignored
    ) {
        return new RefRefMapSerializer(this, property, keySer, vts, valueSer,
                                       ignored);
    }

    @Override
    protected ContainerSerializer<?> _withValueTypeSerializer(TypeSerializer typeSer) {
        return new RefRefMapSerializer(this, _property, _keySerializer,
                                       typeSer, _valueSerializer, _ignoredEntries);
    }

    /*
    /**********************************************************
    /* Post-processing (contextualization)
    /**********************************************************
     */

    @Override
    public JsonSerializer<?> createContextual(
            SerializerProvider provider,
            BeanProperty property
    ) throws JsonMappingException {
        JsonSerializer<?> valueSer = _valueSerializer;
        if (valueSer == null) { // if type is final, can actually resolve:
            JavaType valueType = getContentType();
            if (valueType.isFinal()) {
                valueSer = provider.findValueSerializer(valueType, property);
            }
        } else if (valueSer instanceof ContextualSerializer) {
            valueSer = ((ContextualSerializer) valueSer).createContextual(provider, property);
        }

        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        final AnnotatedMember propertyAcc = (property == null) ? null : property.getMember();
        JsonSerializer<?> keySer = null;

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
        }
        if (valueSer == null) {
            valueSer = _valueSerializer;
        }
        // [datatype-guava#124]: May have a content converter
        valueSer = findContextualConvertingSerializer(provider, property, valueSer);
        if (valueSer == null) {
            // One more thing -- if explicit content type is annotated,
            //   we can consider it a static case as well.
            JavaType valueType = getContentType();
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
            keySer = provider.findKeySerializer(getKeyType(), property);
        } else {
            keySer = provider.handleSecondaryContextualization(keySer, property);
        }
        // finally, TypeSerializers may need contextualization as well
        TypeSerializer typeSer = _valueTypeSerializer;
        if (typeSer != null) {
            typeSer = typeSer.forProperty(property);
        }

        Set<String> ignored = _ignoredEntries;

        if (intr != null && propertyAcc != null) {
            JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnoralByName(provider.getConfig(), propertyAcc);
            if (ignorals != null) {
                Set<String> newIgnored = ignorals.findIgnoredForSerialization();
                if ((newIgnored != null) && !newIgnored.isEmpty()) {
                    ignored = (ignored == null) ? new HashSet<>() : new HashSet<>(ignored);
                    ignored.addAll(newIgnored);
                }
            }
        }
        return withResolved(property, keySer, typeSer, valueSer,
                            ignored);
    }

    @Override
    public JsonSerializer<?> getContentSerializer() {
        return _valueSerializer;
    }

    protected JavaType getKeyType() {
        JavaType[] typeParameters = _type.findTypeParameters(MapIterable.class);
        return typeParameters.length > 0 ? typeParameters[0] : TypeFactory.unknownType();
    }

    @Override
    public JavaType getContentType() {
        JavaType[] typeParameters = _type.findTypeParameters(MapIterable.class);
        return typeParameters.length > 1 ? typeParameters[1] : TypeFactory.unknownType();
    }

    @Override
    public boolean hasSingleElement(MapIterable<?, ?> map) {
        return map.size() == 1;
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, MapIterable<?, ?> value) {
        return value.isEmpty();
    }

    @Override
    public void serialize(MapIterable<?, ?> value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject(value);
        if (!value.isEmpty()) {
            serializeFields(value, gen, provider);
        }
        gen.writeEndObject();
    }

    @Override
    public void serializeWithType(
            MapIterable<?, ?> value, JsonGenerator gen,
            SerializerProvider provider, TypeSerializer typeSer
    ) throws IOException {
        gen.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen,
                                                           typeSer.typeId(value, JsonToken.START_OBJECT));
        if (!value.isEmpty()) {
            serializeFields(value, gen, provider);
        }
        typeSer.writeTypeSuffix(gen, typeIdDef);
    }

    private final void serializeFields(MapIterable<?, ?> mmap, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        Set<String> ignored = _ignoredEntries;
        PropertySerializerMap serializers = _dynamicValueSerializers;
        mmap.forEachKeyValue((key, v) -> {
            try {
                // First, serialize key
                if ((ignored != null) && ignored.contains(key)) {
                    return;
                }
                if (key == null) {
                    provider.findNullKeySerializer(getKeyType(), _property)
                            .serialize(null, gen, provider);
                } else {
                    _keySerializer.serialize(key, gen, provider);
                }
                if (v == null) {
                    provider.defaultSerializeNull(gen);
                    return;
                }
                JsonSerializer<Object> valueSer = _valueSerializer;
                if (valueSer == null) {
                    Class<?> cc = v.getClass();
                    valueSer = serializers.serializerFor(cc);
                    if (valueSer == null) {
                        valueSer = _findAndAddDynamic(serializers, cc, provider);
                    }
                }
                if (_valueTypeSerializer == null) {
                    valueSer.serialize(v, gen, provider);
                } else {
                    valueSer.serializeWithType(v, gen, provider, _valueTypeSerializer);
                }
            } catch (IOException e) {
                PrimitiveMapSerializer.rethrowUnchecked(e);
            }
        });
    }

    /*
    /**********************************************************
    /* Internal helper methods
    /**********************************************************
     */

    protected final JsonSerializer<Object> _findAndAddDynamic(
            PropertySerializerMap map,
            Class<?> type, SerializerProvider provider
    ) throws JsonMappingException {
        PropertySerializerMap.SerializerAndMapResult result =
                map.findAndAddSecondarySerializer(type, provider, _property);
        // did we get a new map of serializers? If so, start using it
        if (map != result.map) {
            _dynamicValueSerializers = result.map;
        }
        return result.serializer;
    }
}
