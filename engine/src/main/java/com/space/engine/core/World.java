package com.space.engine.core;

import com.space.engine.core.components.NameComponent;
import com.space.engine.core.ecs.EcsRegister;

import java.util.ArrayList;

public class World {

    private final EcsRegister       ecsRegistry    = new EcsRegister();
    private final ArrayList<Tick>   tickRegistry   = new ArrayList<>();
    private final ArrayList<Update> updateRegistry = new ArrayList<>();

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

    public EcsRegister getEntityRegistry() {
        return ecsRegistry;
    }

    public void register(Tick tick) {
        tickRegistry.add(tick);
    }

    public void register(Update update) {
        updateRegistry.add(update);
    }

    void update(double deltaTime) {
        updateRegistry.forEach(consumer -> consumer.update(deltaTime));
    }

    void tick(double deltaTime) {
        tickRegistry.forEach(consumer -> consumer.tick(deltaTime));
    }

    @Override
    public String toString() {
        return "World[" + name + "]";
    }
}
