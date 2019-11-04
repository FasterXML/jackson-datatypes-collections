// Generated 20-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.datatype.guava {
    requires com.fasterxml.jackson.annotation;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    requires static guava;
    requires static com.google.common;

    exports com.fasterxml.jackson.datatype.guava;
    exports com.fasterxml.jackson.datatype.guava.deser;
    exports com.fasterxml.jackson.datatype.guava.deser.multimap;
    exports com.fasterxml.jackson.datatype.guava.deser.multimap.list;
    exports com.fasterxml.jackson.datatype.guava.deser.multimap.set;
    exports com.fasterxml.jackson.datatype.guava.deser.util;
    exports com.fasterxml.jackson.datatype.guava.ser;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.guava.GuavaModule;
}
