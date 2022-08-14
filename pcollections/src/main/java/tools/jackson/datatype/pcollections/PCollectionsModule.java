package tools.jackson.datatype.pcollections;

import tools.jackson.core.Version;

import tools.jackson.databind.*;

/**
 * Basic Jackson {@link JacksonModule} that adds support for PCollections types.
 */
public class PCollectionsModule extends JacksonModule
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
