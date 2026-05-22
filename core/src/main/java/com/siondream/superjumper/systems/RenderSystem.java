package com.siondream.superjumper.systems;

import com.siondream.superjumper.Entity;
import com.siondream.superjumper.components.SpriteComponent;
import com.siondream.superjumper.components.TransformComponent;

/**
 * Sistema de renderizado
 */
public class RenderSystem extends GameSystem {
    @Override
    public void update(float delta) {
        batch.begin();

        for (Entity entity : world.getEntitiesWithComponent(SpriteComponent.class)) {
            if (!entity.hasComponent(TransformComponent.class)) continue;

            TransformComponent transform = entity.getComponent(TransformComponent.class);
            SpriteComponent sprite = entity.getComponent(SpriteComponent.class);

            // Aquí se renderizaría la textura real
            // Por ahora es un placeholder
            batch.setColor(sprite.r, sprite.g, sprite.b, sprite.a);
            // batch.draw(texture, transform.x, transform.y, sprite.width, sprite.height);
        }

        batch.end();
    }
}
