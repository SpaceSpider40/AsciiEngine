package com.space.engine.core.actors.components;

import java.util.UUID;

public abstract class ActorComponent {
    private UUID uid = UUID.randomUUID();

    public final void start() {
        onStart();
    }

    public final void update(double deltaTime) {
        onUpdate(deltaTime);
    }

    public final void step(double deltaTime) {
        onStep(deltaTime);
    }

    public final void end() {
        onEnd();
    }

    protected abstract void onStart();

    protected abstract void onUpdate(double deltaTime);

    protected abstract void onStep(double deltaTime);

    protected abstract void onEnd();
}
