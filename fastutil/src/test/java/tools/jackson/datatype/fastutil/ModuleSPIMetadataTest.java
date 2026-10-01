package tools.jackson.datatype.fastutil;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JacksonModule;

import static org.assertj.core.api.Assertions.assertThat;

public class ModuleSPIMetadataTest extends ModuleTestBase
{
    @Test
    void testModuleSPIMetadata() {
        List<String> moduleNames = new ArrayList<>();
        for (JacksonModule module : ServiceLoader.load(JacksonModule.class)) {
            moduleNames.add(module.getClass().getName());
        }
        assertThat(moduleNames).contains(FastutilModule.class.getName());
    }
}
