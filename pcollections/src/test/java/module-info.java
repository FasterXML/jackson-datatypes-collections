//  PCollections module (unit) Test Module descriptor
module jackson.datatype.pcollections
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires org.pcollections;

    // Additional test lib/framework dependencies
    requires junit; // JUnit 4

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.pcollections;
    opens tools.jackson.datatype.pcollections.deser;
}
