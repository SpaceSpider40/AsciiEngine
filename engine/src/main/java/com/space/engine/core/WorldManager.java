package com.space.engine.core;

import com.space.engine.core.ecs.components.ActiveCameraComponent;
import com.space.engine.core.ecs.components.CameraComponent;
import com.space.engine.core.ecs.components.TransformComponent;
import com.space.engine.core.math.Vector3;

public class WorldManager {

    private World currentWorld;

    public World getCurrentWorld() {

        if (currentWorld == null) {
            currentWorld = createTemplateWorld();
        }

        return currentWorld;
    }

    @SuppressWarnings("D")
    private World createTemplateWorld() {
        var world = new World("Empty World");

        return world;
    }
}
