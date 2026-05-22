package com.siondream.superjumper.movementpatterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class WavePattern implements MovementPattern {
    private float time = 0;
    private final float amplitude;
    private final float frequency;
    private final float speed;
    private final boolean horizontal;

    public WavePattern(float amplitude, float frequency, float speed, boolean horizontal) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.speed = speed;
        this.horizontal = horizontal;
    }

    @Override
    public Vector2 getNextPosition(Vector2 current, float deltaTime, Object... params) {
        time += deltaTime;
        float offset = MathUtils.sin(time * frequency) * amplitude;

        if (horizontal) {
            return new Vector2(current.x + speed * deltaTime, current.y + offset);
        } else {
            return new Vector2(current.x + offset, current.y + speed * deltaTime);
        }
    }

    @Override
    public void reset() { time = 0; }
}
