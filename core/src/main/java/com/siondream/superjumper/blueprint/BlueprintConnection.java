package com.siondream.superjumper.blueprint;

/**
 * Punto de conexión del blueprint (puertas, entradas, salidas)
 */
public class BlueprintConnection {
    public int x, y; // Posición local en el blueprint
    public String direction; // "north", "south", "east", "west"
    public boolean required; // Si es obligatorio conectar

    public BlueprintConnection(int x, int y, String direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.required = false;
    }

    public BlueprintConnection clone() {
        BlueprintConnection copy = new BlueprintConnection(x, y, direction);
        copy.required = this.required;
        return copy;
    }
}
