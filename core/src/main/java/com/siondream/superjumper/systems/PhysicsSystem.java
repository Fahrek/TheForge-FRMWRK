package com.siondream.superjumper.systems;

import com.siondream.superjumper.Entity;
import com.siondream.superjumper.components.TransformComponent;
import com.siondream.superjumper.components.VelocityComponent;

/**
 * Sistema de física simple
 */
public class PhysicsSystem extends GameSystem {
    @Override
    public void update(float delta) {
        for (Entity entity : world.getEntitiesWithComponent(VelocityComponent.class)) {
            if (!entity.hasComponent(TransformComponent.class)) continue;

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            VelocityComponent velocity = entity.getComponent(VelocityComponent.class);

            // Aplicar velocidad
            transform.x += velocity.vx * delta;
            transform.y += velocity.vy * delta;
        }
    }
}
