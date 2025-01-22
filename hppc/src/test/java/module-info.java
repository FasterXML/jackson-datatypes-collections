//  HPPC module (unit) Test Module descriptor
module tools.jackson.datatype.hppc
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires tools.jackson.core;
    requires tools.jackson.databind;

    requires com.carrotsearch.hppc;

    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.hppc;
    opens tools.jackson.datatype.hppc.deser;
    opens tools.jackson.datatype.hppc.ser;
}
