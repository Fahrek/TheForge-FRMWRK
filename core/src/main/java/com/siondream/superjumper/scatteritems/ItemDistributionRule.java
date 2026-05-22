package com.siondream.superjumper.scatteritems;

import com.siondream.superjumper.GenerationContext;
import com.siondream.superjumper.Rule;
import com.siondream.superjumper.tiles.Tile;
import com.siondream.superjumper.tiles.TileType;

public class ItemDistributionRule implements Rule {
    private final TileType requiredTile;

    public ItemDistributionRule(String itemType, float minDistance, TileType requiredTile) {
        this.requiredTile = requiredTile;
    }

    @Override
    public boolean evaluate(GenerationContext context) {
        if (!context.has("currentTile")) return false;
        Tile tile = context.get("currentTile", Tile.class);
        return tile.type == requiredTile;
    }

    @Override
    public float getPriority() { return 5f; }
}
