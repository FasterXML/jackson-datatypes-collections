package com.fasterxml.jackson.datatype.guava;

import java.io.*;

import tools.jackson.core.Version;
import tools.jackson.core.Versioned;
import tools.jackson.core.util.VersionUtil;

public class TestVersions extends ModuleTestBase
{
    public void testMapperVersions() throws IOException
    {
        GuavaModule module = new GuavaModule();
        assertVersion(module);
    }

    public void testPackageVersion()
    {
        assertEquals(PackageVersion.VERSION,
                VersionUtil.versionFor(GuavaModule.class));
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private void assertVersion(Versioned vers)
    {
        final Version v = vers.version();
        assertFalse("Should find version information (got "+v+")", v.isUnknownVersion());
        assertEquals(PackageVersion.VERSION, v);
    }
}
