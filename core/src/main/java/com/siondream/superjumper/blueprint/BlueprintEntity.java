package com.siondream.superjumper.blueprint;

import java.util.HashMap;
import java.util.Map;

/**
 * Entidad dentro de un blueprint (enemigo, item, NPC, trigger)
 */
public class BlueprintEntity {
    public String type; // "enemy", "item", "npc", "trigger", "spawn_point"
    public String entityId; // "skeleton", "health_potion", "boss_trigger"
    public float x, y; // Posición relativa al blueprint
    public float rotation;
    public Map<String, Object> properties;

    public BlueprintEntity(String type, String entityId, float x, float y) {
        this.type = type;
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.rotation = 0;
        this.properties = new HashMap<>();
    }

    public BlueprintEntity clone() {
        BlueprintEntity copy = new BlueprintEntity(type, entityId, x, y);
        copy.rotation = this.rotation;
        copy.properties.putAll(this.properties);
        return copy;
    }
}
