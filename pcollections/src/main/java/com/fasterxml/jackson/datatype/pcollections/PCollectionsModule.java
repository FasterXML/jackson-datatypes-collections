package com.fasterxml.jackson.datatype.pcollections;

import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.databind.*;

/**
 * Basic Jackson {@link JacksonModule} that adds support for PCollections types.
 */
public class PCollectionsModule extends com.fasterxml.jackson.databind.JacksonModule
    implements java.io.Serializable
{
    private static final long serialVersionUID = 3L;

    private final String NAME = "PCollectionsModule";

    public PCollectionsModule() {
        super();
    }

    @Override public String getModuleName() { return NAME; }
    @Override public Version version() { return PackageVersion.VERSION; }

    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new PCollectionsDeserializers());
    }
}
