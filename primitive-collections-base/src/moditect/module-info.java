module jackson.datatype.primitive.collections.base {
    requires com.fasterxml.jackson.annotation;

    requires transitive tools.jackson.core;
    requires transitive tools.jackson.databind;

    exports tools.jackson.datatype.primitive_collections_base.deser;
    exports tools.jackson.datatype.primitive_collections_base.deser.map;
    exports tools.jackson.datatype.primitive_collections_base.ser;
    exports tools.jackson.datatype.primitive_collections_base.ser.map;
}
