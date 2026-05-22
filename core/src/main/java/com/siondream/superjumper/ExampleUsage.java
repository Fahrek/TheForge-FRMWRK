package com.siondream.superjumper;

import com.badlogic.gdx.math.MathUtils;
import com.siondream.superjumper.algorithms.BSPMapGenerator;
import com.siondream.superjumper.biome.BiomeGenerator;
import com.siondream.superjumper.components.ColliderComponent;
import com.siondream.superjumper.components.SpriteComponent;
import com.siondream.superjumper.components.TransformComponent;
import com.siondream.superjumper.components.VelocityComponent;
import com.siondream.superjumper.helpers.MapAnalyzer;
import com.siondream.superjumper.movementpatterns.MovementPattern;
import com.siondream.superjumper.movementpatterns.MovementPatternGenerator;
import com.siondream.superjumper.platforms.PlatformGenerator;
import com.siondream.superjumper.proceduraldecoration.DecorationDirector;
import com.siondream.superjumper.proceduraldecoration.DecorationElement;
import com.siondream.superjumper.rooms.Room;
import com.siondream.superjumper.rooms.RoomCorridorGenerator;
import com.siondream.superjumper.scatteritems.ItemDistributionDirector;
import com.siondream.superjumper.scatteritems.ItemPlacement;
import com.siondream.superjumper.shapes.GeometricShape;
import com.siondream.superjumper.shapes.ShapeGenerator;
import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;
import com.siondream.superjumper.algorithms.CellularAutomataGenerator;
import com.siondream.superjumper.platforms.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * Ejemplo de cómo usar todos los directores juntos
 */
class ExampleUsage {

    public static void generateCompleteLevel() {
        long seed = 12345L;

        // 1. GENERAR MAPA BASE CON HABITACIONES Y CORREDORES
        System.out.println("=== Generando estructura base del mapa ===");
        RoomCorridorGenerator roomGen = new RoomCorridorGenerator(100, 80, seed);
        roomGen.setParameter("roomCount", 12);
        roomGen.setParameter("minRoomSize", 6);
        roomGen.setParameter("maxRoomSize", 14);

        TileMap baseMap = roomGen.execute();
        List<Room> rooms = roomGen.getRooms();
        System.out.println("Habitaciones generadas: " + rooms.size());

        // 2. APLICAR BIOMAS A HABITACIONES ESPECÍFICAS
        System.out.println("\n=== Aplicando biomas ===");
        BiomeGenerator biomeGen = new BiomeGenerator(100, 80, seed + 1);
        biomeGen.setParameter("scale", 30f);
        biomeGen.setParameter("octaves", 3);

        TileMap biomeMap = biomeGen.execute();
        // Mezclar biomas con el mapa base (mantener estructura de habitaciones)
        applyBiomesToRooms(baseMap, biomeMap, rooms);

        // 3. AÑADIR FORMAS GEOMÉTRICAS DECORATIVAS
        System.out.println("\n=== Añadiendo formas geométricas ===");
        ShapeGenerator shapeGen = new ShapeGenerator(baseMap, seed + 2);
        shapeGen.setParameter("shapeCount", 5);
        List<GeometricShape> shapes = shapeGen.execute();
        System.out.println("Formas generadas: " + shapes.size());

        // 4. DISTRIBUIR ÍTEMS
        System.out.println("\n=== Distribuyendo ítems ===");
        ItemDistributionDirector itemGen = new ItemDistributionDirector(baseMap, rooms, seed + 3);
        itemGen.setParameter("density", 0.08f);
        List<ItemPlacement> items = itemGen.execute();
        System.out.println("Ítems colocados: " + items.size());

        // 5. AÑADIR DECORACIÓN
        System.out.println("\n=== Añadiendo decoración ===");
        DecorationDirector decorGen = new DecorationDirector(baseMap, seed + 4);
        decorGen.setParameter("density", 0.12f);
        List<DecorationElement> decorations = decorGen.execute();
        System.out.println("Elementos decorativos: " + decorations.size());

        // 6. GENERAR PATRONES DE MOVIMIENTO PARA ENEMIGOS
        System.out.println("\n=== Generando patrones de movimiento ===");
        List<MovementPattern> enemyPatterns = new ArrayList<>();

        for (ItemPlacement item : items) {
            if (item.itemType.equals("ENEMY")) {
                MovementPatternGenerator patternGen = new MovementPatternGenerator(
                    seed + item.position.hashCode(),
                    selectRandomPattern()
                );
                patternGen.setParameter("speed", 100f + MathUtils.random(50f));
                patternGen.setParameter("radius", 30f + MathUtils.random(40f));

                MovementPattern pattern = patternGen.execute();
                enemyPatterns.add(pattern);
            }
        }
        System.out.println("Patrones de enemigos: " + enemyPatterns.size());

        // 7. ANÁLISIS DEL MAPA GENERADO
        System.out.println("\n=== Análisis del mapa ===");
        analyzeMap(baseMap);

        // 8. EJEMPLO ALTERNATIVO: GENERAR CON BSP
        System.out.println("\n=== Generación alternativa con BSP ===");
        BSPMapGenerator bspGen = new BSPMapGenerator(100, 80, seed + 5);
        TileMap bspMap = bspGen.execute();
        analyzeMap(bspMap);

        // 9. EJEMPLO: GENERAR CUEVAS CON CELLULAR AUTOMATA
        System.out.println("\n=== Generación de cuevas ===");
        CellularAutomataGenerator caveGen = new CellularAutomataGenerator(100, 80, seed + 6);
        caveGen.setParameter("wallProbability", 0.48f);
        caveGen.setParameter("iterations", 6);
        TileMap caveMap = caveGen.execute();
        analyzeMap(caveMap);

        // 10. GENERAR NIVEL DE PLATAFORMAS
        System.out.println("\n=== Generando nivel de plataformas ===");
        PlatformGenerator platformGen = new PlatformGenerator(2000, 800, seed + 7);
        platformGen.setParameter("platformCount", 30);
        platformGen.setParameter("minGap", 80f);
        platformGen.setParameter("maxGap", 200f);
        platformGen.setParameter("baseHeight", 150f);

        List<Platform> platforms = platformGen.execute();
        System.out.println("Plataformas generadas: " + platforms.size());

        int movingPlatforms = 0;
        for (Platform p : platforms) {
            if (p.moving) movingPlatforms++;
        }
        System.out.println("Plataformas móviles: " + movingPlatforms);
    }

    private static void applyBiomesToRooms(TileMap baseMap, TileMap biomeMap, List<Room> rooms) {
        // Solo aplicar biomas a tiles de suelo
        for (int x = 0; x < baseMap.getWidth(); x++) {
            for (int y = 0; y < baseMap.getHeight(); y++) {
                if (baseMap.getTile(x, y).type == TileType.FLOOR) {
                    TileType biomeTile = biomeMap.getTile(x, y).type;
                    baseMap.setTile(x, y, biomeTile);
                }
            }
        }
    }

    private static String selectRandomPattern() {
        String[] patterns = {"CIRCULAR", "WAVE", "ZIGZAG"};
        return patterns[MathUtils.random(patterns.length - 1)];
    }

    private static void analyzeMap(TileMap map) {
        int floor = MapAnalyzer.countTilesOfType(map, TileType.FLOOR);
        int wall = MapAnalyzer.countTilesOfType(map, TileType.WALL);
        float openness = MapAnalyzer.calculateOpenness(map);

        System.out.println("  Tiles de suelo: " + floor);
        System.out.println("  Tiles de pared: " + wall);
        System.out.println("  Apertura: " + String.format("%.2f%%", openness * 100));
    }

    // ============================================================================
    // EJEMPLO DE INTEGRACIÓN CON ECS
    // ============================================================================

    public static void integrateWithECS(World world, TileMap map, List<ItemPlacement> items,
                                        List<MovementPattern> patterns) {
        System.out.println("\n=== Integrando con sistema ECS ===");

        // Crear entidad para cada ítem
        int patternIndex = 0;
        for (ItemPlacement item : items) {
            Entity entity = world.createEntity();
            entity.setTag(item.itemType);

            // Añadir componentes
            entity.addComponent(new TransformComponent(
                item.position.x * 32, // Convertir a píxeles
                item.position.y * 32
            ));

            entity.addComponent(new SpriteComponent(
                "textures/" + item.itemType.toLowerCase() + ".png",
                32, 32
            ));

            // Si es enemigo, añadir patrón de movimiento
            if (item.itemType.equals("ENEMY") && patternIndex < patterns.size()) {
                VelocityComponent velocity = new VelocityComponent();
                velocity.maxSpeed = 150f;
                entity.addComponent(velocity);

                // Aquí podrías crear un componente personalizado para el patrón
                // entity.addComponent(new MovementPatternComponent(patterns.get(patternIndex)));
                patternIndex++;
            }

            // Añadir colisión
            entity.addComponent(new ColliderComponent(28, 28));
        }

        System.out.println("Entidades creadas: " + items.size());
    }
}
