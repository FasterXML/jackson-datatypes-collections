package com.fasterxml.jackson.datatype.eclipsecollections.deser.bag;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.impl.factory.SortedBags;

public final class ImmutableSortedBagDeserializer {
    private ImmutableSortedBagDeserializer() {
    }

    public static final class Ref
            extends BaseCollectionDeserializer.Ref<ImmutableSortedBag<?>, MutableSortedBag<Object>> {
        public Ref(CollectionLikeType type, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(type, typeDeserializer, deserializer);
        }

        @Override
        protected MutableSortedBag<Object> createIntermediate() {
            return SortedBags.mutable.empty();
        }

        @Override
        protected ImmutableSortedBag<?> finish(MutableSortedBag<Object> objects) {
            return objects.toImmutable();
        }

        @Override
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new ImmutableSortedBagDeserializer.Ref(_containerType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
