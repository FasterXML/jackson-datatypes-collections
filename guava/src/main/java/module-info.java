// Guava module Main artifact Module descriptor
module tools.jackson.datatype.guava
{
    requires com.fasterxml.jackson.annotation;

    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires com.google.common;

    exports tools.jackson.datatype.guava;
    exports tools.jackson.datatype.guava.deser;
    exports tools.jackson.datatype.guava.deser.multimap;
    exports tools.jackson.datatype.guava.deser.multimap.list;
    exports tools.jackson.datatype.guava.deser.multimap.set;
    exports tools.jackson.datatype.guava.deser.util;
    exports tools.jackson.datatype.guava.ser;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.guava.GuavaModule;
}
