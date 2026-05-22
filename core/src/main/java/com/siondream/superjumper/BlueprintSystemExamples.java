package com.siondream.superjumper;

import com.siondream.superjumper.algorithms.BSPMapGenerator;
import com.siondream.superjumper.biome.BiomeGenerator;
import com.siondream.superjumper.blueprint.*;
import com.siondream.superjumper.rooms.RoomCorridorGenerator;
import com.siondream.superjumper.tiles.Tile;
import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;

import java.util.*;

class BlueprintSystemExamples {

    /**
     * Ejemplo 1: Cargar y colocar blueprints en una mazmorra
     */
    public static void example1_BasicUsage() {
        System.out.println("=== EJEMPLO 1: Uso Básico del Blueprint System ===\n");

        long seed = 12345L;

        // 1. Generar mapa base con habitaciones
        RoomCorridorGenerator roomGen = new RoomCorridorGenerator(100, 80, seed);
        roomGen.setParameter("roomCount", 8);
        TileMap map = roomGen.execute();

        // 2. Crear loader y cargar blueprints
        BlueprintLoader loader = new BlueprintLoader();

        // Crear algunos blueprints de ejemplo
        Blueprint treasureRoom = createTreasureRoom();
        Blueprint trapRoom = createTrapRoom();
        Blueprint bossArena = createBossArena();

        // 3. Configurar el placer
        BlueprintPlacerDirector placer = new BlueprintPlacerDirector(map, seed + 1);
        placer.addBlueprint(treasureRoom);
        placer.addBlueprint(trapRoom);
        placer.addBlueprint(bossArena);

        placer.setParameter("maxAttempts", 50);
        placer.setParameter("placementAttempts", 30);

        // 4. Ejecutar colocación
        List<PlacedBlueprint> placed = placer.execute();

        System.out.println("Blueprints colocados: " + placed.size());
        for (PlacedBlueprint pb : placed) {
            System.out.println("  - " + pb.blueprint.name + " en (" +
                pb.worldX + ", " + pb.worldY + ")");
        }

        // 5. Obtener entidades para ECS
        List<BlueprintEntity> entities = placer.getAllPlacedEntities();
        System.out.println("\nEntidades generadas: " + entities.size());
    }

    /**
     * Ejemplo 2: Pipeline completo con blueprints
     */
    public static void example2_CompletePipeline() {
        System.out.println("\n=== EJEMPLO 2: Pipeline Completo ===\n");

        long seed = 67890L;

        // 1. Generar terreno base con BSP
        BSPMapGenerator bspGen = new BSPMapGenerator(120, 100, seed);
        TileMap map = bspGen.execute();

        // 2. Aplicar biomas
        BiomeGenerator biomeGen = new BiomeGenerator(120, 100, seed + 1);
        biomeGen.setParameter("scale", 40f);
        TileMap biomeMap = biomeGen.execute();

        // Fusionar (mantener estructura pero cambiar tipos de tile)
        fuseBiomesWithStructure(map, biomeMap);

        // 3. Cargar blueprints desde archivos (simulado)
        BlueprintLoader loader = new BlueprintLoader();
        Map<String, Blueprint> blueprints = loadExampleBlueprints(loader);

        // 4. Colocar blueprints especiales
        BlueprintPlacerDirector placer = new BlueprintPlacerDirector(map, seed + 2);

        // Añadir solo blueprints de categoría "special"
        for (Blueprint bp : blueprints.values()) {
            if (bp.category.equals("puzzle") || bp.category.equals("boss_arena")) {
                placer.addBlueprint(bp);
            }
        }

        List<PlacedBlueprint> placed = placer.execute();

        // 5. Distribuir items normales (evitando blueprints)
        distributeItemsAvoidingBlueprints(map, placed, seed + 3);

        System.out.println("Pipeline completado:");
        System.out.println("  - Mapa: " + map.getWidth() + "x" + map.getHeight());
        System.out.println("  - Blueprints: " + placed.size());
    }

    /**
     * Ejemplo 3: Blueprints con conexiones (encadenados)
     */
    public static void example3_ConnectedBlueprints() {
        System.out.println("\n=== EJEMPLO 3: Blueprints Conectados ===\n");

        // Este ejemplo muestra cómo crear una cadena de habitaciones
        // conectadas usando los BlueprintConnection

        long seed = 11111L;
        TileMap map = new TileMap(150, 120);

        // Llenar de paredes
        for (int x = 0; x < 150; x++) {
            for (int y = 0; y < 120; y++) {
                map.setTile(x, y, TileType.WALL);
            }
        }

        BlueprintLoader loader = new BlueprintLoader();

        // Crear habitaciones conectables
        Blueprint entrance = loader.createSimpleRoom("entrance", 15, 12);
        entrance.name = "Entrada";
        entrance.placementRules.spawnProbability = 1.0f; // Siempre aparece

        Blueprint corridor = createCorridor(10, 5);
        Blueprint chamber = loader.createSimpleRoom("chamber", 18, 15);
        chamber.name = "Cámara";

        // Sistema de conexión manual (proof of concept)
        // En producción, esto sería automático
        int startX = 10, startY = 10;

        // Colocar entrada
        placeAndConnect(map, entrance, startX, startY);
        System.out.println("Colocada: Entrada en (" + startX + ", " + startY + ")");

        // Colocar corredor conectado
        int corridorX = startX + entrance.width;
        placeAndConnect(map, corridor, corridorX, startY + 3);
        System.out.println("Colocado: Corredor en (" + corridorX + ", " + (startY + 3) + ")");

        // Colocar cámara conectada
        int chamberX = corridorX + corridor.width;
        placeAndConnect(map, chamber, chamberX, startY);
        System.out.println("Colocada: Cámara en (" + chamberX + ", " + startY + ")");
    }

    /**
     * Ejemplo 4: Variaciones de blueprints (procedural sobre manual)
     */
    public static void example4_BlueprintVariations() {
        System.out.println("\n=== EJEMPLO 4: Variaciones de Blueprints ===\n");

        BlueprintLoader loader = new BlueprintLoader();
        Blueprint baseRoom = loader.createSimpleRoom("base", 12, 10);

        // Crear variaciones del mismo blueprint
        List<Blueprint> variations = new ArrayList<>();

        // Variación 1: Añadir pilares
        Blueprint withPillars = baseRoom.clone();
        withPillars.id = "room_pillars";
        withPillars.setTile(3, 3, TileType.STONE);
        withPillars.setTile(8, 3, TileType.STONE);
        withPillars.setTile(3, 6, TileType.STONE);
        withPillars.setTile(8, 6, TileType.STONE);
        variations.add(withPillars);

        // Variación 2: Añadir piscina central
        Blueprint withPool = baseRoom.clone();
        withPool.id = "room_pool";
        for (int x = 4; x < 8; x++) {
            for (int y = 4; y < 6; y++) {
                withPool.setTile(x, y, TileType.WATER);
            }
        }
        variations.add(withPool);

        // Variación 3: Añadir enemigos
        Blueprint withEnemies = baseRoom.clone();
        withEnemies.id = "room_enemies";
        withEnemies.addEntity(new BlueprintEntity("enemy", "skeleton", 6, 5));
        withEnemies.addEntity(new BlueprintEntity("enemy", "zombie", 4, 7));
        variations.add(withEnemies);

        System.out.println("Variaciones creadas: " + variations.size());
        for (Blueprint v : variations) {
            System.out.println("  - " + v.id);
        }
    }

    // ========================================================================
    // MÉTODOS AUXILIARES PARA LOS EJEMPLOS
    // ========================================================================

    private static Blueprint createTreasureRoom() {
        Blueprint bp = new Blueprint("treasure_room", 10, 10);
        bp.name = "Sala del Tesoro";
        bp.category = "treasure";

        // Paredes
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                if (x == 0 || x == 9 || y == 0 || y == 9) {
                    bp.setTile(x, y, TileType.WALL);
                } else {
                    bp.setTile(x, y, TileType.FLOOR);
                }
            }
        }

        // Cofre en el centro
        bp.addEntity(new BlueprintEntity("item", "treasure_chest", 5, 5));

        // Guardianes
        bp.addEntity(new BlueprintEntity("enemy", "guardian", 3, 3));
        bp.addEntity(new BlueprintEntity("enemy", "guardian", 6, 6));

        // Puerta
        bp.addConnection(5, 0, "south");

        // Reglas de colocación
        bp.placementRules.minDistanceToOthers = 15f;
        bp.placementRules.spawnProbability = 0.3f; // 30% de probabilidad

        return bp;
    }

    private static Blueprint createTrapRoom() {
        Blueprint bp = new Blueprint("trap_room", 8, 12);
        bp.name = "Sala de Trampas";
        bp.category = "trap";

        // Pasillo con trampas
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 12; y++) {
                if (x == 0 || x == 7) {
                    bp.setTile(x, y, TileType.WALL);
                } else {
                    bp.setTile(x, y, TileType.FLOOR);
                }
            }
        }

        // Trampas de pinchos (cada 2 tiles)
        for (int y = 2; y < 12; y += 2) {
            bp.addEntity(new BlueprintEntity("trap", "spikes", 4, y));
        }

        // Conexiones
        bp.addConnection(4, 0, "south");
        bp.addConnection(4, 11, "north");

        return bp;
    }

    private static Blueprint createBossArena() {
        Blueprint bp = new Blueprint("boss_arena", 20, 20);
        bp.name = "Arena del Jefe";
        bp.category = "boss_arena";

        // Arena circular-ish
        int centerX = 10, centerY = 10, radius = 9;

        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                int dx = x - centerX;
                int dy = y - centerY;
                float dist = (float)Math.sqrt(dx * dx + dy * dy);

                if (dist > radius) {
                    bp.setTile(x, y, TileType.WALL);
                } else {
                    bp.setTile(x, y, TileType.FLOOR);
                }
            }
        }

        // Boss en el centro
        bp.addEntity(new BlueprintEntity("enemy", "boss_dragon", 10, 10));

        // Pilares
        bp.setTile(6, 6, TileType.STONE);
        bp.setTile(14, 6, TileType.STONE);
        bp.setTile(6, 14, TileType.STONE);
        bp.setTile(14, 14, TileType.STONE);

        // Entrada
        bp.addConnection(10, 0, "south");

        // Reglas: Solo aparece una vez, en niveles profundos
        bp.placementRules.spawnProbability = 1.0f;
        bp.placementRules.minDepth = 3;
        bp.placementRules.minDistanceToOthers = 30f;

        return bp;
    }

    private static Blueprint createCorridor(int length, int width) {
        Blueprint bp = new Blueprint("corridor", length, width);
        bp.name = "Corredor";
        bp.category = "corridor";

        for (int x = 0; x < length; x++) {
            for (int y = 0; y < width; y++) {
                if (y == 0 || y == width - 1) {
                    bp.setTile(x, y, TileType.WALL);
                } else {
                    bp.setTile(x, y, TileType.FLOOR);
                }
            }
        }

        bp.addConnection(0, width / 2, "west");
        bp.addConnection(length - 1, width / 2, "east");

        return bp;
    }

    private static void fuseBiomesWithStructure(TileMap structure, TileMap biomes) {
        for (int x = 0; x < structure.getWidth(); x++) {
            for (int y = 0; y < structure.getHeight(); y++) {
                Tile structureTile = structure.getTile(x, y);
                Tile biomeTile = biomes.getTile(x, y);

                if (structureTile.type == TileType.FLOOR && biomeTile != null) {
                    structureTile.type = biomeTile.type;
                    structureTile.metadata.putAll(biomeTile.metadata);
                }
            }
        }
    }

    private static Map<String, Blueprint> loadExampleBlueprints(BlueprintLoader loader) {
        Map<String, Blueprint> blueprints = new HashMap<>();

        blueprints.put("puzzle", createPuzzleRoom());
        blueprints.put("boss", createBossArena());
        blueprints.put("treasure", createTreasureRoom());

        return blueprints;
    }

    private static Blueprint createPuzzleRoom() {
        Blueprint bp = new Blueprint("puzzle_room", 15, 15);
        bp.name = "Sala de Puzzle";
        bp.category = "puzzle";

        // Crear habitación básica
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 15; y++) {
                if (x == 0 || x == 14 || y == 0 || y == 14) {
                    bp.setTile(x, y, TileType.WALL);
                } else {
                    bp.setTile(x, y, TileType.FLOOR);
                }
            }
        }

        // Botones y puertas para puzzle
        bp.addEntity(new BlueprintEntity("trigger", "pressure_plate", 5, 7));
        bp.addEntity(new BlueprintEntity("trigger", "lever", 9, 7));
        bp.addEntity(new BlueprintEntity("item", "key", 7, 7));

        return bp;
    }

    private static void distributeItemsAvoidingBlueprints(TileMap map,
                                                          List<PlacedBlueprint> blueprints,
                                                          long seed) {
        Random rand = new Random(seed);
        int itemsPlaced = 0;

        for (int attempt = 0; attempt < 100; attempt++) {
            int x = rand.nextInt(map.getWidth());
            int y = rand.nextInt(map.getHeight());

            Tile tile = map.getTile(x, y);
            if (tile == null || tile.type != TileType.FLOOR) continue;

            // Verificar que no está en un blueprint
            boolean inBlueprint = false;
            for (PlacedBlueprint bp : blueprints) {
                if (bp.bounds.contains(x, y)) {
                    inBlueprint = true;
                    break;
                }
            }

            if (!inBlueprint) {
                // Aquí colocarías el item real
                tile.metadata.put("item", "coin");
                itemsPlaced++;
            }
        }

        System.out.println("Ítems distribuidos: " + itemsPlaced);
    }

    private static void placeAndConnect(TileMap map, Blueprint bp, int x, int y) {
        for (int bx = 0; bx < bp.width; bx++) {
            for (int by = 0; by < bp.height; by++) {
                TileType tile = bp.getTile(bx, by);
                if (tile != TileType.EMPTY) {
                    map.setTile(x + bx, y + by, tile);
                }
            }
        }
    }
}

