package com.siondream.superjumper.movementpatterns;

import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.GenerationDirector;

import java.util.Random;

public class MovementPatternGenerator extends GenerationDirector<MovementPattern> {
    private final String patternType;

    public MovementPatternGenerator(long seed, String patternType) {
        super(seed);
        this.patternType = patternType;
    }

    @Override
    protected void setupRules() {
        // Reglas para validar parámetros de patrones
    }

    @Override
    protected MovementPattern generate() {
        Random rand = context.getRandom();

        switch (patternType.toUpperCase()) {
            case "CIRCULAR":
                return new CircularPattern(
                    new Vector2(0, 0),
                    (float)parameters.getOrDefault("radius", 100f),
                    (float)parameters.getOrDefault("speed", 2f)
                );

            case "WAVE":
                return new WavePattern(
                    (float)parameters.getOrDefault("amplitude", 50f),
                    (float)parameters.getOrDefault("frequency", 2f),
                    (float)parameters.getOrDefault("speed", 100f),
                    (boolean)parameters.getOrDefault("horizontal", true)
                );

            case "ZIGZAG":
                return new ZigzagPattern(
                    (float)parameters.getOrDefault("segmentLength", 100f),
                    (float)parameters.getOrDefault("speed", 150f)
                );

            default:
                return new CircularPattern(new Vector2(0, 0), 50f, 1f);
        }
    }
}
