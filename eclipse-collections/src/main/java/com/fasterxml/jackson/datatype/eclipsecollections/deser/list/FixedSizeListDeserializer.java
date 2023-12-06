package com.fasterxml.jackson.datatype.eclipsecollections.deser.list;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.BaseCollectionDeserializer;
import org.eclipse.collections.api.list.FixedSizeList;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

public final class FixedSizeListDeserializer {
    private FixedSizeListDeserializer() {
    }

    public static final class Ref extends
            BaseCollectionDeserializer.Ref<FixedSizeList<?>, MutableList<Object>> {
        private static final long serialVersionUID = 1L;

        public Ref(JavaType elementType, TypeDeserializer typeDeserializer, JsonDeserializer<?> deserializer) {
            super(FixedSizeList.class, elementType, typeDeserializer, deserializer);
        }

        @Override
        protected MutableList<Object> createIntermediate() {
            return Lists.mutable.empty();
        }

        @Override
        protected FixedSizeList<?> finish(MutableList<Object> objects) {
            return Lists.fixedSize.ofAll(objects);
        }

        @Override
        protected Ref<?, ?> withResolved(
                TypeDeserializer typeDeserializerForValue,
                JsonDeserializer<?> valueDeserializer
        ) {
            return new FixedSizeListDeserializer.Ref(_elementType, typeDeserializerForValue, valueDeserializer);
        }
    }
}
