package com.fasterxml.jackson.datatype.eclipsecollections.deser.pair;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.util.Annotations;

import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author yawkat
 */
public final class PairInstantiators extends ValueInstantiators.Base {
    private static final Map<Class<?>, ValueInstantiator> PURE_PRIMITIVE_INSTANTIATORS =
            new HashMap<>();

    private static final Map<Class<?>, Function<JavaType, ValueInstantiator>> KEY_OR_VALUE_OBJECT_LAMBDAS =
            new HashMap<>();

    @Override
    public ValueInstantiator findValueInstantiator(
            DeserializationConfig config, BeanDescription beanDesc,
            ValueInstantiator defaultInstantiator
    ) {
        Class<?> beanClass = beanDesc.getBeanClass();
        ValueInstantiator purePrimitive = PURE_PRIMITIVE_INSTANTIATORS.get(beanClass);
        if (purePrimitive != null) {
            return purePrimitive;
        }

        JavaType beanType = beanDesc.getType();

        Function<JavaType, ValueInstantiator> keyOrValueObjectLambda =
                KEY_OR_VALUE_OBJECT_LAMBDAS.get(beanClass);
        if (keyOrValueObjectLambda != null) {
            return keyOrValueObjectLambda.apply(beanType);
        }

        if (beanClass == Pair.class) {
            return new ValueInstantiator.Base(beanType) {
                @Override
                public boolean canCreateFromObjectWith() {
                    return true;
                }

                @Override
                public SettableBeanProperty[] getFromObjectArguments(DeserializationContext ctxt) {
                    JavaType oneType = beanType.containedType(0);
                    JavaType twoType = beanType.containedType(1);
                    return makeProperties(ctxt, oneType, twoType);
                }

                @Override
                public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                    return Tuples.pair(args[0], args[1]);
                }
            };
        }

        if (beanClass == Twin.class) {
            return new ValueInstantiator.Base(beanType) {
                @Override
                public boolean canCreateFromObjectWith() {
                    return true;
                }

                @Override
                public SettableBeanProperty[] getFromObjectArguments(DeserializationContext ctxt) {
                    JavaType memberType = beanType.containedType(0);
                    return makeProperties(ctxt, memberType, memberType);
                }

                @Override
                public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                    return Tuples.twin(args[0], args[1]);
                }
            };
        }

        return defaultInstantiator;
    }

    @SuppressWarnings("unused") // Used from PairInstantiatorsPopulator
    static void add(Class<?> objectKeyOrValuePairClass,
            Function<JavaType, ValueInstantiator> lambda) {
        KEY_OR_VALUE_OBJECT_LAMBDAS.put(objectKeyOrValuePairClass, lambda);
    }

    @SuppressWarnings("unused") // Used from PairInstantiatorsPopulator
    static <P> ValueInstantiator primitiveObjectInstantiator(
            JavaType inputType, Class<?> one,
            BiFunction<Object, Object, P> factory
    ) {
        return new ValueInstantiator.Base(inputType) {
            @Override
            public boolean canCreateFromObjectWith() {
                return true;
            }

            @Override
            public SettableBeanProperty[] getFromObjectArguments(DeserializationContext ctxt) {
                JavaType oneType = ctxt.constructType(one);
                JavaType twoType = inputType.containedType(0);
                return makeProperties(ctxt, oneType, twoType);
            }

            @Override
            public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                return factory.apply(args[0], args[1]);
            }
        };
    }

    @SuppressWarnings("unused") // Used from PairInstantiatorsPopulator
    static <P> ValueInstantiator objectPrimitiveInstantiator(
            JavaType inputType, Class<?> two,
            BiFunction<Object, Object, P> factory
    ) {
        return new ValueInstantiator.Base(inputType) {
            @Override
            public boolean canCreateFromObjectWith() {
                return true;
            }

            @Override
            public SettableBeanProperty[] getFromObjectArguments(DeserializationContext ctxt) {
                JavaType oneType = inputType.containedType(0);
                JavaType twoType = ctxt.constructType(two);
                return makeProperties(ctxt, oneType, twoType);
            }

            @Override
            public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                return factory.apply(args[0], args[1]);
            }
        };
    }

    @SuppressWarnings("unused") // Used from PairInstantiatorsPopulator
    static <P> void purePrimitiveInstantiator(
            Class<P> pairClass, Class<?> one, Class<?> two,
            BiFunction<Object, Object, P> factory
    ) {
        PURE_PRIMITIVE_INSTANTIATORS.put(pairClass, new ValueInstantiator.Base(pairClass) {
            @Override
            public boolean canCreateFromObjectWith() {
                return true;
            }

            @Override
            public SettableBeanProperty[] getFromObjectArguments(DeserializationContext ctxt) {
                JavaType oneType = ctxt.constructType(one);
                JavaType twoType = ctxt.constructType(two);
                return makeProperties(ctxt, oneType, twoType);
            }

            @Override
            public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                return factory.apply(args[0], args[1]);
            }
        });
    }

    private static SettableBeanProperty[] makeProperties(
            DeserializationContext ctxt,
            JavaType oneType,
            JavaType twoType
    ) {
        try {
            /*
    public static CreatorProperty construct(PropertyName name, JavaType type, PropertyName wrapperName,
            TypeDeserializer typeDeser,
            Annotations contextAnnotations, AnnotatedParameter param,
            int index, JacksonInject.Value injectable,
            PropertyMetadata metadata)
             */
            return new SettableBeanProperty[]{
                    CreatorProperty.construct(
                            PropertyName.construct("one"), oneType, null,
                            ctxt.findTypeDeserializer(oneType),
                            AnnotationCollector.emptyAnnotations(), null,
                            0, null, PropertyMetadata.STD_REQUIRED
                    ),
                    CreatorProperty.construct(
                            PropertyName.construct("two"), twoType, null,
                            ctxt.findTypeDeserializer(twoType),
                            AnnotationCollector.emptyAnnotations(), null,
                            1, null, PropertyMetadata.STD_REQUIRED
                    )
            };
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        PairInstantiatorsPopulator.populate();
    }
}
