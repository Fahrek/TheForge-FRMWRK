package com.siondream.superjumper.platforms;

import com.badlogic.gdx.math.Vector2;
import com.siondream.superjumper.GenerationDirector;
import com.siondream.superjumper.movementpatterns.CircularPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlatformGenerator extends GenerationDirector<List<Platform>> {
    private final int worldHeight;

    public PlatformGenerator(int width, int height, long seed) {
        super(seed);
        this.worldHeight = height;
    }

    @Override
    protected void setupRules() {
        // Reglas de espaciado y dificultad
    }

    @Override
    protected List<Platform> generate() {
        List<Platform> platforms = new ArrayList<>();
        Random rand = context.getRandom();

        int platformCount = (int)parameters.getOrDefault("platformCount", 20);
        float minGap = (float)parameters.getOrDefault("minGap", 50f);
        float maxGap = (float)parameters.getOrDefault("maxGap", 150f);
        float baseHeight = (float)parameters.getOrDefault("baseHeight", 100f);

        float currentX = 100f;
        float currentY = baseHeight;

        for (int i = 0; i < platformCount; i++) {
            float length = 80f + rand.nextFloat() * 120f;
            float height = 20f;

            Platform platform = new Platform(new Vector2(currentX, currentY), length, height);

            // 30% de probabilidad de ser plataforma móvil
            if (rand.nextFloat() < 0.3f) {
                platform.moving = true;
                platform.movement = new CircularPattern(
                    new Vector2(currentX + length/2, currentY),
                    50f,
                    1f
                );
            }

            platforms.add(platform);

            // Siguiente plataforma
            float gap = minGap + rand.nextFloat() * (maxGap - minGap);
            currentX += length + gap;

            // Variación vertical
            float verticalChange = -50f + rand.nextFloat() * 100f;
            currentY = Math.max(50f, Math.min(worldHeight - 100f, currentY + verticalChange));
        }

        return platforms;
    }
}
