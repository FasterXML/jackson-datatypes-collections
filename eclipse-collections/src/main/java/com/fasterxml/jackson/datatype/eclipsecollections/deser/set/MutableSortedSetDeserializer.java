package com.fasterxml.jackson.datatype.eclipsecollections.deser.set;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.SortedSets;

public final class MutableSortedSetDeserializer {
    private MutableSortedSetDeserializer() {
    }

    public static final class Ref
            extends BaseCollectionDeserializer.Ref<MutableSortedSet<?>, MutableSortedSet<Object>> {
        public Ref(CollectionLikeType type, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(type, typeDeserializer, deserializer);
        }

        @Override
        protected MutableSortedSet<Object> createIntermediate() {
            return SortedSets.mutable.empty();
        }

        @Override
        protected MutableSortedSet<?> finish(MutableSortedSet<Object> objects) {
            return objects;
        }

        @Override
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new MutableSortedSetDeserializer.Ref(_containerType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
