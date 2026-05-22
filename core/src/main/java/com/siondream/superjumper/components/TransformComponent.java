package com.siondream.superjumper.components;

/**
 * Componente de transformación (posición, rotación, escala)
 */
public class TransformComponent extends Component {
    public float x, y;
    public float rotation;
    public float scaleX, scaleY;

    public TransformComponent(float x, float y) {
        this.x = x;
        this.y = y;
        this.rotation = 0;
        this.scaleX = 1;
        this.scaleY = 1;
    }
}
