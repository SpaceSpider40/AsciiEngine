package com.engine;

import com.engine.ecs.EcsRegister;
import com.engine.ecs.components.NameComponent;

public class World {

    public final EcsRegister ecsRegistry = new EcsRegister();

    private final String name;

    public World(String name) {
        this.name = name;
    }

    public int createEntity() {
        return createEntity(null);
    }

    public int createEntity(String name) {
        int entity = ecsRegistry.createEntity();

        if (name == null || name.isEmpty()) {
            name = "Unnamed" + entity;
        }

        ecsRegistry.set(entity, new NameComponent(name));

        return entity;
    }

    public void destroyEntity(int entity) {
        ecsRegistry.destroy(entity);
    }

    record WorldSaveData(

    ) {

    }
}
