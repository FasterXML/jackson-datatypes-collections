package com.fasterxml.jackson.datatype.guava;

import java.io.*;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.guava.PackageVersion;

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
