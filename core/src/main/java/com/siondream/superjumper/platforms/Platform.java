package com.siondream.superjumper.platforms;

import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.movementpatterns.MovementPattern;

public class Platform {
    public Vector2 start;
    public float length;
    public float height;
    public boolean moving;
    public MovementPattern movement;

    public Platform(Vector2 start, float length, float height) {
        this.start = start;
        this.length = length;
        this.height = height;
        this.moving = false;
    }
}
