package com.siondream.superjumper.biome;

import com.siondream.superjumper.GenerationContext;
import com.siondream.superjumper.GenerationDirector;
import com.siondream.superjumper.Rule;
import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeGenerator extends GenerationDirector<TileMap> {
    private final int width, height;
    private final List<BiomeType> biomes;
    private float[][] noiseMap;

    public BiomeGenerator(int width, int height, long seed) {
        super(seed);
        this.width = width;
        this.height = height;
        this.biomes = new ArrayList<>();

        // Biomas predefinidos
        biomes.add(new BiomeType("DESERT", TileType.SAND, TileType.STONE, 0.8f, 0.2f));
        biomes.add(new BiomeType("GRASSLAND", TileType.GRASS, TileType.DIRT, 0.5f, 0.6f));
        biomes.add(new BiomeType("TUNDRA", TileType.ICE, TileType.STONE, -0.8f, 0.3f));
        biomes.add(new BiomeType("VOLCANIC", TileType.LAVA, TileType.STONE, 1.0f, 0.1f));
    }

    @Override
    protected void setupRules() {
        // Reglas para transiciones de biomas
        ruleEngine.addRule(new Rule() {
            public boolean evaluate(GenerationContext ctx) {
                return ctx.has("biome_transition");
            }
            public float getPriority() { return 10f; }
        });
    }

    @Override
    protected TileMap generate() {
        TileMap map = new TileMap(width, height);

        // Generar mapa de ruido Perlin simplificado
        noiseMap = generateNoiseMap(width, height,
            (float)parameters.getOrDefault("scale", 50f),
            (int)parameters.getOrDefault("octaves", 4));

        // Aplicar biomas según el ruido
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                float value = noiseMap[x][y];
                BiomeType biome = selectBiome(value);

                // Añadir variación
                TileType tile = context.getRandom().nextFloat() < 0.85f
                    ? biome.primaryTile : biome.secondaryTile;

                map.setTile(x, y, tile);
                map.getTile(x, y).metadata.put("biome", biome.name);
            }
        }

        return map;
    }

    private BiomeType selectBiome(float value) {
        if (value < 0.25f) return biomes.get(2); // TUNDRA
        else if (value < 0.5f) return biomes.get(1); // GRASSLAND
        else if (value < 0.75f) return biomes.get(0); // DESERT
        else return biomes.get(3); // VOLCANIC
    }

    private float[][] generateNoiseMap(int w, int h, float scale, int octaves) {
        float[][] noise = new float[w][h];
        Random rand = context.getRandom();

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                float amplitude = 1f;
                float frequency = 1f;
                float noiseValue = 0f;

                for (int i = 0; i < octaves; i++) {
                    float sampleX = x / scale * frequency;
                    float sampleY = y / scale * frequency;

                    // Ruido simplificado (en producción usar Perlin real)
                    float perlin = (float)Math.sin(sampleX) * (float)Math.cos(sampleY);
                    noiseValue += perlin * amplitude;

                    amplitude *= 0.5f;
                    frequency *= 2f;
                }

                noise[x][y] = (noiseValue + 1f) / 2f; // Normalizar 0-1
            }
        }

        return noise;
    }
}
