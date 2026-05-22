package com.siondream.superjumper.blueprint;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.siondream.superjumper.tiles.TileType;

import java.util.HashMap;
import java.util.Map;

/**
 * Carga blueprints desde diferentes formatos
 */
public class BlueprintLoader {
    private final Map<String, Blueprint> blueprintCache;

    public BlueprintLoader() {
        this.blueprintCache = new HashMap<>();
        Json json = new Json();
    }

    /**
     * Carga un blueprint desde JSON simple
     */
    public Blueprint loadFromJson(FileHandle file) {
        try {
            JsonValue root = new com.badlogic.gdx.utils.JsonReader().parse(file);

            String id = root.getString("id");
            String name = root.getString("name", id);
            String category = root.getString("category", "generic");
            int width = root.getInt("width");
            int height = root.getInt("height");

            Blueprint blueprint = new Blueprint(id, width, height);
            blueprint.name = name;
            blueprint.category = category;

            // Cargar tiles
            JsonValue tilesArray = root.get("tiles");
            if (tilesArray != null && tilesArray.isArray()) {
                int idx = 0;
                for (JsonValue row : tilesArray) {
                    String rowStr = row.asString();
                    for (int x = 0; x < Math.min(rowStr.length(), width); x++) {
                        char c = rowStr.charAt(x);
                        blueprint.setTile(x, height - 1 - idx, charToTileType(c));
                    }
                    idx++;
                }
            }

            // Cargar entidades
            JsonValue entitiesArray = root.get("entities");
            if (entitiesArray != null && entitiesArray.isArray()) {
                for (JsonValue entityJson : entitiesArray) {
                    String type = entityJson.getString("type");
                    String entityId = entityJson.getString("id");
                    float x = entityJson.getFloat("x");
                    float y = entityJson.getFloat("y");

                    BlueprintEntity entity = new BlueprintEntity(type, entityId, x, y);

                    // Cargar propiedades adicionales
                    JsonValue props = entityJson.get("properties");
                    if (props != null) {
                        for (JsonValue prop : props) {
                            entity.properties.put(prop.name, prop.asString());
                        }
                    }

                    blueprint.addEntity(entity);
                }
            }

            // Cargar conexiones
            JsonValue connectionsArray = root.get("connections");
            if (connectionsArray != null && connectionsArray.isArray()) {
                for (JsonValue connJson : connectionsArray) {
                    int x = connJson.getInt("x");
                    int y = connJson.getInt("y");
                    String dir = connJson.getString("direction");
                    blueprint.addConnection(x, y, dir);
                }
            }

            // Cargar reglas de colocación
            JsonValue rules = root.get("placementRules");
            if (rules != null) {
                loadPlacementRules(blueprint.placementRules, rules);
            }

            blueprintCache.put(id, blueprint);
            return blueprint;

        } catch (Exception e) {
            System.err.println("Error cargando blueprint: " + file.path());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Carga reglas de colocación desde JSON
     */
    private void loadPlacementRules(BlueprintPlacementRules rules, JsonValue json) {
        rules.requiresFloor = json.getBoolean("requiresFloor", true);
        rules.requiresWalls = json.getBoolean("requiresWalls", false);
        rules.allowOverlap = json.getBoolean("allowOverlap", false);
        rules.minDistanceToOthers = json.getFloat("minDistanceToOthers", 5f);
        rules.spawnProbability = json.getFloat("spawnProbability", 1.0f);
        rules.minDepth = json.getInt("minDepth", 0);
        rules.maxDepth = json.getInt("maxDepth", Integer.MAX_VALUE);

        JsonValue requiredBiomes = json.get("requiredBiomes");
        if (requiredBiomes != null && requiredBiomes.isArray()) {
            for (JsonValue biome : requiredBiomes) {
                rules.requiredBiomes.add(biome.asString());
            }
        }

        JsonValue forbiddenBiomes = json.get("forbiddenBiomes");
        if (forbiddenBiomes != null && forbiddenBiomes.isArray()) {
            for (JsonValue biome : forbiddenBiomes) {
                rules.forbiddenBiomes.add(biome.asString());
            }
        }
    }

    /**
     * Convierte un carácter a TileType (para mapas ASCII)
     */
    private TileType charToTileType(char c) {
        switch (c) {
            case '#': return TileType.WALL;
            case '.': return TileType.FLOOR;
            case '~': return TileType.WATER;
            case '^': return TileType.GRASS;
            case 'S': return TileType.STONE;
            case 'L': return TileType.LAVA;
            case 'I': return TileType.ICE;
            case 's': return TileType.SAND;
            case 'd': return TileType.DIRT;
            default: return TileType.EMPTY;
        }
    }

    /**
     * Carga todos los blueprints de un directorio
     */
    public Map<String, Blueprint> loadDirectory(FileHandle directory) {
        Map<String, Blueprint> loaded = new HashMap<>();

        if (directory.exists() && directory.isDirectory()) {
            for (FileHandle file : directory.list(".json")) {
                Blueprint bp = loadFromJson(file);
                if (bp != null) {
                    loaded.put(bp.id, bp);
                }
            }
        }

        return loaded;
    }

    /**
     * Obtiene un blueprint de la caché
     */
    public Blueprint getBlueprint(String id) {
        return blueprintCache.get(id);
    }

    /**
     * Crea un blueprint programáticamente (helper para testing)
     */
    public Blueprint createSimpleRoom(String id, int width, int height) {
        Blueprint bp = new Blueprint(id, width, height);
        bp.name = "Simple Room";
        bp.category = "room";

        // Crear habitación con paredes
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    bp.setTile(x, y, TileType.WALL);
                } else {
                    bp.setTile(x, y, TileType.FLOOR);
                }
            }
        }

        // Añadir puertas en el centro de cada lado
        bp.addConnection(width / 2, 0, "south");
        bp.addConnection(width / 2, height - 1, "north");
        bp.addConnection(0, height / 2, "west");
        bp.addConnection(width - 1, height / 2, "east");

        return bp;
    }
}
