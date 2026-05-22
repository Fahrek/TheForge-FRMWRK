package com.siondream.superjumper.rooms;

import com.siondream.superjumper.GenerationDirector;
import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomCorridorGenerator extends GenerationDirector<TileMap> {
    private final int width, height;
    private final List<Room> rooms;

    public RoomCorridorGenerator(int width, int height, long seed) {
        super(seed);
        this.width = width;
        this.height = height;
        this.rooms = new ArrayList<>();
    }

    @Override
    protected void setupRules() {
        // Reglas para generación de mazmorras
    }

    @Override
    protected TileMap generate() {
        TileMap map = new TileMap(width, height);
        Random rand = context.getRandom();

        // Llenar todo de paredes
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                map.setTile(x, y, TileType.WALL);
            }
        }

        // Generar habitaciones
        int roomCount = (int)parameters.getOrDefault("roomCount", 10);
        int minRoomSize = (int)parameters.getOrDefault("minRoomSize", 5);
        int maxRoomSize = (int)parameters.getOrDefault("maxRoomSize", 12);
        int attempts = 0;
        int maxAttempts = 100;

        while (rooms.size() < roomCount && attempts < maxAttempts) {
            int w = minRoomSize + rand.nextInt(maxRoomSize - minRoomSize);
            int h = minRoomSize + rand.nextInt(maxRoomSize - minRoomSize);
            int x = 1 + rand.nextInt(width - w - 2);
            int y = 1 + rand.nextInt(height - h - 2);

            Room newRoom = new Room(x, y, w, h);
            boolean valid = true;

            for (Room room : rooms) {
                if (newRoom.intersects(room)) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                createRoom(map, newRoom);

                if (!rooms.isEmpty()) {
                    Room prevRoom = rooms.get(rooms.size() - 1);
                    createCorridor(map, prevRoom, newRoom, rand);
                }

                rooms.add(newRoom);
            }

            attempts++;
        }

        return map;
    }

    private void createRoom(TileMap map, Room room) {
        for (int x = room.x; x < room.x + room.width; x++) {
            for (int y = room.y; y < room.y + room.height; y++) {
                map.setTile(x, y, TileType.FLOOR);
            }
        }
    }

    private void createCorridor(TileMap map, Room room1, Room room2, Random rand) {
        int x1 = (int)room1.center.x;
        int y1 = (int)room1.center.y;
        int x2 = (int)room2.center.x;
        int y2 = (int)room2.center.y;

        if (rand.nextBoolean()) {
            // Horizontal luego vertical
            createHorizontalTunnel(map, x1, x2, y1);
            createVerticalTunnel(map, y1, y2, x2);
        } else {
            // Vertical luego horizontal
            createVerticalTunnel(map, y1, y2, x1);
            createHorizontalTunnel(map, x1, x2, y2);
        }
    }

    private void createHorizontalTunnel(TileMap map, int x1, int x2, int y) {
        int start = Math.min(x1, x2);
        int end = Math.max(x1, x2);
        for (int x = start; x <= end; x++) {
            map.setTile(x, y, TileType.FLOOR);
        }
    }

    private void createVerticalTunnel(TileMap map, int y1, int y2, int x) {
        int start = Math.min(y1, y2);
        int end = Math.max(y1, y2);
        for (int y = start; y <= end; y++) {
            map.setTile(x, y, TileType.FLOOR);
        }
    }

    public List<Room> getRooms() { return rooms; }
}
