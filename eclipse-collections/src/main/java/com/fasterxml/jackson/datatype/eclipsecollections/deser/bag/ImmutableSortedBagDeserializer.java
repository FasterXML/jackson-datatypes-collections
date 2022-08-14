package com.fasterxml.jackson.datatype.eclipsecollections.deser.bag;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;
import org.eclipse.collections.api.bag.sorted.ImmutableSortedBag;
import org.eclipse.collections.api.bag.sorted.MutableSortedBag;
import org.eclipse.collections.impl.factory.SortedBags;

public final class ImmutableSortedBagDeserializer {
    private ImmutableSortedBagDeserializer() {
    }

    public static final class Ref
            extends BaseRefCollectionDeserializer<ImmutableSortedBag<?>, MutableSortedBag<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
            super(ImmutableSortedBag.class, elementType, typeDeserializer, deserializer);
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
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new ImmutableSortedBagDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
