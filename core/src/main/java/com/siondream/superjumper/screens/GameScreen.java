package com.siondream.superjumper.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.TimeUtils;
import com.siondream.superjumper.GeneratedLevel;
import com.siondream.superjumper.MasterLevelDirector;
import com.siondream.superjumper.demo.createGameObjectsFromLevel;

public class GameScreen implements Screen {

    @Override
    public void show() {
        long seed = TimeUtils.millis(); // O usa SeedManager
        MasterLevelDirector director = new MasterLevelDirector(seed, "PLATFORMER");
        director.setGlobalParameter("width", 50);
        director.setGlobalParameter("height", 30);

        GeneratedLevel currentLevel = director.generateLevel();
        new createGameObjectsFromLevel();
    }

    public void render(float delta) {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}

