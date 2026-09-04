package com.space.engine.core;

import com.space.engine.core.ecs.components.NameComponent;
import com.space.engine.core.actors.Actor;
import com.space.engine.core.ecs.EcsRegister;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public class World {
    private final String name;

    private final ArrayList<Actor> actors = new ArrayList<>();

    public World(String name) {
        this.name = name;
    }

    public void addActor(Actor actor) {
        actors.add(actor);
    }

    public Collection<Actor> getActors() {
        return actors;
    }

    @Override
    public String toString() {
        return "World[" + name + "]";
    }
}
