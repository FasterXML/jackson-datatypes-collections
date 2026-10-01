package tools.jackson.datatype.fastutil;

import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;

/**
 * Jackson {@link JacksonModule} that adds support for
 * <a href="https://fastutil.di.unimi.it/">fastutil</a> collection and map types.
 *<p>
 * Collections and maps of primitive values are (de)serialized without boxing;
 * abstract collection and map types of references (like {@code ObjectList} or
 * {@code Object2ObjectMap}) are mapped to default fastutil implementations.
 */
public class FastutilModule extends JacksonModule
{
    private static final String NAME = "FastutilModule";

    public FastutilModule() {
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
        context.addDeserializers(new FastutilDeserializers());
        context.addSerializers(new FastutilSerializers());
        context.addAbstractTypeResolver(new FastutilAbstractTypeResolver());
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
