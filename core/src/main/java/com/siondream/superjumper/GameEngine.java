package com.siondream.superjumper;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.siondream.superjumper.systems.InputSystem;
import com.siondream.superjumper.systems.PhysicsSystem;
import com.siondream.superjumper.systems.RenderSystem;
import com.siondream.superjumper.systems.SystemManager;

/**
 * Núcleo principal del motor de juegos
 * Maneja el ciclo de vida y coordina todos los sistemas
 */
public class GameEngine extends ApplicationAdapter {
    private World world;
    private SystemManager systemManager;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    @Override
    public void create() {
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 600);

        systemManager = new SystemManager(world, batch, camera);

        // Inicializar sistemas por defecto
        initializeSystems();
    }

    protected void initializeSystems() {
        // Los sistemas se añaden en orden de ejecución
        systemManager.addSystem(new InputSystem());
        systemManager.addSystem(new PhysicsSystem());
        systemManager.addSystem(new RenderSystem());
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // Limpiar pantalla
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizar cámara
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Actualizar todos los sistemas
        systemManager.update(delta);
    }

    @Override
    public void dispose() {
        batch.dispose();
        systemManager.dispose();
    }
}
