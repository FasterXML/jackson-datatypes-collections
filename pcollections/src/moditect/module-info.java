// Generated 20-Mar-2019 using Moditect maven plugin
module jackson.datatype.pcollections {
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.pcollections;

    exports com.fasterxml.jackson.datatype.pcollections;
    exports com.fasterxml.jackson.datatype.pcollections.deser;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.pcollections.PCollectionsModule;
}
