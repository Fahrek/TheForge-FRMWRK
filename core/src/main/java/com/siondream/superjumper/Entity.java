package com.siondream.superjumper;

import com.siondream.superjumper.components.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Entity {
    private static long nextId = 0;
    private final long id;
    private final Map<Class<? extends Component>, Component> components;
    private boolean active;
    private String tag;

    public Entity() {
        this.id = nextId++;
        this.components = new HashMap<>();
        this.active = true;
        this.tag = "";
    }

    public long getId() { return id; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
        //component.setOwner(this);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return type.cast(components.get(type));
    }

    public <T extends Component> boolean hasComponent(Class<? extends Component> type) {
        return components.containsKey(type);
    }

    public <T extends Component> void removeComponent(Class<T> type) {
        components.remove(type);
    }

    public Collection<Component> getAllComponents() {
        return components.values();
    }
}
