package com.siondream.superjumper.scatteritems;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public class ItemPlacement {
    public String itemType;
    public Vector2 position;
    public float spawnProbability;
    public Map<String, Object> properties;

    public ItemPlacement(String type, Vector2 pos, float probability) {
        this.itemType = type;
        this.position = pos;
        this.spawnProbability = probability;
        this.properties = new HashMap<>();
    }
}
