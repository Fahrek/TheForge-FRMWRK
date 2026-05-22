package com.siondream.superjumper.rooms;

import com.badlogic.gdx.math.Vector2;

public class Room {
    public int x, y, width, height;
    public Vector2 center;

    public Room(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.center = new Vector2(x + width / 2f, y + height / 2f);
    }

    public boolean intersects(Room other) {
        return !(x + width <= other.x || other.x + other.width <= x ||
            y + height <= other.y || other.y + other.height <= y);
    }
}
