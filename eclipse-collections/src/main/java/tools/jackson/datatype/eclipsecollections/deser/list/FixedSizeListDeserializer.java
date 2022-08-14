package tools.jackson.datatype.eclipsecollections.deser.list;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;

import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

public final class FixedSizeListDeserializer {
    private FixedSizeListDeserializer() {
    }

    public static final class Ref extends
            BaseRefCollectionDeserializer<FixedSizeList<?>, MutableList<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
            super(FixedSizeList.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableList<Object> createIntermediate() {
            return Lists.mutable.empty();
        }

        @Override
        protected FixedSizeList<?> finish(MutableList<Object> objects) {
            return Lists.fixedSize.ofAll(objects);
        }

        @Override
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new FixedSizeListDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
