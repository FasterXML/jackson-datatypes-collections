package com.fasterxml.jackson.datatype.hppc;

import java.io.*;

public class TestVersions extends HppcTestBase
{
    public void testMapperVersions() throws IOException
    {
        HppcModule module = new HppcModule();
        assertEquals(PackageVersion.VERSION, module.version());
    }
}

