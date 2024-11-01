package com.fasterxml.jackson.datatype.guava.deser.table;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.google.common.collect.HashBasedTable;

/**
 * Provides deserialization for the Guava HashBasedTable class.
 *
 * @author Abhishekkr3003
 */
public class HashBasedTableDeserializer
    extends MutableTableDeserializer<HashBasedTable<Object, Object, Object>> {
    private static final long serialVersionUID = 1L;
    
    public HashBasedTableDeserializer(MapLikeType type) {
        super(type);
    }
    
    public HashBasedTableDeserializer(MapLikeType type, KeyDeserializer rowDeserializer,
        KeyDeserializer columnDeserializer, TypeDeserializer elementTypeDeserializer,
        JsonDeserializer<?> elementDeserializer, NullValueProvider nvp) {
        super(type, rowDeserializer, columnDeserializer, elementTypeDeserializer,
            elementDeserializer, nvp
        );
    }
    
    @Override
    protected HashBasedTable<Object, Object, Object> createTable() {
        return HashBasedTable.create();
    }
    
    @Override
    protected JsonDeserializer<?> _createContextual(MapLikeType type,
        KeyDeserializer rowDeserializer,
        KeyDeserializer columnDeserializer, TypeDeserializer typeDeserializer,
        JsonDeserializer<?> elementDeserializer, NullValueProvider nvp) {
        return new HashBasedTableDeserializer(
            type, rowDeserializer, columnDeserializer, typeDeserializer, elementDeserializer, nvp);
    }
}
