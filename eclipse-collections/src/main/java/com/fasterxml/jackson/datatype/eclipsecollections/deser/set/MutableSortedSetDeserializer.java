package com.fasterxml.jackson.datatype.eclipsecollections.deser.set;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.primitive_collections_base.deser.BaseRefCollectionDeserializer;
import org.eclipse.collections.api.set.sorted.MutableSortedSet;
import org.eclipse.collections.impl.factory.SortedSets;

public final class MutableSortedSetDeserializer {
    private MutableSortedSetDeserializer() {
    }

    public static final class Ref
            extends BaseRefCollectionDeserializer<MutableSortedSet<?>, MutableSortedSet<Object>> {
        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, ValueDeserializer<?> deserializer) {
            super(MutableSortedSet.class, elementType, typeDeserializer, deserializer);
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
        protected Ref withResolved(
                TypeDeserializer typeDeserializerForValue,
                ValueDeserializer<?> valueDeserializer
        ) {
            return new MutableSortedSetDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
