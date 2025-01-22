//  Guava module (unit) Test Module descriptor
module tools.jackson.datatype.guava
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires com.fasterxml.jackson.annotation;

    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires com.google.common;

    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    // Further, need to open up test packages for JUnit et al
    
    opens tools.jackson.datatype.guava;
    opens tools.jackson.datatype.guava.fuzz;
    opens tools.jackson.datatype.guava.optional;
    opens tools.jackson.datatype.guava.pojo;
}
