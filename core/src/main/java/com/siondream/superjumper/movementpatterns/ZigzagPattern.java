package com.siondream.superjumper.movementpatterns;

import com.badlogic.gdx.math.Vector2;

public class ZigzagPattern implements MovementPattern {
    private float distance = 0;
    private final float segmentLength;
    private final float speed;
    private int direction = 1; // 1 o -1

    public ZigzagPattern(float segmentLength, float speed) {
        this.segmentLength = segmentLength;
        this.speed = speed;
    }

    @Override
    public Vector2 getNextPosition(Vector2 current, float deltaTime, Object... params) {
        distance += speed * deltaTime;

        if (distance >= segmentLength) {
            distance = 0;
            direction *= -1;
        }

        return new Vector2(current.x + speed * deltaTime, current.y + direction * speed * deltaTime);
    }

    @Override
    public void reset() {
        distance = 0;
        direction = 1;
    }
}
