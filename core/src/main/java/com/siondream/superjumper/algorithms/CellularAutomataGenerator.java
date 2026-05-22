package com.siondream.superjumper.algorithms;

import com.siondream.superjumper.GenerationDirector;
import com.siondream.superjumper.tiles.Tile;
import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;

import java.util.Random;

/**
 * Generador usando Cellular Automata
 */
public class CellularAutomataGenerator extends GenerationDirector<TileMap> {
    private final int width, height;

    public CellularAutomataGenerator(int width, int height, long seed) {
        super(seed);
        this.width = width;
        this.height = height;
    }

    @Override
    protected void setupRules() {}

    @Override
    protected TileMap generate() {
        TileMap map = new TileMap(width, height);
        Random rand = context.getRandom();

        float wallProbability = (float)parameters.getOrDefault("wallProbability", 0.45f);
        int iterations = (int)parameters.getOrDefault("iterations", 5);

        // Inicialización aleatoria
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || y == 0 || x == width-1 || y == height-1) {
                    map.setTile(x, y, TileType.WALL);
                } else {
                    map.setTile(x, y, rand.nextFloat() < wallProbability ?
                        TileType.WALL : TileType.FLOOR);
                }
            }
        }

        // Aplicar reglas de autómata celular
        for (int i = 0; i < iterations; i++) {
            applyAutomataRules(map);
        }

        return map;
    }

    private void applyAutomataRules(TileMap map) {
        TileType[][] newTiles = new TileType[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int wallCount = countAdjacentWalls(map, x, y);

                // Regla: si hay 5+ paredes adyacentes, se convierte en pared
                // Si hay 4- paredes adyacentes, se convierte en suelo
                if (wallCount >= 5) {
                    newTiles[x][y] = TileType.WALL;
                } else {
                    newTiles[x][y] = TileType.FLOOR;
                }
            }
        }

        // Aplicar cambios
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map.setTile(x, y, newTiles[x][y]);
            }
        }
    }

    private int countAdjacentWalls(TileMap map, int x, int y) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                Tile tile = map.getTile(x + dx, y + dy);
                if (tile == null || tile.type == TileType.WALL) {
                    count++;
                }
            }
        }
        return count;
    }
}
