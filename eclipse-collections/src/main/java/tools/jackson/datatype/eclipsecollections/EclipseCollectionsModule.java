package tools.jackson.datatype.eclipsecollections;

import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;
import tools.jackson.datatype.eclipsecollections.deser.pair.PairInstantiators;
import tools.jackson.datatype.eclipsecollections.deser.pair.TripleInstantiators;

import org.eclipse.collections.api.tuple.Triple;

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

        context.addValueInstantiators(new TripleInstantiators());
        context.setMixIn(Triple.class, TripleMixin.class);

        context.addTypeModifier(new EclipseCollectionsTypeModifier());

        for (Class<?> pairClass : PairInstantiators.getAllPairClasses()) {
            context.setMixIn(pairClass, PairMixin.class);
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