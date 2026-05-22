package com.siondream.superjumper.components;

/**
 * Componente de velocidad
 */
public class VelocityComponent extends Component {
    public float vx, vy;
    public float maxSpeed;

    public VelocityComponent() {
        this.vx = 0;
        this.vy = 0;
        this.maxSpeed = Float.MAX_VALUE;
    }
}
