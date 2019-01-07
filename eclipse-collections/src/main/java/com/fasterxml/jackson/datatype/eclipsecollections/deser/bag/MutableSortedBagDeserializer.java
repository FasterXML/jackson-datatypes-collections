package com.fasterxml.jackson.datatype.eclipsecollections.deser.bag;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.impl.factory.SortedBags;

public final class MutableSortedBagDeserializer {
    private MutableSortedBagDeserializer() {
    }

    public static final class Ref
            extends BaseRefCollectionDeserializer<MutableSortedBag<?>, MutableSortedBag<Object>> {
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
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new MutableSortedBagDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
