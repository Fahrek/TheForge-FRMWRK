package com.siondream.superjumper.biome;

import com.siondream.superjumper.tiles.TileType;

public class BiomeType {
    public String name;
    public TileType primaryTile;
    public TileType secondaryTile;
    public float temperature; // -1 (frío) a 1 (caliente)
    public float humidity;    // 0 (seco) a 1 (húmedo)

    public BiomeType(String name, TileType primary, TileType secondary, float temp, float humid) {
        this.name = name;
        this.primaryTile = primary;
        this.secondaryTile = secondary;
        this.temperature = temp;
        this.humidity = humid;
    }
}
