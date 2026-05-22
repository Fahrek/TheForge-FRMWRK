package com.siondream.superjumper;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Contexto compartido durante la generación
 */
public class GenerationContext {
    private final Map<String, Object> data;
    private final Random random;

    public GenerationContext(long seed) {
        this.data = new HashMap<>();
        this.random = new Random(seed);
    }

    public void set(String key, Object value) { data.put(key, value); }
    public Object get(String key) { return data.get(key); }
    public <T> T get(String key, Class<T> type) { return type.cast(data.get(key)); }
    public Random getRandom() { return random; }
    public boolean has(String key) { return data.containsKey(key); }
}
