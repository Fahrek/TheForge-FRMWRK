package com.siondream.superjumper.blueprint;

import com.badlogic.gdx.math.Rectangle;

/**
 * Información de un blueprint colocado en el mundo
 */
public class PlacedBlueprint {
    public Blueprint blueprint;
    public int worldX, worldY; // Posición en el mundo
    public Rectangle bounds; // Área ocupada

    public PlacedBlueprint(Blueprint bp, int x, int y) {
        this.blueprint = bp;
        this.worldX = x;
        this.worldY = y;
        this.bounds = new Rectangle(x, y, bp.width, bp.height);
    }
}
