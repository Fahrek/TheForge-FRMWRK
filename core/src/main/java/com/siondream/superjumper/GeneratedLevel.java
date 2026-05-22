package com.siondream.superjumper;

import com.siondream.superjumper.helpers.MapAnalyzer;
import com.siondream.superjumper.platforms.Platform;
import com.siondream.superjumper.proceduraldecoration.DecorationElement;
import com.siondream.superjumper.rooms.Room;
import com.siondream.superjumper.scatteritems.ItemPlacement;
import com.siondream.superjumper.shapes.GeometricShape;
import com.siondream.superjumper.tiles.TileMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenedor para un nivel generado completo
 */
public class GeneratedLevel {
    public String type;
    public TileMap map;
    public List<Room> rooms;
    public List<Platform> platforms;
    public List<ItemPlacement> items;
    public List<DecorationElement> decorations;
    public List<GeometricShape> shapes;

    public GeneratedLevel() {
        this.rooms = new ArrayList<>();
        this.platforms = new ArrayList<>();
        this.items = new ArrayList<>();
        this.decorations = new ArrayList<>();
        this.shapes = new ArrayList<>();
    }

    public void printStatistics() {
        System.out.println("\n========== ESTADÍSTICAS DEL NIVEL ==========");
        System.out.println("Tipo: " + type);
        if (map != null) {
            System.out.println("Dimensiones: " + map.getWidth() + "x" + map.getHeight());
            System.out.println("Apertura: " + String.format("%.1f%%",
                MapAnalyzer.calculateOpenness(map) * 100));
        }
        System.out.println("Habitaciones: " + rooms.size());
        System.out.println("Plataformas: " + platforms.size());
        System.out.println("Ítems: " + items.size());
        System.out.println("Decoraciones: " + decorations.size());
        System.out.println("Formas: " + shapes.size());
        System.out.println("============================================\n");
    }
}
