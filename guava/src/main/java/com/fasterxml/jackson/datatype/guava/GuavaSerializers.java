package com.fasterxml.jackson.datatype.guava;

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
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.Table;
import com.google.common.hash.HashCode;
import com.google.common.net.HostAndPort;
import com.google.common.net.InternetDomainName;
import com.fasterxml.jackson.datatype.guava.ser.GuavaOptionalSerializer;
import com.fasterxml.jackson.datatype.guava.ser.MultimapSerializer;
import com.fasterxml.jackson.datatype.guava.ser.RangeSerializer;
import com.fasterxml.jackson.datatype.guava.ser.TableSerializer;

public class GuavaSerializers extends Serializers.Base
{
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
        Class<?> raw = type.getRawClass();
        if (Range.class.isAssignableFrom(raw)) {
            return new RangeSerializer(_findDeclared(type, Range.class));
        }
        if (Table.class.isAssignableFrom(raw)) {
            return new TableSerializer(_findDeclared(type, Table.class));
        }

        // since 2.4
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
            return new StdDelegatingSerializer(FluentConverter.instance, iterableType, null);
        }
        return super.findSerializer(config, type, beanDesc);
    }

    @Override
    public JsonSerializer<?> findMapLikeSerializer(SerializationConfig config,
            MapLikeType type, BeanDescription beanDesc, JsonSerializer<Object> keySerializer,
            TypeSerializer elementTypeSerializer, JsonSerializer<Object> elementValueSerializer)
    {
        if (Multimap.class.isAssignableFrom(type.getRawClass())) {
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            Object filterId = intr.findFilterId((Annotated)beanDesc.getClassInfo());
            JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Multimap.class,
                    beanDesc.getClassInfo());
            Set<String> ignored = (ignorals == null) ? null : ignorals.getIgnored();
            return new MultimapSerializer(type, beanDesc,
                    keySerializer, elementTypeSerializer, elementValueSerializer, ignored, filterId);
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
