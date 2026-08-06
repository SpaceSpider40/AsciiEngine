package com.engine;

import com.engine.ecs.EcsRegister;
import com.engine.components.NameComponent;

import java.util.ArrayList;

public class World {

    private final EcsRegister        ecsRegistry    = new EcsRegister();
    private final ArrayList<ITick>   tickRegistry   = new ArrayList<>();
    private final ArrayList<IUpdate> updateRegistry = new ArrayList<>();

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

    public void register(ITick tick) {
        tickRegistry.add(tick);
    }

    public void register(IUpdate update) {
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
