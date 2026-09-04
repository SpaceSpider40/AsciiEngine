package com.space.engine.core.actors;

import com.space.engine.core.actors.components.CameraComponent;
import com.space.engine.core.actors.components.TransformComponent;

public class CameraActor extends Actor {

    public CameraActor(
            float near,
            float far,
            float fov
    ) {
        components.add(new CameraComponent(near, far, fov));
        components.add(new TransformComponent());
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onUpdate(double deltaTime) {

    }

    @Override
    protected void onStep(double deltaTime) {

    }

    @Override
    protected void onEnd() {

    }
}
