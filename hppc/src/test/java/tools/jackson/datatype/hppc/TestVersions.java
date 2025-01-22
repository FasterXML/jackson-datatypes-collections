package tools.jackson.datatype.hppc;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestVersions extends ModuleTestBase
{
    public void testMapperVersions() throws IOException
    {
        HppcModule module = new HppcModule();
        assertEquals(PackageVersion.VERSION, module.version());
    }
}

