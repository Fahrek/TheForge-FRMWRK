package com.siondream.superjumper.blueprint;

import com.siondream.superjumper.tiles.TileType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Blueprint representa una estructura prediseñada que puede ser
 * colocada en el mapa generado proceduralmente
 */
public class Blueprint {
    public String id;
    public String name;
    public String category; // "room", "trap", "puzzle", "decoration", "boss_arena"
    public int width, height;
    public TileType[][] tiles;
    public List<BlueprintEntity> entities;
    public List<BlueprintConnection> connections; // Puntos de conexión (puertas, etc)
    public Map<String, Object> metadata;
    public BlueprintPlacementRules placementRules;

    public Blueprint(String id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.tiles = new TileType[width][height];
        this.entities = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.placementRules = new BlueprintPlacementRules();
    }

    /**
     * Obtiene el tile en coordenadas locales del blueprint
     */
    public TileType getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return TileType.EMPTY;
        return tiles[x][y];
    }

    /**
     * Establece un tile en el blueprint
     */
    public void setTile(int x, int y, TileType type) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y] = type;
        }
    }

    /**
     * Añade una entidad al blueprint (enemigo, item, trigger)
     */
    public void addEntity(BlueprintEntity entity) {
        entities.add(entity);
    }

    /**
     * Añade un punto de conexión (para unir con otros blueprints o corredores)
     */
    public void addConnection(int x, int y, String direction) {
        connections.add(new BlueprintConnection(x, y, direction));
    }

    /**
     * Clona el blueprint (útil para variaciones)
     */
    public Blueprint clone() {
        Blueprint copy = new Blueprint(this.id + "_clone", width, height);
        copy.name = this.name;
        copy.category = this.category;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                copy.tiles[x][y] = this.tiles[x][y];
            }
        }

        for (BlueprintEntity entity : entities) {
            copy.entities.add(entity.clone());
        }

        for (BlueprintConnection conn : connections) {
            copy.connections.add(conn.clone());
        }

        copy.metadata.putAll(this.metadata);
        copy.placementRules = this.placementRules.clone();

        return copy;
    }
}
