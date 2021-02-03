package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JacksonModule;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.pair.PairInstantiators;

/**
 * Basic Jackson {@link JacksonModule} that adds support for eclipse-collections types.
 */
public class EclipseCollectionsModule extends JacksonModule {
    private static final String NAME = "EclipseCollectionsModule";

    public EclipseCollectionsModule() {
        super();
    }

    @Override
    public String getModuleName() {
        return NAME;
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addDeserializers(new EclipseCollectionsDeserializers());
        context.addSerializers(new EclipseCollectionsSerializers());

        context.addValueInstantiators(new PairInstantiators());

        context.addTypeModifier(new EclipseCollectionsTypeModifier());
    }

    @Override
    public int hashCode() {
        return NAME.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
