package tools.jackson.datatype.fastutil.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OptionalTypesTest
{
    @Test
    void testCompletedAction() {
        assertTrue(OptionalTypes.registerIfPresent(() -> { }));
    }

    @Test
    void testMissingFastutilClassIgnored() {
        assertFalse(OptionalTypes.registerIfPresent(() -> {
            throw new NoClassDefFoundError("it/unimi/dsi/fastutil/bytes/ByteSet");
        }));
        assertFalse(OptionalTypes.registerIfPresent(() -> {
            throw new BootstrapMethodError(new NoClassDefFoundError("it/unimi/dsi/fastutil/bytes/ByteSet"));
        }));
    }

    @Test
    void testOtherLinkageFailuresPropagate() {
        assertThrows(NoClassDefFoundError.class, () -> OptionalTypes.registerIfPresent(() -> {
            throw new NoClassDefFoundError("com/example/Missing");
        }));
        assertThrows(NoClassDefFoundError.class, () -> OptionalTypes.registerIfPresent(() -> {
            throw new NoClassDefFoundError("Could not initialize class it.unimi.dsi.fastutil.bytes.ByteSet");
        }));
        assertThrows(BootstrapMethodError.class, () -> OptionalTypes.registerIfPresent(() -> {
            throw new BootstrapMethodError(new NoClassDefFoundError("com/example/Missing"));
        }));
    }
}
