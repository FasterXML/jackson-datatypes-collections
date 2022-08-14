module tools.jackson.datatype.hppc {
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires hppc;

    exports tools.jackson.datatype.hppc;
    exports tools.jackson.datatype.hppc.deser;
    exports tools.jackson.datatype.hppc.ser;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.hppc.HppcModule;
}
