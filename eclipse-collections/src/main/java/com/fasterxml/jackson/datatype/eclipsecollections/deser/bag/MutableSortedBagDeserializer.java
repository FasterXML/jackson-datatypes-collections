package com.fasterxml.jackson.datatype.eclipsecollections.deser.bag;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.impl.factory.SortedBags;

public final class MutableSortedBagDeserializer {
    private MutableSortedBagDeserializer() {
    }

    public static final class Ref
            extends BaseCollectionDeserializer.Ref<MutableSortedBag<?>, MutableSortedBag<Object>> {
        private static final long serialVersionUID = 1L;

        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
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
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new MutableSortedBagDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
