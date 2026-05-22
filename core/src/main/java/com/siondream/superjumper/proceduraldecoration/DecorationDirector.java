package com.siondream.superjumper.proceduraldecoration;

import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.GenerationDirector;
import com.siondream.superjumper.tiles.Tile;
import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DecorationDirector extends GenerationDirector<List<DecorationElement>> {
    private final TileMap map;

    public DecorationDirector(TileMap map, long seed) {
        super(seed);
        this.map = map;
    }

    @Override
    protected void setupRules() {
        ruleEngine.addRule(new DecorationRule(TileType.WALL, "TORCH"));
        ruleEngine.addRule(new DecorationRule(TileType.WATER, "PLANT"));
    }

    @Override
    protected List<DecorationElement> generate() {
        List<DecorationElement> decorations = new ArrayList<>();
        Random rand = context.getRandom();

        float decorationDensity = (float)parameters.getOrDefault("density", 0.1f);

        for (int x = 1; x < map.getWidth() - 1; x++) {
            for (int y = 1; y < map.getHeight() - 1; y++) {
                Tile tile = map.getTile(x, y);

                if (tile.type == TileType.FLOOR && rand.nextFloat() < decorationDensity) {
                    String decorType = selectDecoration(x, y, rand);

                    DecorationElement decoration = new DecorationElement(
                        decorType,
                        new Vector2(x, y)
                    );
                    decoration.rotation = rand.nextFloat() * 360f;
                    decoration.scale = 0.8f + rand.nextFloat() * 0.4f;

                    decorations.add(decoration);
                }
            }
        }

        return decorations;
    }

    private String selectDecoration(int x, int y, Random rand) {
        // Verificar tiles adyacentes
        boolean nearWall = isAdjacentToType(x, y, TileType.WALL);
        boolean nearWater = isAdjacentToType(x, y, TileType.WATER);

        if (nearWall && rand.nextFloat() < 0.3f) return "TORCH";
        if (nearWater && rand.nextFloat() < 0.4f) return "PLANT";

        String[] generic = {"ROCK", "DEBRIS", "MUSHROOM", "SKULL", "BARREL"};
        return generic[rand.nextInt(generic.length)];
    }

    private boolean isAdjacentToType(int x, int y, TileType type) {
        int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        for (int[] dir : dirs) {
            Tile adjacent = map.getTile(x + dir[0], y + dir[1]);
            if (adjacent != null && adjacent.type == type) return true;
        }
        return false;
    }
}
