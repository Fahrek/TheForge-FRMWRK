package com.siondream.superjumper.shapes;

import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.tiles.TileType;

import java.util.ArrayList;
import java.util.List;

public class GeometricShape {
    public ShapeType type;
    public Vector2 position;
    public float rotation;
    public float scaleX, scaleY;
    public List<Vector2> vertices;
    public TileType fillType;

    public GeometricShape(ShapeType type, Vector2 position) {
        this.type = type;
        this.position = position;
        this.rotation = 0;
        this.scaleX = this.scaleY = 1f;
        this.vertices = new ArrayList<>();
        this.fillType = TileType.WALL;
    }
}
