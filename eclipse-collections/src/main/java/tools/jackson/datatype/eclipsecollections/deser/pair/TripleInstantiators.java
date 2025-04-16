package tools.jackson.datatype.eclipsecollections.deser.pair;

import tools.jackson.databind.*;
import tools.jackson.databind.deser.CreatorProperty;
import tools.jackson.databind.deser.SettableBeanProperty;
import tools.jackson.databind.deser.ValueInstantiator;
import tools.jackson.databind.deser.ValueInstantiators;
import tools.jackson.databind.introspect.AnnotationCollector;
import tools.jackson.databind.jsontype.TypeDeserializer;
import org.eclipse.collections.api.tuple.Triple;
import org.eclipse.collections.api.tuple.Triplet;
import org.eclipse.collections.impl.tuple.Tuples;

public class TripleInstantiators extends ValueInstantiators.Base {
    {
        // fail early if class does not exist
        Triple.class.getName();
    }

    @Override
    public ValueInstantiator findValueInstantiator(DeserializationConfig config,
            BeanDescription.Supplier beanDescRef)
    {
        if (beanDescRef.getBeanClass() == Triple.class) {
            JavaType beanType = beanDescRef.getType();
            return new TripleInstantiator(
                    beanType,
                    beanType.containedType(0),
                    beanType.containedType(1),
                    beanType.containedType(2),
                    // type deserializers are filled in by createContextual
                    null, null, null
            );
        }
        if (beanDescRef.getBeanClass() == Triplet.class) {
            JavaType beanType = beanDescRef.getType();
            JavaType singleType = beanType.containedType(0);
            return new TripleInstantiator(beanType, singleType, singleType, singleType);
        }
        return super.findValueInstantiator(config, beanDescRef);
    }

    static class TripleInstantiator extends ValueInstantiator.Base {
        private final JavaType beanType;
        private final JavaType oneType;
        private final JavaType twoType;
        private final JavaType threeType;
        private final TypeDeserializer oneTypeDeser;
        private final TypeDeserializer twoTypeDeser;
        private final TypeDeserializer threeTypeDeser;

        TripleInstantiator(
                JavaType beanType,
                JavaType oneType, JavaType twoType, JavaType threeType
        ) {
            // type deserializers are filled in by createContextual
            this(beanType, oneType, twoType, threeType, null, null, null);
        }

        TripleInstantiator(
                JavaType beanType,
                JavaType oneType, JavaType twoType, JavaType threeType,
                TypeDeserializer oneTypeDeser, TypeDeserializer twoTypeDeser, TypeDeserializer threeTypeDeser
        ) {
            super(beanType);
            this.beanType = beanType;
            this.oneType = oneType;
            this.twoType = twoType;
            this.threeType = threeType;
            this.oneTypeDeser = oneTypeDeser;
            this.twoTypeDeser = twoTypeDeser;
            this.threeTypeDeser = threeTypeDeser;
        }

        @Override
        public boolean canCreateFromObjectWith() {
            return true;
        }

        @Override
        public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
            return new SettableBeanProperty[]{
                    CreatorProperty.construct(
                            PropertyName.construct("one"),
                            oneType,
                            null,
                            oneTypeDeser,
                            AnnotationCollector.emptyAnnotations(),
                            null, 0, null, PropertyMetadata.STD_REQUIRED
                    ),
                    CreatorProperty.construct(
                            PropertyName.construct("two"),
                            twoType,
                            null,
                            twoTypeDeser,
                            AnnotationCollector.emptyAnnotations(),
                            null, 1, null, PropertyMetadata.STD_REQUIRED
                    ),
                    CreatorProperty.construct(
                            PropertyName.construct("three"),
                            threeType,
                            null,
                            threeTypeDeser,
                            AnnotationCollector.emptyAnnotations(),
                            null, 2, null, PropertyMetadata.STD_REQUIRED
                    )
            };
        }

        @Override
        public ValueInstantiator createContextual(DeserializationContext ctxt,
                BeanDescription.Supplier beanDescRef) {
            return new TripleInstantiator(
                    beanType,
                    oneType, twoType, threeType,
                    ctxt.findTypeDeserializer(oneType),
                    ctxt.findTypeDeserializer(twoType),
                    ctxt.findTypeDeserializer(threeType)
            );
        }

        @Override
        public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) {
            return Tuples.triple(args[0], args[1], args[2]);
        }
    }
}
