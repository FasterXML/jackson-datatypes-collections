package com.fasterxml.jackson.datatype.eclipsecollections.deser.pair;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.CreatorProperty;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;
import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import org.eclipse.collections.api.tuple.Triple;
import org.eclipse.collections.api.tuple.Triplet;
import org.eclipse.collections.impl.tuple.Tuples;

public class TripleInstantiators extends ValueInstantiators.Base {
    {
        // fail early if class does not exist
        Triple.class.getName();
    }

    @Override
    public ValueInstantiator findValueInstantiator(
            DeserializationConfig config,
            BeanDescription beanDesc,
            ValueInstantiator defaultInstantiator
    ) {
        if (beanDesc.getBeanClass() == Triple.class) {
            JavaType beanType = beanDesc.getType();
            return new TripleInstantiator(
                    beanType,
                    beanType.containedType(0),
                    beanType.containedType(1),
                    beanType.containedType(2));
        }
        if (beanDesc.getBeanClass() == Triplet.class) {
            JavaType beanType = beanDesc.getType();
            JavaType singleType = beanType.containedType(0);
            return new TripleInstantiator(beanType, singleType, singleType, singleType);
        }
        return super.findValueInstantiator(config, beanDesc, defaultInstantiator);
    }

    private static class TripleInstantiator extends ValueInstantiator.Base {
        private final JavaType oneType;
        private final JavaType twoType;
        private final JavaType threeType;

        private TripleInstantiator(JavaType beanType, JavaType oneType, JavaType twoType, JavaType threeType) {
            super(beanType);
            this.oneType = oneType;
            this.twoType = twoType;
            this.threeType = threeType;
        }

        @Override
        public boolean canCreateFromObjectWith() {
            return true;
        }

        @Override
        public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
            try {
                return new SettableBeanProperty[]{
                        CreatorProperty.construct(
                                PropertyName.construct("one"),
                                oneType,
                                null,
                                config.findTypeDeserializer(oneType),
                                AnnotationCollector.emptyAnnotations(),
                                null, 0, null, PropertyMetadata.STD_REQUIRED
                        ),
                        CreatorProperty.construct(
                                PropertyName.construct("two"),
                                twoType,
                                null,
                                config.findTypeDeserializer(twoType),
                                AnnotationCollector.emptyAnnotations(),
                                null, 1, null, PropertyMetadata.STD_REQUIRED
                        ),
                        CreatorProperty.construct(
                                PropertyName.construct("three"),
                                threeType,
                                null,
                                config.findTypeDeserializer(threeType),
                                AnnotationCollector.emptyAnnotations(),
                                null, 2, null, PropertyMetadata.STD_REQUIRED
                        )
                };
            } catch (JsonMappingException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
            return Tuples.triple(args[0], args[1], args[2]);
        }
    }
}
