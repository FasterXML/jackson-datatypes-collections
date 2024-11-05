package tools.jackson.datatype.guava.deser.table;

import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.MapLikeType;

import com.google.common.collect.HashBasedTable;

/**
 * Provides deserialization for the Guava HashBasedTable class.
 *
 * @author Abhishekkr3003
 */
public class HashBasedTableDeserializer
    extends MutableTableDeserializer<HashBasedTable<Object, Object, Object>>
{
    public HashBasedTableDeserializer(MapLikeType type) {
        super(type);
    }
    
    public HashBasedTableDeserializer(MapLikeType type, KeyDeserializer rowDeserializer,
        KeyDeserializer columnDeserializer, TypeDeserializer elementTypeDeserializer,
        ValueDeserializer<?> elementDeserializer, NullValueProvider nvp) {
        super(type, rowDeserializer, columnDeserializer, elementTypeDeserializer,
            elementDeserializer, nvp
        );
    }
    
    @Override
    protected HashBasedTable<Object, Object, Object> createTable() {
        return HashBasedTable.create();
    }
    
    @Override
    protected ValueDeserializer<?> _createContextual(MapLikeType type,
        KeyDeserializer rowDeserializer,
        KeyDeserializer columnDeserializer, TypeDeserializer typeDeserializer,
        ValueDeserializer<?> elementDeserializer, NullValueProvider nvp) {
        return new HashBasedTableDeserializer(type,
                rowDeserializer, columnDeserializer, typeDeserializer, elementDeserializer, nvp);
    }
}
