package com.siondream.superjumper.proceduraldecoration;

import com.siondream.superjumper.GenerationContext;
import com.siondream.superjumper.Rule;
import com.siondream.superjumper.tiles.TileType;

public class DecorationRule implements Rule {
    private final TileType adjacentType;

    public DecorationRule(TileType adjacentType, String decorationType) {
        this.adjacentType = adjacentType;
    }

    @Override
    public boolean evaluate(GenerationContext context) {
        return context.has("adjacentTile") &&
            context.get("adjacentTile", TileType.class) == adjacentType;
    }

    @Override
    public float getPriority() { return 3f; }
}
