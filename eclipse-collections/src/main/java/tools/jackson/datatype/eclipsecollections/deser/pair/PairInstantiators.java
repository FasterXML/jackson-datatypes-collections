package tools.jackson.datatype.eclipsecollections.deser.pair;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.CreatorProperty;
import tools.jackson.databind.deser.SettableBeanProperty;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.deser.ValueInstantiators;
import tools.jackson.databind.introspect.AnnotationCollector;

import tools.jackson.databind.jsontype.TypeDeserializer;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.api.tuple.Twin;
import org.eclipse.collections.impl.tuple.Tuples;

/**
 * @author yawkat
 */
public final class PairInstantiators extends ValueInstantiators.Base {
    private static final Map<Class<?>, ValueInstantiator> PURE_PRIMITIVE_INSTANTIATORS =
            new HashMap<>();

    private static final List<Class<?>> ALL_PAIR_CLASSES =
            new ArrayList<>();

    public static List<Class<?>> getAllPairClasses() {
        return Collections.unmodifiableList(ALL_PAIR_CLASSES);
    }

    private static final Map<Class<?>, Function<JavaType, ValueInstantiator>> KEY_OR_VALUE_OBJECT_LAMBDAS =
            new HashMap<>();

    @Override
    public ValueInstantiator findValueInstantiator(
            DeserializationConfig config, BeanDescription.Supplier beanDescRef
    ) {
        Class<?> beanClass = beanDescRef.getBeanClass();
        ValueInstantiator purePrimitive = PURE_PRIMITIVE_INSTANTIATORS.get(beanClass);
        if (purePrimitive != null) {
            return purePrimitive;
        }

        JavaType beanType = beanDescRef.getType();

        Function<JavaType, ValueInstantiator> keyOrValueObjectLambda =
                KEY_OR_VALUE_OBJECT_LAMBDAS.get(beanClass);
        if (keyOrValueObjectLambda != null) {
            return keyOrValueObjectLambda.apply(beanType);
        }

        // object->object
        if (beanClass == Pair.class) {
            return new PairInstantiator(beanType) {
                @Override
                public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                    return Tuples.pair(args[0], args[1]);
                }

                @Override
                JavaType oneType(DeserializationConfig config) {
                    return beanType.containedType(0);
                }

                @Override
                JavaType twoType(DeserializationConfig config) {
                    return beanType.containedType(1);
                }
            };
        }

        // object->object
        if (beanClass == Twin.class) {
            return new PairInstantiator(beanType) {
                @Override
                public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                    return Tuples.twin(args[0], args[1]);
                }

                @Override
                JavaType oneType(DeserializationConfig config) {
                    return beanType.containedType(0);
                }

                @Override
                JavaType twoType(DeserializationConfig config) {
                    return beanType.containedType(0);
                }
            };
        }

        return null;
    }

    @SuppressWarnings("unused") // Used from PairInstantiatorsPopulator
    static void add(Class<?> objectKeyOrValuePairClass,
            Function<JavaType, ValueInstantiator> lambda) {
        ALL_PAIR_CLASSES.add(objectKeyOrValuePairClass);
        KEY_OR_VALUE_OBJECT_LAMBDAS.put(objectKeyOrValuePairClass, lambda);
    }

    /**
     * primitive->object
     */
    @SuppressWarnings("unused") // Used from PairInstantiatorsPopulator
    static <P> ValueInstantiator primitiveObjectInstantiator(
            JavaType inputType, Class<?> one,
            BiFunction<Object, Object, P> factory
    ) {
        return new PairInstantiator(inputType) {
            @Override
            public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                return factory.apply(args[0], args[1]);
            }

            @Override
            JavaType oneType(DeserializationConfig config) {
                return config.constructType(one);
            }

            @Override
            JavaType twoType(DeserializationConfig config) {
                return inputType.containedType(0);
            }
        };
    }

    /**
     * object->primitive
     */
    @SuppressWarnings("unused") // Used from PairInstantiatorsPopulator
    static <P> ValueInstantiator objectPrimitiveInstantiator(
            JavaType inputType, Class<?> two,
            BiFunction<Object, Object, P> factory
    ) {
        return new PairInstantiator(inputType) {
            @Override
            public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                return factory.apply(args[0], args[1]);
            }

            @Override
            JavaType oneType(DeserializationConfig config) {
                return inputType.containedType(0);
            }

            @Override
            JavaType twoType(DeserializationConfig config) {
                return config.constructType(two);
            }
        };
    }

    /**
     * primitive->primitive
     */
    @SuppressWarnings("unused") // Used from PairInstantiatorsPopulator
    static <P> void purePrimitiveInstantiator(
            Class<P> pairClass, Class<?> one, Class<?> two,
            BiFunction<Object, Object, P> factory
    ) {
        ALL_PAIR_CLASSES.add(pairClass);
        PURE_PRIMITIVE_INSTANTIATORS.put(pairClass, new PairInstantiator(pairClass) {
            @Override
            public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
                return factory.apply(args[0], args[1]);
            }

            @Override
            JavaType oneType(DeserializationConfig config) {
                return config.constructType(one);
            }

            @Override
            JavaType twoType(DeserializationConfig config) {
                return config.constructType(two);
            }
        });
    }

    static abstract class PairInstantiator extends ValueInstantiator.Base {
        public PairInstantiator(Class<?> type) {
            super(type);
        }

        public PairInstantiator(JavaType type) {
            super(type);
        }

        abstract JavaType oneType(DeserializationConfig config);
        abstract JavaType twoType(DeserializationConfig config);

        @Override
        public final boolean canCreateFromObjectWith() {
            return true;
        }

        @Override
        public final SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
            return getFromObjectArguments(config, null, null);
        }

        final SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config,
                                                            TypeDeserializer typeDeserOne,
                                                            TypeDeserializer typeDeserTwo) {
            return new SettableBeanProperty[]{
                    CreatorProperty.construct(
                            PropertyName.construct("one"), oneType(config), null,
                            typeDeserOne,
                            AnnotationCollector.emptyAnnotations(), null,
                            0, null, PropertyMetadata.STD_REQUIRED
                    ),
                    CreatorProperty.construct(
                            PropertyName.construct("two"), twoType(config), null,
                            typeDeserTwo,
                            AnnotationCollector.emptyAnnotations(), null,
                            1, null, PropertyMetadata.STD_REQUIRED
                    )
            };
        }

        @Override
        public ValueInstantiator createContextual(DeserializationContext ctxt,
                BeanDescription.Supplier beanDescRef)
        {
            TypeDeserializer typeDeserOne = ctxt.findTypeDeserializer(oneType(ctxt.getConfig()));
            TypeDeserializer typeDeserTwo = ctxt.findTypeDeserializer(twoType(ctxt.getConfig()));
            return new ValueInstantiator.Delegating(this) {
                private static final long serialVersionUID = 1L;

                @Override
                public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
                    return PairInstantiator.this.getFromObjectArguments(config, typeDeserOne, typeDeserTwo);
                }
            };
        }
    }

    static {
        PairInstantiatorsPopulator.populate();
        // these ones get special handling
        ALL_PAIR_CLASSES.add(Pair.class);
        ALL_PAIR_CLASSES.add(Twin.class);
    }
}
