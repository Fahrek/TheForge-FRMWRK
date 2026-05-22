package com.siondream.superjumper.components;

import com.siondream.superjumper.Entity;

public abstract class Component {
    protected Entity owner;

    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    public Entity getOwner() {
        return owner;
    }
}
