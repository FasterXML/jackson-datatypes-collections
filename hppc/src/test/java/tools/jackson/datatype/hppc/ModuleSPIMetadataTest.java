package tools.jackson.datatype.hppc;

import org.junit.jupiter.api.Test;

import tools.jackson.databind.JacksonModule;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ServiceLoader;

public class ModuleSPIMetadataTest extends ModuleTestBase
{
    @Test
    void testModuleSPIMetadata() {
        ServiceLoader<JacksonModule> loader
            = ServiceLoader.load(JacksonModule.class);
        assertTrue(loader.iterator().hasNext(),
                "Expected at least one `Module` implementation to be found via `ServiceLoader`");
        final String exp = HppcModule.class.getName();
        int count = 0;

        try {
            for (JacksonModule service : loader) {
                ++count;
                if (service.getClass().getName().equals(exp)) {
                    return;
                }
            }
        } catch (Throwable t) {
            fail("Expected to find `"+exp+"` Module (found "+count+" so far), problem: "+t);
        }
        fail("Expecteed to find `"+exp+"` Module (found "+count+" others)");
        assertEquals(1, count);
    }
}
