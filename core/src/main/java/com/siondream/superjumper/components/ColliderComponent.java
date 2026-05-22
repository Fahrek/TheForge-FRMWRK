package com.siondream.superjumper.components;

/**
 * Componente de colisión
 */
public class ColliderComponent extends Component {
    public float width, height;
    public float offsetX, offsetY;
    public boolean isTrigger;
    public String collisionLayer;

    public ColliderComponent(float width, float height) {
        this.width = width;
        this.height = height;
        this.offsetX = 0;
        this.offsetY = 0;
        this.isTrigger = false;
        this.collisionLayer = "default";
    }
}
