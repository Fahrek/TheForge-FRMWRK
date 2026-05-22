package com.siondream.superjumper.systems;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.siondream.superjumper.World;

/**
 * Sistema base - Contiene la lógica que opera sobre componentes
 */
public abstract class GameSystem {
    protected World world;
    protected SpriteBatch batch;
    protected OrthographicCamera camera;

    public void init(World world, SpriteBatch batch, OrthographicCamera camera) {
        this.world = world;
        this.batch = batch;
        this.camera = camera;
    }

    public abstract void update(float delta);

    public void dispose() {
        // Override si es necesario
    }
}
