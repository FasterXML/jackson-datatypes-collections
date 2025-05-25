package com.fasterxml.jackson.datatype.hppc;

import java.io.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestVersions extends ModuleTestBase
{
    @Test
    public void testMapperVersions() throws IOException
    {
        HppcModule module = new HppcModule();
        assertEquals(PackageVersion.VERSION, module.version());
    }
}

