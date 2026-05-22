package com.siondream.superjumper;

import com.siondream.superjumper.algorithms.BSPMapGenerator;
import com.siondream.superjumper.algorithms.CellularAutomataGenerator;
import com.siondream.superjumper.biome.BiomeGenerator;
import com.siondream.superjumper.platforms.PlatformGenerator;
import com.siondream.superjumper.proceduraldecoration.DecorationDirector;
import com.siondream.superjumper.scatteritems.ItemDistributionDirector;

import com.siondream.superjumper.rooms.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Director maestro que coordina todos los otros directores
 * para generar un nivel completo según especificaciones
 */
public class MasterLevelDirector {
    private final long masterSeed;
    private final String levelType; // "DUNGEON", "PLATFORMER", "CAVE", "OPEN_WORLD"
    private final Map<String, Object> globalParameters;

    public MasterLevelDirector(long seed, String levelType) {
        this.masterSeed = seed;
        this.levelType = levelType;
        this.globalParameters = new HashMap<>();
    }

    public void setGlobalParameter(String key, Object value) {
        globalParameters.put(key, value);
    }

    /**
     * Genera un nivel completo según el tipo especificado
     */
    public GeneratedLevel generateLevel() {
        GeneratedLevel level = new GeneratedLevel();

        switch (levelType) {
            case "DUNGEON":
                generateDungeonLevel(level);
                break;

            case "PLATFORMER":
                generatePlatformerLevel(level);
                break;

            case "CAVE":
                generateCaveLevel(level);
                break;

            case "OPEN_WORLD":
                generateOpenWorldLevel(level);
                break;

            default:
                generateDungeonLevel(level);
        }

        return level;
    }

    private void generateDungeonLevel(GeneratedLevel level) {
        // Usar BSP para estructura más organizada
        BSPMapGenerator mapGen = new BSPMapGenerator(
            (int)globalParameters.getOrDefault("width", 100),
            (int)globalParameters.getOrDefault("height", 80),
            masterSeed
        );
        level.map = mapGen.execute();

        // Necesitamos obtener las rooms del BSP (esto requeriría modificar BSPMapGenerator)
        // Por ahora usamos un placeholder
        List<Room> rooms = new ArrayList<>();

        // Distribuir contenido
        ItemDistributionDirector itemGen = new ItemDistributionDirector(level.map, rooms, masterSeed + 1);
        level.items = itemGen.execute();

        DecorationDirector decorGen = new DecorationDirector(level.map, masterSeed + 2);
        level.decorations = decorGen.execute();

        level.type = "DUNGEON";
    }

    private void generatePlatformerLevel(GeneratedLevel level) {
        PlatformGenerator platformGen = new PlatformGenerator(
            (int)globalParameters.getOrDefault("width", 2000),
            (int)globalParameters.getOrDefault("height", 800),
            masterSeed
        );
        platformGen.setParameter("platformCount", 40);
        level.platforms = platformGen.execute();

        level.type = "PLATFORMER";
    }

    private void generateCaveLevel(GeneratedLevel level) {
        CellularAutomataGenerator caveGen = new CellularAutomataGenerator(
            (int)globalParameters.getOrDefault("width", 120),
            (int)globalParameters.getOrDefault("height", 100),
            masterSeed
        );
        caveGen.setParameter("wallProbability", 0.45f);
        caveGen.setParameter("iterations", 6);
        level.map = caveGen.execute();

        level.type = "CAVE";
    }

    private void generateOpenWorldLevel(GeneratedLevel level) {
        // Generar mapa grande con biomas
        BiomeGenerator biomeGen = new BiomeGenerator(
            (int)globalParameters.getOrDefault("width", 200),
            (int)globalParameters.getOrDefault("height", 200),
            masterSeed
        );
        biomeGen.setParameter("scale", 60f);
        biomeGen.setParameter("octaves", 5);
        level.map = biomeGen.execute();

        level.type = "OPEN_WORLD";
    }
}
