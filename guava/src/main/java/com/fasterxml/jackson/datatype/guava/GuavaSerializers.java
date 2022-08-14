package com.fasterxml.jackson.datatype.guava;

import tools.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.guava.util.ImmutablePrimitiveTypes;
import com.fasterxml.jackson.datatype.guava.util.PrimitiveTypes;
import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import tools.jackson.databind.*;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.Serializers;
import tools.jackson.databind.ser.std.ToStringSerializer;
import tools.jackson.databind.type.MapLikeType;
import tools.jackson.databind.type.ReferenceType;
import tools.jackson.databind.ser.std.StdDelegatingSerializer;
import tools.jackson.databind.util.StdConverter;
import com.fasterxml.jackson.datatype.guava.ser.RangeSetSerializer;
import com.fasterxml.jackson.datatype.guava.ser.CacheSerializer;
import com.fasterxml.jackson.datatype.guava.ser.GuavaOptionalSerializer;
import com.fasterxml.jackson.datatype.guava.ser.MultimapSerializer;
import com.fasterxml.jackson.datatype.guava.ser.RangeSerializer;
import com.fasterxml.jackson.datatype.guava.ser.TableSerializer;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.Table;
import com.google.common.hash.HashCode;
import com.google.common.net.HostAndPort;
import com.google.common.net.InternetDomainName;

public class GuavaSerializers extends Serializers.Base
    implements Serializable
{
    static final long serialVersionUID = 1L;

    static class FluentConverter extends StdConverter<Object,Iterable<?>> {
        static final FluentConverter instance = new FluentConverter();

        @Override
        public Iterable<?> convert(Object value) {
            return (Iterable<?>) value;
        }
    }

    @Override
    public ValueSerializer<?> findReferenceSerializer(SerializationConfig config,
            ReferenceType refType, BeanDescription beanDesc, JsonFormat.Value formatOverrides,
            TypeSerializer contentTypeSerializer, ValueSerializer<Object> contentValueSerializer)
    {
        final Class<?> raw = refType.getRawClass();
        if (Optional.class.isAssignableFrom(raw)) {
            boolean staticTyping = (contentTypeSerializer == null)
                    && config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            return new GuavaOptionalSerializer(refType, staticTyping,
                    contentTypeSerializer, contentValueSerializer);
        }
        return null;
    }

    @Override
    public ValueSerializer<?> findSerializer(SerializationConfig config, JavaType type,
            BeanDescription beanDesc, JsonFormat.Value formatOverrides)
    {
        Class<?> raw = type.getRawClass();
        if (RangeSet.class.isAssignableFrom(raw)) {
            return new RangeSetSerializer();
        }
        if (Range.class.isAssignableFrom(raw)) {
            return new RangeSerializer(_findDeclared(type, Range.class));
        }
        if (Table.class.isAssignableFrom(raw)) {
            return new TableSerializer(_findDeclared(type, Table.class));
        }
        if (HostAndPort.class.isAssignableFrom(raw)) {
            return ToStringSerializer.instance;
        }
        if (InternetDomainName.class.isAssignableFrom(raw)) {
            return ToStringSerializer.instance;
        }
        // not sure how useful, but why not?
        if (CacheBuilderSpec.class.isAssignableFrom(raw) || CacheBuilder.class.isAssignableFrom(raw)) {
            return ToStringSerializer.instance;
        }
        if (HashCode.class.isAssignableFrom(raw)) {
            return ToStringSerializer.instance;
        }
        if (FluentIterable.class.isAssignableFrom(raw)) {
            JavaType iterableType = _findDeclared(type, Iterable.class);
            return new StdDelegatingSerializer(FluentConverter.instance, iterableType, null, null);
        }
        // [datatypes-collections#90]: add bogus "serialize as empty" serializer to avoid
        // error on "no properties". If proper serialization (and deserialization) needed,
        // would need to resolve type parameters here
        if (Cache.class.isAssignableFrom(raw)) {
            return new CacheSerializer();
        }
        return ImmutablePrimitiveTypes.isAssignableFromImmutableArray(raw)
                .transform(ImmutablePrimitiveTypes.ImmutablePrimitiveArrays::newSerializer)
                .orNull();
    }

    @Override
    public ValueSerializer<?> findMapLikeSerializer(SerializationConfig config,
            MapLikeType type, BeanDescription beanDesc, JsonFormat.Value formatOverrides,
            ValueSerializer<Object> keySerializer,
            TypeSerializer elementTypeSerializer, ValueSerializer<Object> elementValueSerializer)
    {
        if (Multimap.class.isAssignableFrom(type.getRawClass())) {
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            Object filterId = intr.findFilterId(config, (Annotated)beanDesc.getClassInfo());
            JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Multimap.class,
                    beanDesc.getClassInfo());
            Set<String> ignored = (ignorals == null) ? null : ignorals.getIgnored();
            return new MultimapSerializer(type, beanDesc,
                    keySerializer, elementTypeSerializer, elementValueSerializer, ignored, filterId);
        }
        return null;
    }

    @Override
    public ValueSerializer<?> findCollectionLikeSerializer(SerializationConfig config, CollectionLikeType type,
            BeanDescription beanDesc, JsonFormat.Value formatOverrides, TypeSerializer elementTypeSerializer,
           ValueSerializer<Object> elementValueSerializer)
    {
        Class<?> raw = type.getRawClass();
        Optional<ValueSerializer<?>> primitiveSerializer = PrimitiveTypes.isAssignableFromPrimitive(raw)
                .transform((ignore) -> ToStringSerializer.instance);

        return primitiveSerializer
                .or(() -> super.findCollectionLikeSerializer(config, type, beanDesc, formatOverrides, elementTypeSerializer, elementValueSerializer));
    }

    private JavaType _findDeclared(JavaType subtype, Class<?> target) {
            JavaType decl = subtype.findSuperType(target);
        if (decl == null) { // should never happen but
            throw new IllegalArgumentException("Strange "+target.getName()+" sub-type, "+subtype+", can not find type parameters");
        }
        return decl;
    }
}
