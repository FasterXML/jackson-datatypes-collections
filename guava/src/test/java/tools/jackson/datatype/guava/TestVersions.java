package tools.jackson.datatype.guava;

import java.io.*;

import org.junit.jupiter.api.Test;

import tools.jackson.core.Version;
import tools.jackson.core.Versioned;
import tools.jackson.core.util.VersionUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
