package com.siondream.superjumper;

import com.siondream.superjumper.components.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class World {
    private final List<Entity> entities;
    private final List<Entity> entitiesToAdd;
    private final List<Entity> entitiesToRemove;

    public World() {
        this.entities = new ArrayList<>();
        this.entitiesToAdd = new ArrayList<>();
        this.entitiesToRemove = new ArrayList<>();
    }

    public Entity createEntity() {
        Entity entity = new Entity();
        entitiesToAdd.add(entity);
        return entity;
    }

    public void removeEntity(Entity entity) {
        entitiesToRemove.add(entity);
    }

    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public List<Entity> getEntitiesWithComponent(Class<? extends Component> componentType) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.isActive() && entity.hasComponent(componentType)) {
                result.add(entity);
            }
        }
        return result;
    }


    public Entity getEntityByTag(String tag) {
        for (Entity entity : entities) {
            if (entity.isActive() && entity.getTag().equals(tag)) {
                return entity;
            }
        }
        return null;
    }

    public void update() {
        // Añadir entidades pendientes
        entities.addAll(entitiesToAdd);
        entitiesToAdd.clear();

        // Eliminar entidades marcadas
        entities.removeAll(entitiesToRemove);
        entitiesToRemove.clear();
    }
}
