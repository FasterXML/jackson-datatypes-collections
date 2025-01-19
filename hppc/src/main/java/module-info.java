// HPPC module Main artifact Module descriptor
module tools.jackson.datatype.hppc
{
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    requires com.carrotsearch.hppc;

    exports tools.jackson.datatype.hppc;
    exports tools.jackson.datatype.hppc.deser;
    exports tools.jackson.datatype.hppc.ser;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.hppc.HppcModule;
}
