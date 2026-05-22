package com.siondream.superjumper.movementpatterns;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CircularPattern implements MovementPattern {
    private float angle = 0;
    private final float radius;
    private final float speed;
    private final Vector2 center;

    public CircularPattern(Vector2 center, float radius, float speed) {
        this.center = center;
        this.radius = radius;
        this.speed = speed;
    }

    @Override
    public Vector2 getNextPosition(Vector2 current, float deltaTime, Object... params) {
        angle += speed * deltaTime;
        float x = center.x + MathUtils.cos(angle) * radius;
        float y = center.y + MathUtils.sin(angle) * radius;
        return new Vector2(x, y);
    }

    @Override
    public void reset() { angle = 0; }
}
