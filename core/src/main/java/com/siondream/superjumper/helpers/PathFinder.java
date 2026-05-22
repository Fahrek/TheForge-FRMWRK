package com.siondream.superjumper.helpers;

import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.tiles.TileMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para pathfinding simple (A*)
 */
public class PathFinder {
    private final TileMap map;

    public PathFinder(TileMap map) {
        this.map = map;
    }

    public List<Vector2> findPath(Vector2 start, Vector2 end) {
        // Implementación simplificada de A*
        List<Vector2> path = new ArrayList<>();
        // ... implementación del algoritmo
        return path;
    }
}
