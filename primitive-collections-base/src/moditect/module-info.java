module jackson.datatype.primitive.collections.base {
    requires com.fasterxml.jackson.annotation;

    requires transitive com.fasterxml.jackson.core;
    requires transitive com.fasterxml.jackson.databind;

    exports com.fasterxml.jackson.datatype.primitive_collections_base.deser;
    exports com.fasterxml.jackson.datatype.primitive_collections_base.deser.map;
    exports com.fasterxml.jackson.datatype.primitive_collections_base.ser;
    exports com.fasterxml.jackson.datatype.primitive_collections_base.ser.map;
}