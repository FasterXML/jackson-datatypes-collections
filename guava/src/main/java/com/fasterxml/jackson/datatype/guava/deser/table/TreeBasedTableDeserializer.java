package com.fasterxml.jackson.datatype.guava.deser.table;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.google.common.collect.TreeBasedTable;

/**
 * Provides deserialization for the Guava TreeBasedTable class.
 *
 * @author Abhishekkr3003
 */
public class TreeBasedTableDeserializer
    extends MutableTableDeserializer<TreeBasedTable<Object, Object, Object>> {
    private static final long serialVersionUID = 1L;
    
    public TreeBasedTableDeserializer(JavaType type) {
        super(type);
    }
    
    public TreeBasedTableDeserializer(JavaType type, KeyDeserializer rowDeserializer,
        KeyDeserializer columnDeserializer, TypeDeserializer elementTypeDeserializer,
        JsonDeserializer<?> elementDeserializer, NullValueProvider nvp) {
        super(type, rowDeserializer, columnDeserializer, elementTypeDeserializer,
            elementDeserializer, nvp
        );
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected TreeBasedTable<Object, Object, Object> createTable() {
        TreeBasedTable<?, ?, ?> naturalOrder = TreeBasedTable.create();
        return (TreeBasedTable<Object, Object, Object>) naturalOrder;
    }
    
    @Override
    protected JsonDeserializer<?> _createContextual(JavaType type, KeyDeserializer rowDeserializer,
        KeyDeserializer columnDeserializer, TypeDeserializer typeDeserializer,
        JsonDeserializer<?> elementDeserializer, NullValueProvider nvp) {
        return new TreeBasedTableDeserializer(
            type, rowDeserializer, columnDeserializer, typeDeserializer, elementDeserializer, nvp);
    }
}
