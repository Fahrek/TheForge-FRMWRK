package com.siondream.superjumper.components;

/**
 * Componente de sprite (visual)
 */
public class SpriteComponent extends Component {
    public String texturePath;
    public float width, height;
    public float r, g, b, a; // Color tint

    public SpriteComponent(String texturePath, float width, float height) {
        this.texturePath = texturePath;
        this.width = width;
        this.height = height;
        this.r = this.g = this.b = this.a = 1.0f;
    }
}
