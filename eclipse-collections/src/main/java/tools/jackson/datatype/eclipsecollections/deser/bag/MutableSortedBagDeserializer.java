package tools.jackson.datatype.eclipsecollections.deser.bag;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;

import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.impl.factory.SortedBags;

public final class MutableSortedBagDeserializer {
    private MutableSortedBagDeserializer() {
    }

    public static final class Ref
            extends BaseRefCollectionDeserializer<MutableSortedBag<?>, MutableSortedBag<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
            super(MutableSortedBag.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableSortedBag<Object> createIntermediate() {
            return SortedBags.mutable.empty();
        }

        @Override
        protected MutableSortedBag<?> finish(MutableSortedBag<Object> objects) {
            return objects;
        }

        @Override
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new MutableSortedBagDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
