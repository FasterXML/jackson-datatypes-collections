package com.fasterxml.jackson.datatype.pcollections;

import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.databind.*;

/**
 * Basic Jackson {@link Module} that adds support for PCollections types.
 */
public class PCollectionsModule extends Module
{
    private final String NAME = "PCollectionsModule";

    public PCollectionsModule() {
        super();
    }

    @Override public String getModuleName() { return NAME; }
    @Override public Version version() { return PackageVersion.VERSION; }
    
    @Override
    public void setupModule(SetupContext context)
    {
        context.addDeserializers(new PCollectionsDeserializers());
    }

    @Override
    public int hashCode()
    {
        return NAME.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o;
    }
}
