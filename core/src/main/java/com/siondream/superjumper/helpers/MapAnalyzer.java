package com.siondream.superjumper.helpers;

import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;

/**
 * Utilidad para análisis de mapas
 */
public class MapAnalyzer {
    public static int countTilesOfType(TileMap map, TileType type) {
        int count = 0;
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(x, y).type == type) count++;
            }
        }
        return count;
    }

    public static float calculateOpenness(TileMap map) {
        int floor = countTilesOfType(map, TileType.FLOOR);
        int total = map.getWidth() * map.getHeight();
        return (float)floor / total;
    }
}
