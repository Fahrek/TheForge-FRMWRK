package com.siondream.superjumper;

import java.util.HashMap;
import java.util.Map;

/**
 * Director base para coordinar generación procedural
 */
public abstract class GenerationDirector<T> {
    protected final GenerationContext context;
    protected final RuleEngine ruleEngine;
    protected final Map<String, Object> parameters;

    public GenerationDirector(long seed) {
        this.context = new GenerationContext(seed);
        this.ruleEngine = new RuleEngine();
        this.parameters = new HashMap<>();
    }

    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }

    protected abstract void setupRules();
    protected abstract T generate();

    public T execute() {
        setupRules();
        return generate();
    }
}
