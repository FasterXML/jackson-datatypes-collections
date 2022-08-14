package tools.jackson.datatype.hppc;

import java.io.*;

public class TestVersions extends ModuleTestBase
{
    public void testMapperVersions() throws IOException
    {
        HppcModule module = new HppcModule();
        assertEquals(PackageVersion.VERSION, module.version());
    }
}

