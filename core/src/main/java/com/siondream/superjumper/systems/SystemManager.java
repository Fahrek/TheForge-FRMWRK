package com.siondream.superjumper.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.siondream.superjumper.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Administra y ejecuta todos los sistemas en orden
 */
public class SystemManager {
    private final List<GameSystem> systems;
    private final World world;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;


    public SystemManager(World world, SpriteBatch batch, OrthographicCamera camera) {
        this.systems = new ArrayList<>();
        this.world = new World();
        this.batch = batch;
        this.camera = camera;
    }

    public void addSystem(GameSystem system) {
        system.init(world, batch, camera);
        systems.add(system);
    }

    public void update(float delta) {
        // Actualizar el mundo primero
        world.update();

        // Ejecutar todos los sistemas
        for (GameSystem system : systems) {
            system.update(delta);
        }
    }

    public void dispose() {
        for (GameSystem system : systems) {
            system.dispose();
        }
    }
}
