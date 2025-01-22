//  Eclipse-collections module (unit) Test Module descriptor
module tools.jackson.datatype.eclipsecollections
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    requires tools.jackson.datatype.primitive_collections_base;

    requires org.eclipse.collections.api;
    requires org.eclipse.collections.impl;

    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    // Further, need to open up test packages for JUnit et al
    
    opens tools.jackson.datatype.eclipsecollections;
}
