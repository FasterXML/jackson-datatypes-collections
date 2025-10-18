package com.fasterxml.jackson.datatype.guava;

import com.fasterxml.jackson.datatype.guava.ser.RangeMapSerializer;
import com.google.common.collect.RangeMap;
import java.io.Serializable;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.fasterxml.jackson.datatype.guava.ser.RangeSetSerializer;
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
import com.fasterxml.jackson.datatype.guava.ser.CacheSerializer;
import com.fasterxml.jackson.datatype.guava.ser.GuavaOptionalSerializer;
import com.fasterxml.jackson.datatype.guava.ser.MultimapSerializer;
import com.fasterxml.jackson.datatype.guava.ser.RangeSerializer;
import com.fasterxml.jackson.datatype.guava.ser.TableSerializer;

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
    public JsonSerializer<?> findReferenceSerializer(SerializationConfig config,
            ReferenceType refType, BeanDescription beanDesc,
            TypeSerializer contentTypeSerializer, JsonSerializer<Object> contentValueSerializer)
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
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc)
    {
        if (type.isTypeOrSubTypeOf(RangeSet.class)) {
            return new RangeSetSerializer();
        }
        if (type.isTypeOrSubTypeOf(Range.class)) {
            return new RangeSerializer(_findDeclared(type, Range.class));
        }

        // since 2.4
        if (type.isTypeOrSubTypeOf(HostAndPort.class)) {
            return ToStringSerializer.instance;
        }
        if (type.isTypeOrSubTypeOf(InternetDomainName.class)) {
            return ToStringSerializer.instance;
        }
        // not sure how useful, but why not?
        if (type.isTypeOrSubTypeOf(CacheBuilderSpec.class) || type.isTypeOrSubTypeOf(CacheBuilder.class)) {
            return ToStringSerializer.instance;
        }
        if (type.isTypeOrSubTypeOf(HashCode.class)) {
            return ToStringSerializer.instance;
        }
        if (type.isTypeOrSubTypeOf(FluentIterable.class)) {
            JavaType iterableType = _findDeclared(type, Iterable.class);
            return new StdDelegatingSerializer(FluentConverter.instance, iterableType, null);
        }
        return super.findSerializer(config, type, beanDesc);
    }

    @Override
    public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config,
            MapLikeType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer,
            TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
    {
        if (type.isTypeOrSubTypeOf(Multimap.class)) {
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            Object filterId = intr.findFilterId((Annotated)beanDesc.getClassInfo());
            JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Multimap.class,
                    beanDesc.getClassInfo());
            Set<String> ignored = (ignorals == null) ? null : ignorals.getIgnored();
            return new MultimapSerializer(type, beanDesc,
                    keySerializer, elementTypeSerializer, elementValueSerializer, ignored, filterId);
        }
        if (type.isTypeOrSubTypeOf(RangeMap.class)) {
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            Object filterId = intr.findFilterId(beanDesc.getClassInfo());
            JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(RangeMap.class,
                    beanDesc.getClassInfo());
            Set<String> ignored = (ignorals == null) ? null : ignorals.getIgnored();
            return new RangeMapSerializer(type, beanDesc,
                    keySerializer, elementTypeSerializer, elementValueSerializer, ignored, filterId);
        }
        if (type.isTypeOrSubTypeOf(Cache.class)) {
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            Object filterId = intr.findFilterId((Annotated)beanDesc.getClassInfo());
            JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Cache.class,
                beanDesc.getClassInfo());
            Set<String> ignored = (ignorals == null) ? null : ignorals.getIgnored();
            return new CacheSerializer(type, beanDesc,
                keySerializer, elementTypeSerializer, elementValueSerializer, ignored, filterId);
        }
        if (type.isTypeOrSubTypeOf(Table.class)) {
            return new TableSerializer(type);
        }
        return null;
    }

    private JavaType _findDeclared(JavaType subtype, Class<?> target) {
        JavaType decl = subtype.findSuperType(target);
        if (decl == null) { // should never happen but
            throw new IllegalArgumentException("Strange "+target.getName()+" sub-type, "+subtype+", can not find type parameters");
        }
        return decl;
    }
}
