package com.siondream.superjumper.shapes;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.GenerationDirector;
import com.siondream.superjumper.tiles.TileMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShapeGenerator extends GenerationDirector<List<GeometricShape>> {
    private final TileMap targetMap;

    public ShapeGenerator(TileMap map, long seed) {
        super(seed);
        this.targetMap = map;
    }

    @Override
    protected void setupRules() {
        // Reglas de generación de formas
    }

    @Override
    protected List<GeometricShape> generate() {
        List<GeometricShape> shapes = new ArrayList<>();
        Random rand = context.getRandom();

        int shapeCount = (int)parameters.getOrDefault("shapeCount", 10);

        for (int i = 0; i < shapeCount; i++) {
            ShapeType type = ShapeType.values()[rand.nextInt(ShapeType.values().length - 1)];
            Vector2 pos = new Vector2(
                rand.nextInt(targetMap.getWidth()),
                rand.nextInt(targetMap.getHeight())
            );

            GeometricShape shape = createShape(type, pos, rand);
            shapes.add(shape);
            applyShapeToMap(shape);
        }

        return shapes;
    }

    private GeometricShape createShape(ShapeType type, Vector2 pos, Random rand) {
        GeometricShape shape = new GeometricShape(type, pos);
        shape.rotation = rand.nextFloat() * 360f;

        switch (type) {
            case RECTANGLE:
                shape.scaleX = 5 + rand.nextInt(15);
                shape.scaleY = 5 + rand.nextInt(15);
                break;

            case CIRCLE:
                float radius = 5 + rand.nextInt(10);
                shape.scaleX = shape.scaleY = radius;
                break;

            case TRIANGLE:
                shape.vertices.add(new Vector2(0, 10));
                shape.vertices.add(new Vector2(-5, 0));
                shape.vertices.add(new Vector2(5, 0));
                break;

            case POLYGON:
                int sides = 5 + rand.nextInt(3);
                float polyRadius = 8f;
                for (int i = 0; i < sides; i++) {
                    float angle = (float)(i * 2 * Math.PI / sides);
                    shape.vertices.add(new Vector2(
                        MathUtils.cos(angle) * polyRadius,
                        MathUtils.sin(angle) * polyRadius
                    ));
                }
                break;
        }

        return shape;
    }

    private void applyShapeToMap(GeometricShape shape) {
        int cx = (int)shape.position.x;
        int cy = (int)shape.position.y;

        switch (shape.type) {
            case RECTANGLE:
                for (int x = 0; x < (int)shape.scaleX; x++) {
                    for (int y = 0; y < (int)shape.scaleY; y++) {
                        targetMap.setTile(cx + x, cy + y, shape.fillType);
                    }
                }
                break;

            case CIRCLE:
                int radius = (int)shape.scaleX;
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        if (x * x + y * y <= radius * radius) {
                            targetMap.setTile(cx + x, cy + y, shape.fillType);
                        }
                    }
                }
                break;
        }
    }
}
