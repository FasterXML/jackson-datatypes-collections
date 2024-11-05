package tools.jackson.datatype.guava.deser.table;

import tools.jackson.databind.KeyDeserializer;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.deser.NullValueProvider;
import tools.jackson.databind.jsontype.TypeDeserializer;
import tools.jackson.databind.type.MapLikeType;

import com.google.common.collect.TreeBasedTable;

/**
 * Provides deserialization for the Guava TreeBasedTable class.
 *
 * @author Abhishekkr3003
 */
public class TreeBasedTableDeserializer
    extends MutableTableDeserializer<TreeBasedTable<Object, Object, Object>> {
    private static final long serialVersionUID = 1L;
    
    public TreeBasedTableDeserializer(MapLikeType type) {
        super(type);
    }
    
    public TreeBasedTableDeserializer(MapLikeType type, KeyDeserializer rowDeserializer,
        KeyDeserializer columnDeserializer, TypeDeserializer elementTypeDeserializer,
        ValueDeserializer<?> elementDeserializer, NullValueProvider nvp) {
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
    protected ValueDeserializer<?> _createContextual(MapLikeType type,
        KeyDeserializer rowDeserializer,
        KeyDeserializer columnDeserializer, TypeDeserializer typeDeserializer,
        ValueDeserializer<?> elementDeserializer, NullValueProvider nvp) {
        return new TreeBasedTableDeserializer(
            type, rowDeserializer, columnDeserializer, typeDeserializer, elementDeserializer, nvp);
    }
}
