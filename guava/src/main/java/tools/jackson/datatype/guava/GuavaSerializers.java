package tools.jackson.datatype.guava;

import java.io.Serializable;
import java.util.Set;

import com.google.common.collect.RangeMap;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import tools.jackson.databind.*;
import tools.jackson.databind.introspect.Annotated;
import tools.jackson.databind.jsontype.TypeSerializer;
import tools.jackson.databind.ser.Serializers;
import tools.jackson.databind.ser.std.ToStringSerializer;
import tools.jackson.databind.type.CollectionLikeType;
import tools.jackson.databind.type.MapLikeType;
import tools.jackson.databind.type.ReferenceType;
import tools.jackson.databind.ser.std.StdDelegatingSerializer;
import tools.jackson.databind.util.StdConverter;
import tools.jackson.datatype.guava.ser.*;
import tools.jackson.datatype.guava.ser.primitives.ImmutableDoubleArraySerializer;
import tools.jackson.datatype.guava.ser.primitives.ImmutableIntArraySerializer;
import tools.jackson.datatype.guava.ser.primitives.ImmutableLongArraySerializer;

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
import com.google.common.primitives.ImmutableDoubleArray;
import com.google.common.primitives.ImmutableIntArray;
import com.google.common.primitives.ImmutableLongArray;

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
            ReferenceType refType, BeanDescription.Supplier beanDescRef, JsonFormat.Value formatOverrides,
            TypeSerializer contentTypeSerializer, ValueSerializer<Object> contentValueSerializer)
    {
        if (refType.isTypeOrSubTypeOf(Optional.class)) {
            boolean staticTyping = (contentTypeSerializer == null)
                    && config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            return new GuavaOptionalSerializer(refType, staticTyping,
                    contentTypeSerializer, contentValueSerializer);
        }
        return null;
    }

    @Override
    public ValueSerializer<?> findSerializer(SerializationConfig config,
            JavaType type, BeanDescription.Supplier beanDescRef, JsonFormat.Value formatOverrides)
    {
        if (type.isTypeOrSubTypeOf(RangeSet.class)) {
            return new RangeSetSerializer();
        }
        if (type.isTypeOrSubTypeOf(Range.class)) {
            return new RangeSerializer(_findDeclared(type, Range.class));
        }
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
            return new StdDelegatingSerializer(FluentConverter.instance, iterableType, null, null);
        }
        if (type.isTypeOrSubTypeOf(ImmutableIntArray.class)) {
            return new ImmutableIntArraySerializer();
        }
        if (type.isTypeOrSubTypeOf(ImmutableLongArray.class)) {
            return new ImmutableLongArraySerializer();
        }
        if (type.isTypeOrSubTypeOf(ImmutableDoubleArray.class)) {
            return new ImmutableDoubleArraySerializer();
        }
        return null;
   }

    @Override
    public ValueSerializer<?> findMapLikeSerializer(SerializationConfig config,
            MapLikeType type, BeanDescription.Supplier beanDescRef, JsonFormat.Value formatOverrides,
            ValueSerializer<Object> keySerializer,
            TypeSerializer elementTypeSerializer, ValueSerializer<Object> elementValueSerializer)
    {
        if (type.isTypeOrSubTypeOf(Multimap.class)) {
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            Object filterId = intr.findFilterId(config, beanDescRef.getClassInfo());
            JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Multimap.class,
                    beanDescRef.getClassInfo());
            Set<String> ignored = (ignorals == null) ? null : ignorals.getIgnored();
            return new MultimapSerializer(type, beanDescRef,
                    keySerializer, elementTypeSerializer, elementValueSerializer, ignored, filterId);
        }
        if (type.isTypeOrSubTypeOf(RangeMap.class)) {
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            Object filterId = intr.findFilterId(config, beanDescRef.getClassInfo());
            JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(RangeMap.class,
                    beanDescRef.getClassInfo());
            Set<String> ignored = (ignorals == null) ? null : ignorals.getIgnored();
            return new RangeMapSerializer(type, beanDescRef,
                    keySerializer, elementTypeSerializer, elementValueSerializer, ignored, filterId);
        }
        if (type.isTypeOrSubTypeOf(Cache.class)) {
            final AnnotationIntrospector intr = config.getAnnotationIntrospector();
            Object filterId = intr.findFilterId(config, (Annotated)beanDescRef.getClassInfo());
            JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(Cache.class,
                    beanDescRef.getClassInfo());
            Set<String> ignored = (ignorals == null) ? null : ignorals.getIgnored();
            return new CacheSerializer(type, beanDescRef,
                keySerializer, elementTypeSerializer, elementValueSerializer, ignored, filterId);
        }
        if (type.isTypeOrSubTypeOf(Table.class)) {
            return new TableSerializer(type);
        }
        return null;
    }

    @Override
    public ValueSerializer<?> findCollectionLikeSerializer(SerializationConfig config,
            CollectionLikeType type, BeanDescription.Supplier beanDescRef, JsonFormat.Value formatOverrides, TypeSerializer elementTypeSerializer,
           ValueSerializer<Object> elementValueSerializer)
    {
        // 23-Jan-2026, tatu: Appears unneeded; commenting out won't fail any tests
        /*
        Class<?> raw = type.getRawClass();
        Optional<ValueSerializer<?>> primitiveSerializer = PrimitiveTypes.isAssignableFromPrimitive(raw)
                .transform((ignore) -> ToStringSerializer.instance);

        return primitiveSerializer
                .or(() -> super.findCollectionLikeSerializer(config, type, beanDescRef, formatOverrides, elementTypeSerializer, elementValueSerializer));
                */
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
