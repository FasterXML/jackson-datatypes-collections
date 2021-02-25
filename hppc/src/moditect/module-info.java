// Generated 20-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.datatype.hppc {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires hppc;

    exports com.fasterxml.jackson.datatype.hppc;
    exports com.fasterxml.jackson.datatype.hppc.deser;
    exports com.fasterxml.jackson.datatype.hppc.ser;

    provides com.fasterxml.jackson.databind.JacksonModule with
        com.fasterxml.jackson.datatype.hppc.HppcModule;
}
