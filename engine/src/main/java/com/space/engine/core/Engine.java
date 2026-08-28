package com.space.engine.core;

public interface Engine {
    public EngineState state();

    // RUNTIME
    public void init();

    public void run();

    public void shutdown();

    // CONTENT
    public World currentWorld();

    // ECS
    public int createEntity();

    public void destroyEntity(int entity);


    //PHYSICS
}
