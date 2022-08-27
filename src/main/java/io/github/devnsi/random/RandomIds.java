package io.github.devnsi.random;

import java.util.List;

/**
 * {@link RandomIds} adds the lightweight capabilities to generate memorable identifiers by concatenating
 * distinguishable english words together.
 * <p>
 * Random alphanumeric strings and {@link java.util.UUID UUIDs} are commonly used for test identifiers. These are easily
 * produced but are more difficult to discern in logs and remember while correlating issues. There are some test data
 * generators (for random names, cities, ...) to improve readability as an alternative.
 */
public final class RandomIds {

    private RandomIds() {
        // hide constructor for static class.
    }

    private static RandomIdGen getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Generates a new identifier with the default configuration.
     * @return an identifier.
     */
    public static String next() {
        return getInstance().next();
    }

    /**
     * Generates identifiers with the default configuration.
     * @param amount of identifiers that will be generated.
     * @return specified amount of identifiers.
     */
    public static List<String> next(int amount) {
        return getInstance().next(amount);
    }

    /** Initialization-on-demand holder idiom. */
    private static class LazyHolder {

        private static final RandomIdGen INSTANCE = new RandomIdGen();
    }
}
