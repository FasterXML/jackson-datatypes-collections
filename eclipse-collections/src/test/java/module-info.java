//  Eclipse-collections module (unit) Test Module descriptor
module tools.jackson.datatype.eclipsecollections
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;

    requires tools.jackson.datatype.primitive_collections_basee;

    requires org.eclipse.collections.api;
    requires org.eclipse.collections.impl;

    // Additional test lib/framework dependencies
    requires junit; // JUnit 4

    // Further, need to open up test packages for JUnit et al
    
    opens tools.jackson.datatype.eclipsecollections;
}
