package com.siondream.superjumper;

import com.siondream.superjumper.helpers.SeedManager;

public class FrameworkDemo {
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║  FRAMEWORK UNIVERSAL DE GENERACIÓN PROCEDURAL 2D  ║");
        System.out.println("╚═══════════════════════════════════════════════════╝\n");

        // Ejemplo 1: Generación manual con control total
        System.out.println("█ EJEMPLO 1: Generación manual paso a paso");
        ExampleUsage.generateCompleteLevel();

        // Ejemplo 2: Usar el Master Director
        System.out.println("\n\n█ EJEMPLO 2: Generación automática con Master Director");

        long seed = SeedManager.generateFromString("MiNivelEpico");

        MasterLevelDirector master = new MasterLevelDirector(seed, "DUNGEON");
        master.setGlobalParameter("width", 100);
        master.setGlobalParameter("height", 80);

        GeneratedLevel dungeonLevel = master.generateLevel();
        dungeonLevel.printStatistics();

        // Generar diferentes tipos
        String[] types = {"CAVE", "PLATFORMER", "OPEN_WORLD"};
        for (String type : types) {
            MasterLevelDirector director = new MasterLevelDirector(seed + type.hashCode(), type);
            director.setGlobalParameter("width", 120);
            director.setGlobalParameter("height", 100);

            GeneratedLevel level = director.generateLevel();
            level.printStatistics();
        }

        System.out.println("✓ Framework demostrado exitosamente");
    }
}
