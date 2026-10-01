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

    /**
     * Runs given registration action, unless it fails because of a missing class.
     *
     * @return Whether action completed, that is, whether all types it uses are available
     */
    public static boolean registerIfPresent(Runnable registration)
    {
        try {
            registration.run();
            return true;
        } catch (NoClassDefFoundError e) {
            return false;
        } catch (BootstrapMethodError e) {
            // method references to missing classes fail during linkage
            if (e.getCause() instanceof NoClassDefFoundError) {
                return false;
            }
            throw e;
        }
    }
}
