package com.fasterxml.jackson.datatype.guava;

import java.io.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

import static org.junit.jupiter.api.Assertions.*;

public class TestVersions extends ModuleTestBase
{
    @Test
    public void testMapperVersions() throws IOException
    {
        GuavaModule module = new GuavaModule();
        assertVersion(module);
    }

    @Test
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
        assertFalse(v.isUnknownVersion(), "Should find version information (got "+v+")");
        assertEquals(PackageVersion.VERSION, v);
    }
}
