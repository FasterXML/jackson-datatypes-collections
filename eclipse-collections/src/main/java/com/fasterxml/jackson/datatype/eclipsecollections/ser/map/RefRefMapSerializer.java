package com.fasterxml.jackson.datatype.eclipsecollections.ser.map;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.eclipse.collections.api.map.MapIterable;

/**
 * @author yawkat
 */
public final class RefRefMapSerializer extends ContainerSerializer<MapIterable<?, ?>>
{
    private final JavaType _type;
    private final JavaType _keyType, _valueType;

    private final JsonSerializer<Object> _keySerializer;
    private final TypeSerializer _valueTypeSerializer;
    private final JsonSerializer<Object> _valueSerializer;

    /**
     * Set of entries to omit during serialization, if any
     */
    protected final Set<String> _ignoredEntries;

    public RefRefMapSerializer(
            JavaType type,
            JsonSerializer<Object> keySerializer, TypeSerializer vts, JsonSerializer<Object> valueSerializer,
            Set<String> ignoredEntries
    ) {
        super(type, null);
        _type = type;
        JavaType[] typeParameters = _type.findTypeParameters(MapIterable.class);
        _keyType = (typeParameters.length > 0) ? typeParameters[0] : TypeFactory.unknownType();
        _valueType = (typeParameters.length > 1) ? typeParameters[1] : TypeFactory.unknownType();
        _keySerializer = keySerializer;
        _valueTypeSerializer = vts;
        _valueSerializer = valueSerializer;
        _ignoredEntries = ignoredEntries;
    }

    @SuppressWarnings("unchecked")
    protected RefRefMapSerializer(
            RefRefMapSerializer src, BeanProperty property,
            JsonSerializer<?> keySerializer, TypeSerializer vts, JsonSerializer<?> valueSerializer,
            Set<String> ignoredEntries
    ) {
        super(src, property);
        _type = src._type;
        _keyType = src._keyType;
        _valueType = src._valueType;
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
    public JsonSerializer<?> createContextual(SerializerProvider provider,
            BeanProperty property) throws JsonMappingException
    {
        JsonSerializer<?> valueSer = _valueSerializer;
        if (valueSer == null) { // if type is final, can actually resolve:
            JavaType valueType = getContentType();
            if (valueType.isFinal()) {
                valueSer = provider.findSecondaryPropertySerializer(valueType, property);
            }
        } else {
            valueSer = valueSer.createContextual(provider, property);
        }

        final AnnotationIntrospector intr = provider.getAnnotationIntrospector();
        final AnnotatedMember propertyAcc = (property == null) ? null : property.getMember();
        JsonSerializer<?> keySer = null;

        // First: if we have a property, may have property-annotation overrides
        if (propertyAcc != null && intr != null) {
            Object serDef = intr.findKeySerializer(provider.getConfig(), propertyAcc);
            if (serDef != null) {
                keySer = provider.serializerInstance(propertyAcc, serDef);
            }
            serDef = intr.findContentSerializer(provider.getConfig(), propertyAcc);
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
                valueSer = provider.findSecondaryPropertySerializer(valueType, property);
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
            typeSer = typeSer.forProperty(provider, property);
        }

        Set<String> ignored = _ignoredEntries;

        if (intr != null && propertyAcc != null) {
            JsonIgnoreProperties.Value ignorals = intr.findPropertyIgnorals(propertyAcc);
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
        return _keyType;
    }

    @Override
    public JavaType getContentType() {
        return _valueType;
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
            SerializerProvider ctxt, TypeSerializer typeSer
    ) throws IOException {
        gen.setCurrentValue(value);
        WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, ctxt,
                typeSer.typeId(value, JsonToken.START_OBJECT));
        if (!value.isEmpty()) {
            serializeFields(value, gen, ctxt);
        }
        typeSer.writeTypeSuffix(gen, ctxt, typeIdDef);
    }

    private final void serializeFields(MapIterable<?, ?> mmap, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        Set<String> ignored = _ignoredEntries;
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
                    provider.defaultSerializeNullValue(gen);
                    return;
                }
                JsonSerializer<Object> valueSer = _valueSerializer;
                if (valueSer == null) {
                    valueSer = _findSerializer(provider, v);
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

    private final JsonSerializer<Object> _findSerializer(SerializerProvider ctxt,
            Object value) throws JsonMappingException
    {
        final Class<?> cc = value.getClass();
        JsonSerializer<Object> valueSer = _dynamicValueSerializers.serializerFor(cc);
        if (valueSer != null) {
            return valueSer;
        }
        if (_valueType.hasGenericTypes()) {
            return _findAndAddDynamic(ctxt,
                    ctxt.constructSpecializedType(_valueType, cc));
        }
        return _findAndAddDynamic(ctxt, cc);
    }
}
