package com.siondream.superjumper.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestor de semillas para reproducibilidad
 */
public class SeedManager {
    private static final Map<String, Long> namedSeeds = new HashMap<>();

    public static void registerSeed(String name, long seed) {
        namedSeeds.put(name, seed);
    }

    public static long getSeed(String name) {
        return namedSeeds.getOrDefault(name, System.currentTimeMillis());
    }

    public static long generateFromString(String text) {
        return text.hashCode();
    }
}
