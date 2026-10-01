// fastutil module (unit) Test Module descriptor
module tools.jackson.datatype.fastutil
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    requires tools.jackson.datatype.primitive_collections_base;

    requires it.unimi.dsi.fastutil;

    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.fastutil;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.fastutil.FastutilModule;
    uses tools.jackson.databind.JacksonModule;
}
