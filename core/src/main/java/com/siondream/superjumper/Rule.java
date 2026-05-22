package com.siondream.superjumper;

/**
 * Regla configurable para generación procedural
 */
public interface Rule {
    boolean evaluate(GenerationContext context);
    float getPriority();
}
