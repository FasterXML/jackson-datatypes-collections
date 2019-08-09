package com.fasterxml.jackson.datatype.guava.deser;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.google.common.collect.TreeMultiset;

public class TreeMultisetDeserializer extends GuavaMultisetDeserializer<TreeMultiset<Object>>
{
    private static final long serialVersionUID = 1L;

    public TreeMultisetDeserializer(JavaType selfType,
            JsonDeserializer<?> deser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        super(selfType, deser, typeDeser, nuller, unwrapSingle);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected TreeMultiset<Object> createMultiset() {
        @SuppressWarnings("rawtypes")
        TreeMultiset<?> naturalOrder = TreeMultiset.<Comparable> create();
        return (TreeMultiset<Object>) naturalOrder;
    }

    @Override
    public GuavaCollectionDeserializer<TreeMultiset<Object>> withResolved(JsonDeserializer<?> valueDeser, TypeDeserializer typeDeser,
            NullValueProvider nuller, Boolean unwrapSingle) {
        return new TreeMultisetDeserializer(_containerType,
                valueDeser, typeDeser, nuller, unwrapSingle);
    }
}
