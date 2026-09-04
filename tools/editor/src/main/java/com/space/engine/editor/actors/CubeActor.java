package com.space.engine.editor.actors;

import com.space.engine.core.MeshFactory;
import com.space.engine.core.actors.Actor;
import com.space.engine.core.actors.components.MeshComponent;
import com.space.engine.core.actors.components.TransformComponent;
import com.space.engine.core.math.Vector3;

public class CubeActor extends Actor {

    public CubeActor() {
        components.add(new TransformComponent());
        components.add(new MeshComponent(MeshFactory.createBox(new Vector3(1, 1, 1)).tris()));
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onUpdate(double deltaTime) {
        getComponent(TransformComponent.class).rotation.y += 0.01f * (float) deltaTime;
    }

    @Override
    protected void onStep(double deltaTime) {

    }

    @Override
    protected void onEnd() {

    }
}
