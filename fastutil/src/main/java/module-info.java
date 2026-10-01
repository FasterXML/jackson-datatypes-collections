// fastutil module Main artifact Module descriptor
module tools.jackson.datatype.fastutil
{
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    requires tools.jackson.datatype.primitive_collections_base;

    requires it.unimi.dsi.fastutil;

    exports tools.jackson.datatype.fastutil;
    exports tools.jackson.datatype.fastutil.deser;
    exports tools.jackson.datatype.fastutil.deser.map;
    exports tools.jackson.datatype.fastutil.ser;
    exports tools.jackson.datatype.fastutil.ser.map;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.fastutil.FastutilModule;
}
