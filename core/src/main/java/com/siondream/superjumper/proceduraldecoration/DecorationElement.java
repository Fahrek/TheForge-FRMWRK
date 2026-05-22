package com.siondream.superjumper.proceduraldecoration;

import com.badlogic.gdx.math.Vector2;

public class DecorationElement {
    public String decorationType;
    public Vector2 position;
    public float rotation;
    public float scale;

    public DecorationElement(String type, Vector2 pos) {
        this.decorationType = type;
        this.position = pos;
        this.rotation = 0;
        this.scale = 1f;
    }
}
