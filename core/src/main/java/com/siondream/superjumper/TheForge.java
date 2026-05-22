package com.siondream.superjumper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.czyzby.noise4j.map.Grid;
import com.github.czyzby.noise4j.map.generator.cellular.CellularAutomataGenerator;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;
import com.github.czyzby.noise4j.map.generator.room.RoomType.DefaultRoomType;
import com.github.czyzby.noise4j.map.generator.room.dungeon.DungeonGenerator;
import com.github.czyzby.noise4j.map.generator.util.Generators;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class TheForge extends ApplicationAdapter {
    /** Size of the generated maps. */
    public static final int SIZE = 200;

    private final Grid grid = new Grid(SIZE);
    private Batch batch;
    private Texture texture;
    private Pixmap pixmap;

    @Override
    public void create() {
        pixmap = new Pixmap(SIZE, SIZE, Format.RGBA8888);
        batch = new SpriteBatch();
        // Añadiendo el oyente de eventos - recreando el mapa al hacer clic:
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                rollMap();
                return true;
            }
        });
        // Creación de un mapa aleatorio:
        rollMap();
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        batch.begin();
        batch.draw(texture, 0f, 0f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        texture.dispose();
        pixmap.dispose();
    }

    private void rollMap() {
        // Borrar todos los valores de la cuadrícula:
        grid.set(0f);
        // Elegir generador de mapas:
        float test = MathUtils.random();
        if (test < 0.25f) {
            createNoiseMap();
        } else if (test < 0.50f) {
            createCellularMap();
        } else if (test < 0.75f) {
            createSimpleDungeonMap();
        } else {
            createDungeonMap();
        }

        createTexture();
    }

    /** Utiliza NoiseGenerator para crear un mapa de altura. */
    private void createNoiseMap() {
        NoiseGenerator noiseGenerator = new NoiseGenerator();
        // El primer valor es el radio, el segundo es el modificador. Asegurando que las regiones más grandes tengan el mayor número
        // El modificador permite generar mapas interesantes con transiciones suaves entre regiones.
        noiseStage(noiseGenerator, 32, 0.45f);
        noiseStage(noiseGenerator, 16, 0.25f);
        noiseStage(noiseGenerator, 8, 0.15f);
        noiseStage(noiseGenerator, 4, 0.1f);
        noiseStage(noiseGenerator, 2, 0.05f);
    }

    private void noiseStage(NoiseGenerator noiseGenerator, int radius, float modifier) {
        noiseGenerator.setRadius(radius); // Radio de un solo sector.
        noiseGenerator.setModifier(modifier); // El valor máximo añadido a una sola celda.
        // La semilla asegura aleatoriedad, se puede guardar si sientes la necesidad de generar el mismo mapa en el futuro.
        noiseGenerator.setSeed(Generators.rollSeed());
        noiseGenerator.generate(grid);
    }

    /** Utiliza CellularAutomataGenerator para crear un mapa tipo cueva. */
    private void createCellularMap() {
        CellularAutomataGenerator cellularGenerator = new CellularAutomataGenerator();
        cellularGenerator.setAliveChance(0.5f); // El 50% de las células empezarán como llenas.
        cellularGenerator.setIterationsAmount(4); // Cuantas más iteraciones, más suave es el mapa.
        cellularGenerator.generate(grid);
    }

    /** Utiliza DungeonGenerator para crear un mapa sencillo tipo muro-pasillo-habitación. */
    private void createSimpleDungeonMap() {
        DungeonGenerator dungeonGenerator = new DungeonGenerator();
        dungeonGenerator.setRoomGenerationAttempts(500); // Cuanto más grande sea, más habitaciones es probable que aparezcan.
        dungeonGenerator.setMaxRoomSize(21); // Tamaño máximo de la habitación, debería ser extraño.
        dungeonGenerator.setTolerance(5); // Máxima diferencia entre ancho y alto.
        dungeonGenerator.setMinRoomSize(5); // Tamaño mínimo de la habitación, debería ser extraño.
        dungeonGenerator.generate(grid);
    }

    /** Utiliza DungeonGenerator para crear un mapa tipo muro-pasillo-habitación con diferentes tipos de salas. */
    private void createDungeonMap() {
        DungeonGenerator dungeonGenerator = new DungeonGenerator();
        dungeonGenerator.setRoomGenerationAttempts(500); // Cuanto más grande sea, más habitaciones es probable que aparezcan.
        dungeonGenerator.setMaxRoomSize(25); // Tamaño máximo de la habitación, debería ser raro.
        dungeonGenerator.setTolerance(5); // Diferencia máxima entre ancho y altura.
        dungeonGenerator.setMinRoomSize(9); // Tamaño mínimo de la habitación, debería ser raro.
        dungeonGenerator.addRoomTypes(DefaultRoomType.values()); // Añadir diferentes tipos de habitaciones.
        dungeonGenerator.generate(grid);
    }

    private void createTexture() {
        // Destruyendo texturas anteriores:
        if (texture != null) {
            texture.dispose();
        }
        // Dibujar en pixmap según los valores de la cuadrícula:
        Color color = new Color();
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                float cell = grid.get(x, y);
                color.set(cell, cell, cell, 1f);
                pixmap.drawPixel(x, y, Color.rgba8888(color));
            }
        } // Crear una nueva textura con los valores de pixmap:
        texture = new Texture(pixmap);
    }
}
