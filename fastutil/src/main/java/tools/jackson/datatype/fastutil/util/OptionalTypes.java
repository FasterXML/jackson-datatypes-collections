package tools.jackson.datatype.fastutil.util;

/**
 * Helper for registering handlers of fastutil types that may not be available at
 * runtime: {@code fastutil-core}, a smaller alternative to the full {@code fastutil}
 * jar, only includes some of the types (for example no {@code boolean} or {@code byte}
 * collections, and no maps with {@code float} keys).
 *<p>
 * Class literals ({@code IntSet.class}) and method references
 * ({@code IntOpenHashSet::new}) load the referenced classes when evaluated, so all
 * code that references such types needs to be in the body of the action passed to
 * {@link #registerIfPresent}: the action itself (a lambda that only references the
 * types of this module) can always be created.
 *<p>
 * Internal API: not exported from the module.
 */
public final class OptionalTypes
{
    private OptionalTypes() { }

    private final static String FASTUTIL_PACKAGE_INTERNAL = "it/unimi/dsi/fastutil/";

    /**
     * Runs given registration action, unless it fails because a fastutil class is missing.
     * Other linkage failures (a missing class of another library, a failing static
     * initializer) are not expected, and are rethrown instead of hiding broken setup.
     *
     * @return Whether action completed, that is, whether all types it uses are available
     */
    public static boolean registerIfPresent(Runnable registration)
    {
        try {
            registration.run();
            return true;
        } catch (NoClassDefFoundError e) {
            if (_isMissingFastutilClass(e)) {
                return false;
            }
            throw e;
        } catch (BootstrapMethodError e) {
            // method references to missing classes fail during linkage
            if (e.getCause() instanceof NoClassDefFoundError ncdfe
                    && _isMissingFastutilClass(ncdfe)) {
                return false;
            }
            throw e;
        }
    }

    // JVM reports missing class as "it/unimi/dsi/fastutil/..."; a class that is present
    // but failed to initialize is reported as "Could not initialize class ..." instead
    private static boolean _isMissingFastutilClass(NoClassDefFoundError e) {
        final String msg = e.getMessage();
        return (msg != null) && msg.startsWith(FASTUTIL_PACKAGE_INTERNAL);
    }
}
