module jackson.datatype.pcollections {
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires pcollections;

    exports tools.jackson.datatype.pcollections;
    exports tools.jackson.datatype.pcollections.deser;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.pcollections.PCollectionsModule;
}
