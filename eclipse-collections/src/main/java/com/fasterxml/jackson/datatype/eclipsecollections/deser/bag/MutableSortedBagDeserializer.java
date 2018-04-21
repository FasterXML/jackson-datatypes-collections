package com.fasterxml.jackson.datatype.eclipsecollections.deser.bag;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.impl.factory.SortedBags;

public final class MutableSortedBagDeserializer {
    private MutableSortedBagDeserializer() {
    }

    public static final class Ref
            extends BaseCollectionDeserializer.Ref<MutableSortedBag<?>, MutableSortedBag<Object>> {
        public Ref(CollectionLikeType type, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(type, typeDeserializer, deserializer);
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
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new MutableSortedBagDeserializer.Ref(_containerType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
