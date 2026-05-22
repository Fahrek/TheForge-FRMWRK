package com.siondream.superjumper.scatteritems;

import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.GenerationDirector;
import com.siondream.superjumper.rooms.Room;
import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;

import java.util.*;

public class ItemDistributionDirector extends GenerationDirector<List<ItemPlacement>> {
    private final TileMap map;
    private final List<Room> rooms;

    public ItemDistributionDirector(TileMap map, List<Room> rooms, long seed) {
        super(seed);
        this.map = map;
        this.rooms = rooms;
    }

    @Override
    protected void setupRules() {
        // Reglas de distribución
        ruleEngine.addRule(new ItemDistributionRule("TREASURE", 20f, TileType.FLOOR));
        ruleEngine.addRule(new ItemDistributionRule("ENEMY", 15f, TileType.FLOOR));
        ruleEngine.addRule(new ItemDistributionRule("POWERUP", 10f, TileType.FLOOR));
    }

    @Override
    protected List<ItemPlacement> generate() {
        List<ItemPlacement> items = new ArrayList<>();
        Random rand = context.getRandom();

        float density = (float) parameters.getOrDefault("density", 0.05f);
        Map<String, Float> itemWeights = new HashMap<>();
        itemWeights.put("COIN", 0.5f);
        itemWeights.put("HEALTH", 0.2f);
        itemWeights.put("WEAPON", 0.1f);
        itemWeights.put("KEY", 0.05f);
        itemWeights.put("TREASURE", 0.05f);
        itemWeights.put("ENEMY", 0.1f);

        // Distribuir en habitaciones
        for (Room room : rooms) {
            int itemsInRoom = (int) (room.width * room.height * density);

            for (int i = 0; i < itemsInRoom; i++) {
                int x = room.x + 1 + rand.nextInt(room.width - 2);
                int y = room.y + 1 + rand.nextInt(room.height - 2);

                if (map.getTile(x, y).type == TileType.FLOOR) {
                    String itemType = selectWeightedItem(itemWeights, rand);
                    ItemPlacement item = new ItemPlacement(
                        itemType,
                        new Vector2(x, y),
                        1.0f
                    );
                    items.add(item);
                }
            }
        }

        // Distribuir en corredores (menor densidad)
        distributeInCorridors(items, density * 0.3f, itemWeights, rand);

        return items;
    }

    private void distributeInCorridors(List<ItemPlacement> items, float density,
                                       Map<String, Float> weights, Random rand) {
        for (int x = 0; x < map.getWidth(); x++) {
            for (int y = 0; y < map.getHeight(); y++) {
                if (map.getTile(x, y).type == TileType.FLOOR && rand.nextFloat() < density) {
                    if (!isInRoom(x, y)) {
                        String itemType = selectWeightedItem(weights, rand);
                        items.add(new ItemPlacement(itemType, new Vector2(x, y), 1.0f));
                    }
                }
            }
        }
    }

    private boolean isInRoom(int x, int y) {
        for (Room room : rooms) {
            if (x >= room.x && x < room.x + room.width &&
                y >= room.y && y < room.y + room.height) {
                return true;
            }
        }
        return false;
    }

    private String selectWeightedItem(Map<String, Float> weights, Random rand) {
        float total = 0f;
        for (float w : weights.values()) total += w;

        float value = rand.nextFloat() * total;
        float cumulative = 0f;

        for (Map.Entry<String, Float> entry : weights.entrySet()) {
            cumulative += entry.getValue();
            if (value <= cumulative) {
                return entry.getKey();
            }
        }

        return "COIN";
    }
}
