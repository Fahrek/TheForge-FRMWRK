package com.siondream.superjumper.tiles;

public class TileMap {
    private final int width, height;
    private final Tile[][] tiles;

    public TileMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new Tile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles[x][y] = new Tile(x, y, TileType.EMPTY);
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return null;
        return tiles[x][y];
    }

    public void setTile(int x, int y, TileType type) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            tiles[x][y].type = type;
        }
    }

    public boolean isWalkable(int x, int y) {
        Tile tile = getTile(x, y);
        return tile != null && tile.type != TileType.WALL && tile.type != TileType.EMPTY;
    }
}
