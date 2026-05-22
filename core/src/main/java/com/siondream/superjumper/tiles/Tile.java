package com.siondream.superjumper.tiles;

import java.util.HashMap;
import java.util.Map;

public class Tile {
    public TileType type;
    public int x, y;
    public Map<String, Object> metadata;

    public Tile(int x, int y, TileType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.metadata = new HashMap<>();
    }
}
