package tools.jackson.datatype.eclipsecollections.deser.set;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;

import org.eclipse.collections.api.set.FixedSizeSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;

public final class FixedSizeSetDeserializer {
    private FixedSizeSetDeserializer() {
    }

    public static final class Ref extends
            BaseRefCollectionDeserializer<FixedSizeSet<?>, MutableSet<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
            super(FixedSizeSet.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableSet<Object> createIntermediate() {
            return Sets.mutable.empty();
        }

        // Fixed-size sets only exist for up to 4 elements
        @Override
        protected FixedSizeSet<?> finish(DeserializationContext ctxt, MutableSet<Object> objects)
            throws JacksonException
        {
            if (objects.size() > 4) {
                return ctxt.reportInputMismatch(this,
                        "Cannot construct `FixedSizeSet` with more than 4 elements (got %d)", objects.size());
            }
            return finish(objects);
        }

        @Override
        protected FixedSizeSet<?> finish(MutableSet<Object> objects) {
            Object[] elems = objects.toArray(new Object[4]);
            switch (objects.size()) {
            case 0:
                return Sets.fixedSize.of();
            case 1:
                return Sets.fixedSize.of(elems[0]);
            case 2:
                return Sets.fixedSize.of(elems[0], elems[1]);
            case 3:
                return Sets.fixedSize.of(elems[0], elems[1], elems[2]);
            default:
                return Sets.fixedSize.of(elems[0], elems[1], elems[2], elems[3]);
            }
        }

        @Override
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new FixedSizeSetDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
