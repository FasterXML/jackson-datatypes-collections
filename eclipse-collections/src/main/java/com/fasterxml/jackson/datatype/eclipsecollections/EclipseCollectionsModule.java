package com.fasterxml.jackson.datatype.eclipsecollections;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.pair.PairInstantiators;
import com.fasterxml.jackson.datatype.eclipsecollections.deser.pair.TripleInstantiators;
import org.eclipse.collections.api.tuple.Triple;

/**
 * Basic Jackson {@link Module} that adds support for eclipse-collections types.
 */
public class EclipseCollectionsModule extends Module {
    private final String NAME = "EclipseCollectionsModule";

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
        try {
            context.addValueInstantiators(new TripleInstantiators());
            context.setMixInAnnotations(Triple.class, TripleMixin.class);
        } catch (NoClassDefFoundError ignored) {
            // triples were added in EC 10
        }

        context.addTypeModifier(new EclipseCollectionsTypeModifier());

        for (Class<?> pairClass : PairInstantiators.getAllPairClasses()) {
            context.setMixInAnnotations(pairClass, PairMixin.class);
        }
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
