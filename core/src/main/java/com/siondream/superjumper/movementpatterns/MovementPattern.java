package com.siondream.superjumper.movementpatterns;

import com.badlogic.gdx.math.Vector2;

public interface MovementPattern {
    Vector2 getNextPosition(Vector2 current, float deltaTime, Object... params);
    void reset();
}
