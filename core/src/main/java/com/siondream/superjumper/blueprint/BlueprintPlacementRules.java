package com.siondream.superjumper.blueprint;

import java.util.ArrayList;
import java.util.List;

/**
 * Reglas de colocación del blueprint
 */
public class BlueprintPlacementRules {
    public boolean requiresFloor; // Necesita suelo debajo
    public boolean requiresWalls; // Necesita paredes alrededor
    public boolean allowOverlap; // Puede superponerse con otros blueprints
    public float minDistanceToOthers; // Distancia mínima a otros blueprints
    public List<String> requiredBiomes; // Solo en ciertos biomas
    public List<String> forbiddenBiomes; // No en estos biomas
    public int minDepth; // Profundidad mínima del nivel (para dungeons)
    public int maxDepth; // Profundidad máxima
    public float spawnProbability; // 0-1, probabilidad de aparecer

    public BlueprintPlacementRules() {
        this.requiresFloor = true;
        this.requiresWalls = false;
        this.allowOverlap = false;
        this.minDistanceToOthers = 5f;
        this.requiredBiomes = new ArrayList<>();
        this.forbiddenBiomes = new ArrayList<>();
        this.minDepth = 0;
        this.maxDepth = Integer.MAX_VALUE;
        this.spawnProbability = 1.0f;
    }

    public BlueprintPlacementRules clone() {
        BlueprintPlacementRules copy = new BlueprintPlacementRules();
        copy.requiresFloor = this.requiresFloor;
        copy.requiresWalls = this.requiresWalls;
        copy.allowOverlap = this.allowOverlap;
        copy.minDistanceToOthers = this.minDistanceToOthers;
        copy.requiredBiomes = new ArrayList<>(this.requiredBiomes);
        copy.forbiddenBiomes = new ArrayList<>(this.forbiddenBiomes);
        copy.minDepth = this.minDepth;
        copy.maxDepth = this.maxDepth;
        copy.spawnProbability = this.spawnProbability;
        return copy;
    }
}
