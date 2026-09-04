package com.space.engine.core.actors.components;

import com.space.engine.core.math.Vector3;

public class TransformComponent extends ActorComponent {
    public final Vector3 position = Vector3.zero();
    public final Vector3 rotation = Vector3.zero();
    public final Vector3 scale = Vector3.one();

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
