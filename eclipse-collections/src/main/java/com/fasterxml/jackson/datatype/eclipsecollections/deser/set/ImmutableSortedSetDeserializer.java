package com.fasterxml.jackson.datatype.eclipsecollections.deser.set;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.SortedSets;

public final class ImmutableSortedSetDeserializer {
    private ImmutableSortedSetDeserializer() {
    }

    public static final class Ref
            extends BaseCollectionDeserializer.Ref<ImmutableSortedSet<?>, MutableSortedSet<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(ImmutableSortedSet.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableSortedSet<Object> createIntermediate() {
            return SortedSets.mutable.empty();
        }

        @Override
        protected ImmutableSortedSet<?> finish(MutableSortedSet<Object> objects) {
            return objects.toImmutable();
        }

        @Override
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new ImmutableSortedSetDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
