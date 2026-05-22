package com.siondream.superjumper.blueprint;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.GenerationContext;
import com.siondream.superjumper.GenerationDirector;
import com.siondream.superjumper.Rule;
import com.siondream.superjumper.tiles.Tile;
import com.siondream.superjumper.tiles.TileMap;
import com.siondream.superjumper.tiles.TileType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Director que coloca blueprints inteligentemente en el mapa
 */
public class BlueprintPlacerDirector extends GenerationDirector<List<PlacedBlueprint>> {
    private final TileMap targetMap;
    private final List<Blueprint> availableBlueprints;
    private final List<PlacedBlueprint> placedBlueprints;
    private int currentDepth; // Para reglas de profundidad

    public BlueprintPlacerDirector(TileMap map, long seed) {
        super(seed);
        this.targetMap = map;
        this.availableBlueprints = new ArrayList<>();
        this.placedBlueprints = new ArrayList<>();
        this.currentDepth = 1;
    }

    /**
     * Añade un blueprint a la lista de candidatos
     */
    public void addBlueprint(Blueprint blueprint) {
        availableBlueprints.add(blueprint);
    }

    /**
     * Añade múltiples blueprints
     */
    public void addBlueprints(Collection<Blueprint> blueprints) {
        availableBlueprints.addAll(blueprints);
    }

    /**
     * Establece la profundidad actual del nivel (para mazmorras)
     */
    public void setDepth(int depth) {
        this.currentDepth = depth;
    }

    @Override
    protected void setupRules() {
        // Reglas de validación de colocación
        ruleEngine.addRule(new Rule() {
            public boolean evaluate(GenerationContext ctx) {
                Blueprint bp = ctx.get("candidate", Blueprint.class);
                Vector2 pos = ctx.get("position", Vector2.class);

                return canPlaceBlueprint(bp, (int)pos.x, (int)pos.y);
            }
            public float getPriority() { return 10f; }
        });
    }

    @Override
    protected List<PlacedBlueprint> generate() {
        Random rand = context.getRandom();

        int maxAttempts = (int)parameters.getOrDefault("maxAttempts", 100);
        int placementAttempts = (int)parameters.getOrDefault("placementAttempts", 50);

        // Filtrar blueprints válidos para este nivel
        List<Blueprint> validBlueprints = filterValidBlueprints();

        if (validBlueprints.isEmpty()) {
            System.out.println("No hay blueprints válidos para este nivel");
            return placedBlueprints;
        }

        // Intentar colocar blueprints
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Seleccionar blueprint aleatorio (considerando probabilidades)
            Blueprint selected = selectRandomBlueprint(validBlueprints, rand);

            if (selected == null) continue;

            // Encontrar posición válida
            Vector2 position = findValidPosition(selected, placementAttempts, rand);

            if (position != null) {
                // Colocar el blueprint
                placeBlueprint(selected, (int)position.x, (int)position.y);
            }
        }

        System.out.println("Blueprints colocados: " + placedBlueprints.size());
        return placedBlueprints;
    }

    /**
     * Filtra blueprints según reglas de profundidad y bioma
     */
    private List<Blueprint> filterValidBlueprints() {
        List<Blueprint> valid = new ArrayList<>();

        for (Blueprint bp : availableBlueprints) {
            BlueprintPlacementRules rules = bp.placementRules;

            // Verificar profundidad
            if (currentDepth < rules.minDepth || currentDepth > rules.maxDepth) {
                continue;
            }

            // Verificar probabilidad de spawn
            if (context.getRandom().nextFloat() > rules.spawnProbability) {
                continue;
            }

            valid.add(bp);
        }

        return valid;
    }

    /**
     * Selecciona un blueprint aleatorio (puede ser ponderado)
     */
    private Blueprint selectRandomBlueprint(List<Blueprint> blueprints, Random rand) {
        if (blueprints.isEmpty()) return null;
        return blueprints.get(rand.nextInt(blueprints.size()));
    }

    /**
     * Busca una posición válida para colocar el blueprint
     */
    private Vector2 findValidPosition(Blueprint bp, int maxAttempts, Random rand) {
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int x = rand.nextInt(targetMap.getWidth() - bp.width);
            int y = rand.nextInt(targetMap.getHeight() - bp.height);

            if (canPlaceBlueprint(bp, x, y)) {
                return new Vector2(x, y);
            }
        }

        return null;
    }

    /**
     * Verifica si se puede colocar un blueprint en la posición dada
     */
    private boolean canPlaceBlueprint(Blueprint bp, int worldX, int worldY) {
        BlueprintPlacementRules rules = bp.placementRules;

        // Verificar límites del mapa
        if (worldX < 0 || worldY < 0 ||
            worldX + bp.width > targetMap.getWidth() ||
            worldY + bp.height > targetMap.getHeight()) {
            return false;
        }

        // Verificar solapamiento con otros blueprints
        if (!rules.allowOverlap) {
            Rectangle newBounds = new Rectangle(worldX, worldY, bp.width, bp.height);

            for (PlacedBlueprint placed : placedBlueprints) {
                if (newBounds.overlaps(placed.bounds)) {
                    return false;
                }

                // Verificar distancia mínima
                float distance = new Vector2(worldX, worldY)
                    .dst(placed.worldX, placed.worldY);

                if (distance < rules.minDistanceToOthers) {
                    return false;
                }
            }
        }

        // Verificar requisitos de suelo/paredes
        if (rules.requiresFloor) {
            if (!hasValidFloor(bp, worldX, worldY)) {
                return false;
            }
        }

        if (rules.requiresWalls) {
            if (!hasValidWalls(bp, worldX, worldY)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica si hay suelo adecuado para el blueprint
     */
    private boolean hasValidFloor(Blueprint bp, int worldX, int worldY) {
        // Verificar que el área esté mayormente vacía o sea suelo
        int floorCount = 0;
        int totalTiles = 0;

        for (int x = 0; x < bp.width; x++) {
            for (int y = 0; y < bp.height; y++) {
                TileType blueprintTile = bp.getTile(x, y);
                if (blueprintTile == TileType.EMPTY) continue; // Ignorar tiles vacíos del blueprint

                totalTiles++;
                Tile worldTile = targetMap.getTile(worldX + x, worldY + y);

                if (worldTile != null && worldTile.type == TileType.FLOOR) {
                    floorCount++;
                }
            }
        }

        // Al menos 70% debe ser suelo
        return totalTiles > 0 && (float)floorCount / totalTiles >= 0.7f;
    }

    /**
     * Verifica si hay paredes adecuadas alrededor del blueprint
     */
    private boolean hasValidWalls(Blueprint bp, int worldX, int worldY) {
        final int[] wallCount = {0};
        final int[] perimeterTiles = {0};

        for (int x = -1; x <= bp.width; x++) {
            checkWallTile(worldX + x, worldY - 1, ref -> { wallCount[0] += ref[0]; perimeterTiles[0] += ref[1]; });
            checkWallTile(worldX + x, worldY + bp.height, ref -> { wallCount[0] += ref[0]; perimeterTiles[0] += ref[1]; });
        }

        for (int y = 0; y < bp.height; y++) {
            checkWallTile(worldX - 1, worldY + y, ref -> { wallCount[0] += ref[0]; perimeterTiles[0] += ref[1]; });
            checkWallTile(worldX + bp.width, worldY + y, ref -> { wallCount[0] += ref[0]; perimeterTiles[0] += ref[1]; });
        }

        return perimeterTiles[0] > 0 && (float)wallCount[0] / perimeterTiles[0] >= 0.5f;
    }

    private void checkWallTile(int x, int y, java.util.function.Consumer<int[]> consumer) {
        Tile tile = targetMap.getTile(x, y);
        int[] ref = new int[2];
        ref[1] = 1; // perimeterTiles
        if (tile != null && tile.type == TileType.WALL) {
            ref[0] = 1; // wallCount
        }
        consumer.accept(ref);
    }

    /**
     * Coloca el blueprint en el mapa
     */
    private void placeBlueprint(Blueprint bp, int worldX, int worldY) {
        // Copiar tiles del blueprint al mapa
        for (int x = 0; x < bp.width; x++) {
            for (int y = 0; y < bp.height; y++) {
                TileType blueprintTile = bp.getTile(x, y);

                if (blueprintTile != TileType.EMPTY) {
                    targetMap.setTile(worldX + x, worldY + y, blueprintTile);
                }
            }
        }

        // Registrar el blueprint colocado
        PlacedBlueprint placed = new PlacedBlueprint(bp, worldX, worldY);
        placedBlueprints.add(placed);

        // Marcar en metadata del mapa
        for (int x = 0; x < bp.width; x++) {
            for (int y = 0; y < bp.height; y++) {
                Tile tile = targetMap.getTile(worldX + x, worldY + y);
                if (tile != null) {
                    tile.metadata.put("blueprint", bp.id);
                    tile.metadata.put("blueprint_local_x", x);
                    tile.metadata.put("blueprint_local_y", y);
                }
            }
        }
    }

    /**
     * Obtiene todos los blueprints colocados
     */
    public List<PlacedBlueprint> getPlacedBlueprints() {
        return new ArrayList<>(placedBlueprints);
    }

    /**
     * Obtiene las entidades de todos los blueprints colocados
     * (para integración con ECS)
     */
    public List<BlueprintEntity> getAllPlacedEntities() {
        List<BlueprintEntity> allEntities = new ArrayList<>();

        for (PlacedBlueprint placed : placedBlueprints) {
            for (BlueprintEntity entity : placed.blueprint.entities) {
                // Crear copia con posición ajustada al mundo
                BlueprintEntity worldEntity = entity.clone();
                worldEntity.x += placed.worldX;
                worldEntity.y += placed.worldY;
                allEntities.add(worldEntity);
            }
        }

        return allEntities;
    }
}
