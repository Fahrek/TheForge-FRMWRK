package com.siondream.superjumper.algorithms;

import com.siondream.superjumper.GenerationDirector;
import com.siondream.superjumper.rooms.Room;
import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;

import java.util.Random;

/**
 * Generador usando Binary Space Partitioning (BSP)
 */
public class BSPMapGenerator extends GenerationDirector<TileMap> {
    private final int width, height;

    private static class BSPNode {
        int x, y, width, height;
        BSPNode left, right;
        Room room;

        BSPNode(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    public BSPMapGenerator(int width, int height, long seed) {
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

        // Llenar de paredes
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map.setTile(x, y, TileType.WALL);
            }
        }

        // Crear árbol BSP
        BSPNode root = new BSPNode(0, 0, width, height);
        splitNode(root, rand, 0);

        // Crear habitaciones
        createRooms(root, map, rand);

        // Conectar habitaciones
        connectRooms(root, map, rand);

        return map;
    }

    private void splitNode(BSPNode node, Random rand, int depth) {
        if (depth >= 4 || node.width < 20 || node.height < 20) return;

        boolean horizontal = rand.nextBoolean();

        if (horizontal) {
            int split = node.height / 2 + rand.nextInt(node.height / 4) - node.height / 8;
            node.left = new BSPNode(node.x, node.y, node.width, split);
            node.right = new BSPNode(node.x, node.y + split, node.width, node.height - split);
        } else {
            int split = node.width / 2 + rand.nextInt(node.width / 4) - node.width / 8;
            node.left = new BSPNode(node.x, node.y, split, node.height);
            node.right = new BSPNode(node.x + split, node.y, node.width - split, node.height);
        }

        splitNode(node.left, rand, depth + 1);
        splitNode(node.right, rand, depth + 1);
    }

    private void createRooms(BSPNode node, TileMap map, Random rand) {
        if (node.left == null && node.right == null) {
            int roomWidth = node.width / 2 + rand.nextInt(node.width / 3);
            int roomHeight = node.height / 2 + rand.nextInt(node.height / 3);
            int roomX = node.x + rand.nextInt(node.width - roomWidth);
            int roomY = node.y + rand.nextInt(node.height - roomHeight);

            node.room = new Room(roomX, roomY, roomWidth, roomHeight);

            for (int x = roomX; x < roomX + roomWidth; x++) {
                for (int y = roomY; y < roomY + roomHeight; y++) {
                    map.setTile(x, y, TileType.FLOOR);
                }
            }
        } else {
            if (node.left != null) createRooms(node.left, map, rand);
            if (node.right != null) createRooms(node.right, map, rand);
        }
    }

    private void connectRooms(BSPNode node, TileMap map, Random rand) {
        if (node.left != null && node.right != null) {
            connectRooms(node.left, map, rand);
            connectRooms(node.right, map, rand);

            Room room1 = getRoom(node.left);
            Room room2 = getRoom(node.right);

            if (room1 != null && room2 != null) {
                createCorridor(map, room1, room2, rand);
            }
        }
    }

    private Room getRoom(BSPNode node) {
        if (node.room != null) return node.room;
        if (node.left != null) {
            Room room = getRoom(node.left);
            if (room != null) return room;
        }
        if (node.right != null) return getRoom(node.right);
        return null;
    }

    private void createCorridor(TileMap map, Room r1, Room r2, Random rand) {
        int x1 = (int)r1.center.x;
        int y1 = (int)r1.center.y;
        int x2 = (int)r2.center.x;
        int y2 = (int)r2.center.y;

        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            map.setTile(x, y1, TileType.FLOOR);
        }
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            map.setTile(x2, y, TileType.FLOOR);
        }
    }
}
