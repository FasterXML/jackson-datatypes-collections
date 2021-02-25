package com.fasterxml.jackson.datatype.eclipsecollections.deser.set;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ValueDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;
import org.eclipse.collections.api.set.sorted.ImmutableSortedSet;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.SortedSets;

public final class ImmutableSortedSetDeserializer {
    private ImmutableSortedSetDeserializer() {
    }

    public static final class Ref
            extends BaseRefCollectionDeserializer<ImmutableSortedSet<?>, MutableSortedSet<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
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
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new ImmutableSortedSetDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
